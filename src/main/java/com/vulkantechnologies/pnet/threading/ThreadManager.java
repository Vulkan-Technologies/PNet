/*
 * MIT License
 *
 * Copyright (c) 2017 Pim van den Berg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vulkantechnologies.pnet.threading;

import java.util.concurrent.*;

public class ThreadManager {
    private static final ExecutorService service = Executors.newCachedThreadPool(new ErrataThreadFactory());

    /**
     * Launches a new thread
     *
     * @param runnable Runnable to run in a thread
     * @return Future
     */
    public static Future<?> launchThread(final Runnable runnable) {
        return service.submit(runnable);
    }

    /**
     * Waits for a thread to finish
     *
     * @param future Future to wait for
     * @see Future#get()
     */
    public static void waitForCompletion(final Future<?> future) throws ExecutionException, InterruptedException {
        future.get();
    }

    /**
     * Stops all running threads
     */
    public static void shutdown() {
        service.shutdownNow();
    }
}