package org.tastefuljava.tools.jinja.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public class YamlFile extends AbstractFileSource {

    @Override
    public Map<String, Object> loadValues(File srcDir) throws MojoExecutionException {
        File file = getFile(srcDir);
        try (InputStream stream = new FileInputStream(file)) {
            LoadSettings settings = LoadSettings.builder().build();
            Load load = new Load(settings);
            return (Map<String, Object>)load.loadFromInputStream(stream);
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error reading yaml from file: " + file, ex);
        }
    }
}
