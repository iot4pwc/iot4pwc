<<<<<<< HEAD
import com.iot4pwc.constants.ConstLib;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
=======
import com.iot4pwc.constants.VerticleNumbers;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
>>>>>>> d94fd3ecb1fa00a5f8cda841f5f87d6f5d0f1c92
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * The main class for the program. Run: mvn package to get A fat jar.
 * In terminal, run: java -jar servicePlatform-1.0-SNAPSHOT-fat.jar [options] to run the program. Please
 * note that every time a new vertx will be created.
 *
 * options:
 *
 * -sp: when -sp is used, the service platform will be initialized.
 * -dg: when -dg is specified, the data generator will be initialized.
 *
 */
public class Main {
<<<<<<< HEAD
  public static void main(String[] args) {
    String option = args[0];

    VertxOptions vertxOptions = new VertxOptions()
      .setClustered(true)
      .setEventBusOptions(new EventBusOptions()
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
            break;
          }
          case ConstLib.DATA_GENERATOR_OPTION: {
            DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(ConstLib.DUMMY_SENSOR_NUMBER);
            vertx.deployVerticle("com.iot4pwc.verticles.DummySensor", deploymentOptions);
            break;
          }
          default: {
            System.out.println(
              String.format("Use %s to start the service platform.", ConstLib.SERVICE_PLATFORM_OPTION)
            );
            System.out.println(
              String.format("Use %s to start the data generator.", ConstLib.DATA_GENERATOR_OPTION)
            );
          }
        }
      } else {
        System.out.println(vertxAsyncResult.cause());
=======
  private static final String SERVICE_PLATFORM = "-sp";
  private static final String DATA_GENERATOR = "-dg";

  public static void main(String[] args) {
    String option = args[0];
    VertxOptions vertxOptions = new VertxOptions()
      .setClustered(true)
      .setClusterManager(new HazelcastClusterManager());

    Vertx.clusteredVertx(vertxOptions, vertxAsyncResult -> {
      Vertx vertx =vertxAsyncResult.result();

      switch (option) {
        case SERVICE_PLATFORM: {
          DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(VerticleNumbers.DATA_PARSER_NUMBER);
          vertx.deployVerticle("com.iot4pwc.verticles.DataParser", deploymentOptions);
          deploymentOptions = new DeploymentOptions().setInstances(VerticleNumbers.DATA_PUBLISHER_NUMBER);
          vertx.deployVerticle("com.iot4pwc.verticles.DataPublisher", deploymentOptions);
          deploymentOptions = new DeploymentOptions().setInstances(VerticleNumbers.DATA_SERVICE_NUMBER);
          vertx.deployVerticle("com.iot4pwc.verticles.DataService", deploymentOptions);
          break;
        }
        case DATA_GENERATOR: {
          DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(VerticleNumbers.DUMMY_SENSOR_NUMBER);
          vertx.deployVerticle("com.iot4pwc.verticles.DummySensor", deploymentOptions);
          break;
        }
        default: {
          System.out.println(
            String.format("Use %s to start the service platform.", SERVICE_PLATFORM)
          );
          System.out.println(
            String.format("Use %s to start the data generator.", DATA_GENERATOR)
          );
        }
>>>>>>> d94fd3ecb1fa00a5f8cda841f5f87d6f5d0f1c92
      }
    });
  }
}
