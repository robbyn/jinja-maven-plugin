package org.tastefuljava.tools.jinja.maven.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jinjava.Jinjava;
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
    public void putValues(
            Jinjava jinja, Map<String, Object> context, File srcDir)
            throws MojoExecutionException {
        File file = new File(srcDir, fileName);
        try {
            ObjectMapper mapper = new ObjectMapper();
            context.putAll(mapper.readValue(file, Json.MAP_TYPE));
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error reading values from file: " + file, ex);
        }
    }
}
