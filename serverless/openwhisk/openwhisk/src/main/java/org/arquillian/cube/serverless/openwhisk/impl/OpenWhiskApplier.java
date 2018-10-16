package org.arquillian.cube.serverless.openwhisk.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.arquillian.cube.serverless.api.Function;
import org.arquillian.cube.serverless.core.FunctionMaterializer;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.projectodd.openwhisk.ActionOptions;
import org.projectodd.openwhisk.OWskClient;
import org.projectodd.openwhisk.invoker.ApiException;
import org.projectodd.openwhisk.model.ActionExec;

public class OpenWhiskApplier {

    private static final Logger logger = Logger.getLogger(OpenWhiskApplier.class.getName());

    private Set<String> registeredActions = new HashSet<>();

    public void applyOpenWhiskResourcesAtClassScope(@Observes(precedence = -20) BeforeClass beforeClass, final OWskClient openwhiskClient)
        throws IOException {

        final TestClass testClass = beforeClass.getTestClass();

        final FunctionMaterializer functionMaterializer = new FunctionMaterializer(testClass.getJavaClass());
        final Map<String, File> definedFunctions = functionMaterializer.materialize();

        final Function[] functions = findAnnotations(testClass);

        for (Function function: functions) {
            String functionLocation = function.value();
            functionLocation = RunnerExpressionParser.parseExpressions(functionLocation);

            String functionPath = getFunctionPath(definedFunctions, functionLocation);

            String name = getName(function);

            logger.fine(String.format("Deploying %s function located at %s", name, functionPath));

            final ActionOptions actionOptions = new ActionOptions(name)
                .code(functionPath)
                .kind(getKind(function.kind()))
                .web(function.web())
                .overwrite(function.overwrite());

            if (!function.main().isEmpty()) {
                actionOptions.main(function.main());
            }

            openwhiskClient.actions()
                .create(actionOptions);
            registeredActions.add(name);
        }

    }

    private String getFunctionPath(Map<String, File> definedFunctions, String functionLocation) {
        final String functionPath;
        if (definedFunctions.containsKey(functionLocation)) {
            functionPath = definedFunctions.get(functionLocation).getAbsolutePath();
        } else {
            functionPath = ResourceResolver.resolve(functionLocation);
        }
        return functionPath;
    }

    private ActionExec.KindEnum getKind(String kind) {
        return ActionExec.KindEnum.fromValue(kind);
    }

    private String getName(Function f) {
        if ("".equals(f.name())) {
            return Paths.get(f.value()).getFileName().toString().replace('.', '_');
        }

        return f.name();
    }

    private Function[] findAnnotations(TestClass testClass) {
        if (testClass.isAnnotationPresent(Function.class)) {
            return new Function[] {testClass.getAnnotation(Function.class)};
        } else {
            if (testClass.isAnnotationPresent(Function.List.class)) {
                return testClass.getAnnotation(Function.List.class).value();
            }
        }

        return new Function[0];
    }

    public void deleteOpenWhiskResourcesAtClassScope(@Observes(precedence = -20) AfterClass afterClassClass, final OWskClient openwhiskClient) {
        for (String action : registeredActions) {
            try {
                openwhiskClient.actions().delete(action);
            } catch (ApiException e) {
                // If error occurs during delete phase, it is ignored.
            }
        }
    }

}
