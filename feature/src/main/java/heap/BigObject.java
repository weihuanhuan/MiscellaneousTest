package heap;

/**
 * Created by JasonFitch on 4/29/2019.
 */
public class BigObject {

    private int size = 1024;

    private byte[] bytes = new byte[size];

    public BigObject() {
    }

    public BigObject(int size, byte[] bytes) {
        this.size = size;
        this.bytes = bytes;
    }

    public int getSize() {

        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

}
