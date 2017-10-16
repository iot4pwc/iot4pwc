package midterm_demo_app;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import io.vertx.core.json.JsonObject;

public class MidtermClient {

  // Instance variables.
  Scanner inputScanner;
  String userName;
  Map<String, String> actuatorSecrets;

  public static void main(String[] args) {

    // Obtain an instance of this class.
    MidtermClient mc = new MidtermClient();

    // Set actuatorSecrets to validate.
    mc.actuatorSecrets = (Map<String, String>) mc.initActuatorSecrets();

    int userChoice = 0;
    while (userChoice != 999) {
      userChoice = mc.makeChoice();

      switch (userChoice) {
        case 1:
          mc.sendSensorInformation();
          break;
        case 2:
          mc.readSensorInformation();
          break;
        case 3:
          mc.makeActuatorRequest();
          break;
        case 999:
          System.out.println("Goodbye, " + mc.userName);
          break;
        default:
          System.out.println("I am sorry, I did not understand you.");
          break;
      }
    }
  }

  private Object initActuatorSecrets() {
    Map<String, String> tempSecrets = new HashMap<String, String>();
    tempSecrets.put("A001", "giovanni");
    return tempSecrets;
  }

  MidtermClient() {
    inputScanner = new Scanner(System.in);
    System.out.println("Hello there.\nWhat's your name?");
    userName = inputScanner.next();
  }

  private int makeChoice() {
    System.out.println("Hi " + userName + ", what do you want to do?");
    System.out.println("1. Send new sensor information");
    System.out.println("2. Read most recent sensor information");
    System.out.println("3. Make actuator request");
    System.out.println("999. End this session");
    int tempChoice = inputScanner.nextInt();
    return tempChoice;
  }

  private void sendSensorInformation() {
    System.out.println("Great, which sensor do you want to send new information for? Please enter sensor number:");
    int sensorNumber = inputScanner.nextInt();
    System.out.println("Nice. And what is the payload?");
    int payload = inputScanner.nextInt();
    String jsonPayload = this.generateData(sensorNumber, payload);
    System.out.println("Awesome. Here's what I'll send:");
    System.out.println(jsonPayload);
    System.out.println("Just sent this over the wire.\n");
  }

  private void readSensorInformation() {
    System.out.println("Which sensor do want to obtain information about? Please enter sensor number:");
    int sensorNumber = inputScanner.nextInt();
    System.out.println("Here is all the information in the system:");

    // @TODO We need an actual business logic here.
    String[] values = {"92", "22", "23", "38", "11"};
    String[] topics = {"temperature", "temperature", "temperature", "temperature", "temperature"};
    String[] timeStamps = {"02:00PM", "11:37AM", "11:33AM", "10:47AM", "09:30AM"};
    System.out.println("time    | payload   | topic");
    System.out.println("----------------------------------------------------");
    for (int i = 0; i < values.length; i++)
      System.out.println(timeStamps[i] + " | " + values[i] + "        | " + topics[i]);
    System.out.println("----------------------------------------------------\n");
  }

  private void makeActuatorRequest() {
    System.out.println("Which actuator do you want to effect?");
    String actuatorID = inputScanner.next();

    if (!(this.actuatorSecrets.containsKey(actuatorID))) {
      System.out.println("Sensor does not seem to exist.");
    } else {

      System.out.println("What is your secret for actuator " + actuatorID + " ?");
      String secret = "";

      int attempts = 3;

      while (attempts > 0) {
        secret = inputScanner.next();
        if (!this.actuatorSecrets.get(actuatorID).equals(secret)) {
          if (--attempts > 0) {
            System.out.println("Access denied");
            System.out.println("What is your secret for actuator " + actuatorID + " ?");
          } else {
            System.out.println("Entered incorrect secret 3 times.\nFailed to validate");
          }
        } else {
          attempts = 0;
          System.out.println("What’s your request?\n[1] Turn on\n[2] Turn off\n[3] Toggle");
          int actuatorRequest = inputScanner.nextInt();
          switch (actuatorRequest) {
            case 1:
              System.out.println("Turned on actuator " + actuatorID);
              break;
            case 2:
              System.out.println("Turned off actuator " + actuatorID);
              break;
            case 3:
              System.out.println("Toggled actuator " + actuatorID);
              break;
            default:
              System.out.println("Unknown command, please try again at a later time.");
              break;
          }
        }
      }
    }
    System.out.println();
  }

  private String generateData(int sensorNumber, int payload) {
    Map<String, Object> payloads = new HashMap<String, Object>();
    payloads.put("sensorID", sensorNumber);
    payloads.put("payload", payload);
    payloads.put("time", Instant.now().toEpochMilli());
    payloads.put("topic", "temperature");
    JsonObject jsonObject = new JsonObject(payloads);
    return jsonObject.encode();
  }

}
