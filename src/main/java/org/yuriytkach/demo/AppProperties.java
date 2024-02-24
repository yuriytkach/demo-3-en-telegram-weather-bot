package org.yuriytkach.demo;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "app")
public interface AppProperties {
  String geoCodeApiKey();
  String telegramSecretToken();
}
