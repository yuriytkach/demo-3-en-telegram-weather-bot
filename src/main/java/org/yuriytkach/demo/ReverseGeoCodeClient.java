package org.yuriytkach.demo;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@RegisterRestClient(configKey = "reverse-geocode-api")
public interface ReverseGeoCodeClient {

  @GET
  @Path("/data/reverse-geocode-client")
  ReverseGeoCodeData getReverseGeoCodeData(
    @QueryParam("latitude") String lat,
    @QueryParam("longitude") String lon
  );

  @RegisterForReflection
  record ReverseGeoCodeData(String city, String countryName, LocalityInfo localityInfo) {
    public String getCityDescription() {
      return localityInfo.administrative().stream()
        .filter(a -> a.name().equals(city))
        .map(LocalityInfoData::description)
        .findFirst()
        .orElse("");
    }

    public String getCountryDescription() {
      return localityInfo.administrative().stream()
        .filter(a -> a.name().equals(countryName))
        .map(LocalityInfoData::description)
        .findFirst()
        .orElse("");
    }
  }

  @RegisterForReflection
  record LocalityInfo(List<LocalityInfoData> administrative) {}

  @RegisterForReflection
  record LocalityInfoData(String name, String description) {}

}
