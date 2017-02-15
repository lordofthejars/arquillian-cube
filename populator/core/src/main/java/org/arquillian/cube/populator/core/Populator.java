package org.arquillian.cube.populator.core;

import org.arquillian.cube.HostIpContext;
import org.arquillian.cube.populator.spi.PopulatorService;
import org.arquillian.cube.spi.Cube;
import org.arquillian.cube.spi.CubeRegistry;
import org.arquillian.cube.spi.metadata.HasPortBindings;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;

import java.util.logging.Logger;

/**
 * This class represents base class for all Populators DSL for storing configuration.
 * It implements common operations o to configure all populators.
 *
 * @param <T> Type of service that implements operations with configured parameters.
 * @param <R> Type of custom DSLs for specific populators.
 */
public abstract class Populator<T extends PopulatorService, R extends Populator.PopulatorConfigurator> {

    private static final Logger logger = Logger.getLogger(Populator.class.getName());

    @Inject
    Instance<CubeRegistry> cubeRegistryInstance;

    @Inject
    Instance<HostIpContext> hostUriContext;

    protected T populatorService;
    protected String host;
    protected int bindPort;

    protected Populator(T populatorService) {
        this.populatorService = populatorService;
    }

    /**
     * Method that needs to be implemented that implements custom DSL methods.
     * For example in case of SQL databases some parameters like driver class or JDBC are required meanwhile in case of NoSQL you only need database name.
     * @return Class implementing {@link PopulatorConfigurator}
     */
    public abstract R createExecutor();

    /**
     * Initial method for Populators DSL
     * @param containerName of service to populate.
     * @param port exposed by the service
     * @return Next commands to configure service.
     */
    public R forContainer(String containerName, int port) {
        this.host = hostUriContext.get().getHost();
        this.bindPort = getBindingPort(containerName, port);
        return createExecutor();
    }

    private int getBindingPort(String cubeId, int exposedPort) {

        int bindPort = -1;

        final Cube cube = getCube(cubeId);

        if (cube != null) {
            final HasPortBindings portBindings = (HasPortBindings) cube.getMetadata(HasPortBindings.class);
            final HasPortBindings.PortAddress mappedAddress = portBindings.getMappedAddress(exposedPort);

            if (mappedAddress != null) {
                bindPort = mappedAddress.getPort();
            }

        }

        return bindPort;
    }

    private Cube getCube(String cubeId) {
        return cubeRegistryInstance.get().getCube(cubeId);
    }

    /**
     * Populator Configuration interface that all custom DSLs must implements.
     * It defines the DSL terminators.
     */
    public interface PopulatorConfigurator {

        /**
         * Terminator method that executes the datasets against configured service.
         */
        void execute();

        /**
         * Terminator method that clean configures service.
         */
        void clean();

    }
}
