package org.tastefuljava.tools.jinja.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;

public class Properties extends AbstractValueSource {
    @Override
    public Map<String, Object> loadValues(File srcDir) throws MojoExecutionException {
        try (Reader reader = new StringReader(getSource())) {
            java.util.Properties props = new java.util.Properties();
            props.load(reader);
            Map<String,Object> result = new HashMap<>();
            props.forEach((key,value) -> {
                result.put(Util.varName((String)key), value);
            });
            return result;
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error reading properties", ex);
        }
    }
}
