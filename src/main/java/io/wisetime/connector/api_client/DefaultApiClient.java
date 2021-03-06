/*
 * Copyright (c) 2018 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.api_client;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import io.wisetime.connector.api_client.support.RestRequestExecutor;
import io.wisetime.connector.config.ConnectorConfigKey;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.generated.connect.DeleteTagResponse;
import io.wisetime.generated.connect.TeamInfoResult;
import io.wisetime.generated.connect.UpsertTagRequest;
import io.wisetime.generated.connect.UpsertTagResponse;

/**
 * @author thomas.haines@practiceinsight.io
 */
public class DefaultApiClient implements ApiClient {

  private static final Logger log = LoggerFactory.getLogger(DefaultApiClient.class);
  private final RestRequestExecutor restRequestExecutor;

  public DefaultApiClient(String apiKey) {
    if (StringUtils.isEmpty(apiKey)) {
      String errorMsg = "No api key defined";
      log.error(errorMsg);
      throw new RuntimeException(errorMsg);
    }
    restRequestExecutor = new RestRequestExecutor(apiKey);
  }

  /**
   * Blank constructor assumes that the {@see io.wisetime.connector.config.ConnectorConfigKey.API_KEY} variable is provided
   * via an environment variable or system property.
   */
  public DefaultApiClient() {
    this(RuntimeConfig.findString(ConnectorConfigKey.API_KEY).orElse(""));
  }

  @Override
  public UpsertTagResponse tagUpsert(UpsertTagRequest upsertTagRequest) throws IOException {
    return restRequestExecutor.executeTypedBodyRequest(
        UpsertTagResponse.class,
        EndpointPath.TagUpsert,
        upsertTagRequest
    );
  }

  @Override
  public DeleteTagResponse tagDelete(String tagName) throws IOException {
    return restRequestExecutor.executeTypedRequest(
        DeleteTagResponse.class,
        EndpointPath.TagDelete,
        Lists.newArrayList(new BasicHeader("tagName", tagName))
    );
  }

  public TeamInfoResult teamInfo() throws IOException {
    return restRequestExecutor.executeTypedRequest(TeamInfoResult.class, EndpointPath.TeamInfo);
  }
}
