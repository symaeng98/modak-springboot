package com.modak.modakapp.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modak.modakapp.domain.metadata.MDRepeatTag;

import javax.persistence.AttributeConverter;

public class MDRepeatTagAttributeConverter implements AttributeConverter<MDRepeatTag,String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    @Override
    public String convertToDatabaseColumn(MDRepeatTag attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("데이터베이스에 변환되지 않았습니다.");
        }
    }

    @Override
    public MDRepeatTag convertToEntityAttribute(String dbData) {
        try {
            System.out.println("dbData = " + dbData);
            return objectMapper.readValue(dbData, MDRepeatTag.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("엔티티로 변환이 안됐습니다.");
        }
    }
}