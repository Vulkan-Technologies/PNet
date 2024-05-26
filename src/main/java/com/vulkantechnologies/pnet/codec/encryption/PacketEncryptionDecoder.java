package com.vulkantechnologies.pnet.codec.encryption;

import java.security.GeneralSecurityException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

import com.vulkantechnologies.pnet.codec.PacketDecoder;
import com.vulkantechnologies.pnet.packet.Packet;
import com.vulkantechnologies.pnet.utils.CryptoBuffer;

public class PacketEncryptionDecoder implements PacketDecoder {

    private final CryptoBuffer decodeBuf;

    public PacketEncryptionDecoder(SecretKey sharedSecret) {
        try {
            this.decodeBuf = new CryptoBuffer(Cipher.DECRYPT_MODE, sharedSecret);
        } catch (GeneralSecurityException e) {
            throw new AssertionError("Failed to initialize encrypted channel", e);
        }
    }

    @Override
    public Packet decode(Packet packet) throws IllegalBlockSizeException, BadPaddingException {
        return packet.alterData(decodeBuf.crypt(packet.getData()));
    }
}
