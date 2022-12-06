package collection.property.recursive;

import java.util.Properties;

/**
 * reference https://github.com/MideO/CucumberJVM/blob/master/src/tests/cukes/stepdefinitions/config/XProperties.java
 */
public class RecursiveProperties extends Properties {

    // The prefix and suffix for constant names
    // within property values
    //我们的 START_CONST 修改为 `${`
    private static final String START_CONST = "${";
    private static final String END_CONST = "}";

    // The maximum depth for recursive substitution
    // of constants within property values
    // (e.g., A={B} .. B={C} .. C={D} .. etc.)
    private static final int MAX_SUBST_DEPTH = 5;

    /**
     * Creates an empty property list with no default
     * values.
     */
    public RecursiveProperties() {
        super();
    }

    /**
     * Searches for the property with the specified
     * key in this property list. If the key is not
     * found in this property list, the default
     * property list, and its defaults, recursively,
     * are then checked. The method returns
     * <code>null</code> if the property is not found.
     *
     * @param key the property key.
     * @return the value in this property list with
     * the specified key value.
     */
    public String getProperty(String key) {
        // Return the property value starting at level 0
        return getProperty(key, 0);
    }


    /**
     * Searches for the property with the specified
     * key in this property list. If the key is not
     * found in this property list, the default
     * property list, and its defaults, recursively,
     * are then checked. The method returns
     * <code>null</code> if the property is not found.
     *
     * <p>The level parameter specifies the current
     * level of recursive constant substitution. If
     * the requested property value includes a
     * constant, its value is substituted in place
     * through a recursive call to this method,
     * incrementing the level. Once level exceeds
     * MAX_SUBST_DEPTH, no further constant
     * substitutions are performed within the
     * current requested value.
     *
     * @param key   the property key.
     * @param level the level of recursion so far
     * @return the value in this property list with
     * the specified key value.
     */
    private String getProperty(String key, int level) {
        String value = super.getProperty(key);

        if (value != null) {
            // Get the index of the first constant, if any
            int beginIndex = 0;
            int startName = value.indexOf(START_CONST, beginIndex);

            while (startName != -1) {
                if (level + 1 > MAX_SUBST_DEPTH) {
                    // Exceeded MAX_SUBST_DEPTH
                    // Return the value as is
                    return value;
                }

                //我们的 START_CONST 长度是已知的，可以查找时忽略他。
                int endName = value.indexOf(END_CONST, startName + START_CONST.length());
                if (endName == -1) {
                    // Terminating symbol not found
                    // Return the value as is
                    return value;
                }

                //我们的 constName 只需要 ${} 中的内部值
                String constName = value.substring(startName + START_CONST.length(), endName);
                String constValue = getProperty(constName, level + 1);

                if (constValue == null) {
                    // Property name not found
                    // Return the value as is
                    return value;
                }

                // Insert the constant value into the
                // original property value
                //这里要截取原先的 prefix ， newValue = prefix + variable + suffix
                //修改后由于 START_CONST = "${"; ，此时 prefix 就不包含 $ 了
                String newValue = startName > 0 ? value.substring(0, startName) : "";
                newValue += constValue;

                // Start checking for constants at this index
                beginIndex = newValue.length();

                // Append the remainder of the value
                //我们的 END_CONST 长度是已知的，可以截取时忽略他。
                newValue += value.substring(endName + END_CONST.length());

                value = newValue;

                // Look for the next constant
                //这里相当于使用解析完了一个属性的新 value 来递归的进行再次解析，所以开始位置就为新 value ，解析了 ${} 之后的开始位置
                startName = value.indexOf(START_CONST, beginIndex);
            }
        }

        // Return the value as is
        return value;
    }

}
