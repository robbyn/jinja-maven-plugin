package org.tastefuljava.tools.jinja.maven.plugin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jinjava.Jinjava;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public class Rendering {
    private static final String JINJA_EXT = ".j2";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE
            = new TypeReference<Map<String, Object>>(){};

    private String templateFile;
    private String outputFile;
    private String json;
    private String jsonFile;
    private String yaml;
    private String yamlFile;

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getJsonFile() {
        return jsonFile;
    }

    public void setJsonFile(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    public String getYaml() {
        return yaml;
    }

    public void setYaml(String yaml) {
        this.yaml = yaml;
    }

    public String getYamlFile() {
        return yamlFile;
    }

    public void setYamlFile(String yamlFile) {
        this.yamlFile = yamlFile;
    }

    public Map<String, Object> buildContext(
            Map<String, Object> parentContext,
            File srcDir) throws MojoExecutionException {
        Map<String,Object> context = new HashMap<>();
        if (parentContext != null) {
            context.putAll(parentContext);
        }
        if (jsonFile != null) {
            context.putAll(loadValuesFromJsonFile(
                    new File(srcDir, jsonFile)));
        }
        if (json != null) {
            context.putAll(loadValuesFromJson(json));
        }
        if (yamlFile != null) {
            context.putAll(loadValuesFromYamlFile(
                    new File(srcDir, yamlFile)));
        }
        if (yaml != null) {
            context.putAll(loadValuesFromYaml(yaml));
        }
        return context;
    }

    public void render(Jinjava jinja, File outDir, Map<String, Object> context)
            throws MojoExecutionException {
        try {
            String temp = templateFile;
            String template = jinja.getResourceLocator().getString(
                    templateFile, UTF_8, null);
            String output = jinja.render(template, context);
            String outFile = outputFile;
            if (outFile == null) {
                if (temp.endsWith(JINJA_EXT)) {
                    outFile = temp.substring(
                            0, temp.length()-JINJA_EXT.length());
                } else {
                    outFile = temp;
                }
            }
            writeTextToFile(output, new File(outDir, outFile));
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error rendering from template: " + templateFile, ex);
        }
    }

    private Map<String, Object> loadValuesFromJsonFile(File file)
            throws MojoExecutionException {
        try {
            return mapper.readValue(file, MAP_TYPE);
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error reading values from file: " + file, ex);
        }
    }

    private Map<String, Object> loadValuesFromJson(String json)
            throws MojoExecutionException {
        try {
            return mapper.readValue(json, MAP_TYPE);
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error reading values from JSon", ex);
        }
    }

    private Map<String, Object> loadValuesFromYamlFile(File file)
            throws MojoExecutionException {
        try (InputStream stream = new FileInputStream(file)) {
            LoadSettings settings = LoadSettings.builder().build();
            Load load = new Load(settings);
            return (Map<String, Object>)load.loadFromInputStream(stream);
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error reading yaml from file: " + file, ex);
        }
    }

    private Map<String, Object> loadValuesFromYaml(String yaml)
            throws MojoExecutionException {
        LoadSettings settings = LoadSettings.builder().build();
        Load load = new Load(settings);
        return (Map<String, Object>)load.loadFromString(yaml);
    }

    private String loadTextFromFile(File file) throws MojoExecutionException {
        try (InputStream stream = new FileInputStream(file);
                    Reader reader = new InputStreamReader(stream, UTF_8);
                    BufferedReader in = new BufferedReader(reader)) {
            StringBuilder buf = new StringBuilder();
            String s = in.readLine();
            while (s != null) {
                buf.append(s);
                buf.append('\n');
                s = in.readLine();
            }
            return buf.toString();
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error reading text from file: " + file, ex);
        }
    }

    private void writeTextToFile(String text, File file)
            throws MojoExecutionException {
        File parent = file.getParentFile();
        if (parent != null) {
            if (!parent.mkdirs()) {
                throw new MojoExecutionException(
                    "Error creating directory: " + parent);
            }
        }
        try (Reader reader = new StringReader(text);
                    BufferedReader in = new BufferedReader(reader);
                    OutputStream stream = new FileOutputStream(file);
                    Writer writer = new OutputStreamWriter(stream, UTF_8);
                    PrintWriter out = new PrintWriter(writer)) {
                String s = in.readLine();
                while (s != null) {
                    out.println(s);
                    s = in.readLine();
                }
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Error writing text to file: " + file, ex);
        }
    }
}
