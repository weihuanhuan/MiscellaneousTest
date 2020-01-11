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
package disruptor.async;

import com.lmax.disruptor.ExceptionHandler;

public abstract class AbstractAsyncExceptionHandler<T> implements ExceptionHandler<T> {

    @Override
    public void handleEventException(final Throwable throwable, final long sequence, final T event) {
        throwable.printStackTrace();
    }

    @Override
    public void handleOnStartException(final Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void handleOnShutdownException(final Throwable throwable) {
        throwable.printStackTrace();
    }
}
