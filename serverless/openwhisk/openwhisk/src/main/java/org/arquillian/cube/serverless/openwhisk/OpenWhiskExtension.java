package org.arquillian.cube.serverless.openwhisk;

import org.arquillian.cube.serverless.openwhisk.impl.OpenWhiskApplier;
import org.arquillian.cube.serverless.openwhisk.impl.OpenWhiskClientCreator;
import org.arquillian.cube.serverless.openwhisk.impl.OpenWhiskClientResourceProvider;
import org.arquillian.cube.serverless.openwhisk.impl.OpenWhiskConfigurator;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class OpenWhiskExtension implements LoadableExtension  {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(OpenWhiskClientCreator.class)
            .observer(OpenWhiskConfigurator.class)
            .observer(OpenWhiskApplier.class)
            .service(ResourceProvider.class, OpenWhiskClientResourceProvider.class);

    }
}
