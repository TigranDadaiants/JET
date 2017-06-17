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

package com.github.jdtk0x5d.eve.jet.model.db;


import io.ebean.SqlRow;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public class ResultOrder {

  private Integer typeId;

  private Integer sellOrderId;
  private Integer buyOrderId;

  private Double sellPrice;
  private Double buyPrice;

  private Long sellQuantity;
  private Long buyQuantity;
  private Long buyMinQuantity;
  private Long tradeQuantity;

  private Integer buyLocation;
  private Integer sellLocation;

  private Double itemCargoVolume;
  private Double itemCargoFreeVolume;

  private Double profit;

  public ResultOrder() {
  }

  public Integer getTypeId() {
    return typeId;
  }

  public void setTypeId(Integer typeId) {
    this.typeId = typeId;
  }

  public Integer getSellOrderId() {
    return sellOrderId;
  }

  public void setSellOrderId(Integer sellOrderId) {
    this.sellOrderId = sellOrderId;
  }

  public Integer getBuyOrderId() {
    return buyOrderId;
  }

  public void setBuyOrderId(Integer buyOrderId) {
    this.buyOrderId = buyOrderId;
  }

  public Double getSellPrice() {
    return sellPrice;
  }

  public void setSellPrice(Double sellPrice) {
    this.sellPrice = sellPrice;
  }

  public Double getBuyPrice() {
    return buyPrice;
  }

  public void setBuyPrice(Double buyPrice) {
    this.buyPrice = buyPrice;
  }

  public Long getSellQuantity() {
    return sellQuantity;
  }

  public void setSellQuantity(Long sellQuantity) {
    this.sellQuantity = sellQuantity;
  }

  public Long getBuyQuantity() {
    return buyQuantity;
  }

  public void setBuyQuantity(Long buyQuantity) {
    this.buyQuantity = buyQuantity;
  }

  public Long getBuyMinQuantity() {
    return buyMinQuantity;
  }

  public void setBuyMinQuantity(Long buyMinQuantity) {
    this.buyMinQuantity = buyMinQuantity;
  }

  public Long getTradeQuantity() {
    return tradeQuantity;
  }

  public void setTradeQuantity(Long tradeQuantity) {
    this.tradeQuantity = tradeQuantity;
  }

  public Integer getBuyLocation() {
    return buyLocation;
  }

  public void setBuyLocation(Integer buyLocation) {
    this.buyLocation = buyLocation;
  }

  public Integer getSellLocation() {
    return sellLocation;
  }

  public void setSellLocation(Integer sellLocation) {
    this.sellLocation = sellLocation;
  }

  public Double getItemCargoVolume() {
    return itemCargoVolume;
  }

  public void setItemCargoVolume(Double itemCargoVolume) {
    this.itemCargoVolume = itemCargoVolume;
  }

  public Double getItemCargoFreeVolume() {
    return itemCargoFreeVolume;
  }

  public void setItemCargoFreeVolume(Double itemCargoFreeVolume) {
    this.itemCargoFreeVolume = itemCargoFreeVolume;
  }

  public Double getProfit() {
    return profit;
  }

  public void setProfit(Double profit) {
    this.profit = profit;
  }

  @Override
  public String toString() {
    return "ResultOrder{" + "typeId=[" + typeId + "], sellOrderId=[" + sellOrderId + "], buyOrderId=[" + buyOrderId + "], sellPrice=[" + sellPrice + "], buyPrice=[" + buyPrice + "], sellQuantity=[" + sellQuantity + "], buyQuantity=[" + buyQuantity + "], buyMinQuantity=[" + buyMinQuantity + "], buyLocation=[" + buyLocation + "], sellLocation=[" + sellLocation + "], itemCargoVolume=[" + itemCargoVolume + "], itemCargoFreeVolume=[" + itemCargoFreeVolume + "], profit=[" + profit + "]}";
  }
}
