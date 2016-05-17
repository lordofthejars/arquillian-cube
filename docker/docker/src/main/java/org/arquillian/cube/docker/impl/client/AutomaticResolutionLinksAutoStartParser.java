
package org.arquillian.cube.docker.impl.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arquillian.cube.docker.impl.client.config.CubeContainer;
import org.arquillian.cube.docker.impl.client.config.DockerCompositions;
import org.arquillian.cube.docker.impl.client.config.Link;
import org.arquillian.cube.docker.impl.util.AutoStartOrderUtil;

public class AutomaticResolutionLinksAutoStartParser implements AutoStartParser {

    private List<String> deployableContainers;
    private DockerCompositions containerDefinition;

    public AutomaticResolutionLinksAutoStartParser(List<String> deployableContainers, DockerCompositions containerDefinitions) {
        this.deployableContainers = deployableContainers;
        this.containerDefinition = containerDefinitions;
    }

    @Override
    public Map<String, AutoStartOrderUtil.Node> parse() {

        Map<String, AutoStartOrderUtil.Node> nodes = new HashMap<>();

        for(String deployableContainer : this.deployableContainers) {
            CubeContainer content = containerDefinition.get(deployableContainer);
            if (content == null) {
                return nodes;
            }

                Collection<String> dependencies = content.getDependingContainers();
                for (String name : dependencies) {

                    if (containerDefinition.get(name) != null) {
                        AutoStartOrderUtil.Node child = AutoStartOrderUtil.Node.from(name);
                        nodes.put(name, child);
                    }
                }
        }

        return nodes;
    }

    @Override
    public String toString() {
        return AutoStartOrderUtil.toString(parse());
    }
}
