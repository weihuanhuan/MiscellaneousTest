package yaml.mc.worldedit;


import yaml.mc.bukkit.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldEditJavaPlugin extends JavaPlugin {

    public WorldEditJavaPlugin(String dataFolder) {
        super(dataFolder);
    }

    public static void main(String[] args) {
        File workDir = new File(System.getProperty("user.dir"));
        File testFile = new File(workDir, "feature/src/main/java/yaml");

        WorldEditJavaPlugin worldEditJavaPlugin = new WorldEditJavaPlugin(testFile.getAbsolutePath());

        YAMLProcessor configuration = worldEditJavaPlugin.createConfiguration();

        configuration.setProperty("test-name","test-value");
        configuration.setComment("test-name","test-comment");

        boolean save = configuration.save();

    }

    public YAMLProcessor createConfiguration() {
        final File configFile = new File(getDataFolder(), "config.yml");
        YAMLProcessor config = new YAMLProcessor(configFile, true, YAMLFormat.EXTENDED);
        YAMLProcessor comments = new DefaultsFileYAMLProcessor(getDataFolder()+"/config.yml", false);
        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            }
            config.load();
            comments.load();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error loading configuration: ", e);
        }

        for (Map.Entry<String, Object> e : comments.getMap().entrySet()) {
            if (e.getValue() != null) {
                config.setComment(e.getKey(), e.getValue().toString());
            }
        }

//        // Migrate the old configuration, if we need to
//        final String result = new LegacyCommandBookConfigurationMigrator(configFile, config).migrate();
//        if (result != null) {
//            logger().severe("Error migrating CommandBook configuration: " + result);
//        }

        return config;
    }


    public Logger getLogger() {
        return Logger.getLogger(this.getClass().getName());
    }

    public Logger logger() {
        return Logger.getLogger(this.getClass().getName());
    }

}
