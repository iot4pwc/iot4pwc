package midterm_demo_app;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTDemoApp implements MqttCallback{

  private MqttClient mqttClient = null;
  private List<String> topicList= null;

  public static void main(String[] args) {
    MQTTDemoApp demo = new MQTTDemoApp();
    
    // Init the topics
    demo.topicList = new LinkedList<String>();
    demo.topicList.add("/noise/gates-conf-room");
    demo.topicList.add("/noise/monroe-conf-room");
    demo.topicList.add("/humidity/gates-conf-room");
    demo.topicList.add("/temperature/gates-conf-room");
    demo.topicList.add("/temperature/monroe-conf-room");
    demo.topicList.add("/temperature/main-lobby");    
    
    System.out.println(demo.topicList);
    
    // Conduct the demo.
    demo.doDemo();
 
  }

  private void doDemo() {
    // TODO Auto-generated method stub
    String broker       = "tcp://ec2-18-221-127-99.us-east-2.compute.amazonaws.com:1883";
    MemoryPersistence persistence = new MemoryPersistence();

    //@TODO Write to MQTT the newly updated JSON object.
    try {
      this.mqttClient = getMqttClient(broker, this.getClass().getName());

      mqttClient.setCallback(this);
      
      subscribeClientToTopics(this.mqttClient, this.topicList);
     
      publishToMqtt(mqttClient, "sampleTopic", "Hello world", 2);
    } catch(MqttException me) {
      System.out.println("reason " + me.getReasonCode());
      System.out.println("msg " + me.getMessage());
      System.out.println("loc " + me.getLocalizedMessage());
      System.out.println("cause " + me.getCause());
      System.out.println("excep " + me);
      me.printStackTrace();
    }
  }

  // TODO left in there because required to implement interface
  public void connectionLost(Throwable arg0) {
    // TODO Auto-generated method stub
  }

  // TODO left in there because required to implement interface
  public void deliveryComplete(IMqttDeliveryToken arg0) {
    // TODO Auto-generated method stub
  }

  // TODO  left in there because required to implement interface
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    System.out.println("Received message: " + message);       
  }

  private static MqttClient getMqttClient(String broker, String clientId) throws MqttException {
    // Not sure what to do with this persistence Setting, leaving default for now
    MemoryPersistence persistence = new MemoryPersistence();

    // Create the MqttClient object
    MqttClient tempClient = new MqttClient (broker, clientId, persistence);

    // Prepare for connection. Not sure what to do with this setting, leaving default for now
    MqttConnectOptions connOpts = new MqttConnectOptions();
    connOpts.setCleanSession(true);

    // Connect
    tempClient.connect(connOpts);

    System.out.println("Returned to MQTT");
    
    // Return the connected Client
    return tempClient;
  }

  private static void subscribeClientToTopics(MqttClient mqttClient, List<String> topics) {
    for (String topic : topics) {
      try {
        mqttClient.subscribe(topic);
      } catch (MqttException me) {
        System.out.println("reason " + me.getReasonCode());
        System.out.println("msg " + me.getMessage());
        System.out.println("loc " + me.getLocalizedMessage());
        System.out.println("cause " + me.getCause());
        System.out.println("excep " + me);
        me.printStackTrace();      
      }
    }
  }

  private static void publishToMqtt(MqttClient mqttClient, String topic, String message, int qualityOfService) {
    MqttMessage mqttMessage = new MqttMessage(message.getBytes());
    mqttMessage.setQos(qualityOfService);
    try {
      mqttClient.publish(topic, mqttMessage);
      System.out.println("Published message: " + message);
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
    }
  }


}
