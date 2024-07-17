package org.study.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EncryptService {

    private final AesBytesEncryptor encryptor;

    // 전화번호 암호화
    public String encryptPhoneNo(String info) {
        // 전화번호 형식의 문자열에서 대시('-')를 제거
        String sanitizedInfo = info.replaceAll("-", "");
        byte[] encrypt = encryptor.encrypt(sanitizedInfo.getBytes(StandardCharsets.UTF_8));
        return byteArrayToString(encrypt);
    }

    // 전화번호 복호화
    public String decryptPhoneNo(String encryptString) {
        byte[] decryptBytes = stringToByteArray(encryptString);
        byte[] decrypt = encryptor.decrypt(decryptBytes);
        String decryptedInfo = new String(decrypt, StandardCharsets.UTF_8);
        // 복호화된 전화번호에 대시('-')를 추가할 필요가 없으면 이 부분을 생략 가능
        return decryptedInfo;
    }

    // 사용자 이름 암호화
    public String encryptUsername(String username) {
        byte[] encrypt = encryptor.encrypt(username.getBytes(StandardCharsets.UTF_8));
        return byteArrayToString(encrypt);
    }

    // 사용자 이름 복호화
    public String decryptUsername(String encryptString) {
        byte[] decryptBytes = stringToByteArray(encryptString);
        byte[] decrypt = encryptor.decrypt(decryptBytes);
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    // byte -> String
    public String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte abyte : bytes) {
            sb.append(abyte);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    // String -> byte
    public byte[] stringToByteArray(String byteString) {
        String[] split = byteString.split("\\s");
        ByteBuffer buffer = ByteBuffer.allocate(split.length);
        for (String s : split) {
            buffer.put(Byte.parseByte(s));
        }
        return buffer.array();
    }
}
