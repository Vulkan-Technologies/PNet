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

package nl.pvdberg.pnet.server.util;

import java.net.InetSocketAddress;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.pvdberg.pnet.server.Server;

public class PlainServerTest {
    protected static final int port1 = 42365;
    protected static final int port2 = 42366;

    protected static Server server1;
    protected static Server server2;

    @BeforeAll
    public static void setUp() throws Exception {
        server1 = new PlainServer();
        Assertions.assertTrue(server1.start(new InetSocketAddress("localhost", port1)));
        server2 = new PlainServer();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        server1.stop();
        server2.stop();
    }

    @Test
    public void illegalStart() throws Exception {
        Assertions.assertFalse(server2.start(new InetSocketAddress("localhost", port1)));
    }

    @Test
    public void start() throws Exception {
        Assertions.assertTrue(server2.start(new InetSocketAddress("localhost", port2)));
    }
}