package com.example.newsservice.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
        return OffsetDateTime.parse(p.getValueAsString(), fmt);
    }

}
