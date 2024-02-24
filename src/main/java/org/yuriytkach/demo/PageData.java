package org.yuriytkach.demo;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
@RegisterForReflection
public class PageData {
  private final String ip;
  private final String country;
  private final String city;
  private final String cityDesc;
  private final String countryDesc;
  private final String lon;
  private final String lat;
  private final String temperature;
  private final String apparentTemp;
  private final String updatedAt;
}
