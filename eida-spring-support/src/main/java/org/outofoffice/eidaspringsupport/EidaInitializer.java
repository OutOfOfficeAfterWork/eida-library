package org.outofoffice.eidaspringsupport;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outofoffice.lib.core.socket.EidaSocketClient;
import org.outofoffice.lib.context.EidaContext;
import org.outofoffice.lib.core.ui.EidaEntity;
import org.outofoffice.lib.core.ui.EidaRepository;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class EidaInitializer {

    public static void init(ConfigurableListableBeanFactory beanFactory, Class<?> mainClass) {
        log.info("Starting EidaInitialize by {}", mainClass.getSimpleName());
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

        EidaContext.init(mainClass, new EidaSocketClient());
        EidaContext.getRepositories().forEach(repository -> registerBean(registry, repository));
        log.info("EidaInitialize Finished");
    }

    private static void registerBean(BeanDefinitionRegistry registry, EidaRepository<? extends EidaEntity<?>, ?> repository) {
        String beanName = toCamelCase(repository.getClass().getSimpleName());
        log.debug("register as spring bean: {}", beanName);

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(repository.getClass());
        beanDefinition.setInstanceSupplier(() -> repository);
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private static String toCamelCase(String string) {
        String initial = string.substring(0, 1);
        return string.replaceFirst(initial, initial.toLowerCase());
    }

}
