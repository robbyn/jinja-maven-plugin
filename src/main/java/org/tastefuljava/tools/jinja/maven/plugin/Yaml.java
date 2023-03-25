package org.tastefuljava.tools.jinja.maven.plugin;

import com.hubspot.jinjava.Jinjava;
import java.io.File;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public class Yaml implements ValueSource {
    private String yaml;

    public void set(String yaml) {
        this.yaml = yaml;
    }

    @Override
    public void putValues(
            Jinjava jinja, Map<String, Object> context, File srcDir)
            throws MojoExecutionException {
        LoadSettings settings = LoadSettings.builder().build();
        Load load = new Load(settings);
        Map<String, Object> values
                = (Map<String, Object>)load.loadFromString(yaml);
        context.putAll(values);
    }
}
