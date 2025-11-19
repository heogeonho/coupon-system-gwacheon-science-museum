package com.gwacheon.naturemuseum.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES-128 암호화/복호화 유틸리티
 */
@Slf4j
@Component
public class CryptoUtil {

	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

	private final SecretKeySpec secretKey;

	public CryptoUtil(@Value("${crypto.secret-key}") String secretKey) {
		this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
	}

	/**
	 * 문자열 암호화
	 */
	public String encrypt(String plainText) {
		if (plainText == null || plainText.isEmpty()) {
			return plainText;
		}

		try {
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			log.error("암호화 실패: {}", plainText, e);
			throw new RuntimeException("암호화 중 오류가 발생했습니다.", e);
		}
	}

	/**
	 * 문자열 복호화
	 */
	public String decrypt(String encryptedText) {
		if (encryptedText == null || encryptedText.isEmpty()) {
			return encryptedText;
		}

		try {
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decoded = Base64.getDecoder().decode(encryptedText);
			byte[] decrypted = cipher.doFinal(decoded);
			return new String(decrypted, StandardCharsets.UTF_8);
		} catch (Exception e) {
			log.error("복호화 실패: {}", encryptedText, e);
			throw new RuntimeException("복호화 중 오류가 발생했습니다.", e);
		}
	}
}
