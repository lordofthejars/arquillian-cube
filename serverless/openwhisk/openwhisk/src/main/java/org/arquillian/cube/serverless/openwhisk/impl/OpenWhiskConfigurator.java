package org.arquillian.cube.serverless.openwhisk.impl;

import java.util.Map;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.projectodd.openwhisk.Configuration;

public class OpenWhiskConfigurator {

    @Inject
    @ApplicationScoped
    private InstanceProducer<Configuration> openwhiskConfigurationProducer;

    public void configure(@Observes(precedence = -10) ArquillianDescriptor arquillianDescriptor) {

        final Map<String, String> config = arquillianDescriptor.extension("openwhisk").getExtensionProperties();

        final Configuration.Builder builder = new Configuration.Builder();

        if (config.containsKey("host")) {
            builder.host(config.get("host"));
        }

        if (config.containsKey("debugging")) {
            builder.debugging(Boolean.parseBoolean(config.get("debugging")));
        }

        if (config.containsKey("insecure")) {
            builder.insecure(Boolean.parseBoolean(config.get("insecure")));
        }

        if (config.containsKey("port")) {
            builder.port(Integer.parseInt(config.get("port")));
        }

        if (config.containsKey("timeout")) {
            builder.timeout(Integer.parseInt(config.get("timeout")));
        }

        if (config.containsKey("auth")) {
            builder.auth(config.get("auth"));
        }

        if (config.containsKey("namespace")) {
            builder.namespace(config.get("namespace"));
        }

        if (config.containsKey("actionPackage")) {
            builder.actionPackage(config.get("actionPackage"));
        }

        openwhiskConfigurationProducer.set(builder.build());

    }


}
