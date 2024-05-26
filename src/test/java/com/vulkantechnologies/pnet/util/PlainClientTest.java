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

package com.vulkantechnologies.pnet.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.*;

import com.vulkantechnologies.pnet.client.Client;
import com.vulkantechnologies.pnet.client.util.PlainClient;
import com.vulkantechnologies.pnet.event.PNetListener;
import com.vulkantechnologies.pnet.event.ReceiveListener;
import com.vulkantechnologies.pnet.packet.Packet;
import com.vulkantechnologies.pnet.packet.io.PacketWriter;
import com.vulkantechnologies.pnet.server.Server;
import com.vulkantechnologies.pnet.server.util.PlainServer;
import static org.junit.jupiter.api.Assertions.*;


public class PlainClientTest {
    protected static final int port = 42365;

    protected Server server;
    protected Client client;

    @BeforeEach
    public void setUp() throws Exception {
        server = new PlainServer();
        assertTrue(server.start(new InetSocketAddress("localhost", port)));
        client = new PlainClient();
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void connect() throws Exception {
        assertTrue(client.connect("localhost", port));
    }

    @Test
    public void nonConnectedSend() throws Exception {
        final Packet packet = new PacketWriter().build();
        assertFalse(client.send(packet));
    }

    @Test
    @Timeout(1000)
    public void send() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final Packet packet = new PacketWriter()
                .withString("hello send test")
                .build();

        server.setListener(new ReceiveListener() {
            @Override
            public void onReceive(final Packet p, final Client c) throws IOException {
                latch.countDown();
                assertArrayEquals(packet.getData(), p.getData());
            }
        });

        assertTrue(client.connect("localhost", port));
        assertTrue(client.send(packet));

        latch.await();
    }

    @Test
    public void clientType() throws Exception {
        client.setClientListener(new PNetListener() {
            @Override
            public void onConnect(final Client c) {
                assertInstanceOf(PlainClient.class, c);
            }

            @Override
            public void onDisconnect(final Client c) {

            }

            @Override
            public void onReceive(final Packet p, final Client c) throws IOException {

            }
        });

        assertTrue(client.connect("localhost", port));
    }
}