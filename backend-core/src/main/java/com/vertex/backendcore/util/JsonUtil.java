package com.vertex.backendcore.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vertex.core.exceptions.ServiceException;
import com.vertex.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
public class JsonUtil {


    private static ObjectMapper MAPPER;

    static {
        JsonUtil.MAPPER = new ObjectMapper().registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        final SimpleModule dateSerializationModule = new SimpleModule();

        dateSerializationModule.addSerializer(Date.class, new DateSerializer());
        dateSerializationModule.addDeserializer(Date.class, new DateDeserializer());

        JsonUtil.MAPPER.registerModule(dateSerializationModule);

        log.info("JsonUtil MAPPER configured");
    }

    private JsonUtil() {

    }

    public static class DateDeserializer extends JsonDeserializer<Date> {

        private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public Date deserialize(JsonParser p, DeserializationContext context) throws IOException {
            return Date.from(LocalDateTime.parse(p.getValueAsString(), fmt).atZone(ZoneId.ofOffset("GMT", ZoneOffset.UTC)).toInstant());
        }
    }

    public static class DateSerializer extends JsonSerializer<Date> {

        private final DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.toInstant().atZone(ZoneId.ofOffset("GMT", ZoneOffset.UTC)).format(format));
        }
    }

    public static <T> String toJson(T type) {
        try {
            return MAPPER.writeValueAsString(type);
        } catch (JsonProcessingException e) {
            log.info("JsonProcessingException(toJson): {}", e.getMessage());
        }
        return "";
    }

    public static <T> T toObject(String json, Class<T> clz) {
        try {
            return MAPPER.readValue(json.getBytes(StandardCharsets.UTF_8), clz);
        } catch (IOException e) {
            log.info("JsonProcessingException(toObject): {}", e.getMessage());
            return null;
        }
    }

    public static <T> T readForUpdate(T entity, String jsonContent) {
        try {
            return MAPPER.readerForUpdating(entity).readValue(jsonContent);
        } catch (IOException e) {
            throw new ServiceException("can not update json to entity");
        }
    }

    public static boolean isJson(String str) {
        if (StringUtil.isEmpty(str))
            return false;
        if (str.startsWith("{") && str.endsWith("}"))
            return true;
        return str.startsWith("[") && str.endsWith("]");
    }

    public static <T> List<T> toList(String json, TypeReference<List<T>> typeRef) {
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (IOException e) {
            log.info("JsonProcessingException(toList): {}", e.getMessage());
            return null;
        }
    }

    public static Object getJsonFieldValue(String json, String fieldName) {
        try {
            JSONObject jsonVal = new JSONObject(json);
            if (!jsonVal.has(fieldName))
                throw new ServiceException(fieldName + " is not present");
            return jsonVal.get(fieldName);
        } catch (JSONException je) {
            log.info("JsonProcessingException(getJsonFieldValue): {}", je.getMessage());
        }
        return null;
    }
}
