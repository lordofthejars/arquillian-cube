package org.arquillian.cube.serverless.openwhisk.impl;

import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.projectodd.openwhisk.Configuration;
import org.projectodd.openwhisk.OWskClient;

public class OpenWhiskClientCreator {

    @Inject
    @ApplicationScoped
    private InstanceProducer<OWskClient> openwhiskClientProducer;

    public void configure(@Observes(precedence = -10) Configuration configuration) {
        final OWskClient oWskClient = new OWskClient();
        oWskClient.configure(configuration);
        openwhiskClientProducer.set(oWskClient);
    }

}
