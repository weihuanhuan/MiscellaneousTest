package yaml.snake;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        Yaml yaml = new Yaml();

        String property = System.getProperty("user.dir");
        File file = new File(property, "feature/src/main/java/yaml/config.yml");

        Object load = yaml.load(new FileInputStream(file));

        String parent = file.getParent();
        String name = file.getName();
        File newFile = new File(parent, name + "-snake");
        yaml.dump(load, new FileWriter(newFile));

    }

}
