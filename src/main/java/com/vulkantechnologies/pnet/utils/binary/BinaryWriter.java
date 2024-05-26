package com.vulkantechnologies.pnet.utils.binary;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class BinaryWriter extends OutputStream {

    private ByteBuffer buffer;

    public BinaryWriter(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public BinaryWriter(int capacity) {
        this.buffer = ByteBuffer.allocate(capacity);
    }

    public void writeVarInt(int value) {
        while ((value & 0xFFFFFF80) != 0L) {
            buffer.put((byte) ((value & 0x7F) | 0x80));
            value >>>= 7;
        }
        buffer.put((byte) (value & 0x7F));
    }

    public void writeInt(int value) {
        buffer.putInt(value);
    }

    public void writeLong(long value) {
        buffer.putLong(value);
    }

    public void writeShort(short value) {
        buffer.putShort(value);
    }

    public void writeByte(byte value) {
        buffer.put(value);
    }

    public void writeFloat(float value) {
        buffer.putFloat(value);
    }

    public void writeDouble(double value) {
        buffer.putDouble(value);
    }

    public void writeChar(char value) {
        buffer.putChar(value);
    }

    public void writeString(String value) {
        writeVarInt(value.length());
        writeBytes(value.getBytes(StandardCharsets.UTF_8));
    }

    public void writeUUID(UUID value) {
        writeLong(value.getMostSignificantBits());
        writeLong(value.getLeastSignificantBits());
    }

    public void write(Writeable writeable) {
        writeable.write(this);
    }

    public void write(ByteBuffer buffer) {
        this.buffer.put(buffer);
    }

    public void writeBytes(byte[] value) {
        buffer.put(value);
    }

    public void writeVarIntArray(int[] values) {
        writeVarInt(values.length);
        for (int value : values) {
            writeVarInt(value);
        }
    }

    public void writeIntArray(int[] values) {
        writeVarInt(values.length);
        for (int value : values) {
            writeInt(value);
        }
    }

    public void writeLongArray(long[] values) {
        writeVarInt(values.length);
        for (long value : values) {
            writeLong(value);
        }
    }

    public void writeArray(Writeable[] writeables) {
        writeVarInt(writeables.length);
        for (Writeable writeable : writeables) {
            write(writeable);
        }
    }

    public void writeAtStart(BinaryWriter headerWriter) {
        final ByteBuffer headerBuffer = headerWriter.buffer;
        final ByteBuffer finalBuffer = ByteBuffer.allocate(headerBuffer.remaining() + buffer.remaining());
        finalBuffer.put(headerBuffer);
        finalBuffer.put(buffer);
        buffer = finalBuffer;
    }

    public void writeAtEnd(BinaryWriter footerWriter) {
        final ByteBuffer footerBuffer = footerWriter.buffer;
        final ByteBuffer finalBuffer = ByteBuffer.allocate(buffer.remaining() + footerBuffer.remaining());
        finalBuffer.put(buffer);
        finalBuffer.put(footerBuffer);
        buffer = finalBuffer;
    }

    public byte[] toByteArray() {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }

    @Override
    public void write(int b) throws IOException {
        buffer.put((byte) b);
    }
}
