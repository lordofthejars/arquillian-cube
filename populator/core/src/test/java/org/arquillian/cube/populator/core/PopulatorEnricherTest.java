package org.arquillian.cube.populator.core;

import org.arquillian.cube.populator.spi.PopulatorService;
import org.assertj.core.api.Assertions;
import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PopulatorEnricherTest {

   @Mock
   ServiceLoader serviceLoader;

   @Mock
   Injector injector;

   @Before
   public void setupMocks() {
      PopulatorService populatorService = new TestPopulatorService();
      when(injector.inject(any())).thenAnswer(element -> element.getArgumentAt(0, Populator.class));
      when(serviceLoader.all(PopulatorService.class)).thenReturn(Arrays.asList(populatorService));
   }

   @Test
   public void should_create_populator_with_configured_annotation() throws NoSuchFieldException {
      MyPopulatorEnricher populatorEnricher = new MyPopulatorEnricher();
      populatorEnricher.serviceLoaderInstance = () -> serviceLoader;
      populatorEnricher.injectorInstance = () -> injector;

      final Object populator = populatorEnricher.lookup(null, new Annotation() {
         @Override
         public Class<? extends Annotation> annotationType() {
            return MyBackend.class;
         }
      });

      assertThat(populator).isInstanceOf(MyPopulator.class);
      assertThat(((Populator)populator).populatorService).isInstanceOf(TestPopulatorService.class);

   }

   public static class TestPopulatorService implements PopulatorService<MyBackend> {

      @Override
      public Class<MyBackend> getPopulatorAnnotation() {
         return MyBackend.class;
      }
   }

}


