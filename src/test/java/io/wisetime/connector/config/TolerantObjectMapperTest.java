/*
 * Copyright (c) 2018 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author thomas.haines@practiceinsight.io
 */
class TolerantObjectMapperTest {
  private static final Logger log = LoggerFactory.getLogger(TolerantObjectMapperTest.class);

  @Test
  void create() {
    assertThat(runTest(new ObjectMapper())).isNotPresent();
    assertThat(runTest(TolerantObjectMapper.create())).contains("success");
  }

  private Optional<String> runTest(ObjectMapper objectMapper) {
    final String json = " {  \"noSuchProp\" : true, \"Property\" : \"success\"   } ";

    try {
      return Optional.of(objectMapper.readValue(json, MapTest.class).getProperty());
    } catch (Exception e) {
      log.debug(e.getMessage());
      return Optional.empty();
    }
  }

  @SuppressWarnings("unused")
  static class MapTest {
    private String property;

    String getProperty() {
      return property;
    }

    MapTest setProperty(String property) {
      this.property = property;
      return this;
    }
  }
}
