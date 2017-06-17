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

package com.github.jdtk0x5d.eve.jet.consts;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public enum DotlanRouteOption {

  FASTEST("", -1),
  HIGH_SEC("2", 0.5),
  LOW_SEC("3", 0);

  private final String value;
  private final double security;

  DotlanRouteOption(String value, double security) {
    this.value = value;
    this.security = security;
  }

  public String getValue() {
    return value;
  }

  public double getSecurity() {
    return security;
  }
}
