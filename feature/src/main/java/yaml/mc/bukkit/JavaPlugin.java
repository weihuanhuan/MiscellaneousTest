package yaml.mc.bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class JavaPlugin {

    String dataFolder;

    public JavaPlugin(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    public File getDataFolder() {
        return new File(dataFolder);
    }

    public InputStream getResource(String fileName) {
        File file = new File(getDataFolder(), fileName);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveResource(String fileName, boolean b) {

    }

    public PropertyInfo getDescription() {
        return new PropertyInfo("test-plugin");
    }

    public static class PropertyInfo {

        String name;

        public PropertyInfo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
