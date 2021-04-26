package FileTest.watch;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWatcher implements Runnable {

    protected File folder;
    protected List<FileListener> listeners = new ArrayList<>();

    protected Thread thread;

    protected static final List<WatchService> watchServices = new ArrayList<>();

    public FileWatcher(File folder) {
        this.folder = folder;
    }

    public void start() {
        thread = new Thread(this, "file-watcher");
        thread.setDaemon(true);
        thread.start();
    }

    public void close() {
        for (WatchService watchService : FileWatcher.getWatchServices()) {
            try {
                //虽然 run 函数中的 try-resource 语句已经在 run 结束时 close 了，但是这个方法可以重复的调用。
                watchService.close();
                System.out.println("Success to stop a watch service!");
            } catch (IOException e) {
                System.out.println("Failed to stop a watch service!");
                e.printStackTrace();
            }
        }
    }

    /**
     * 主要我们要中断的是当前正在执行 WatchService 的线程, 而这个线程是我们自己内部创建的。
     * 我们应该通过这个内部创建的 FileWatcher#thread 句柄来停止当前的 WatchService 任务.
     * <p>
     * 而当我们使用了下面的语句时，如果调用线程来自其他的线程则会引起其他线程的中断，
     * 而不是执行 WatchService 服务的线程，这就会导致一个错误的线程中断。
     * Thread.currentThread().interrupt();
     */
    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path = Paths.get(folder.getAbsolutePath());
            path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            watchServices.add(watchService);

            boolean poll = true;
            while (!Thread.currentThread().isInterrupted() && poll) {
                poll = pollEvents(watchService);
            }
        } catch (IOException | InterruptedException | ClosedWatchServiceException e) {
            //恢复中断信息，供稍后检测线程的状态。
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                return;
            }
            e.printStackTrace();
        }
    }

    protected boolean pollEvents(WatchService watchService) throws InterruptedException {
        WatchKey key = watchService.take();
        Path path = (Path) key.watchable();
        for (WatchEvent<?> event : key.pollEvents()) {
            notifyListeners(event.kind(), path.resolve((Path) event.context()).toFile());
        }
        return key.reset();
    }

    protected void notifyListeners(WatchEvent.Kind<?> kind, File file) {
        FileEvent event = new FileEvent(file);
        if (kind == ENTRY_CREATE) {
            for (FileListener listener : listeners) {
                listener.onCreated(event);
            }
            if (file.isDirectory()) {
                // create a new FileWatcher instance to watch the new directory
                new FileWatcher(file).setListeners(listeners).start();
            }
        } else if (kind == ENTRY_MODIFY) {
            for (FileListener listener : listeners) {
                listener.onModified(event);
            }
        } else if (kind == ENTRY_DELETE) {
            for (FileListener listener : listeners) {
                listener.onDeleted(event);
            }
        }
    }

    public FileWatcher addListener(FileListener listener) {
        listeners.add(listener);
        return this;
    }

    public FileWatcher removeListener(FileListener listener) {
        listeners.remove(listener);
        return this;
    }

    public FileWatcher setListeners(List<FileListener> listeners) {
        this.listeners = listeners;
        return this;
    }

    public List<FileListener> getListeners() {
        return listeners;
    }

    /**
     * 这里返回了所有的 FileWatcher 类的示例，对其执行关闭会关闭所有的 WatchService.
     * 目前实现有问题，要注意在所有的 FileWatcher 的线程之后，才能调用这个函数的返回值来 close。
     * 否则在 java.nio.file.WatchService#take() 中会抛出 ClosedWatchServiceException
     */
    public static List<WatchService> getWatchServices() {
        return Collections.unmodifiableList(watchServices);
    }
}