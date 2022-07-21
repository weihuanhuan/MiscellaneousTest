package SystemTest.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcessExecutorHelper {

    //使用 "\n" 能同时处理 "\r\n" 和 "\n" 俩种情况
    private static final String LINE_SEPARATOR = "\n";

    public static void main(String[] args) {
        int numLines = 5;

        printLastLinesAsString(null, numLines);

        printLastLinesAsString("", numLines);

        printLastLinesAsString("aaa\nbbb\nccc", numLines);
        printLastLinesAsString("aaa\nbbb\nccc\n", numLines);
        printLastLinesAsString("aaa\nbbb\nccc\nddd", numLines);
        printLastLinesAsString("aaa\nbbb\nccc\nddd\n", numLines);
        printLastLinesAsString("aaa\nbbb\nccc\nddd\neee", numLines);
        printLastLinesAsString("aaa\nbbb\nccc\nddd\neee\n", numLines);
        printLastLinesAsString("aaa\nbbb\nccc\nddd\neee\nfff", numLines);
        printLastLinesAsString("aaa\nbbb\nccc\nddd\neee\nfff\n", numLines);

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        printLastLinesAsString("aaa\r\nbbb\r\nccc", numLines);
        printLastLinesAsString("aaa\r\nbbb\r\nccc\r\n", numLines);
        printLastLinesAsString("aaa\r\nbbb\r\nccc\r\nddd", numLines);
        printLastLinesAsString("aaa\r\nbbb\r\nccc\r\nddd\r\n", numLines);
        printLastLinesAsString("aaa\r\nbbb\r\nccc\r\nddd\r\neee", numLines);
        printLastLinesAsString("aaa\r\nbbb\r\nccc\r\nddd\r\neee\r\n", numLines);
        printLastLinesAsString("aaa\r\nbbb\r\nccc\r\nddd\r\neee\r\nfff", numLines);
        printLastLinesAsString("aaa\r\nbbb\r\nccc\r\nddd\r\neee\r\nfff\r\n", numLines);
    }

    public static void printLastLinesAsString(String input, int numLines) {
        System.out.println("################################################");
        String lastLinesAsString = getLastLinesAsString(input, numLines);
        System.out.println(input);
        System.out.println("------------------------------------------------");
        System.out.println(lastLinesAsString);
    }

    public static String getLastLinesAsString(String string, int numLines) {
        List<String> lastLines = getLastLines(string, numLines);
        if (lastLines == null) {
            return null;
        }

        return String.join(LINE_SEPARATOR, lastLines);
    }

    public static List<String> getLastLines(String string, int numLines) {
        if (string == null) {
            return null;
        }

        int currentStringLength = string.length();
        int lineSeparatorLength = LINE_SEPARATOR.length();

        List<String> lines = new ArrayList<>();

        //处理行末存在换行符的情况，这里我们暂时只考虑 java.lang.System.lineSeparator 换行符
        for (int i = 0; currentStringLength >= 0 && i < numLines; ++i) {
            // currentStringLength = index + 1 所以，其值是不在真实 string 可索引位置的，我们可以减去他。
            int lineStartIndex = string.lastIndexOf(LINE_SEPARATOR, currentStringLength - 1);

            //行数不够需要的行，应该将第一行也加入到其中
            if (lineStartIndex < 0) {
                lines.add(string.substring(0, currentStringLength));
                break;
            }

            //倒序增加, 增加时移除行分隔符，
            //由于对 LINE_SEPARATOR 序列查找成功的 index 值是该序列的开始 index
            //所以这里可以安全的增加一个 LINE_SEPARATOR 的长度，从而正好可以忽略 LINE_SEPARATOR 的值
            String lastLine = string.substring(lineStartIndex + lineSeparatorLength, currentStringLength);
            lines.add(lastLine);

            //下一轮查询，相当于在原先的 string 中缩短了一行后继续进行查询，而且在次查询时，也应该跳过行分隔符。
            currentStringLength = currentStringLength - lastLine.length() - lineSeparatorLength;
        }

        //恢复为正序
        Collections.reverse(lines);
        return lines;
    }

}


