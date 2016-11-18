package org.arquillian.cube.docker.impl.docker.open;

import org.arquillian.cube.docker.impl.client.Converter;
import org.arquillian.cube.docker.impl.client.config.DockerCompositions;
import org.arquillian.cube.docker.impl.docker.compose.DockerComposeEnvironmentVarResolver;
import org.arquillian.cube.docker.impl.util.ConfigUtil;
import org.arquillian.cube.impl.util.IOUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class OpenComposeConverter implements Converter {

    private final Map<String, Object> openComposeCubeDefinitionMap;
    private final Path openComposeRootDirectory;

    private OpenComposeConverter(Path location) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(location.toFile())) {
            String content = IOUtil.asStringPreservingNewLines(inputStream);
            this.openComposeCubeDefinitionMap = loadConfig(content);
            this.openComposeRootDirectory = location.getParent();
        }
    }

    private OpenComposeConverter(String content) {
        this.openComposeCubeDefinitionMap = loadConfig(content);
        this.openComposeRootDirectory = Paths.get(".");
    }

    public static OpenComposeConverter create(Path location) {
        try {
            return new OpenComposeConverter(location);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static OpenComposeConverter create(String content) {
        return new OpenComposeConverter(content);
    }

    @Override
    public DockerCompositions convert() {
        return new ContainerBuilder(this.openComposeRootDirectory).build(this.openComposeCubeDefinitionMap);
    }

    private Map<String, Object> loadConfig(String content) {
        return (Map<String, Object>) new Yaml().load(content);
    }
}
