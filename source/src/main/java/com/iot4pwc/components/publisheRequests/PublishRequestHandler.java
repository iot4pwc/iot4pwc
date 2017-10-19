package com.iot4pwc.components.publisheRequests;

import com.iot4pwc.components.helpers.MqttHelper;

public interface PublishRequestHandler {
  public void handlePublish(MqttHelper mqttHelper);
}
