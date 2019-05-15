package listener.simple;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by JasonFitch on 5/14/2019.
 */
public class Main {

    public static void main(String[] args) {

        SelectorThread selectorThread = new SelectorThread();
        selectorThread.init();

        Pipeline pipeline = selectorThread.getPipeline();

        pipeline.addThread();
        pipeline.removeThread();

        ConcurrentLinkedQueue<ProcessorTask> processorTasks = selectorThread.getProcessorTasks();
        int size = processorTasks.size();
        System.out.println(size);

    }
}
