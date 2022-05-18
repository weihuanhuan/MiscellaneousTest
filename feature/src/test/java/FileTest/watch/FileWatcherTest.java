package FileTest.watch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileWatcherTest {

    @Test
    public void test() throws IOException, InterruptedException {
        final Map<String, String> map = new HashMap<>();

        //测试时需要目录存在才能生成相应的测试文件
        //git 默认不控制空目录，所以使用 .gitkeep 文件 来保持空目录的版本控制
        File folder = new File("src/test/resources");
        FileWatcher watcher = new FileWatcher(folder);

        watcher.addListener(new FileAdapter() {
            public void onCreated(FileEvent event) {
                map.put("file.created", event.getFile().getName());
                System.out.println(String.format("%s:%s", "file.created", event.getFile().getName()));
            }

            public void onModified(FileEvent event) {
                map.put("file.modified", event.getFile().getName());
                System.out.println(String.format("%s:%s", "file.modified", event.getFile().getName()));
            }

            public void onDeleted(FileEvent event) {
                map.put("file.deleted", event.getFile().getName());
                System.out.println(String.format("%s:%s", "file.deleted", event.getFile().getName()));
            }
        });

        wait(1000);
        watcher.start();

        wait(1000);
        assertEquals(1, watcher.getListeners().size());

        wait(1000);
        File absoluteFile = folder.getAbsoluteFile();
        File file = new File(absoluteFile, "test-file-watcher.log");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Some String");
        }

        wait(1000);
        file.delete();

        wait(1000);
        assertEquals(file.getName(), map.get("file.created"));
        assertEquals(file.getName(), map.get("file.modified"));
        assertEquals(file.getName(), map.get("file.deleted"));

        wait(1000);
        watcher.stop();

        wait(1000);
        watcher.close();
    }

    public void wait(int time) throws InterruptedException {
        Thread.sleep(time);
    }
}
