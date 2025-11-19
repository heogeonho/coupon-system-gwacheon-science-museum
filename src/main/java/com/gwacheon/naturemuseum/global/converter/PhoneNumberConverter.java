package com.gwacheon.naturemuseum.global.converter;

import com.gwacheon.naturemuseum.global.util.CryptoUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

/**
 * 전화번호 자동 암호화/복호화 JPA Converter
 */
@Converter
@RequiredArgsConstructor
public class PhoneNumberConverter implements AttributeConverter<String, String> {

	private final CryptoUtil cryptoUtil;

	/**
	 * DB 저장 시 암호화
	 */
	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (attribute == null) {
			return null;
		}
		return cryptoUtil.encrypt(attribute);
	}

	/**
	 * DB 조회 시 복호화
	 */
	@Override
	public String convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return cryptoUtil.decrypt(dbData);
	}
}
