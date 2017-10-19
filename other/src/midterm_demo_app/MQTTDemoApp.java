package midterm_demo_app;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTDemoApp implements MqttCallback{

  public static void main(String[] args) {
    MQTTDemoApp demo = new MQTTDemoApp();
    demo.doDemo();
  }

  private void doDemo() {
 // TODO Auto-generated method stub
    String content      = "Xianru is so cool!";
    int qos             = 2;
    String broker       = "tcp://ec2-18-221-127-99.us-east-2.compute.amazonaws.com:1883";
    String clientId     = "JavaSample";
    MemoryPersistence persistence = new MemoryPersistence();
    
    //@TODO Write to MQTT the newly updated JSON object.
    try {
      MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
      MqttConnectOptions connOpts = new MqttConnectOptions();
      
      connOpts.setCleanSession(true);
      System.out.println("Connecting to broker: "+broker);
      sampleClient.connect(connOpts);
      sampleClient.setCallback(this);
      
      sampleClient.subscribe("/noise/gates-conf-room");
      sampleClient.subscribe("/noise/monroe-conf-room");
      sampleClient.subscribe("/humidity/gates-conf-room");
      sampleClient.subscribe("/temperature/gates-conf-room");
      sampleClient.subscribe("/humidity/monroe-conf-room");
      sampleClient.subscribe("/temperature/monroe-conf-room");
      sampleClient.subscribe("/temperature/main-lobby");
      sampleClient.subscribe("/motion/main-enterence");
      
      System.out.println("Connected to MQTT");
      System.out.println("Published message: "+content);
      
      MqttMessage mqttMessage = new MqttMessage(content.getBytes());
      mqttMessage.setQos(qos);
      sampleClient.publish("hello", mqttMessage);
            
      System.out.println(sampleClient.isConnected());
      System.out.println("Message published");
      
      
  } catch(MqttException me) {
      System.out.println("reason "+me.getReasonCode());
      System.out.println("msg "+me.getMessage());
      System.out.println("loc "+me.getLocalizedMessage());
      System.out.println("cause "+me.getCause());
      System.out.println("excep "+me);
      me.printStackTrace();
  }
  }
  
  public void connectionLost(Throwable arg0) {
    // TODO Auto-generated method stub
    
  }

  public void deliveryComplete(IMqttDeliveryToken arg0) {
    // TODO Auto-generated method stub
    
  }

  public void messageArrived(String topic, MqttMessage message) throws Exception {
    System.out.println("Received message: " + message);       
  }

}
