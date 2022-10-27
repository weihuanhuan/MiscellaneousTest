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

import com.lmax.disruptor.EventFactory;

public class RingBufferLogEvent {

    public static final Factory FACTORY = new Factory();

    private char[] message;
    private transient AsyncWriter asyncWriter;

    private boolean endOfBatch = false;

    public RingBufferLogEvent() {
    }


    public void setValues(final AsyncWriter asyncWriter, final char[] msg) {
        this.asyncWriter = asyncWriter;
        this.message = msg;
    }

    /**
     * Event processor that reads the event from the ringbuffer can call this method.
     *
     * @param endOfBatch flag to indicate if this is the last event in a batch from the RingBuffer
     */
    public void execute(final boolean endOfBatch) {
        this.endOfBatch = endOfBatch;
        asyncWriter.actualWriterMessage(this);
    }


    public char[] getMessage() {
        return message;
    }

    public void clear() {
        this.asyncWriter = null;
        this.message = null;
    }

    private static class Factory implements EventFactory<RingBufferLogEvent> {

        @Override
        public RingBufferLogEvent newInstance() {
            final RingBufferLogEvent result = new RingBufferLogEvent();
            return result;
        }
    }

}
