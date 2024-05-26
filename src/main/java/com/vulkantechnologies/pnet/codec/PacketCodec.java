package com.vulkantechnologies.pnet.codec;

import java.io.IOException;

import com.vulkantechnologies.pnet.packet.Packet;

public interface PacketCodec {

    Packet decode(Packet packet) throws IOException;

    Packet encode(Packet packet) throws IOException;
}
