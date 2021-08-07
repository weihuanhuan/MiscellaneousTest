package yaml.mc.comment;

import yaml.mc.bukkit.JavaPlugin;
import yaml.mc.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main extends CommentConfiguration {

    private String fileName;
    private JavaPlugin plugin;
    private File file;

    public Main(JavaPlugin jp, String name) {
        this.plugin = jp;
        this.fileName = name.endsWith(".yml") ? name : name + ".yml";

        loadFile();
        createData();

        try {
            loadConfig();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() throws FileNotFoundException, IOException, InvalidConfigurationException {
        this.load(file);
    }

    public File loadFile() {
        this.file = new File(this.plugin.getDataFolder(), this.fileName);
        return this.file;
    }

    public void saveData() {
        this.file = new File(this.plugin.getDataFolder(), this.fileName + "-comment");
        try {
            this.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Attempting to fix error...");
            createData();
            saveData();
        }
    }

    @Override
    public void save(File file) throws IOException {
        super.save(file);
    }

    public void createData() {
        if (!file.exists()) {
            if (!this.plugin.getDataFolder().exists()) {
                this.plugin.getDataFolder().mkdirs();
            }

            // If file isn't a resource, create from scratch
            if (this.plugin.getResource(this.fileName) == null) {
                try {
                    this.file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                this.plugin.saveResource(this.fileName, false);
            }
        }
    }

    public void delete() {
        if (this.file.exists()) {
            this.file.delete();
        }
    }

    public static void main(String[] args) {
        File workDir = new File(System.getProperty("user.dir"));
        File testFile = new File(workDir, "feature/src/main/java/yaml");
        CommentPlugin commentPlugin = new CommentPlugin(testFile.getAbsolutePath());

        Main cfg = new Main(commentPlugin, "config.yml"); // You'd reference your JavaPlugin here, ofc

        System.out.println(cfg.getInt("Test.path.integer"));

        cfg.set("Test.path.integer", 99);
        cfg.saveData();
    }

}