package com.vulkantechnologies.pnet.codec.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.vulkantechnologies.pnet.codec.PacketDecoder;
import com.vulkantechnologies.pnet.packet.Packet;

public class PacketCompressionDecoder implements PacketDecoder {

    private static final int INFLATE_BUFFER_SIZE = 16;

    @Override
    public Packet decode(Packet packet) throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());
        final GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Read from input until everything is inflated
        final byte[] buffer = new byte[INFLATE_BUFFER_SIZE];
        int bytesInflated;
        while ((bytesInflated = gzipInputStream.read(buffer)) >= 0) {
            byteArrayOutputStream.write(buffer, 0, bytesInflated);
        }

        return new Packet(
                packet.getId(),
                byteArrayOutputStream.toByteArray()
        );
    }
}
