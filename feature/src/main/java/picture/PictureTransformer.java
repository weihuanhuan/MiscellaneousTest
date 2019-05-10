package picture;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.png.PngMetadataReader;
import com.drew.imaging.png.PngProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.sun.imageio.plugins.bmp.BMPCompressionTypes;
import com.sun.imageio.plugins.bmp.BMPConstants;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.bmp.BMPImageWriteParam;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

/**
 * Created by JasonFitch on 5/5/2019.
 */
public class PictureTransformer {

    static Logger logger = Logger.getLogger(PictureTransformer.class.getName());


    public static void main(String[] args) throws IOException {

        //jpg
        String srcFilePath = "E:/boc.jpg";
        String descFilePath = "E:/boc-compress.jpg";
        PictureTransformer.compressPic(srcFilePath, descFilePath);

        //jpg with exif
        String srcFilePath_1M = "E:/1M.jpg";
        String descFilePath_1M = "E:/1M-compress.jpg";
        PictureTransformer.compressPic(srcFilePath_1M, descFilePath_1M);

        //png
        String srcFilePath_png = "E:/logo.png";
        String descFilePath_png = "E:/logo-compress.png";
        PictureTransformer.compressPic(srcFilePath_png, descFilePath_png);

        //no suffix
        String srcFilePath_no = "E:/logo-nosuffix";
        String descFilePath_no = "E:/logo-nosuffix.png";
        PictureTransformer.compressPic(srcFilePath_no, descFilePath_no);

        //no suffix
        String srcFilePath_bmp = "E:/DefaultSnapshot.bmp";
        String descFilePath_bmp = "E:/DefaultSnapshot-compress.bmp";
        PictureTransformer.compressPic(srcFilePath_bmp, descFilePath_bmp);

//        new GZIPOutputStream

    }

    public static boolean compressPic(String srcFilePath, String descFilePath) throws IOException {

        try {

            File file = null;
            BufferedImage bufferedImage = null;
            FileOutputStream fileOutputStream = null;
            ImageWriter imageWriter;
            ImageWriteParam imageWriteParam;

            // 指定写图片的方式为 jpg
//        imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();
            String suffix = srcFilePath.substring(srcFilePath.lastIndexOf(".") + 1, srcFilePath.length());
            imageWriter = ImageIO.getImageWritersBySuffix(suffix).next();

            //BMP需要区别对待
            if (suffix.equalsIgnoreCase("bmp")) {
                imageWriteParam = new BMPImageWriteParam(null);
                imageWriteParam.setCompressionMode(imageWriteParam.MODE_EXPLICIT);
                //BMP 压缩后无法查看, 压缩类型 BI_PNG
                imageWriteParam.setCompressionType(BMPCompressionTypes.getCompressionTypes()[5]);
            } else {
                imageWriteParam = new JPEGImageWriteParam(null);
                imageWriteParam.setCompressionMode(imageWriteParam.MODE_EXPLICIT);
                imageWriteParam.setProgressiveMode(imageWriteParam.MODE_COPY_FROM_METADATA);
            }

            // 要使用压缩，必须指定压缩方式为 MODE_EXPLICIT
            // 这里指定压缩的程度，参数qality是取值0~1范围内，0 -> 1  低 -> 高
            imageWriteParam.setCompressionQuality((float) 0.50);

            // 指定压缩时使用的色彩模式
//        ColorModel colorModel = ImageIO.read(new File(srcFilePath)).getColorModel();// ColorModel.getRGBdefault();
//        imageWriteParam.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));


            if (isBlank(srcFilePath)) {
                return false;
            } else {
                file = new File(srcFilePath);
                bufferedImage = ImageIO.read(file);

//                bufferedImage = transformRotate(bufferedImage);

                AffineTransform affineTransform = readMetadata(file);
                bufferedImage = transformRotateWithsform(bufferedImage, affineTransform);

                fileOutputStream = new FileOutputStream(descFilePath);

                imageWriter.reset();
                // 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何
                // OutputStream构造
                imageWriter.setOutput(ImageIO.createImageOutputStream(fileOutputStream));
                // 调用write方法，就可以向输入流写图片
                imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static AffineTransform readMetadata(File file) throws JpegProcessingException, IOException, PngProcessingException {
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(file);

            int directoryCount = metadata.getDirectoryCount();
            logger.info("directory-Count: [" + file.getAbsolutePath() + "] = " + directoryCount);

            Iterable<Directory> directories = metadata.getDirectories();
            for (Directory directory : directories) {
                Collection<Tag> tags = directory.getTags();
                for (Tag tag : tags) {
                    logger.info(tag.toString());
                }
            }
            logger.info("-------------all directory---------");

            ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (exifIFD0Directory == null) {
                return null;
            }
            Collection<Tag> tags = exifIFD0Directory.getTags();
            if (tags == null) {
                return null;
            }
            for (Tag tag : tags) {
                logger.info(tag.toString());
            }
            logger.info("-------------ExifIFD0Directory all tags---------");


            Metadata imageMetadata = ImageMetadataReader.readMetadata(file);
            Directory directory = imageMetadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            JpegDirectory jpegDirectory = imageMetadata.getFirstDirectoryOfType(JpegDirectory.class);

            int orientation = 1;
            try {
                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            } catch (MetadataException me) {
            }
            int width = jpegDirectory.getImageWidth();
            int height = jpegDirectory.getImageHeight();

            AffineTransform exifTransformation = getExifTransformation(orientation, width, height);
            return exifTransformation;

        } catch (ImageProcessingException e) {
            //   ImageProcessingException 包括
            //   JpegProcessingException
            //   PngProcessingException  等的处理
            e.printStackTrace();
//            Metadata metadata = PngMetadataReader.readMetadata(file);
        } catch (MetadataException e) {
            e.printStackTrace();
        }

        logger.info("#######################################################");

        return null;
    }

    // Look at http://chunter.tistory.com/143 for information
    public static AffineTransform getExifTransformation(int tagOrientation, int width, int height) {

        AffineTransform t = new AffineTransform();

        switch (tagOrientation) {
            case 1:
                break;
            case 2: // Flip X
                t.scale(-1.0, 1.0);
                t.translate(width, 0);
                break;
            case 3: // PI rotation
                t.translate(width, height);
                t.rotate(Math.PI);
                break;
            case 4: // Flip Y
                t.scale(1.0, -1.0);
                t.translate(0, -height);
                break;
            case 5: // - PI/2 and Flip X
                t.rotate(-Math.PI / 2);
                t.scale(-1.0, 1.0);
                break;
            case 6: // -PI/2 and -width
                t.translate(height, 0);
                t.rotate(Math.PI / 2);
                break;
            case 7: // PI/2 and Flip
                t.scale(-1.0, 1.0);
                t.translate(-height, 0);
                t.translate(0, width);
                t.rotate(3 * Math.PI / 2);
                break;
            case 8: // PI / 2
                t.translate(0, width);
                t.rotate(3 * Math.PI / 2);
                break;
        }

        return t;
    }

    private static BufferedImage transformWithAffineTransform(BufferedImage bufferedImage, AffineTransform affineTransform) {
        AffineTransformOp op = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BICUBIC);

        BufferedImage destinationImage = op.createCompatibleDestImage(bufferedImage, (bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY) ? bufferedImage.getColorModel() : null);
        Graphics2D g = destinationImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
        destinationImage = op.filter(bufferedImage, destinationImage);
        return destinationImage;
    }

    private static BufferedImage transformRotateWithsform(BufferedImage originalImage, AffineTransform affineTransform) {
        if (affineTransform == null)
            return originalImage;

        AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);

        // new destination image where height = width and width = height.
        BufferedImage newImage = new BufferedImage(originalImage.getHeight(), originalImage.getWidth(), originalImage.getType());
        affineTransformOp.filter(originalImage, newImage);

        return newImage;
    }

    private static BufferedImage transformRotate(BufferedImage originalImage) {

        AffineTransform affineTransform = new AffineTransform();

        // last, width = height and height = width :)
        affineTransform.translate(originalImage.getHeight() / 2, originalImage.getWidth() / 2);
        affineTransform.rotate(Math.PI / 2);
        // first - center image at the origin so rotate works OK
        affineTransform.translate(-originalImage.getWidth() / 2, -originalImage.getHeight() / 2);

        AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);

        // new destination image where height = width and width = height.
        BufferedImage newImage = new BufferedImage(originalImage.getHeight(), originalImage.getWidth(), originalImage.getType());
        affineTransformOp.filter(originalImage, newImage);

        return newImage;
    }


    public static boolean isBlank(String string) {
        if (string == null || string.length() == 0 || string.trim().equals("")) {
            return true;
        }
        return false;
    }


    public static byte[] gzipCompress(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }


}