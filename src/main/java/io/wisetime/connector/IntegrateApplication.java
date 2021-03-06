/*
 * Copyright (c) 2018 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

import io.wisetime.connector.config.TolerantObjectMapper;
import io.wisetime.connector.integrate.WiseTimeConnector;
import io.wisetime.generated.connect.TimeGroup;
import spark.ModelAndView;
import spark.servlet.SparkApplication;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

/**
 * @author thomas.haines@practiceinsight.io
 */
public class IntegrateApplication implements SparkApplication {

  public static final String PING_RESPONSE = "pong";
  private final ObjectMapper om;
  private final WiseTimeConnector wiseTimeConnector;

  IntegrateApplication(WiseTimeConnector wiseTimeConnector) {
    this.wiseTimeConnector = wiseTimeConnector;
    om = TolerantObjectMapper.create();
  }

  @Override
  public void init() {
    staticFileLocation("/public");
    addEndpoints();
  }

  /**
   * Invoked from the SparkFilter. Add routes here.
   */
  private void addEndpoints() {
    get("/", (rq, rs) -> new ModelAndView(new HashMap(), "home"), new ThymeleafTemplateEngine());

    get("/ping", (rq, rs) -> {
      rs.type("plain/text");
      return PING_RESPONSE;
    });

    // endpoint re posted time
    if (wiseTimeConnector == null) {
      throw new UnsupportedOperationException("time poster was not configured in server builder");
    }

    post("/receiveTimePostedEvent", (request, response) -> {
      TimeGroup userPostedTime = om.readValue(request.body(), TimeGroup.class);
      wiseTimeConnector.postTime(request, userPostedTime);
      response.type("plain/text");
      return "success";
    });
  }

  @Override
  public void destroy() {
  }
}
