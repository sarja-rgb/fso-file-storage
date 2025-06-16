package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON Helper class
 */
public class JsonUtil {
    private final static ObjectMapper mapper = new ObjectMapper();

    private JsonUtil(){}

    /**
     * Convert Object to Json string
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String objectToJson(Object object) throws JsonProcessingException{
        return mapper.writeValueAsString(object);
    }

    /**
     * Convert JSON string content to Object type
     * @param <T>
     * @param json
     * @param valueType
     * @return
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public static <T> T jsonToObject(String json, Class<T> valueType) throws JsonMappingException,
     JsonProcessingException {
        return mapper.readValue(json, valueType);
    }
}
