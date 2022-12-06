package collection.property.recursive;

import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ExecuteConfig {

    private static final String COMMAND_SEPARATOR = " ";

    private final String executeKey;

    private final String binPattern;
    private final String argsPrefix;
    private final String argsSuffix;

    private final Pattern pattern;

    public ExecuteConfig(String key, String binPattern, String argsPrefix, String argsSuffix) {
        this.executeKey = key;

        this.binPattern = binPattern;
        this.argsPrefix = argsPrefix;
        this.argsSuffix = argsSuffix;

        if (binPattern == null) {
            this.pattern = null;
        } else {
            this.pattern = Pattern.compile(binPattern);
        }
    }

    boolean isExecuteMatches(String[] commands) {
        if (commands == null || commands.length == 0) {
            return false;
        }

        return isExecuteMatches(commands[0]);
    }

    boolean isExecuteMatches(String commandName) {
        if (pattern == null) {
            return false;
        }

        if (commandName == null || commandName.isEmpty()) {
            return false;
        }

        Matcher matcher = pattern.matcher(commandName);
        return matcher.matches();
    }

    public String extendExecuteArgs(String commandName) {
        if (commandName == null || commandName.isEmpty()) {
            return commandName;
        }

        String[] cmdArray = new String[]{commandName};
        String[] extendCmdArray = extendExecuteArgs(cmdArray);

        String join = String.join(COMMAND_SEPARATOR, extendCmdArray);
        return join;
    }

    public String[] extendExecuteArgs(String[] commandArray) {
        if (commandArray == null || commandArray.length == 0) {
            return commandArray;
        }

        String[] prefixArray = stringToArray(getArgsPrefix());
        String[] suffixArray = stringToArray(getArgsSuffix());

        //first element is original command executable file name
        Stream<String> limit = Arrays.stream(commandArray).limit(1);
        Stream<String> concatPrefix = Stream.concat(limit, Arrays.stream(prefixArray));

        //remaining elements of this array after discarding the first element is original command args array.
        Stream<String> skip = Arrays.stream(commandArray).skip(1);
        Stream<String> concatCommand = Stream.concat(concatPrefix, skip);

        Stream<String> concatSuffix = Stream.concat(concatCommand, Arrays.stream(suffixArray));

        String[] concatCommandArray = concatSuffix.toArray(String[]::new);
        return concatCommandArray;
    }

    private String[] stringToArray(String str) {
        if (str == null || str.isEmpty()) {
            return new String[0];
        }

        StringTokenizer st = new StringTokenizer(str);
        String[] array = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++) {
            array[i] = st.nextToken();
        }
        return array;
    }

    public String getExecuteKey() {
        return executeKey;
    }

    public String getBinPattern() {
        return binPattern;
    }

    public String getArgsPrefix() {
        return argsPrefix;
    }

    public String getArgsSuffix() {
        return argsSuffix;
    }

    @Override
    public String toString() {
        return "ExecuteConfig{" + "executeKey='" + executeKey + '\'' + ", binPattern='" + binPattern + '\'' + ", argsPrefix='" + argsPrefix + '\'' + ", argsSuffix='" + argsSuffix + '\'' + '}';
    }

}
