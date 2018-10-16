package org.arquillian.cube.serverless.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

    public static List<Method> getMethodsWithAnnotation(final Class<?> source,
        final Class<? extends Annotation> annotationClass) {
        List<Method> declaredAccessableMethods = AccessController
            .doPrivileged(new PrivilegedAction<List<Method>>() {
                public List<Method> run() {
                    List<Method> foundMethods = new ArrayList<Method>();
                    for (Method method : source.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(annotationClass)) {
                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }
                            foundMethods.add(method);
                        }
                    }
                    return foundMethods;
                }
            });
        return declaredAccessableMethods;
    }

}
