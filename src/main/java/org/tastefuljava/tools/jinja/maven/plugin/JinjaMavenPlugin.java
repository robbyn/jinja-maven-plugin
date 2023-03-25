package org.tastefuljava.tools.jinja.maven.plugin;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.loader.CascadingResourceLocator;
import com.hubspot.jinjava.loader.ClasspathResourceLocator;
import com.hubspot.jinjava.loader.FileLocator;
import com.hubspot.jinjava.loader.ResourceLocator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class JinjaMavenPlugin extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(required=false)
    private List<ValueSource> values;

    @Parameter(required = true)
    private List<Rendering> renderings;

    @Parameter(property = "gina.jinja.templateDirectory",
            defaultValue = "${basedir}/src/main/template")
    private File templateDirectory;

    @Parameter(property = "gina.jinja.dataDirectory",
            defaultValue = "${basedir}/src/main/data")
    private File dataDirectory;

    @Parameter(property = "javacc.outputDirectory",
            defaultValue = "${project.build.directory}/generated-resources/jinja")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Jinja classpath: " + System.getProperty("java.class.path"));
        JinjavaConfig jc
                = JinjavaConfig.newBuilder()
//                      .withFailOnUnknownTokens(failOnMissingValues)
                        .build();
        Jinjava jinja = new Jinjava(jc);
            jinja.setResourceLocator(new CascadingResourceLocator(
                    fileLocator(templateDirectory),
                    new ClasspathResourceLocator()));
        Map<String, Object> context = new HashMap<>();
        if (values != null) {
            for (ValueSource vs: values) {
                vs.putValues(jinja, context, dataDirectory);
            }
        }
        for (Rendering rendering: renderings) {
            rendering.render(jinja, dataDirectory, outputDirectory, context);
        }
        if (outputDirectory.isDirectory()) {
            addResourceDir(outputDirectory);
        }
    }

    private void addResourceDir(File dir) {
        Resource resource = new Resource();
        resource.setDirectory(dir.getAbsolutePath());
        project.addResource(resource);
    }

    private ResourceLocator fileLocator(File dir)
            throws MojoExecutionException {
        try {
            return new FileLocator(dir);
        } catch (FileNotFoundException ex) {
            throw new MojoExecutionException(
                "Error creating file locator for directory: " + dir, ex);
        }
    }
}
