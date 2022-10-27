package io.FileTest.FileNIOTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

/**
 * Created by JasonFitch on 10/8/2018.
 */
public class FileNIOTest {

    public static void main(String[] args) {

        String appName = "app";
        String srcDir = "/nioCopy" + File.separator + appName;
        String destDir = "/nioCopy/deploy" + File.separator + appName;

        Path srcPath = Paths.get(srcDir);
        Path destPath = Paths.get(destDir);
//        注意,Path是nio包中的一个接口，获得其需通过 Paths的静态方法来去得。

        Path relativePath1 = srcPath.relativize(destPath);
        System.out.println(relativePath1); // src 如何到达 dest ---> ..\dest

        Path relativePath2 = srcPath.relativize(srcPath);
        System.out.println(relativePath2); // src to src ---> 返回空的Path

        System.out.println(destPath.getParent()); // /niCopy/deploy

        System.out.println(Files.notExists(srcPath));

        EnumSet<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);

        FileVisitor<Path> fileVisitor = new CopyTreeExtends(srcPath, destPath);
        try {
            Files.walkFileTree(srcPath, options, Integer.MAX_VALUE, fileVisitor);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

//遍历目录树时，继承nio自带的简单实现类。java.nio.file.SimpleFileVisitor
class CopyTreeExtends extends SimpleFileVisitor<Path> {

    //  这种方法可以使用默认的实现，做到只重写自己感兴趣的遍历任务即可，而实现接口则要强制重写全部方法。
    private final Path src;
    private final Path dest;

    public CopyTreeExtends(Path src, Path dest) {
        this.src = src;
        this.dest = dest;
    }

    private void filesCopy(Path path, Path destDir, StandardCopyOption replaceExisting) throws IOException {

        try {

            Files.copy(path, destDir, replaceExisting);
        } catch (DirectoryNotEmptyException dirNotEmptyEx) {
            System.out.println("dir :"+destDir+ " not empty");
        }

        //此copy命令是将 source 拷贝为 target ，而不是将 source 拷贝到 target目录下面，
        // 即不是 target/source的形式，是 source ---> target 的形式.
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path destDir = dest.resolve(src.relativize(dir));
        System.out.println("walking  dir :" + dir + " to" + destDir);

        filesCopy(dir, destDir, StandardCopyOption.REPLACE_EXISTING);

        return FileVisitResult.CONTINUE;
    }


    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path destFile = dest.resolve(src.relativize(file));
        System.out.println("walking file :" + file + " to" + destFile);

        filesCopy(file, destFile, StandardCopyOption.REPLACE_EXISTING);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc != null) {
            throw exc;
        }
        return super.postVisitDirectory(dir, exc);
    }
}

//遍历目录树，实现nio的接口 java.nio.file.FileVisitor
//class CopyTreeImpl implements FileVisitor<Path> {}