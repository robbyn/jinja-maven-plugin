package org.tastefuljava.tools.jinja.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.plugin.MojoExecutionException;

public class PropertiesFile extends AbstractFileSource {

    @Override
    public Map<String, Object> loadValues(File srcDir) throws MojoExecutionException {
        File file = getFile(srcDir);
        try (InputStream stream = new FileInputStream(file)) {
            Properties props = new Properties();
            props.load(stream);
            Map<String,Object> result = new HashMap<>();
            props.forEach((key,value) -> {
                result.put(Util.varName((String)key), value);
            });
            return result;
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error reading properties from file: " + file, ex);
        }
    }
}
