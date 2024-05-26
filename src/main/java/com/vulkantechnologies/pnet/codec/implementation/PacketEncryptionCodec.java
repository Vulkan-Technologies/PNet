package com.vulkantechnologies.pnet.codec.implementation;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.vulkantechnologies.pnet.codec.PacketCodec;
import com.vulkantechnologies.pnet.packet.Packet;

public class PacketEncryptionCodec implements PacketCodec {

    private final CryptBuf encodeBuf;
    private final CryptBuf decodeBuf;

    public PacketEncryptionCodec(SecretKey sharedSecret) {
        try {
            this.encodeBuf = new CryptBuf(Cipher.ENCRYPT_MODE, sharedSecret);
            this.decodeBuf = new CryptBuf(Cipher.DECRYPT_MODE, sharedSecret);
        } catch (GeneralSecurityException e) {
            throw new AssertionError("Failed to initialize encrypted channel", e);
        }
    }

    @Override
    public Packet decode(Packet packet) throws IOException, IllegalBlockSizeException, BadPaddingException {
        return packet.alterData(decodeBuf.crypt(packet.getData()));
    }

    @Override
    public Packet encode(Packet packet) throws IOException, IllegalBlockSizeException, BadPaddingException {
        return packet.alterData(encodeBuf.crypt(packet.getData()));
    }

    private static class CryptBuf {

        private final Cipher cipher;

        private CryptBuf(int mode, SecretKey sharedSecret) throws GeneralSecurityException {
            cipher = Cipher.getInstance("AES/CFB8/NoPadding"); // NON-NLS
            cipher.init(mode, sharedSecret, new IvParameterSpec(sharedSecret.getEncoded()));
        }

        public byte[] crypt(byte[] inputBytes) throws IllegalBlockSizeException, BadPaddingException {
            return cipher.doFinal(inputBytes);
        }
    }
}
