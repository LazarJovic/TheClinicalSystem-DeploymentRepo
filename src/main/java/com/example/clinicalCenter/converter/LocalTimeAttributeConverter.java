package com.example.clinicalCenter.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Time;
import java.time.LocalTime;

@Converter(autoApply = true)
public class LocalTimeAttributeConverter implements AttributeConverter<LocalTime, Time> {

    @Override
    public Time convertToDatabaseColumn(LocalTime attribute) {
        if (attribute != null) {
            return Time.valueOf(attribute);
        } else {
            return null;
        }
    }

    @Override
    public LocalTime convertToEntityAttribute(Time dbData) {
        if (dbData != null) {
            return dbData.toLocalTime();
        } else {
            return null;
        }
    }
}
