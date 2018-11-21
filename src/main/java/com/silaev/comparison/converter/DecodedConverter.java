package com.silaev.comparison.converter;

import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * Encodes a string by means of Base64 decoder.
 */
@Component
public class DecodedConverter {

    public String convert(String encodedString) {
        if (encodedString == null) {
            return null;
        }
        byte[] bytes = Base64.getDecoder().decode(encodedString);
        return new String(bytes);
    }
}
