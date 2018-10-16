package org.arquillian.cube.serverless.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define a static method that returns the definition of a function.
 * This method must return a type JavaArchive (ShrinkWrap) which packages everything required by the function.
 *
 * This file is returned as a jar file
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FunctionDefinition {

    /**
     * This value must match with value set in <code>@Function</code> annotation.
     * @return identifier.
     */
    String value();

}
