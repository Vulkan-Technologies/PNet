package com.vulkantechnologies.pnet.codec;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import com.vulkantechnologies.pnet.packet.Packet;

@FunctionalInterface
public interface PacketEncoder extends PacketCodec {

    Packet encode(Packet packet) throws IOException, IllegalBlockSizeException, BadPaddingException;

}
