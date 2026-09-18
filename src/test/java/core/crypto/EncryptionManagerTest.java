package core.crypto;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionManagerTest{

    @Test 
    void encryptDecryptRoundTrip(){
        SecretKey key = EncryptionManager.deriveKey("correct-password", "test-salt".getBytes(StandardCharsets.UTF_8));
        
        String plaintext = "Hello OmniPass";

        String encrypted = EncryptionManager.encrypt(plaintext, key);
        String decrypted = EncryptionManager.decrypt(encrypted, key);

        assertEquals(plaintext, decrypted);
    }

    @Test 
    void encryptedOutputUsesGcmFormat(){
        SecretKey key = EncryptionManager.deriveKey("correct-password", "test-salt".getBytes(StandardCharsets.UTF_8));

        String encrypted = EncryptionManager.encrypt("Hello OmniPass", key);

        assertTrue(encrypted.startsWith("OMNIPASS_GCM_V1:"));

        String[] parts = encrypted.split(":", 3);

        assertEquals(3, parts.length);
        assertEquals("OMNIPASS_GCM_V1", parts[0]);
    }

    @Test 
    void encryptionUsesDifferentNonces(){
        SecretKey key = EncryptionManager.deriveKey("correct-password", "test-salt".getBytes(StandardCharsets.UTF_8));

        String plaintext = "Same plaintext";

        String encrypted1 = EncryptionManager.encrypt(plaintext, key);
        String encrypted2 = EncryptionManager.encrypt(plaintext, key);

        assertNotEquals(encrypted1, encrypted2);
    }

    @Test 
    void wringKeyFailsDecryption(){
        SecretKey correctkey = EncryptionManager.deriveKey("correct-password", "test-salt".getBytes(StandardCharsets.UTF_8));

        SecretKey wrongKey = EncryptionManager.deriveKey("wrong-password", "test-salt".getBytes(StandardCharsets.UTF_8));

        String encrypted = EncryptionManager.encrypt("Secret data", correctkey);

        assertThrows(
            RuntimeException.class, 
            () -> EncryptionManager.decrypt(encrypted, wrongKey)
        );
    }

    @Test 
    void tamperedCiphertextFails(){
        SecretKey key = EncryptionManager.deriveKey("correct-password","test-salt".getBytes(StandardCharsets.UTF_8));

        String encrypted = EncryptionManager.encrypt("Secret data", key);

        String[] parts = encrypted.split(":",3);

        byte[] ciphertext = Base64.getDecoder().decode(parts[2]);

        String tampared = parts[0] + ":" + parts[1] + ":" + Base64.getEncoder().encode(ciphertext);

        assertThrows(
            RuntimeException.class, 
            () -> EncryptionManager.decrypt(tampared, key)
        );
    }   
    
    @Test 
    void invalidFormatFails(){
        SecretKey key = EncryptionManager.deriveKey("correct-password", "test-salt".getBytes(StandardCharsets.UTF_8));

        assertThrows(
            RuntimeException.class, 
            () -> EncryptionManager.decrypt("invalid-data", key)
        );
    }

    @Test 
    void deriveKeyIsDeterministic(){
        byte[] salt = "same-salt".getBytes(StandardCharsets.UTF_8);

        SecretKey key1 = EncryptionManager.deriveKey("correct-password", salt);

        SecretKey key2 = EncryptionManager.deriveKey("correct-password", salt);

        assertArrayEquals(key1.getEncoded(), key2.getEncoded());
    }

    @Test 
    void generateSaltProducesFreshSalt(){
        byte[] salt1 = EncryptionManager.generateSalt();
        byte[] salt2 = EncryptionManager.generateSalt();

        assertEquals(16, salt1.length);
        assertEquals(16, salt2.length);
        assertFalse(java.util.Arrays.equals(salt1,salt2));
    }

    @Test 
    void unicodeRoundTrip(){
        SecretKey key = EncryptionManager.deriveKey("correct-password", "test-salt".getBytes(StandardCharsets.UTF_8));

        String plaintext = "OmniPass 🔐 — नमस्ते 世界";

        String encrypted = EncryptionManager.encrypt(plaintext, key);
        String decrypted = EncryptionManager.decrypt(encrypted, key);

        assertEquals(plaintext, decrypted);
    }
}