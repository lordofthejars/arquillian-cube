package org.arquillian.cube.serverless.openwhisk;

import java.util.LinkedHashMap;
import java.util.Map;
import org.arquillian.cube.serverless.api.Function;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.projectodd.openwhisk.InvokeOptions;
import org.projectodd.openwhisk.OWskClient;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@Function(value = "file:src/test/resources/echo.jar", name = "echo", main = "org.projectodd.Echo")
public class EchoTest {

    @ArquillianResource
    OWskClient client;

    @Test
    public void should_invoke_echo() {

        Map<String, Object> params = mapOf("test", "hello");

        Map<String, Map<String, Object>> result = client.actions().invoke(new InvokeOptions("echo")
            .blocking(true)
            .results(true)
            .parameters(params));

        assertThat(result)
            .containsEntry("echoed", mapOf("test", "hello"));

    }

    protected Map<String, Object> mapOf(final String... strings) {
        final Map<String, Object> map = new LinkedHashMap<>();
        for (int index = 0; index < strings.length - 1; index += 2) {
            map.put(strings[index], strings[index + 1]);
        }
        return map;
    }

}
