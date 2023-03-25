package org.tastefuljava.tools.jinja.maven.plugin;

import com.hubspot.jinjava.Jinjava;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public class YamlFile implements ValueSource {
    private String fileName;

    public void set(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void putValues(
            Jinjava jinja, Map<String, Object> context, File srcDir)
            throws MojoExecutionException {
        File file = new File(srcDir, fileName);
        try (InputStream stream = new FileInputStream(file)) {
            LoadSettings settings = LoadSettings.builder().build();
            Load load = new Load(settings);
            Map<String, Object> values
                    = (Map<String, Object>)load.loadFromInputStream(stream);
            context.putAll(values);
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error reading yaml from file: " + file, ex);
        }
    }
}
