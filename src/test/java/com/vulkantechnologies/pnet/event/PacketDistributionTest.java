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

package com.vulkantechnologies.pnet.event;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vulkantechnologies.pnet.packet.Packet;
import com.vulkantechnologies.pnet.packet.io.PacketWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class PacketDistributionTest {
    protected static final Packet packet1 = new PacketWriter((short) 1)
            .build();

    protected static final Packet packet2 = new PacketWriter((short) 2)
            .build();

    protected PacketDistributer packetDistributer;

    @BeforeEach
    public void setUp() throws Exception {
        packetDistributer = new PacketDistributer();
    }

    @Test
    public void registeredHandler() throws Exception {
        final List<Packet> receivedPackets = new ArrayList<>();

        packetDistributer.addHandler(packet1.getId(), (p, c) -> receivedPackets.add(p));

        packetDistributer.onReceive(packet1, null);
        assertEquals(1, receivedPackets.size());
    }

    @Test
    public void defaultHandler() throws Exception {
        final List<Packet> receivedPackets = new ArrayList<>();

        packetDistributer.addHandler(packet1.getId(), (p, c) -> receivedPackets.add(p));
        packetDistributer.setDefaultHandler((p, c) -> receivedPackets.add(p));

        packetDistributer.onReceive(packet2, null);
        assertEquals(1, receivedPackets.size());
    }

    @Test
    public void globalHandler() throws Exception {
        final List<Packet> receivedPackets = new ArrayList<>();

        final PacketDistributer globalHandler = new PacketDistributer();
        globalHandler.setDefaultHandler((p, c) -> receivedPackets.add(p));
        packetDistributer.setGlobalHandler(globalHandler);

        packetDistributer.onReceive(packet1, null);
        assertEquals(1, receivedPackets.size());

        packetDistributer.addHandler(packet2.getId(), (p, c) -> receivedPackets.add(p));

        packetDistributer.onReceive(packet2, null);
        assertEquals(3, receivedPackets.size());
    }
}