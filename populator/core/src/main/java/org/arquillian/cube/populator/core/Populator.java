package org.arquillian.cube.populator.core;

import org.arquillian.cube.HostIpContext;
import org.arquillian.cube.populator.spi.PopulatorService;
import org.arquillian.cube.spi.Cube;
import org.arquillian.cube.spi.CubeRegistry;
import org.arquillian.cube.spi.metadata.HasPortBindings;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;

import java.util.logging.Logger;

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

   public abstract R createExecutor();

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

   public interface PopulatorConfigurator {

      void execute();
      void clean();

   }
}
