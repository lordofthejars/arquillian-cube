package org.arquillian.cube.populator.postman;

import io.restassured.builder.RequestSpecBuilder;
import org.arquillian.cube.DockerUrl;
import org.arquillian.cube.docker.impl.requirement.RequiresDockerMachine;
import org.arquillian.cube.populator.rest.api.RestPopulator;
import org.arquillian.cube.populator.rest.postman.Postman;
import org.arquillian.cube.requirement.ArquillianConditionalRunner;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@RunWith(ArquillianConditionalRunner.class)
@RequiresDockerMachine(name = "dev")
public class PostmanTest {

    @ArquillianResource
    @DockerUrl(containerName = "messenger", exposedPort = 8080)
    RequestSpecBuilder requestSpecBuilder;

    @ArquillianResource
    @Postman
    RestPopulator populator;

    @Test
    public void should_get_messages() {

        populator.forContainer("messenger", 8080)
                .usingDataSets("/message.json")
                .execute();

        given()
                .spec(requestSpecBuilder.build())
                .when()
                .get("/message")
                .then()
                .assertThat().body(is("Hello From Populator Test"));

    }

}
