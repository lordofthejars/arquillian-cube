package org.arquillian.cube.serverless.openwhisk.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResourceResolver {

    static final String FILE_PREFIX = "file:";

    private static File createTemporalDefinition(String content) throws IOException {
        // In case it is not a http, file nor a classpath protocol,
        // we assume that this is plain text. We store it in a temporary
        // file and return the URL to it.
        File tmp = File.createTempFile("arquillian-cube", ".res");

        // Remove the temporary file after running the test
        tmp.deleteOnExit();

        // Write content to temporary file
        BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));
        writer.write(content);
        writer.close();

        return tmp;
    }

    public static String resolve(String content) {
        try {
            if (content.startsWith(FILE_PREFIX)) {
                return content.substring(FILE_PREFIX.length());
            } else {
                File tmp = ResourceResolver.createTemporalDefinition(content);
                return tmp.getAbsolutePath();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
