package org.arquillian.cube.serverless.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.arquillian.cube.serverless.api.FunctionDefinition;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class FunctionMaterializer {

    private static final Logger logger = Logger.getLogger(FunctionMaterializer.class.getName());

    public static final String TEMPORARY_FOLDER_PREFIX = "arquilliancube_";
    public static final String TEMPORARY_FOLDER_SUFFIX = ".function";

    private Class<?> clazz;

    public FunctionMaterializer(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Map<String, File> materialize() throws IOException {

        final  Map<String, File> createdFuntions = new HashMap<>();

        final List<Method> methodsWithFunctionDefinition =
            ReflectionUtil.getMethodsWithAnnotation(clazz, FunctionDefinition.class);

        if (!methodsWithFunctionDefinition.isEmpty()) {
            final File temporalDirectoryForCopyingFuntions = createTemporalDirectoryForCopyingFuntions();
            logger.finer(String.format("Created %s directory for storing functions.", temporalDirectoryForCopyingFuntions));

            for (Method methodWithFunctionDefinition : methodsWithFunctionDefinition) {

                checkDefinition(methodWithFunctionDefinition);
                materializeFunction(temporalDirectoryForCopyingFuntions, methodWithFunctionDefinition, createdFuntions);

            }

        }

        return createdFuntions;

    }

    private Map<String, File> materializeFunction(File temporalDirectoryForCopyingFuntions, Method methodWithFunctionDefinition, final  Map<String, File> createdFuntions) {
        try {
            final JavaArchive archive = (JavaArchive) methodWithFunctionDefinition.invoke(null, new Object[0]);
            final FunctionDefinition annotation =
                methodWithFunctionDefinition.getAnnotation(FunctionDefinition.class);

            final String value = annotation.value();
            final File target = new File(temporalDirectoryForCopyingFuntions, archive.getName());
            archive.as(ZipExporter.class).exportTo(
                target);

            createdFuntions.put(value, target);

        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }

        return createdFuntions;
    }

    private void checkDefinition(Method methodWithFunctionDefinition) {
        boolean isMethodStatic = Modifier.isStatic(methodWithFunctionDefinition.getModifiers());
        boolean methodHasNoArguments = methodWithFunctionDefinition.getParameterCount() == 0;
        boolean methodReturnsAnArchive = JavaArchive.class.isAssignableFrom(methodWithFunctionDefinition.getReturnType());

        if (!isMethodStatic || !methodHasNoArguments || !methodReturnsAnArchive) {
            throw new IllegalArgumentException(
                String.format("Method %s annotated with %s is expected to be static, no args and return %s.",
                    methodWithFunctionDefinition, FunctionDefinition.class.getSimpleName(), JavaArchive.class.getSimpleName()));
        }
    }

    private static File createTemporalDirectoryForCopyingFuntions() throws IOException {
        File dir = File.createTempFile(TEMPORARY_FOLDER_PREFIX, TEMPORARY_FOLDER_SUFFIX);
        dir.delete();
        if (!dir.mkdirs()) {
            throw new IllegalArgumentException("Temp Dir for storing Funtions contents could not be created.");
        }
        dir.deleteOnExit();
        return dir;
    }

}
