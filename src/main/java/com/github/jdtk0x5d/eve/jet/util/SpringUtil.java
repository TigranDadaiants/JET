/*
 * Copyright 2017 Tigran Dadaiants
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jdtk0x5d.eve.jet.util;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanInitializationException;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public class SpringUtil {

  public static Pair<Class<?>, Object> checkForDinamicProxy(Object bean) throws BeanInitializationException{
    try {
      Class<?> type = bean.getClass();
      if (AopUtils.isJdkDynamicProxy(bean)) {
        Advised advised = (Advised) bean;
        type = advised.getTargetClass();
        bean = advised.getTargetSource().getTarget();
      }
      return Pair.of(type, bean);
    } catch (Exception e) {
      throw new BeanInitializationException(e.getMessage(), e);
    }
  }



}
