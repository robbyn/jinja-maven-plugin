package org.tastefuljava.tools.jinja.maven.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;

public class JsonFile implements ValueSource {
    private String fileName;

    public void set(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Map<String, Object> loadValues(File srcDir) throws MojoExecutionException {
        File file = new File(srcDir, fileName);
        try {
            return new ObjectMapper().readValue(file, Json.MAP_TYPE);
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error reading values from file: " + file, ex);
        }
    }
}
