package com.iot4pwc.components.helpers;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;
import com.iot4pwc.components.publisheRequests.PublishRequestHandler;
import com.iot4pwc.constants.ConstLib;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.paho.client.mqttv3.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * A helper class that provide method for easier bulk mosquitto operations, refer to Paho (https://www.eclipse.org/paho/) to learn more
 * Author: Xianru Wu
 */
public class MqttHelper {
  MqttClient client;
  boolean isTLSEnabled;
  static int clientSuffix = 0;

  /**
   * Initialize the MQTTHelperm, either with TLS or not
   * @params
   * isTLSEnabled: boolean, whether the MQTT uses TLS auth
   */  
  public MqttHelper(boolean isTLSEnabled) {
    this.isTLSEnabled = isTLSEnabled;
    client = getAliveClient();
  }

  /**
   * Publish messages
   * @params
   * publishRequests: List<PublishRequestHandler>, a list of publish requests.
   */ 
  public void publish(List<PublishRequestHandler> publishRequests) {
    for (PublishRequestHandler request: publishRequests) {
      request.handlePublish(this);
    }
  }

  /**
   * Subscribe the client to several topics
   * @params
   * topics: Set<String>, topics to subscribe to
   */   
  public void subscribe(Set<String> topics) {
    for (String topic : topics) {
      try {
        client = getAliveClient();
        client.subscribe(topic);
      } catch (MqttException me) {
        me.printStackTrace();
      }
    }
  }


  /**
   * Close the client connection
   */    
  public void closeConnection() {
    try {
      client.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Get an MQTT client
   */   
  private MqttClient getMqttClient() {
    MqttClient client = null;
    try {
      String broker = String.format(ConstLib.MQTT_BROKER_STRING, System.getenv("MQTT_URL"));
      client = new MqttClient (broker, ConstLib.MQTT_CLIENT_ID + Integer.toString(clientSuffix++));
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      client.connect(connOpts);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return client;
    }
  }

  /**
   * Get an MQTT client with TLS
   */    
  private MqttClient getMqttTLSClient() {
    MqttClient client = null;
    try {
      String broker = String.format(ConstLib.MQTT_BROKER_TLS_STRING, System.getenv("MQTT_URL"));
      client = new MqttClient (broker, ConstLib.MQTT_CLIENT_ID + Integer.toString(clientSuffix++));
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      connOpts.setSocketFactory(getSocketFactory(ConstLib.MQTT_CA_FILE));
      client.connect(connOpts);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return client;
    }
  }

  /**
   * Get an MQTT client based on isTLSEnabled
   */  
  public MqttClient getAliveClient() {
    if (client == null || !client.isConnected()) {
      client = this.isTLSEnabled ? getMqttTLSClient() : getMqttClient();
    }
    return client;
  }


  /**
   * Get socket factory that supports TLS for Moquitto TLS to work
   * @params
   * caCrtFile: String, the crt file name
   */     
  private SSLSocketFactory getSocketFactory(final String caCrtFile) throws Exception {
    Security.addProvider(new BouncyCastleProvider());

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

    SSLContext context = SSLContext.getInstance(ConstLib.MQTT_TLS_VERSION);
    context.init(null, tmf.getTrustManagers(), null);

    return context.getSocketFactory();
  }
}
