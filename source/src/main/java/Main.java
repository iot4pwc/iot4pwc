import com.iot4pwc.constants.ConstLib;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
/**
 * The main class for the program. Run: mvn package to get A fat jar.
 * In terminal, run: java -jar servicePlatform-1.0-SNAPSHOT-fat.jar [options] to run the program. Please
 * note that every time a new vertx will be created.
 *
 * options:
 *
 * -sp: when -sp is used, the service platform will be initialized.
 * -dg: when -dg is specified, the data generator will be initialized.
 * if no option is specified, run everything locally.
 */
public class Main {
  public static void main(String[] args) {
  // set system properties
  Properties props = System.getProperties();
  props.setProperty("java.util.logging.config.file", ConstLib.LOGGING_CONFIG);
  props.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
  // Modify logging configuration here
  Configurator.setLevel("com.iot4pwc.verticles", ConstLib.LOGGING_LEVEL);
  Configurator.setLevel("com.iot4pwc.actuator.controller", ConstLib.LOGGING_LEVEL);
  Configurator.setRootLevel(ConstLib.LOGGING_LEVEL);
  Logger logger = LogManager.getLogger(Main.class);

    if (args.length == 0) {
      Vertx vertx = Vertx.vertx();

      DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(ConstLib.DUMMY_SENSOR_NUMBER);
      vertx.deployVerticle("com.iot4pwc.verticles.DummySensor", deploymentOptions);
      deploymentOptions = new DeploymentOptions().setInstances(ConstLib.DATA_PARSER_NUMBER);
      vertx.deployVerticle("com.iot4pwc.verticles.DataParser", deploymentOptions);
      deploymentOptions = new DeploymentOptions().setInstances(ConstLib.DATA_PUBLISHER_NUMBER);
      vertx.deployVerticle("com.iot4pwc.verticles.DataPublisher", deploymentOptions);
      deploymentOptions = new DeploymentOptions().setInstances(ConstLib.DATA_SERVICE_NUMBER);
      vertx.deployVerticle("com.iot4pwc.verticles.DataService", deploymentOptions);
      deploymentOptions = new DeploymentOptions().setInstances(ConstLib.RESTFUL_DB_SERVICE_NUMBER);
      vertx.deployVerticle("com.iot4pwc.verticles.RESTfulDBService", deploymentOptions);
      deploymentOptions = new DeploymentOptions().setInstances(ConstLib.ACTUATOR_NUMBER);
      vertx.deployVerticle("com.iot4pwc.verticles.ActuatorController", deploymentOptions);
      deploymentOptions = new DeploymentOptions().setInstances(ConstLib.APP_AUTHENICATOR_NUMBER);
      vertx.deployVerticle("com.iot4pwc.verticles.AppAuthenticator", deploymentOptions);
    } else {
      String option = args[0];

      VertxOptions vertxOptions = new VertxOptions()
        .setClustered(true)
        .setEventBusOptions(new EventBusOptions()
          .setSsl(true)
          .setPemKeyCertOptions(
            new PemKeyCertOptions()
              .setKeyPath(ConstLib.PRIVATE_KEY_PATH)
              .setCertPath(ConstLib.CERTIFICATE_PATH))
          .setClustered(true)
          .setPort(ConstLib.CLUSTER_EVENT_BUS_PORT)
          .setHost(System.getenv("HOST")))
        .setClusterManager(new HazelcastClusterManager());

      Vertx.clusteredVertx(vertxOptions, vertxAsyncResult -> {
        if (vertxAsyncResult.succeeded()) {
          Vertx vertx =vertxAsyncResult.result();

          switch (option) {
            case ConstLib.SERVICE_PLATFORM_OPTION: {
              DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(ConstLib.DATA_PARSER_NUMBER);
              vertx.deployVerticle("com.iot4pwc.verticles.DataParser", deploymentOptions);
              deploymentOptions = new DeploymentOptions().setInstances(ConstLib.DATA_PUBLISHER_NUMBER);
              vertx.deployVerticle("com.iot4pwc.verticles.DataPublisher", deploymentOptions);
              deploymentOptions = new DeploymentOptions().setInstances(ConstLib.DATA_SERVICE_NUMBER);
              vertx.deployVerticle("com.iot4pwc.verticles.DataService", deploymentOptions);
              deploymentOptions = new DeploymentOptions().setInstances(ConstLib.RESTFUL_DB_SERVICE_NUMBER);
              vertx.deployVerticle("com.iot4pwc.verticles.RESTfulDBService", deploymentOptions);
              deploymentOptions = new DeploymentOptions().setInstances(ConstLib.ACTUATOR_NUMBER);
              vertx.deployVerticle("com.iot4pwc.verticles.ActuatorController", deploymentOptions);
              deploymentOptions = new DeploymentOptions().setInstances(ConstLib.APP_AUTHENICATOR_NUMBER);
              vertx.deployVerticle("com.iot4pwc.verticles.AppAuthenticator", deploymentOptions);
              break;
            }
            case ConstLib.DATA_GENERATOR_OPTION: {
              DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(ConstLib.DUMMY_SENSOR_NUMBER);
              vertx.deployVerticle("com.iot4pwc.verticles.DummySensor", deploymentOptions);
              break;
            }
            default: {
              logger.info(String.format("Use %s to start the service platform.", ConstLib.SERVICE_PLATFORM_OPTION));
              logger.info(String.format("Use %s to start the data generator.", ConstLib.DATA_GENERATOR_OPTION));
            }
          }
        } else {
          logger.error(vertxAsyncResult.cause());
        }
      });
    }
  }
}
