/*
 * Copyright 2018 Tigran Dadaiants
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

package com.github.tddts.evetrader.consts;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public enum RouteOption {

  SHORTEST(-1, "shortest"),

  SECURE(0.5, "secure"),

  INSECURE(-1, "insecure");

  private final double security;
  private final String value;

  RouteOption(double security, String value) {
    this.security = security;
    this.value = value;
  }

  public double getSecurity() {
    return security;
  }

  public String getValue() {
    return value;
  }
}
