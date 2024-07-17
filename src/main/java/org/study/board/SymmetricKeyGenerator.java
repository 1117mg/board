package org.study.board;

import java.security.SecureRandom;
import java.util.Base64;

// 양방향 비밀키 생성
public class SymmetricKeyGenerator {
    public static void main(String[] args) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[16]; // 128-bit key
        secureRandom.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        System.out.println("Generated Symmetric Key: " + encodedKey);
    }
}
