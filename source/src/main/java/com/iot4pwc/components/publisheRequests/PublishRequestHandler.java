package com.iot4pwc.components.publisheRequests;

import com.iot4pwc.components.helpers.MqttHelper;

public interface PublishRequestHandler {
  void handlePublish(MqttHelper mqttHelper);
}
