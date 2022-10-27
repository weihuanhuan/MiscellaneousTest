package collection.stream.bean;

public class StreamTestObject {

    private int type;

    private int priority;

    public StreamTestObject(int type, int priority) {
        this.type = type;
        this.priority = priority;
    }

    public int getType() {
        System.out.println("getType:" + priority);
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriority() {
        System.out.println("getPriority:" + priority);
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "ComparedObject{" + "type='" + type + '\'' + ", priority=" + priority + '}';
    }

}
