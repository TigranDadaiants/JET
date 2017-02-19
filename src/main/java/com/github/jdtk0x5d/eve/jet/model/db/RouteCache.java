package com.github.jdtk0x5d.eve.jet.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
@Entity
public class RouteCache {

  @Id
  @Column
  private Long routePK;

  @Column
  private Long startPointId;
  @Column
  private Long endPointId;
  @Column
  private String routeJson;

  public RouteCache() {
  }

  public RouteCache(Long startPointId, Long endPointId, String routeJson) {
    this.startPointId = startPointId;
    this.endPointId = endPointId;
    this.routeJson = routeJson;
  }

  public Long getRoutePK() {
    return routePK;
  }

  public void setRoutePK(Long routePK) {
    this.routePK = routePK;
  }

  public Long getStartPointId() {
    return startPointId;
  }

  public void setStartPointId(Long startPointId) {
    this.startPointId = startPointId;
  }

  public Long getEndPointId() {
    return endPointId;
  }

  public void setEndPointId(Long endPointId) {
    this.endPointId = endPointId;
  }

  public String getRouteJson() {
    return routeJson;
  }

  public void setRouteJson(String routeJson) {
    this.routeJson = routeJson;
  }
}
