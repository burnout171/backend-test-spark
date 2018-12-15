package backend.test.spark.util;

import backend.test.spark.exception.InternalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JsonUtils {

    private final ObjectMapper objectMapper;

    public JsonUtils() {
        this.objectMapper = new ObjectMapper();
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new InternalException(e);
        }
    }

    public <T> String toJson(T object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new InternalException(e);
        }
    }
}
