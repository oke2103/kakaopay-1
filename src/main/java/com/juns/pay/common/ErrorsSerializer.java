package com.juns.pay.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

    private Errors errors;
    private JsonGenerator gen;
    private SerializerProvider serializerProvider;

    @Override
    public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        this.errors = errors;
        this.gen = gen;
        this.serializerProvider = serializerProvider;
        gen.writeStartArray();

        errors.getGlobalErrors().forEach(e -> {
            try {
                gen.writeStartObject();
                gen.writeStringField("objectName", e.getObjectName());
                gen.writeStringField("code", e.getCode());
                gen.writeStringField("defaultMessage", e.getDefaultMessage());
                gen.writeEndObject();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        gen.writeEndArray();
    }
}