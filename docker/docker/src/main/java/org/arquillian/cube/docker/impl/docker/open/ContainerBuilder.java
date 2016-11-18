package org.arquillian.cube.docker.impl.docker.open;

import org.arquillian.cube.docker.impl.client.config.CubeContainer;
import org.arquillian.cube.docker.impl.client.config.DockerCompositions;
import org.arquillian.cube.docker.impl.client.config.Image;
import org.arquillian.cube.docker.impl.client.config.Network;
import org.arquillian.cube.docker.impl.client.config.PortBinding;
import org.arquillian.cube.docker.impl.docker.compose.NetworkBuilder;
import org.arquillian.cube.docker.impl.util.YamlUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.arquillian.cube.docker.impl.util.YamlUtil.asListOfString;
import static org.arquillian.cube.docker.impl.util.YamlUtil.asMap;

public class ContainerBuilder {

    private static final String SERVICES = "services";
    private static final String TYPE = "type";
    private static final String IMAGE = "image";
    private static final String CONTAINER_NAME = "container_name";
    private static final String COMMAND = "command";
    private static final String ENVIRONMENT = "environment";
    private static final String PORTS = "ports";
    private static final String VOLUMES = "volumes";
    private static final String SIZE = "size";
    private static final String MODE = "mode";

    private static final String NETWORK_NAME_SUFFIX = "_default";

    private Path openComposeDirectory;

    public ContainerBuilder(Path openComposeDirectory) {
        this.openComposeDirectory = openComposeDirectory;
    }

    public DockerCompositions build(Map<String, Object> openComposeContainerDefinition) {
        if (openComposeContainerDefinition.containsKey(SERVICES)) {

            final String defaultNetworkName = getDefaultNetworkName();
            final DockerCompositions dockerCompositions = new DockerCompositions();
            final Map<String, Object> services = asMap(openComposeContainerDefinition, SERVICES);

            final Set<String> serviceNames = services.keySet();

            for (String serviceName : serviceNames) {
                final Map<String, Object> serviceConfiguration = asMap(services, serviceName);
                final CubeContainer cubeContainer = new CubeContainer();

                if (serviceConfiguration.containsKey(IMAGE)) {
                    cubeContainer.setImage(Image.valueOf(YamlUtil.asString(serviceConfiguration, IMAGE)));
                }

                if (serviceConfiguration.containsKey(CONTAINER_NAME)) {
                    cubeContainer.setContainerName(YamlUtil.asString(serviceConfiguration, CONTAINER_NAME));
                }

                if (serviceConfiguration.containsKey(COMMAND)) {
                    cubeContainer.setCmd(asListOfString(serviceConfiguration, COMMAND));
                }

                if (serviceConfiguration.containsKey(ENVIRONMENT)) {
                    final Collection<String> environment = toEnvironment(asMap(serviceConfiguration, ENVIRONMENT));
                    if (cubeContainer.getEnv() != null) {
                        cubeContainer.getEnv().addAll(environment);
                    } else {
                        cubeContainer.setEnv(environment);
                    }
                }

                if (serviceConfiguration.containsKey(PORTS)) {
                    final Collection<PortBinding> portBindings = toPorts(asListOfString(serviceConfiguration, PORTS));
                    if (cubeContainer.getPortBindings() != null) {
                        cubeContainer.getPortBindings().addAll(portBindings);
                    } else {
                        cubeContainer.setPortBindings(portBindings);
                    }
                }

                if (serviceConfiguration.containsKey(VOLUMES)) {
                    final Collection<String> volumes = asListOfString(serviceConfiguration, VOLUMES);
                    if (cubeContainer.getVolumes() != null) {
                        Collection<String> oldVolumes = cubeContainer.getVolumes();
                        oldVolumes.addAll(volumes);
                    } else {
                        cubeContainer.setVolumes(new HashSet<String>(volumes));
                    }
                }

                // Since there is no networks sections we register to the default one.

                cubeContainer.setNetworks(Arrays.asList(defaultNetworkName));
                cubeContainer.setNetworkMode(defaultNetworkName);

                dockerCompositions.add(serviceName, cubeContainer);
            }

            NetworkBuilder networkBuilder = new NetworkBuilder();
            Network network = networkBuilder.withDefaultDriver().build();
            dockerCompositions.add(defaultNetworkName, network);

            return dockerCompositions;
        } else {
            throw new IllegalArgumentException(String.format("Open Compose format should have at least one %s element.", SERVICES));
        }
    }

    private String getDefaultNetworkName() {
        return this.openComposeDirectory.toFile()
                .getAbsoluteFile()
                .getParentFile()
                .getName() + NETWORK_NAME_SUFFIX;
    }

    private Collection<PortBinding> toPorts(Collection<?> ports) {
        Collection<PortBinding> listOfPorts = new HashSet<>();
        for (Object portObject : ports) {
            // It can be an integer or a string
            String port = portObject.toString();
            String[] elements = port.split(":");
            switch (elements.length) {
                case 1: {
                    //same host port
                    listOfPorts.add(PortBinding.valueOf(elements[0] + "->" + elements[0]));
                    break;
                }
                case 2: {
                    try {
                        //hostport:containerport
                        Integer.parseInt(elements[0]);
                        listOfPorts.add(PortBinding.valueOf(port.replaceAll(":", "->")));
                    } catch (NumberFormatException e) {
                        //protocol:containerport
                        listOfPorts.add(PortBinding.valueOf(elements[1] + "->" + elements[1] + "/"+ elements[0]));
                    }
                    break;
                }
                case 3: {
                    //protocol:hostport:containerport
                    listOfPorts.add(PortBinding.valueOf(elements[1] + "->" + elements[2] + "/"+ elements[0]));
                    break;
                }
            }
        }

        return listOfPorts;
    }

    private Collection<String> toEnvironment(Map<String, Object> serviceEnvironment) {
        List<String> envs = new ArrayList<>();
        for (Map.Entry<String, Object> envEntry : serviceEnvironment.entrySet()) {
            envs.add(toEnv(envEntry));
        }

        return envs;
    }

    private String toEnv(Map.Entry<String, Object> envEntry) {
        return String.format("%s=%s", envEntry.getKey(), envEntry.getValue());
    }

}
