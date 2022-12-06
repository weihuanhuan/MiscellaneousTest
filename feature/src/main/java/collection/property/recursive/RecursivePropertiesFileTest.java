package collection.property.recursive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by JasonFitch on 7/20/2020.
 */
public class RecursivePropertiesFileTest {

    public static void main(String[] args) throws IOException {
        String userDir = System.getProperty("user.dir");
        String subDir = "feature" + "/src/main/java";
        File propertiesFile = new File(userDir, subDir + "/" + "collection/property/recursive/recursive.properties");
        System.out.println(propertiesFile.getAbsolutePath());

        System.out.println("##################### recursivePropertyValueTest #####################");
        Properties recursiveProperties = recursivePropertyValueTest(propertiesFile);

        System.out.println("##################### executeConfigHelperTest #####################");
        executeConfigHelperTest(recursiveProperties);
    }

    private static RecursiveProperties recursivePropertyValueTest(File propertiesFile) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(propertiesFile);

        RecursiveProperties recursiveProperties = new RecursiveProperties();
        recursiveProperties.load(fileInputStream);

        Enumeration<?> enumeration = recursiveProperties.propertyNames();
        while (enumeration.hasMoreElements()) {
            Object key = enumeration.nextElement();

            if (key instanceof String) {
                String keyString = (String) key;

                Object value = recursiveProperties.getProperty(keyString);
                String format = String.format("[%s]=[%s]", keyString, value);
                System.out.println(format);
            } else {
                String format = String.format("key [%s] is not a string!", key);
                System.out.println(format);
            }
        }
        return recursiveProperties;
    }

    public static void executeConfigHelperTest(Properties recursiveProperties) {
        ExecuteConfigFactory executeConfigFactoryHelper = new ExecuteConfigFactory(recursiveProperties);

        List<String> commands = new ArrayList<>();
        commands.add("ping");
        commands.add("/bin/ping");
        commands.add("/bin/ping6");

        //TODO 在 string 形式的 command name 中存在空格时，会被 StringTokenizer.nextToken() 错误的将一个完整的命令名 `/b in/ping6` 拆分为了 `/b` 和 `in/ping6` 两个项，
        // 而 array 的处理是正确的，这主要时 nextToken 时，不会对字符串内部的带空格的字符串进行保留。
        // 这种情况主要发生在提供的路径包含空格的情况，比如 "path to executable arg1 arg2 arg3",这种情况，path 必须包在双引号里面传递。
        // 由于我们的 ExecuteConfig.isExecuteMatches(java.lang.String) 是对整个 command string 形式的匹配，所以我们直接将整个 string 形式的命名转化为 array 形式即可，
        // 这样子就要要求 string 形式的 command name ，中不能包含参数，他固定为视作一个命令路径整体来使用。
        commands.add("/b in/ping6");

        commands.add("echo");
        commands.add("/bin/echo");
        commands.add("/bin/echo6");

        //注意由于我们将 string 形式的 command name 整体是为命名名，因此 127.0.0.1 这个看起来像参数的部分，也作为了命令名的一部分。
        commands.add("\"/b in/echo6\" 127.0.0.1");

        commands.add("dummy");

        List<ExecuteConfig> executeConfigs = executeConfigFactoryHelper.toExecuteConfigs();
        for (ExecuteConfig executeConfig : executeConfigs) {
            System.out.println("-----------------------------------");
            System.out.println(executeConfig);

            List<String> collect = commands.stream()
                    .filter(executeConfig::isExecuteMatches)
                    .collect(Collectors.toList());
            String format = String.format("[%s] matches command list [%s]", executeConfig.getExecuteKey(), collect);
            System.out.println(format);

            for (String commandString : collect) {
                //string command
                if (executeConfig.isExecuteMatches(commandString)) {
                    String extendExecuteArgs = executeConfig.extendExecuteArgs(commandString);
                    String newCommandString = String.format("command [%s] extend args to new command string [%s]", commandString, extendExecuteArgs);
                    System.out.println(newCommandString);
                }

                //array command
                String[] commandArray = {commandString};
                if (executeConfig.isExecuteMatches(commandArray)) {
                    String[] extendExecuteArgsArray = executeConfig.extendExecuteArgs(commandArray);
                    String deepToString = Arrays.deepToString(extendExecuteArgsArray);
                    String newCommandArray = String.format("command [%s] extend args to new command array [%s]", commandString, deepToString);
                    System.out.println(newCommandArray);
                }
            }
        }
    }

}
