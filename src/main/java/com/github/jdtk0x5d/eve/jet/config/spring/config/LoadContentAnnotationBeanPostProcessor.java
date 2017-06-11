package com.github.jdtk0x5d.eve.jet.config.spring.config;

import com.github.jdtk0x5d.eve.jet.config.spring.annotations.LoadContent;
import com.github.jdtk0x5d.eve.jet.util.SpringUtil;
import com.github.jdtk0x5d.eve.jet.util.Util;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public class LoadContentAnnotationBeanPostProcessor implements BeanPostProcessor {

  @Resource(name = "applicationProperties")
  private Properties properties;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    Pair<Class<?>, Object> typeObjectPair = SpringUtil.checkForDinamicProxy(bean);
    Class<?> type = typeObjectPair.getLeft();
    Object target = typeObjectPair.getRight();

    try {

      for (Field field : type.getDeclaredFields()) {
        if (field.isAnnotationPresent(LoadContent.class) && String.class.equals(field.getType())) {

          field.setAccessible(true);
          LoadContent annotation = field.getAnnotation(LoadContent.class);

          String fileName =
              annotation.value().isEmpty() ? (String) field.get(target) :
                  annotation.property() ? properties.getProperty(annotation.value()) : annotation.value();

          if (fileName != null && !fileName.isEmpty()) {
            field.set(target, Util.loadContent(fileName));
          }
        }
      }

    } catch (Exception e) {
      throw new BeanCreationException(e.getMessage(), e);
    }

    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }
}
