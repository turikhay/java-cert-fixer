package com.turikhay.caf.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Stores both SHA-1 and SHA-256 checksum
 */
public class DualFingerprint {

    /**
     * Calculates dual fingerprint of the given byte array
     * @param data byte array to compute checksum for
     * @return DualFingerprint instance
     */
    public static DualFingerprint compute(byte[] data) {
        return new DualFingerprint(
                computeWithAlgo(data, "SHA-1"),
                computeWithAlgo(data, "SHA-256")
        );
    }

    private final String sha1, sha256;

    /**
     * Get SHA-1
     * @return SHA-1
     */
    public String getSha1() {
        return sha1;
    }

    /**
     * Get SHA-256
     * @return SHA-256
     */
    public String getSha256() {
        return sha256;
    }

    private DualFingerprint(String sha1, String sha256) {
        this.sha1 = sha1;
        this.sha256 = sha256;
    }

    private static String computeWithAlgo(byte[] data, String algo) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algo);
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Well-known algorithm is missing ("+ algo +")", e);
        }
        md.update(data);
        return toHex(md.digest());
    }

    private static String toHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
