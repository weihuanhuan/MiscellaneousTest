package spring.loadclass;

import java.util.ArrayList;

public class JDK8Outer {

    protected final java.util.List<Object> objects;

    private class JDK8List {
        private java.util.List<String> params;

        void run() throws Exception {
        }
    }

    public JDK8Outer() {
        objects = new ArrayList<>();
        objects.add(new JDK8List());
    }

}