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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

public class PacketReader {

    private final Packet packet;
    private final DataInputStream dataInputStream;

    /**
     * Provides an easy way to read data from a Packet
     * Note: data has to be read in the same order as it was written!
     */
    public PacketReader(final Packet packet) {
        this.packet = packet;
        dataInputStream = new DataInputStream(new ByteArrayInputStream(packet.getData()));
    }

    /**
     * See {@link Packet#getId()}
     */
    public short getPacketId() {
        return packet.getId();
    }

    /**
     * Reads a byte
     * @return Byte
     * @throws IOException when unable to read
     */
    public synchronized byte readByte() throws IOException {
        return dataInputStream.readByte();
    }

    /**
     * Reads byte array into output
     * @return Bytes
     * @throws IOException when not enough data is available
     */
    public synchronized byte[] readBytes() throws IOException {
        final int dataLength = dataInputStream.readInt();
        final byte[] data = new byte[dataLength];

        final int dataRead = dataInputStream.read(data, 0, dataLength);
        if (dataRead != dataLength) throw new IOException("Not enough data available");

        return data;
    }

    /**
     * Reads an integer
     * @return Integer
     * @throws IOException when unable to read
     */
    public synchronized int readInt() throws IOException {
        return dataInputStream.readInt();
    }

    /**
     * Reads a String
     * @return UTF-8 String
     * @throws IOException when unable to read
     */
    @Nullable
    public synchronized String readString() throws IOException {
        return new String(readBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Reads a boolean
     * @return Boolean
     * @throws IOException when unable to read
     */
    public synchronized boolean readBoolean() throws IOException {
        return dataInputStream.readBoolean();
    }

    /**
     * Reads a float
     * @return Float
     * @throws IOException when unable to read
     */
    public synchronized float readFloat() throws IOException {
        return dataInputStream.readFloat();
    }

    /**
     * Reads a double
     * @return Double
     * @throws IOException when unable to read
     */
    public synchronized double readDouble() throws IOException {
        return dataInputStream.readDouble();
    }

    /**
     * Reads a long
     * @return Long
     * @throws IOException when unable to read
     */
    public synchronized long readLong() throws IOException {
        return dataInputStream.readLong();
    }

    /**
     * Reads a short
     * @return Short
     * @throws IOException when unable to read
     */
    public synchronized short readShort() throws IOException {
        return dataInputStream.readShort();
    }

    /**
     * Reads a UUID
     * @return UUID
     * @throws IOException when unable to read
     */
    @Nullable
    public synchronized UUID readUUID() throws IOException {
        return new UUID(dataInputStream.readLong(), dataInputStream.readLong());
    }

    /**
     * Reads a Date
     * @return Date
     * @throws IOException when unable to read
     */
    @Nullable
    public synchronized Date readDate() throws IOException {
        return new Date(dataInputStream.readLong());
    }

    /**
     * Returns internal Packet
     * @return Packet
     */
    public Packet getPacket() {
        return packet;
    }
}
