package com.groupgrow.groupgrow.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PrivacyConverter implements AttributeConverter<GroupDetails.Privacy, String> {

    @Override
    public String convertToDatabaseColumn(GroupDetails.Privacy privacy) {
        if (privacy == null) {
            return null;
        }
        return privacy.name().toLowerCase();
    }

    @Override
    public GroupDetails.Privacy convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return GroupDetails.Privacy.PUBLIC; // Valor por defecto
        }
        try {
            return GroupDetails.Privacy.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Si el valor no coincide, retornar PUBLIC por defecto
            return GroupDetails.Privacy.PUBLIC;
        }
    }
}

