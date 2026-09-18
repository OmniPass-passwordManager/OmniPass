package core.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;

import java.nio.charset.StandardCharsets;

public class EncryptionManager {

    public static String encrypt(String text, SecretKey key){
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            byte[] nonce = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(nonce);

            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);

            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

            String nonceString = Base64.getEncoder().encodeToString(nonce);
            String encryptedString = Base64.getEncoder().encodeToString(encrypted);

            return "OMNIPASS_GCM_V1:" + nonceString + ":" + encryptedString;
            
        } catch (Exception e){
            throw new RuntimeException("Encryption failed.", e);
        }
    }

    public static String decrypt(String encryptedText, SecretKey key){
        try {
            String[] parts = encryptedText.split(":",3);

            if (parts.length != 3 || !parts[0].equals("OMNIPASS_GCM_V1")){
                throw new RuntimeException("Invalid encrypted data format.");
            }

            byte[] nonce = Base64.getDecoder().decode(parts[1]);
            byte[] encrypted = Base64.getDecoder().decode(parts[2]);

            if (nonce.length != 12){
                throw new RuntimeException("Invalid encryption nonce.");
            }

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);

            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted,StandardCharsets.UTF_8);
        } catch (Exception e){
            throw new RuntimeException("Decryption failed.", e);
        }
    }

    public static SecretKey deriveKey(String password, byte[] salt) {

        try {

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] key = factory.generateSecret(spec).getEncoded();

            return new SecretKeySpec(key, "AES");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToHex(byte[] bytes) {

        StringBuilder builder = new StringBuilder();

        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }

    public static byte[] hexToBytes(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hexadecimal string.");
        }

        byte[] bytes = new byte[hex.length() / 2];

        for (int i = 0; i < bytes.length; i++) {

            int index = i * 2;

            try {
                bytes[i] = (byte) Integer.parseInt(
                    hex.substring(index, index + 2),
                    16
                );
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid hexadecimal string.", e);
            }
        }
        
        return bytes;
    }

    public static byte[] generateSalt() {

        SecureRandom random = new SecureRandom();

        byte[] salt = new byte[16];

        random.nextBytes(salt);

        return salt;
    }

    public static String pbkdf2Hash(String password, byte[] salt) {

        try {

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return bytesToHex(hash);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
