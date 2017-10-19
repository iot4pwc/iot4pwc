package com.iot4pwc.components.helpers;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;

import com.iot4pwc.constants.ConstLib;
import com.iot4pwc.verticles.DataPublisher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.paho.client.mqttv3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MqttHelper {
  public static void publish(MqttClient client, Set<String> topicList, String message, int qualityOfService) {
    for (String topic : topicList) {
      MqttHelper.publishToMqtt(client, topic, message, qualityOfService);
    }
  }

  public static void publish(MqttClient client, List<PublishRequest> publishRequests) {
    for (PublishRequest request: publishRequests) {
      // this is more open to change
      request.handlePublish(client);
    }
  }

  public static void publishToMqtt(MqttClient client, String topic, String message, int qualityOfService) {
    MqttMessage mqttMessage = new MqttMessage(message.getBytes());
    mqttMessage.setQos(qualityOfService);
    try {
      client.publish(topic, mqttMessage);
      System.out.println(DataPublisher.class.getName() + ": Published message: " + message);
    } catch (MqttPersistenceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (MqttException me) {
      // TODO Auto-generated catch block
      System.out.println("reason " + me.getReasonCode());
      System.out.println("msg " + me.getMessage());
      System.out.println("loc " + me.getLocalizedMessage());
      System.out.println("cause " + me.getCause());
      System.out.println("excep " + me);
      me.printStackTrace();
    }
  }

  public static MqttClient getMqttClient() {
    MqttClient client = null;
    try {
      String broker = ConstLib.MQTT_BROKER_STRING;
      String clientID = ConstLib.MQTT_CLIENT_ID;
      client = new MqttClient (broker, clientID);
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      client.connect(connOpts);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return client;
    }
  }

  public static MqttClient getMqttTLSClient() {
    MqttClient client = null;
    try {
      String broker = ConstLib.MQTT_BROKER_TLS_STRING;
      String clientID = ConstLib.MQTT_CLIENT_ID;
      client = new MqttClient (broker, clientID);
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      connOpts.setSocketFactory(MqttHelper.getSocketFactory(ConstLib.MQTT_CA_FILE));
      client.connect(connOpts);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return client;
    }
  }

  public static SSLSocketFactory getSocketFactory(final String caCrtFile) throws Exception {
    Security.addProvider(new BouncyCastleProvider());

    // load CA certificate
    X509Certificate caCert = null;

    FileInputStream fis = new FileInputStream(caCrtFile);
    BufferedInputStream bis = new BufferedInputStream(fis);
    CertificateFactory cf = CertificateFactory.getInstance("X.509");

    while (bis.available() > 0) {
      caCert = (X509Certificate) cf.generateCertificate(bis);
    }

    KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
    caKs.load(null, null);
    caKs.setCertificateEntry("ca-certificate", caCert);
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
    tmf.init(caKs);

    // finally, create SSL socket factory
    SSLContext context = SSLContext.getInstance(ConstLib.MQTT_TLS_VERSION);
    context.init(null, tmf.getTrustManagers(), null);

    return context.getSocketFactory();
  }
}

class PublishRequest {
  private String topic;
  private String message;
  private int qos;

  public PublishRequest(String topic, String message, int qos) {
    this.topic = topic;
    this.message = message;
    this.qos = qos;
  }

  public void handlePublish(MqttClient client) {
    MqttMessage mqttMessage = new MqttMessage(message.getBytes());
    mqttMessage.setQos(qos);
    try {
      client.publish(topic, mqttMessage);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}