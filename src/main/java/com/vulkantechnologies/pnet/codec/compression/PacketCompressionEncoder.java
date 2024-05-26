package com.vulkantechnologies.pnet.codec.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import com.vulkantechnologies.pnet.codec.PacketEncoder;
import com.vulkantechnologies.pnet.packet.Packet;

public class PacketCompressionEncoder implements PacketEncoder {

    private static final int INFLATE_BUFFER_SIZE = 16;

    @Override
    public Packet encode(Packet packet) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream) {
            {
                def.setLevel(Deflater.BEST_COMPRESSION);
            }
        };

        // Deflate all data
        gzipOutputStream.write(packet.getData());
        gzipOutputStream.close();

        return new Packet(
                packet.getId(),
                byteArrayOutputStream.toByteArray()
        );
    }
}
