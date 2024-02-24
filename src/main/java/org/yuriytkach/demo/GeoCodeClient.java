package org.yuriytkach.demo;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@RegisterRestClient(configKey = "geocode-api")
public interface GeoCodeClient {

  @GET
  @Path("/search")
  List<GeoCodeData> getGeoLocationData(
    @QueryParam("q") String address,
    @QueryParam("api_key") String apiKey
  );

  @RegisterForReflection
  record GeoCodeData(
    @JsonProperty("display_name")
    String displayName,

    String lat,
    String lon
  ) {

    public float getLattitude() {
      return Float.parseFloat(lat);
    }

    public float getLongitude() {
      return Float.parseFloat(lon);
    }

  }
}
