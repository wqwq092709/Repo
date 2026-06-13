package com.sky.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 修复 Springfox 3.0.0 在 Spring Boot 2.7.x 下的 NPE 问题
 * 过滤掉 PatternsRequestCondition 为 null 的 handler mappings
 */
@Component
public class SpringfoxBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof WebMvcRequestHandlerProvider) {
            customizeSpringfoxHandlerMappings(bean);
        }
        return bean;
    }

    private void customizeSpringfoxHandlerMappings(Object bean) {
        try {
            Field handlerMappingsField = WebMvcRequestHandlerProvider.class
                    .getDeclaredField("handlerMappings");
            handlerMappingsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<RequestMappingInfoHandlerMapping> handlerMappings =
                    (List<RequestMappingInfoHandlerMapping>) handlerMappingsField.get(bean);

            List<RequestMappingInfoHandlerMapping> filtered = handlerMappings.stream()
                    .filter(mapping -> mapping.getPatternParser() == null)
                    .collect(Collectors.toList());
            handlerMappingsField.set(bean, filtered);
        } catch (Exception e) {
            // ignore
        }
    }
}
