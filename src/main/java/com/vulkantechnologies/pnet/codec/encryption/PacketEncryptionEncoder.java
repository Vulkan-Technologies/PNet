package com.vulkantechnologies.pnet.codec.encryption;

import java.security.GeneralSecurityException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

import com.vulkantechnologies.pnet.codec.PacketEncoder;
import com.vulkantechnologies.pnet.packet.Packet;
import com.vulkantechnologies.pnet.utils.CryptoBuffer;

public class PacketEncryptionEncoder implements PacketEncoder {

    private final CryptoBuffer encodeBuf;

    public PacketEncryptionEncoder(SecretKey sharedSecret) {
        try {
            this.encodeBuf = new CryptoBuffer(Cipher.ENCRYPT_MODE, sharedSecret);
        } catch (GeneralSecurityException e) {
            throw new AssertionError("Failed to initialize encrypted channel", e);
        }
    }


    @Override
    public Packet encode(Packet packet) throws IllegalBlockSizeException, BadPaddingException {
        return packet.alterData(encodeBuf.crypt(packet.getData()));
    }
}
