package listener.simple;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by JasonFitch on 5/14/2019.
 */
public class SelectorThread {

    private ConcurrentLinkedQueue<ProcessorTask> processorTasks = new ConcurrentLinkedQueue<>();

    private Pipeline pipeline;

    public SelectorThread() {
    }

    public void init() {

        pipeline = new Pipeline();

        Listener listener = new Listener(this);
        pipeline.setListener(listener);

    }

    public void addTask() {
        System.out.println("addTask");
        processorTasks.offer(new ProcessorTask());
    }

    public void removeTask() {
        System.out.println("removeTask");
        processorTasks.poll();
    }

    public ConcurrentLinkedQueue<ProcessorTask> getProcessorTasks() {
        return processorTasks;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }
}
