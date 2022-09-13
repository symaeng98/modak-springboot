package com.modak.modakapp.utils.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modak.modakapp.dto.metadata.MetaData;

import javax.persistence.AttributeConverter;

public class MetaDataAttributeConverter implements AttributeConverter<MetaData, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public String convertToDatabaseColumn(MetaData attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("데이터베이스에 변환되지 않았습니다.");
        }
    }

    @Override
    public MetaData convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, MetaData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("엔티티로 변환이 안됐습니다.");
        }
    }
}
