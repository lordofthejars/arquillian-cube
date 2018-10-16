package org.arquillian.cube.serverless.code;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.arquillian.cube.serverless.api.FunctionDefinition;
import org.arquillian.cube.serverless.core.FunctionMaterializer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionMaterializerTest {


    @Test
    public void should_materialize_a_function() throws IOException {

        // Given
        FunctionMaterializer functionMaterializer = new FunctionMaterializer(DefinedFunction.class);

        // When
        final Map<String, File> functions = functionMaterializer.materialize();

        // Then
        assertThat(functions)
            .containsKey("xxx");

        File createdFile = functions.get("xxx");
        assertThat(createdFile).exists();

    }


    public static class DefinedFunction {

        @FunctionDefinition("xxx")
        public static JavaArchive createFunction() {
            return ShrinkWrap.create(JavaArchive.class)
                .addClass(String.class);
        }

    }

}
