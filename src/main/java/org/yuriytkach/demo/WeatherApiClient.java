package org.yuriytkach.demo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@RegisterRestClient(configKey = "weather-api")
public interface WeatherApiClient {

  @GET
  @Path("/v1/forecast")
  WeatherData getWeather(
    @QueryParam("latitude") float latitude,
    @QueryParam("longitude") float longitude,
    @QueryParam("current") String current
  );

  @RegisterForReflection
  record WeatherData(
    CurrentData current,
    @JsonProperty("current_units") CurrentUnits currentUnits,
    String timezone
  ) {

    public Instant getUpdatedAt() {
      return LocalDateTime.parse(current.time())
        .atZone(ZoneId.of(timezone))
        .toInstant();
    }
  }

  @RegisterForReflection
  record CurrentData(
    @JsonProperty("apparent_temperature")
    float apparentTemperature,
    @JsonProperty("temperature_2m")
    float temperature2m,
    String time
  ) {}

  @RegisterForReflection
  record CurrentUnits(
    @JsonProperty("apparent_temperature")
    String apparentTemperature,
    @JsonProperty("temperature_2m")
    String temperature2m
  ) {}
}
