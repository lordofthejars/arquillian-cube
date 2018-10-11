package org.arquillian.cube.serverless.openwhisk.impl;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.arquillian.cube.serverless.api.Function;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.projectodd.openwhisk.ActionOptions;
import org.projectodd.openwhisk.OWskClient;
import org.projectodd.openwhisk.invoker.ApiException;
import org.projectodd.openwhisk.model.ActionExec;

public class OpenWhiskApplier {

    private Set<String> registeredActions = new HashSet<>();

    public void applyOpenWhiskResourcesAtClassScope(@Observes(precedence = -20) BeforeClass beforeClass, final OWskClient openwhiskClient) {

        final TestClass testClass = beforeClass.getTestClass();
        final Function[] functions = findAnnotations(testClass);

        for (Function function: functions) {
            String functionLocation = function.value();
            functionLocation = RunnerExpressionParser.parseExpressions(functionLocation);
            final String functionPath = ResourceResolver.resolve(functionLocation);

            final ActionOptions actionOptions = new ActionOptions(getName(function))
                .code(functionPath)
                .kind(getKind(function.kind()))
                .web(function.web())
                .overwrite(function.overwrite());

            if (!function.main().isEmpty()) {
                actionOptions.main(function.main());
            }

            openwhiskClient.actions()
                .create(actionOptions);
            registeredActions.add(function.name());
        }


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
