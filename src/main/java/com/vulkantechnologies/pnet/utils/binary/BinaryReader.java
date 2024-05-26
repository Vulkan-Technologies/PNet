package com.vulkantechnologies.pnet.utils.binary;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.function.Supplier;


public class BinaryReader extends InputStream {

    private final ByteBuffer buffer;

    public BinaryReader(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public BinaryReader(byte[] bytes) {
        this.buffer = ByteBuffer.wrap(bytes);
    }

    public int readVarInt() {
        int value = 0;
        int i = 0;
        int b;
        while (((b = readByte()) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
        }
        return value | (b << i);
    }

    public int readInt() {
        return buffer.getInt();
    }

    public long readLong() {
        return buffer.getLong();
    }

    public short readShort() {
        return buffer.getShort();
    }

    public byte readByte() {
        return buffer.get();
    }

    public float readFloat() {
        return buffer.getFloat();
    }

    public double readDouble() {
        return buffer.getDouble();
    }

    public char readChar() {
        return buffer.getChar();
    }

    public String readString(int maxLength) {
        final int length = readVarInt();
        if (buffer.remaining() < length)
            throw new IllegalStateException("String length is longer than buffer");
        String string = new String(buffer.array(), buffer.position(), length, StandardCharsets.UTF_8);
        if (string.length() > maxLength)
            throw new IllegalStateException("String length is longer than allowed");
        buffer.position(buffer.position() + length);
        return string;
    }

    public String readString() {
        return readString(Short.MAX_VALUE);
    }

    public String[] readStringArray(int maxLength) {
        int length = readVarInt();
        if (buffer.remaining() < length * maxLength)
            throw new IllegalStateException("String array length is longer than buffer");
        String[] strings = new String[length];
        for (int i = 0; i < length; i++) {
            strings[i] = readString(maxLength);
        }
        return strings;
    }

    public String[] readStringArray() {
        return readStringArray(Short.MAX_VALUE);
    }

    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }

    public byte[] readBytes() {
        int length = readVarInt();
        if (buffer.remaining() < length)
            throw new IllegalStateException("Byte array length is longer than buffer");
        return readBytes(length);
    }

    public boolean readBoolean() {
        return readByte() != 0;
    }

    public UUID readUniqueId() {
        long mostSigBits = readLong();
        long leastSigBits = readLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public Date readDate() {
        return new Date(readLong());
    }

    public int[] readVarIntArray() {
        int length = readVarInt();
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = readVarInt();
        }
        return array;
    }

    public long[] readLongArray() {
        int length = readVarInt();
        long[] array = new long[length];
        for (int i = 0; i < length; i++) {
            array[i] = readLong();
        }
        return array;
    }

    public <T extends Readable> T read(Supplier<T> supplier) {
        T readable = supplier.get();
        readable.read(this);
        return readable;
    }

    public <T extends Readable> T[] readArray(Supplier<T> supplier) {
        Readable[] array = new Readable[readVarInt()];
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get();
            array[i].read(this);
        }
        return (T[]) array;
    }

    @Override
    public int read() {
        return readByte() & 0xFF;
    }

    public int available() {
        return buffer.remaining();
    }

    public boolean hasRemaining() {
        return buffer.hasRemaining();
    }

    public byte[] readRemaining() {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }

    public byte[] extractBytes(Runnable runnable) {
        int position = buffer.position();
        runnable.run();
        int length = buffer.position() - position;
        buffer.position(position);
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }
}
