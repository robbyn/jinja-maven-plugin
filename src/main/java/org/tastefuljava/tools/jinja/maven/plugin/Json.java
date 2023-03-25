package org.tastefuljava.tools.jinja.maven.plugin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;

public class Json implements ValueSource {
    public static final TypeReference<Map<String, Object>> MAP_TYPE
            = new TypeReference<Map<String, Object>>(){};

    private String json;

    public void set(String json) {
        this.json = json;
    }

    @Override
    public Map<String, Object> loadValues(File srcDir)
            throws MojoExecutionException {
        try {
            return new ObjectMapper().readValue(json, MAP_TYPE);
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error reading values from JSon", ex);
        }
    }
}
