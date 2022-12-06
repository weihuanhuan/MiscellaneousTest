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

        commands.add("echo");
        commands.add("/bin/echo");
        commands.add("/bin/echo6");

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
                String extendExecuteArgs = executeConfig.extendExecuteArgs(commandString);
                String newCommandString = String.format("command [%s] extend args to new command string [%s]", commandString, extendExecuteArgs);
                System.out.println(newCommandString);

                //array command
                String[] extendExecuteArgsArray = executeConfig.extendExecuteArgs(new String[]{commandString});
                String deepToString = Arrays.deepToString(extendExecuteArgsArray);
                String newCommandArray = String.format("command [%s] extend args to new command array [%s]", commandString, deepToString);
                System.out.println(newCommandArray);
            }
        }
    }

}
