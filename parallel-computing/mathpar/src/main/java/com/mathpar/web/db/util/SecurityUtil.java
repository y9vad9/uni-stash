package com.mathpar.web.db.util;

import com.mathpar.func.Page;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {
    private static final int ITERATIONS = 10;

    private SecurityUtil() {
    }

    /**
     * From a password and a salt, returns the corresponding digest
     * (with {@code ITERATIONS} number of iterations).
     *
     * @param password String The password to encrypt
     * @param salt     byte[] The salt
     * @return byte[] The digested password
     * @throws NoSuchAlgorithmException If the algorithm doesn't exist
     */
    public static byte[] getHash(String password, byte[] salt)
            throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(password.getBytes(Page.CHARSET_DEFAULT));
        for (int i = 0; i < ITERATIONS; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return input;
    }

    /**
     * From a base 64 representation, returns the corresponding byte[]
     *
     * @param data String The base64 representation
     * @return byte[]
     * @throws IOException
     */
    public static byte[] base64ToByte(String data) throws IOException {
        return DatatypeConverter.parseBase64Binary(data);
    }

    /**
     * From a byte[] returns a base 64 representation
     *
     * @param data byte[]
     * @return String
     */
    public static String byteToBase64(byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }
}
