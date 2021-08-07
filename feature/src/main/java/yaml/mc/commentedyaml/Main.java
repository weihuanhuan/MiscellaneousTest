package yaml.mc.commentedyaml;

import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InvalidConfigurationException {

        CommentsProvider commentsProvider = new CommentsProvider() {

            @Override
            public String[] apply(String s) {
                if (!s.contains("test")) {
                    return null;
                }
                return new String[]{"test-comment1", "test-comment2"};
            }
        };
        CommentedYamlConfiguration commentedYamlConfiguration = new CommentedYamlConfiguration(commentsProvider);

        File workDir = new File(System.getProperty("user.dir"));
        File testFile = new File(workDir, "feature/src/main/java/yaml");
        commentedYamlConfiguration.load(new File(testFile, "config.yml"));

        commentedYamlConfiguration.set("test-key", "test-value");

        commentedYamlConfiguration.save(new File(testFile, "config.yml-commented"));

    }

}
