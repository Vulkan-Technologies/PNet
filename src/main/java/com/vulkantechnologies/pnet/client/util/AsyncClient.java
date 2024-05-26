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

package com.vulkantechnologies.pnet.client.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

import com.vulkantechnologies.pnet.packet.Packet;
import com.vulkantechnologies.pnet.client.Client;
import com.vulkantechnologies.pnet.event.AsyncListener;

import static com.vulkantechnologies.pnet.threading.ThreadManager.launchThread;
import static com.vulkantechnologies.pnet.threading.ThreadManager.waitForCompletion;

public class AsyncClient extends ClientDecorator {

    private final LinkedBlockingDeque<AsyncPacket> asyncSenderQueue;
    private Future<?> asyncSenderFuture;

    /**
     * Adds asynchronous functionality to given Client implementation
     *
     * @param client Client implementation
     */
    public AsyncClient(final Client client) {
        super(client);

        asyncSenderQueue = new LinkedBlockingDeque<>();
    }

    /**
     * @param asyncListener Nullable completion listener. Contains boolean : true if successfully connected
     * @see Client#connect(String, int)
     */
    public synchronized void connectAsync(final String host, final int port, final AsyncListener asyncListener) {
        if (client.isConnected() && asyncListener != null) {
            asyncListener.onCompletion(false);
            return;
        }

        launchThread(() -> {
            final boolean result = client.connect(host, port);
            if (asyncListener != null) asyncListener.onCompletion(result);
        });
    }

    /**
     * Blocks until all packets are sent asynchronously
     *
     * @see Future#get()
     */
    public synchronized void waitForAsyncCompletion() throws InterruptedException, ExecutionException {
        if (asyncSenderFuture != null)
            waitForCompletion(asyncSenderFuture);
    }

    /**
     * Calls {@link AsyncClient#sendAsync(Packet, AsyncListener, boolean) sendAsync(Packet, AsyncListener, false)}
     */
    public synchronized void sendAsync(final Packet packet, final AsyncListener asyncListener) {
        sendAsync(packet, asyncListener, false);
    }

    /**
     * @param asyncListener Nullable completion listener. Contains boolean : true if successfully sent
     * @param topPriority   Whether to add this Packet at the head of the queue
     * @see Client#send(Packet)
     */
    public synchronized void sendAsync(final Packet packet, final AsyncListener asyncListener, final boolean topPriority) {
        if (topPriority) {
            asyncSenderQueue.addFirst(new AsyncPacket(packet, asyncListener));
        } else {
            asyncSenderQueue.addLast(new AsyncPacket(packet, asyncListener));
        }

        // Start thread if needed
        if (asyncSenderFuture == null || asyncSenderFuture.isDone()) {
            asyncSenderFuture = launchThread(this::asyncSenderThreadImpl);
        }
    }

    private void asyncSenderThreadImpl() {
        while (!asyncSenderQueue.isEmpty()) {
            try {
                final AsyncPacket asyncPacket = asyncSenderQueue.takeFirst();
                asyncPacket.onComplete(client.send(asyncPacket.getPacket()));
            } catch (final InterruptedException e) {
                asyncSenderQueue.clear();
                break;
            }
        }
    }

    @Override
    public synchronized void close() {
        client.close();
        if (asyncSenderFuture != null) asyncSenderFuture.cancel(true);
    }

    private static class AsyncPacket {
        private final Packet packet;
        private final AsyncListener asyncListener;

        public AsyncPacket(final Packet packet, final AsyncListener asyncListener) {
            this.packet = packet;
            this.asyncListener = asyncListener;
        }

        public void onComplete(final boolean result) {
            if (asyncListener != null)
                asyncListener.onCompletion(result);
        }

        public Packet getPacket() {
            return packet;
        }
    }
}
