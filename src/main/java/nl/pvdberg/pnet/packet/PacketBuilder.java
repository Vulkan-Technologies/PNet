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

package nl.pvdberg.pnet.packet;


import java.io.*;
import java.nio.charset.StandardCharsets;

public class PacketBuilder {

    private final ByteArrayOutputStream byteArrayOutputStream;
    private final DataOutputStream dataOutputStream;
    private final short packetID;
    private boolean isBuilt;

    /**
     * Provides an easy way to build a Packet.
     * Note: data has to be written in the same order as it will be fromStream!
     */
    public PacketBuilder(short id) {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        this.isBuilt = false;
        this.packetID = id;
    }

    public PacketBuilder() {
        this((short) 0);
    }

    /**
     * @throws IllegalStateException when Packet is already built
     */
    private void checkBuilt() {
        if (isBuilt) throw new IllegalStateException("Packet already built");
    }

    /**
     * Adds a byte
     *
     * @param b Byte
     * @throws IllegalStateException see {@link #checkBuilt()}
     */
    public synchronized PacketBuilder withByte(final byte b) {
        checkBuilt();
        try {
            dataOutputStream.writeByte(b);
        } catch (final IOException ignored) {
        }
        return this;
    }

    /**
     * Adds byte array
     *
     * @param b Byte array
     * @throws IllegalStateException see {@link #checkBuilt()}
     */
    public synchronized PacketBuilder withBytes(final byte[] b) {
        checkBuilt();
        try {
            dataOutputStream.writeInt(b.length);
            dataOutputStream.write(b);
        } catch (final IOException ignored) {
        }
        return this;
    }

    /**
     * Adds an integer
     *
     * @param i Integer
     * @throws IllegalStateException see {@link #checkBuilt()}
     */
    public synchronized PacketBuilder withInt(final int i) {
        checkBuilt();
        try {
            dataOutputStream.writeInt(i);
        } catch (final IOException ignored) {
        }
        return this;
    }

    /**
     * Adds a String
     *
     * @param s UTF-8 String
     * @throws IllegalStateException see {@link #checkBuilt()}
     */
    public synchronized PacketBuilder withString(final String s) {
        withBytes(s.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    /**
     * Adds a boolean
     *
     * @param b Boolean
     * @throws IllegalStateException see {@link #checkBuilt()}
     */
    public synchronized PacketBuilder withBoolean(final boolean b) {
        checkBuilt();
        try {
            dataOutputStream.writeBoolean(b);
        } catch (final IOException ignored) {
        }
        return this;
    }

    /**
     * Adds a float
     *
     * @param f Float
     * @throws IllegalStateException see {@link #checkBuilt()}
     */
    public synchronized PacketBuilder withFloat(final float f) {
        checkBuilt();
        try {
            dataOutputStream.writeFloat(f);
        } catch (final IOException ignored) {
        }
        return this;
    }

    /**
     * Adds a double
     *
     * @param d Double
     * @throws IllegalStateException see {@link #checkBuilt()}
     */
    public synchronized PacketBuilder withDouble(final double d) {
        checkBuilt();
        try {
            dataOutputStream.writeDouble(d);
        } catch (final IOException ignored) {
        }
        return this;
    }

    /**
     * Adds a long
     *
     * @param l Long
     * @throws IllegalStateException see {@link #checkBuilt()}
     */
    public synchronized PacketBuilder withLong(final long l) {
        checkBuilt();
        try {
            dataOutputStream.writeLong(l);
        } catch (final IOException ignored) {
        }
        return this;
    }

    /**
     * Adds a short
     *
     * @param s Short
     * @throws IllegalStateException see {@link #checkBuilt()}
     */
    public synchronized PacketBuilder withShort(final short s) {
        checkBuilt();
        try {
            dataOutputStream.writeShort(s);
        } catch (final IOException ignored) {
        }
        return this;
    }

    /**
     * Returns current data as a byte array
     *
     * @return Byte array
     */
    public synchronized byte[] getBytes() {
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Builds Packet with given data
     *
     * @return Packet
     * @throws IllegalStateException see {@link #checkBuilt()}
     */
    public synchronized Packet build() {
        checkBuilt();
        isBuilt = true;

        try {
            dataOutputStream.close();
        } catch (final IOException ignored) {
        }

        return new Packet(
                packetID,
                byteArrayOutputStream.toByteArray()
        );
    }
}
