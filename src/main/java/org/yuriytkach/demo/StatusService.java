package org.yuriytkach.demo;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class StatusService {

  @Inject
  @RestClient
  ReverseGeoCodeClient reverseGeoCodeClient;

  @Inject
  @RestClient
  WeatherApiClient weatherApiClient;

  public PageData getWeatherDataForGeolocation(
    final float lat,
    final float lon
  ) {
    final var reverseGeoCodeData = reverseGeoCodeClient.getReverseGeoCodeData(
      String.valueOf(lat),
      String.valueOf(lon)
    );
    log.info("Reverse geo code data: {}", reverseGeoCodeData);

    final var weatherData = weatherApiClient.getWeather(
      lat,
      lon,
      "temperature_2m,apparent_temperature"
    );
    log.info("Weather data: {}", weatherData);

    return PageData.builder()
      .city(reverseGeoCodeData.city())
      .cityDesc(reverseGeoCodeData.getCityDescription())
      .country(reverseGeoCodeData.countryName())
      .countryDesc(reverseGeoCodeData.getCountryDescription())
      .lon(String.valueOf(lon))
      .lat(String.valueOf(lat))
      .temperature(String.format(
        "%.1f %s",
        weatherData.current().temperature2m(),
        weatherData.currentUnits().temperature2m()
      ))
      .apparentTemp(String.format(
        "%.1f %s",
        weatherData.current().apparentTemperature(),
        weatherData.currentUnits().apparentTemperature()
      ))
      .updatedAt(weatherData.getUpdatedAt().toString())
      .build();
  }
}
