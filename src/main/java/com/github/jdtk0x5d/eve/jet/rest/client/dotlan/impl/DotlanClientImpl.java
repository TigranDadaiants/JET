package com.github.jdtk0x5d.eve.jet.rest.client.dotlan.impl;

import com.github.jdtk0x5d.eve.jet.config.spring.annotations.RestClient;
import com.github.jdtk0x5d.eve.jet.config.spring.annotations.Retry;
import com.github.jdtk0x5d.eve.jet.consts.DotlanRouteOption;
import com.github.jdtk0x5d.eve.jet.rest.RestResponse;
import com.github.jdtk0x5d.eve.jet.rest.client.dotlan.DotlanClient;
import com.github.jdtk0x5d.eve.jet.rest.provider.RestClientProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
@Component
@RestClient
public class DotlanClientImpl implements DotlanClient {

  @Value("${url.dotlan.route}")
  private String addressDotlan;

  @Autowired
  private RestClientProvider client;

  @Retry
  @Override
  public RestResponse<String> getRoutePage(DotlanRouteOption dotlanRouteOption, String... waypoints) {
    String routeOption = dotlanRouteOption == DotlanRouteOption.FASTEST ? "" : dotlanRouteOption.getValue() + ":";
    String url = addressDotlan + "?&path=" + routeOption + String.join(":", waypoints);

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(new MediaType("image", "svg+xml")));
    HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

    return new RestResponse<>(client.restOperations().exchange(url, HttpMethod.GET, entity, String.class));
  }

}