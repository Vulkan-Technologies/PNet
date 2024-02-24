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

package nl.pvdberg.pnet;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.pvdberg.pnet.client.Client;
import nl.pvdberg.pnet.client.util.PlainClient;
import nl.pvdberg.pnet.event.ReceiveListener;
import nl.pvdberg.pnet.packet.Packet;
import nl.pvdberg.pnet.packet.PacketBuilder;
import nl.pvdberg.pnet.server.Server;
import nl.pvdberg.pnet.server.util.PlainServer;


public class BenchmarkTest {
    protected static final int port = 42365;
    protected static final float invMega = 1 / 1000000f;
    protected static final float invNano = 1 / 1000000000f;

    protected static Server server;
    protected static Client client;
    protected long start;
    protected long end;

    @BeforeAll
    public static void setup() throws Exception {
        server = new PlainServer();
        server.start(port);

        client = new PlainClient();
        client.connect("localhost", port);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testEmptyPacketsPerSecond() throws Exception {
        final int amount = 1000;

        final Packet packet = new PacketBuilder((short) 1).build();

        server.setListener(new ReceiveListener() {
            @Override
            public void onReceive(final Packet p, final Client c) throws IOException {
                Assertions.assertEquals(1, p.getPacketID());
            }
        });

        start = System.nanoTime();
        for (int i = 0; i < amount; i++) {
            Assertions.assertTrue(client.send(packet));
        }
        end = System.nanoTime();

        System.out.println(amount / ((end - start) * invNano) + " packets per second");
    }

    @Test
    public void testMBPerSecond() throws Exception {
        final int amount = 1000;

        final byte[] randomData = new byte[50000];
        new Random().nextBytes(randomData);

        final Packet packet = new PacketBuilder()
                .withBytes(randomData)
                .build();

        server.setListener(new ReceiveListener() {
            @Override
            public void onReceive(final Packet p, final Client c) throws IOException {
                //assertArrayEquals(randomData, new PacketReader(p).readBytes());
                Assertions.assertEquals(randomData.length + 4, p.getData().length);
            }
        });

        start = System.nanoTime();
        for (int i = 0; i < amount; i++) {
            Assertions.assertTrue(client.send(packet));
        }
        end = System.nanoTime();

        System.out.println((randomData.length * invMega * amount) / ((end - start) * invNano) + " MB per second");
    }
}
