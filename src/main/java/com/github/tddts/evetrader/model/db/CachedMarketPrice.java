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

package com.github.tddts.evetrader.model.db;

import com.github.tddts.evetrader.model.client.esi.market.MarketPrice;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * {@code CachedMarketPrice} represents a market price cached in database.
 *
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
@Table
@Entity
@Getter
@Setter
@ToString
public class CachedMarketPrice {

  @Column
  private double averagePrice;
  @Column
  private double adjustedPrice;
  @Id
  @Column(unique = true)
  private double typeId;

  public CachedMarketPrice() {
  }

  public CachedMarketPrice(MarketPrice marketPrice) {
    averagePrice = marketPrice.getAverage_price();
    averagePrice = marketPrice.getAdjusted_price();
    typeId = marketPrice.getType_id();
  }

  public CachedMarketPrice(double averagePrice, double adjustedPrice, double typeId) {
    this.averagePrice = averagePrice;
    this.adjustedPrice = adjustedPrice;
    this.typeId = typeId;
  }
}
