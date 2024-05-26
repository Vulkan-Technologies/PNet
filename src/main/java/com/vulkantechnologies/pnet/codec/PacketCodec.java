package com.vulkantechnologies.pnet.codec;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import com.vulkantechnologies.pnet.packet.Packet;

public interface PacketCodec {

    Packet decode(Packet packet) throws IOException, IllegalBlockSizeException, BadPaddingException;

    Packet encode(Packet packet) throws IOException, IllegalBlockSizeException, BadPaddingException;
}
