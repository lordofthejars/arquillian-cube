package org.arquillian.cube.serverless.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define the function
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE,})
public @interface Function {

    /**
     * Location of the function. If starts with file: the file is search in given file location.
     * If not then it is considered as directly the content of the function.
     *
     * This value accepts <code>${var:default}</code> format where <code>var</code> can be set in system properties or environment variables level.
     * @return Location of function to deploy.
     */
    String value();

    /**
     * Name of the action. If not set the name is taken from the name of deployed file, replacing dots (.) to underlines (_)
     * @return name of function.
     */
    String name() default "";

    /**
     * The language of the function. If implementation does not need this information it can be ignored.
     * @return Function language.
     */
    String kind() default "java";

    /**
     * Main class location. If no main class (i.e JS) then you can leave it as blank.
     * @return Fully qualified class of Main.
     */
    String main() default "";

    /**
     * Set if function should be available in web protocol or not.
     * @return
     */
    boolean web() default false;

    /**
     * If function is already deployed, if it should be overwritten.
     * @return
     */
    boolean overwrite() default false;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @interface List {
        Function[] value();
    }

}
