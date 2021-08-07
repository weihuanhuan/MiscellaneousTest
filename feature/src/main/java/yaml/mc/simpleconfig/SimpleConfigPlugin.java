package yaml.mc.simpleconfig;

import yaml.mc.bukkit.JavaPlugin;

import java.io.File;

public class SimpleConfigPlugin extends JavaPlugin {

    public SimpleConfigManager manager;

    public SimpleConfig config;
    public SimpleConfig messages;

    public SimpleConfigPlugin(String dataFolder) {
        super(dataFolder);
    }

    public void onDisable() {

    }

    public void onEnable() {

        String[] comments = {"Multiple lines", "Of nice comments", "Are supported !"};
        String[] header = {"This is super simple", "And highly customizable", "new and fresh SimpleConfig !"};

        this.manager = new SimpleConfigManager(this);

        this.config = manager.getNewConfig("config.yml", header);

        this.config.set("path1", "value1", comments);
        this.config.set("path2", "value2", "This is second comment !");
        this.config.saveConfig();


    }

    public static void main(String[] args) {

        File workDir = new File(System.getProperty("user.dir"));
        File testFile = new File(workDir, "feature/src/main/java/yaml");

        SimpleConfigPlugin simpleConfigPlugin = new SimpleConfigPlugin(testFile.getAbsolutePath());
        simpleConfigPlugin.onEnable();

    }

}
