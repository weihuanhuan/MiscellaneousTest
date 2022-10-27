/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package concurrency.disruptor.async;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import concurrency.disruptor.util.WorkThreadFactory;
import concurrency.disruptor.util.DisruptorUtil;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for async loggers: AsyncWriterDisruptor handles the mechanics of working with the LMAX Disruptor, and
 * works with its associated AsyncLoggerContext to synchronize the life cycle of the Disruptor and its thread with the
 * life cycle of the context. The AsyncWriterDisruptor of the context is shared by all AsyncWriter objects created by
 * that AsyncLoggerContext.
 */
public class AsyncWriterDisruptor {
    private static final int SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS = 50;
    private static final int MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN = 200;

    private volatile Disruptor<RingBufferLogEvent> disruptor;
    private int ringBufferSize;

    public AsyncWriterDisruptor() {
        start();
    }


    public Disruptor<RingBufferLogEvent> getDisruptor() {
        return disruptor;
    }

    public synchronized void start() {
        if (disruptor != null) {
            return;
        }
        ringBufferSize = DisruptorUtil.calculateRingBufferSize("AsyncWriter.RingBufferSize");
        final WaitStrategy waitStrategy = DisruptorUtil.createWaitStrategy("AsyncWriter.WaitStrategy");

        final ThreadFactory threadFactory = new WorkThreadFactory("AsyncWriterDisruptor", true, Thread.NORM_PRIORITY);

        disruptor = new Disruptor<>(RingBufferLogEvent.FACTORY, ringBufferSize, threadFactory, ProducerType.MULTI,
                waitStrategy);

        final ExceptionHandler<RingBufferLogEvent> errorHandler = DisruptorUtil.getAsyncLoggerExceptionHandler();
        disruptor.setDefaultExceptionHandler(errorHandler);

        final RingBufferLogEventHandler[] handlers = {new RingBufferLogEventHandler()};
        disruptor.handleEventsWith(handlers);

        disruptor.start();
    }

    /**
     * Decreases the reference count. If the reference count reached zero, the Disruptor and its associated thread are
     * shut down and their references set to {@code null}.
     */
//    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        final Disruptor<RingBufferLogEvent> temp = getDisruptor();
        if (temp == null) {
            return true; // disruptor was already shut down by another thread
        }

        // We must guarantee that publishing to the RingBuffer has stopped before we call disruptor.shutdown().
        disruptor = null; // client code fails with NPE if log after stop. This is by design.

        // Calling Disruptor.shutdown() will wait until all enqueued events are fully processed,
        // but this waiting happens in a busy-spin. To avoid (postpone) wasting CPU,
        // we sleep in short chunks, up to 10 seconds, waiting for the ringbuffer to drain.
        for (int i = 0; hasBacklog(temp) && i < MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN; i++) {
            try {
                Thread.sleep(SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS); // give up the CPU for a while
            } catch (final InterruptedException e) { // ignored
            }
        }
        try {
            // busy-spins until all events currently in the disruptor have been processed, or timeout
            temp.shutdown(timeout, timeUnit);
        } catch (final TimeoutException e) {
            temp.halt(); // give up on remaining log events, if any
        }


        return true;
    }

    /**
     * Returns {@code true} if the specified disruptor still has unprocessed events.
     */
    private static boolean hasBacklog(final Disruptor<?> theDisruptor) {
        final RingBuffer<?> ringBuffer = theDisruptor.getRingBuffer();
        return !ringBuffer.hasAvailableCapacity(ringBuffer.getBufferSize());
    }


    public boolean tryPublish(final RingBufferLogEventTranslator translator) {
        try {
            return disruptor.getRingBuffer().tryPublishEvent(translator);
        } catch (final NullPointerException npe) {
            // LOG4J2-639: catch NPE if disruptor field was set to null in stop()
            return false;
        }
    }

    public void enqueueLogMessageInfo(final RingBufferLogEventTranslator translator) {
        try {
            // Note: we deliberately access the volatile disruptor field afresh here.
            // Avoiding this and using an older reference could result in adding a log event to the disruptor after it
            // was shut down, which could cause the publishEvent method to hang and never return.
            disruptor.publishEvent(translator);
        } catch (final NullPointerException npe) {
            // LOG4J2-639: catch NPE if disruptor field was set to null in stop()
        }
    }

}
