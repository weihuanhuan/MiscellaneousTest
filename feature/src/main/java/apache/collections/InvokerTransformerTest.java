package apache.collections;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InvokerTransformer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class InvokerTransformerTest {

    public static void main(String[] args) {

        String fname = "InvokerTransformerTest.log";
        String bytes = "InvokerTransformerTest-data";

        Transformer reflectWrite = InvokerTransformer.getInstance("write");

        WriteHelper writeHelper = new WriteHelper(fname, bytes);
        reflectWrite.transform(writeHelper);
    }

    public static class WriteHelper {

        String fname;
        String bytes;

        public WriteHelper(String fname, String bytes) {
            this.fname = fname;
            this.bytes = bytes;
        }

        public void write() throws IOException {
            FileOutputStream fileOutputStream = new FileOutputStream(fname);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(bytes);
            outputStreamWriter.close();
        }
    }

}
