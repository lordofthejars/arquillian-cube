package org.arquillian.cube.serverless.openwhisk;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
@Function(value = "file:src/test/resources/hello.js", name = "splitsville", kind = "nodejs:6")
public class SplitTest {


    @ArquillianResource
    OWskClient client;

    @Test
    public void should_invoke_echo() {

        final String sentence = "I'm a simple sentence.";
        final Map<String, Object> words = mapOf("words", sentence);

        final Map<String, List<String>> result = client.actions().invoke(new InvokeOptions("splitsville")
            .parameters(words)
            .blocking(true)
            .results(true));

        assertThat(result)
            .containsEntry("js-result", Arrays.asList("I'm", "a", "simple", "sentence."));

    }

    protected Map<String, Object> mapOf(final String... strings) {
        final Map<String, Object> map = new LinkedHashMap<>();
        for (int index = 0; index < strings.length - 1; index += 2) {
            map.put(strings[index], strings[index + 1]);
        }
        return map;
    }

}
