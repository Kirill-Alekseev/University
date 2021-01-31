package ua.com.foxminded.university.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static byte[] toJson(Object object) throws JsonProcessingException {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectWriter objectWriter = objectMapper.writer();

        return objectWriter.writeValueAsBytes(object);
    }

    public static String toJsonString(Object object) throws JsonProcessingException {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

        return objectWriter.writeValueAsString(object);
    }

    public static <T> T fromJson(String contentAsString, TypeReference<T> valueType) throws JsonProcessingException {
        return new ObjectMapper().readValue(contentAsString, valueType);
    }

    public static <T> T fromJson(String contentAsString, Class<T> valueType) throws JsonProcessingException {
        return new ObjectMapper().readValue(contentAsString, valueType);
    }
}

