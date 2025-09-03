package logic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

public class ConfigLoader {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Config load(String path) {
        try {
            return mapper.readValue(new File(path), Config.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + path, e);
        }
    }
}

