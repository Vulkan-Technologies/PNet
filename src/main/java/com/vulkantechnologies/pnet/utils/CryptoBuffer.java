package com.vulkantechnologies.pnet.utils;

import java.security.GeneralSecurityException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptoBuffer {

    private final Cipher cipher;

    public CryptoBuffer(int mode, SecretKey sharedSecret) throws GeneralSecurityException {
        cipher = Cipher.getInstance("AES/CFB8/NoPadding"); // NON-NLS
        cipher.init(mode, sharedSecret, new IvParameterSpec(sharedSecret.getEncoded()));
    }

    public byte[] crypt(byte[] inputBytes) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(inputBytes);
    }
}
