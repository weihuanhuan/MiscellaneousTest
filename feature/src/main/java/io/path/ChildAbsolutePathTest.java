package io.path;

import java.io.File;

public class ChildAbsolutePathTest {

    public static void main(String[] args) {

        absolutePathTest();

    }

    public static void absolutePathTest() {

        //只要提供了 parent 路径，即使 child 使用绝对路径，其也会被作为 parent 的相对路径进行解析
        printAbsolutePath("c:/parent", "child/relative");

        printAbsolutePath("c:/parent", "/child/absolute");

        // child 使用绝对路径时，也可以正常处理 windows 路径格式
        printAbsolutePath("c:\\parent", "child\\relative");

        printAbsolutePath("c:\\parent", "\\child\\absolute");

        //可以混合使用 windows 路径格式 和 linux 路径
        printAbsolutePath("c:\\parent", "child/relative");

        printAbsolutePath("c:\\parent", "/child/absolute");

        //在 windows 下 / 被解析为 work dir drive 所在的盘符
        printAbsolutePath("/c/parent", "child/relative");

        printAbsolutePath("/c/parent", "/child/absolute");

    }

    /**
     * child 的路径解析，详见如下的 java doc
     * {@link java.io.File#File(java.lang.String, java.lang.String)}
     * {@link java.io.WinNTFileSystem#resolve(java.lang.String, java.lang.String)}
     */
    public static void printAbsolutePath(String parent, String child) {
        File file = new File(parent, child);
        String format = String.format("parent [%s], child [%s], java.io.File.getAbsolutePath=[%s]", parent, child, file.getAbsolutePath());
        System.out.println(format);
    }

}
