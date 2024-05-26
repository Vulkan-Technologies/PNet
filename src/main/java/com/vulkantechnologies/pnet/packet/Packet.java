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

package com.vulkantechnologies.pnet.packet;

import java.io.*;

import lombok.Getter;

@Getter
public class Packet {

    private final short id;
    private final int length;
    private final byte[] data;

    /**
     * Creates a new immutable Packet
     * <p>
     * `     * @param packetID Packet ID
     *
     * @param data Packet Data
     */
    public Packet(final short id, final byte[] data) {
        this.id = id;
        this.length = data.length;
        this.data = data;
    }

    /**
     * Writes Packet into DataOutputStream
     *
     * @param out DataOutputStream to write into
     * @throws IOException when unable to write to stream
     */
    public void write(final DataOutputStream out) throws IOException {
        // Packet ID
        out.writeShort(id);

        // Data Length
        out.writeInt(length);

        // Data
        out.write(data);
    }

    /**
     * Reads a Packet from raw input data
     *
     * @param in DataInputStream to fromStream from
     * @return Packet created from input
     * @throws IOException when unable to read from stream
     */
    public static Packet fromStream(final DataInputStream in) throws IOException {
        // Packet ID
        final short packetID = in.readShort();

        // Data Length
        final int dataLength = in.readInt();

        // Data
        final byte[] data = new byte[dataLength];
        in.readFully(data);

        return new Packet(
                packetID,
                data
        );
    }
}
