package org.arquillian.cube.docker.impl.docker.open;

import org.arquillian.cube.docker.impl.client.config.CubeContainer;
import org.arquillian.cube.docker.impl.client.config.DockerCompositions;
import org.arquillian.cube.docker.impl.client.config.Network;
import org.arquillian.cube.docker.impl.client.config.PortBinding;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class OpenComposeConverterTest {

    private OpenComposeConverter openComposeConverter;

    @Before
    public void parse() throws URISyntaxException {
        URI openComposeDocker = OpenComposeConverterTest.class.getResource("/services-example-complete-app.yml").toURI();
        openComposeConverter = OpenComposeConverter.create(Paths.get(openComposeDocker));
    }

    @Test
    public void should_convert_services_open_compose_format() {


        final DockerCompositions dockerCompositions = openComposeConverter.convert();

        final CubeContainer frontendCubeContainer = dockerCompositions.get("frontend");
        assertThat(frontendCubeContainer, is(notNullValue()));
        assertThat(frontendCubeContainer.getImage().toString(), is("docker.io/surajd/frontend:v1"));

        final CubeContainer backendCubeContainer = dockerCompositions.get("backend");
        assertThat(backendCubeContainer, is(notNullValue()));
        assertThat(backendCubeContainer.getImage().toString(), is("docker.io/surajd/backend:v1"));

        final CubeContainer mongodbCubeContainer = dockerCompositions.get("mongodb");
        assertThat(mongodbCubeContainer, is(notNullValue()));
        assertThat(mongodbCubeContainer.getImage().toString(), is("tomaskral/mongodb-centos7"));

    }

    @Test
    public void should_convert_ports_open_compose_format() {
        final DockerCompositions dockerCompositions = openComposeConverter.convert();

        final CubeContainer frontendCubeContainer = dockerCompositions.get("frontend");
        assertThat(frontendCubeContainer, is(notNullValue()));
        Collection<PortBinding> frontendPorts = frontendCubeContainer.getPortBindings();
        assertThat(frontendPorts, containsInAnyOrder(PortBinding.valueOf("8080->8080")));

        final CubeContainer backendCubeContainer = dockerCompositions.get("backend");
        assertThat(backendCubeContainer, is(notNullValue()));
        Collection<PortBinding> backendPorts = backendCubeContainer.getPortBindings();
        assertThat(backendPorts, containsInAnyOrder(PortBinding.valueOf("3001->3000"), PortBinding.valueOf("4000->4000/udp")));

        final CubeContainer mongodbCubeContainer = dockerCompositions.get("mongodb");
        assertThat(mongodbCubeContainer, is(notNullValue()));
        Collection<PortBinding> mongodbPorts = mongodbCubeContainer.getPortBindings();
        assertThat(mongodbPorts, containsInAnyOrder(PortBinding.valueOf("27017->27017/udp")));
    }

    @Test
    public void should_convert_environment_variables_open_compose_format() {
        final DockerCompositions dockerCompositions = openComposeConverter.convert();

        final CubeContainer backendCubeContainer = dockerCompositions.get("backend");
        assertThat(backendCubeContainer, is(notNullValue()));
        final Collection<String> envs = backendCubeContainer.getEnv();
        assertThat(envs, containsInAnyOrder("MONGODB_PASSWORD=pass", "MONGODB_USER=user", "MONGODB_DATABASE=db", "MONGODB_SERVER=mongodb:27017"));
    }

    @Test
    public void should_convert_volumes_open_compose_format() {
        final DockerCompositions dockerCompositions = openComposeConverter.convert();

        final CubeContainer mongodbContainer = dockerCompositions.get("mongodb");
        assertThat(mongodbContainer, is(notNullValue()));
        final Collection<String> volumes = mongodbContainer.getVolumes();
        assertThat(volumes, containsInAnyOrder("db-store:/var/lib/mongodb/data"));
    }

    @Test
    public void should_register_to_a_default_network() {

        final DockerCompositions dockerCompositions = openComposeConverter.convert();

        final CubeContainer frontendCubeContainer = dockerCompositions.get("frontend");
        assertThat(frontendCubeContainer, is(notNullValue()));
        assertThat(frontendCubeContainer.getNetworks(), containsInAnyOrder("target_default"));

        final CubeContainer backendCubeContainer = dockerCompositions.get("backend");
        assertThat(backendCubeContainer, is(notNullValue()));
        assertThat(frontendCubeContainer.getNetworks(), containsInAnyOrder("target_default"));

        final CubeContainer mongodbCubeContainer = dockerCompositions.get("mongodb");
        assertThat(mongodbCubeContainer, is(notNullValue()));
        assertThat(frontendCubeContainer.getNetworks(), containsInAnyOrder("target_default"));

        final Network target_default = dockerCompositions.getNetwork("target_default");
        assertThat(target_default, is(notNullValue()));

    }

}
