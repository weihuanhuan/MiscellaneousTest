package SystemTest.process.helper;

import java.util.List;

public class ReadLastLineHelper {

    //使用 "\n" 能同时处理 "\r\n" 和 "\n" 俩种情况
    public static final String LINE_SEPARATOR = "\n";

    public static String joinLinesAsString(List<String> inputList) {
        if (inputList == null) {
            return null;
        }

        return String.join(LINE_SEPARATOR, inputList);
    }

}
