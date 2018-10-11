package org.arquillian.cube.serverless.openwhisk.impl;

import java.lang.annotation.Annotation;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.projectodd.openwhisk.OWskClient;

public class OpenWhiskClientResourceProvider implements ResourceProvider {

    @Inject
    Instance<OWskClient> openwhiskClientInstance;

    @Override
    public boolean canProvide(Class<?> type) {
        return OWskClient.class.isAssignableFrom(type);
    }

    @Override
    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        return openwhiskClientInstance.get();
    }
}
