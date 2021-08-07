package yaml.mc.commentedyaml;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;

/**
 * A yaml file with comments on certain properties, as returned by the given {@link CommentsProvider}.
 * Unlike {@link YamlConfiguration}, this class does not provide a header support.
 */
public class CommentedYamlConfiguration extends YamlConfiguration {

    private final DumperOptions yamlOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter();
    private final CommentedYaml yaml;

    public CommentedYamlConfiguration(CommentsProvider commentsProvider) {
        this.yaml = new CommentedYaml(new YamlConstructor(), this.yamlRepresenter, this.yamlOptions, commentsProvider);
    }

    @Override
    protected String parseHeader(String input) {
        // No header support.
        return "";
    }

    @Override
    public String saveToString() {
        this.yamlOptions.setIndent(this.options().indent());
        this.yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        String dump = this.yaml.dump(this.getValues(false));
        if (dump.equals("{}\n")) {
            dump = "";
        }

        // No header support.

        return dump;
    }

    public static CommentedYamlConfiguration loadConfiguration(CommentsProvider commentsProvider, File file) {
        Validate.notNull(file, "File cannot be null");
        CommentedYamlConfiguration config = new CommentedYamlConfiguration(commentsProvider);

        try {
            config.load(file);
        } catch (FileNotFoundException var3) {
        } catch (IOException | InvalidConfigurationException var4) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var4);
        }

        return config;
    }

    public static CommentedYamlConfiguration loadConfiguration(CommentsProvider commentsProvider, Reader reader) {
        Validate.notNull(reader, "Stream cannot be null");
        CommentedYamlConfiguration config = new CommentedYamlConfiguration(commentsProvider);

        try {
            config.load(reader);
        } catch (IOException | InvalidConfigurationException var3) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", var3);
        }

        return config;
    }
}
