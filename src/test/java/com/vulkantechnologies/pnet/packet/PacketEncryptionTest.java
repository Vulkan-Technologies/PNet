package com.vulkantechnologies.pnet.packet;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vulkantechnologies.pnet.codec.encryption.PacketEncryptionDecoder;
import com.vulkantechnologies.pnet.codec.encryption.PacketEncryptionEncoder;
import com.vulkantechnologies.pnet.packet.io.PacketReader;
import com.vulkantechnologies.pnet.packet.io.PacketWriter;

public class PacketEncryptionTest {

    @Test
    public void encryptAndDecrypt() throws Exception {
        final byte[] data = new byte[8096];
        new Random().nextBytes(data);

        final Packet packet = new PacketWriter()
                .withBytes(data)
                .build();

        SecretKey sharedKey = generateSharedKey();


        Packet encrypted = new PacketEncryptionEncoder(sharedKey).encode(packet);
        Packet decrypted = new PacketEncryptionDecoder(sharedKey).decode(encrypted);

        Assertions.assertNotEquals(encrypted.getData(), data);
        Assertions.assertArrayEquals(data, new PacketReader(packet).readBytes());
        Assertions.assertArrayEquals(data, new PacketReader(decrypted).readBytes());
    }

    public static SecretKey generateSharedKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128);
            return generator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
