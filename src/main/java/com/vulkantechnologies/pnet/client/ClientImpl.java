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

package com.vulkantechnologies.pnet.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.vulkantechnologies.pnet.packet.Packet;
import com.vulkantechnologies.pnet.event.PNetListener;
import com.vulkantechnologies.pnet.factory.SocketFactory;
import com.vulkantechnologies.pnet.utils.Check;

import static com.vulkantechnologies.pnet.threading.ThreadManager.launchThread;
import lombok.Setter;

public class ClientImpl implements Client {

    private final UUID uniqueId;
    private final SocketFactory socketFactory;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    @Setter
    private PNetListener clientListener;

    /**
     * Creates a new Client
     */
    public ClientImpl(final SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
        this.uniqueId = UUID.randomUUID();
    }

    @Override
    public UUID uniqueId() {
        return this.uniqueId;
    }

    @Override
    public synchronized boolean connect(@NotNull String host, int port) {
        Check.stateCondition(socket != null && !socket.isClosed(), "Client not closed");
        Check.stateCondition(host.isEmpty() || port == -1, "Host and port are not set");

        try {
            setSocket(this.socketFactory.getSocket(host, port));
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public synchronized void setSocket(final Socket socket) throws IOException {
        Check.notNull(this.socket != null && !this.socket.isClosed(), "Client not closed");

        this.socket = socket;
        socket.setKeepAlive(false);
        dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        launchThread(this::listenerThreadImpl);

        if (clientListener != null)
            clientListener.onConnect(this);
    }

    private void listenerThreadImpl() {
        while (true) {
            final Packet packet;

            try {
                // Block while waiting for a Packet
                packet = Packet.fromStream(dataInputStream);
            } catch (final SocketException | EOFException e) {
                // Ignore : socket is closed
                close();
                break;
            } catch (final IOException e) {
                close();
                break;
            }


            // Fire event
            if (clientListener == null)
                continue;
            try {
                clientListener.onReceive(packet, this);
            } catch (final Exception ignored) {
            }
        }
    }

    @Override
    public synchronized boolean send(final Packet packet) {
        if (!isConnected()) return false;

        try {
            packet.write(dataOutputStream);
            dataOutputStream.flush();
            return true;
        } catch (final IOException e) {
            return false;
        }
    }

    @Override
    public synchronized void close() {
        if (socket == null || socket.isClosed()) return;

        try {
            socket.close();
        } catch (final IOException ignored) {
        }

        if (clientListener != null) clientListener.onDisconnect(this);
    }

    @Override
    public synchronized boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    @Override
    public synchronized InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    @Override
    public synchronized Socket getSocket() {
        return socket;
    }

    @Override
    public synchronized String toString() {
        return socket.toString();
    }
}
