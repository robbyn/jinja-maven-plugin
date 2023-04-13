package org.tastefuljava.tools.jinja.maven.plugin;

import java.io.File;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public class Yaml extends AbstractValueSource {
    @Override
    public Map<String, Object> loadValues(File srcDir)
            throws MojoExecutionException {
        LoadSettings settings = LoadSettings.builder().build();
        Load load = new Load(settings);
        return (Map<String, Object>)load.loadFromString(getSource());
    }
}
