package org.arquillian.cube.serverless.openwhisk;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * echo Echo
 */
public class Echo {
   public static JsonObject main(JsonObject args) {
        JsonObject response = new JsonObject();
        final List<String> entries = args.entrySet().stream()
            .map(entry -> entry.getKey())
            .filter(key -> key.startsWith("__ow_"))
            .collect(Collectors.toList());
        entries.forEach(args::remove);

        response.add("echoed", args);
        return response;
    }
}
