package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 

/**
 * JSON Helper class
 */
public class JsonUtil {
    private static ObjectMapper mapper = new ObjectMapper();

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
}
