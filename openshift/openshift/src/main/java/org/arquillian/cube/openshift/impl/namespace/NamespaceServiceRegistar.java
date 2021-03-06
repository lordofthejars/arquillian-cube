package org.arquillian.cube.openshift.impl.namespace;

import org.arquillian.cube.kubernetes.api.Configuration;
import org.arquillian.cube.kubernetes.api.NamespaceService;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;

public class NamespaceServiceRegistar {

    @Inject
    private Instance<ServiceLoader> serviceLoader;

    @Inject @ApplicationScoped
    InstanceProducer<NamespaceService> namespaceService;

    public void install(@Observes(precedence = 100) Configuration configuration) {
        namespaceService.set(serviceLoader.get().onlyOne(NamespaceService.class, OpenshiftNamespaceService.class));
    }
}
