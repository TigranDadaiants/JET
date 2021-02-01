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

package com.github.tddts.evetrader.model.client.esi.universe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@code UniverseType} represents a universal type object from OpenAPI for EVE Online.
 *
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniverseType {

  private DogmaEffect[] dogma_effects;

  private String group_id;

  private String portion_size;

  private String type_id;

  private String mass;

  private String graphic_id;

  private String description;

  private String name;

  private String volume;

  private String capacity;

  private DogmaAttribute[] dogma_attributes;

  private String radius;

  private String published;
}
