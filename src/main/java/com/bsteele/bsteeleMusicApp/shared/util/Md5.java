package com.bsteele.bsteeleMusicApp.shared.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {

    public static final String getMd5Digest(String data) {
        if (data == null)
            return null;
        return getHex(getMd5DigestFromBytes(data.getBytes()));
    }

    private static String getHex(byte[] raw) {
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(hexes.charAt((b & 0xF0) >> 4)).append(hexes.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    private static final String hexes = "0123456789ABCDEF";

    /**
     * Generate MD5 digest.
     *
     * @param input input data to be hashed.
     * @return MD5 digest.
     */
    public static final byte[] getMd5DigestFromBytes(byte[] input) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5"); //  fixme: too often?
            md5.reset();
            md5.update(input);
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 implementation not found", e);
        }
    }
}
