package com.iot4pwc.verticles;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.constants.ConstLib;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RESTfulDBService extends AbstractVerticle {
  Logger logger = LogManager.getLogger(RESTfulDBService.class);
  @Override
  public void start() {
    Router router = Router.router(vertx);
    /**
     * GET /data?topic=topic&start=starttime&end=endtime&limit=limit  -- topic related data
     * GET /data/getSensor?limit=limit                            -- all sensor installed
     * GET /data/getLocation?location=location&limit=limit                        -- all sensor installed in certain location
     * POST /data  {sensorid:sensorid, action:command}
     */
    router.route().handler(BodyHandler.create());
    router.get("/data").handler(this::getSensorHistory);
    router.get("/data/getSensor").handler(this::getInstalledSensor);
    router.get("/data/getLocation").handler(this::getLocInfo);
    router.post("/actuate").handler(this::actuationCommand);

    vertx.createHttpServer(
      new HttpServerOptions()
        .setSsl(true)
        .setPemKeyCertOptions(
          new PemKeyCertOptions()
            .setKeyPath(ConstLib.PRIVATE_KEY_PATH)
            .setCertPath(ConstLib.CERTIFICATE_PATH)
        )
    ).requestHandler(router::accept).listen(8443);
    logger.info("RESTful service running on port 8443");
  }

  private void getSensorHistory(RoutingContext routingContext) {
    logger.info(RESTfulDBService.class.getName() + " : GET " + routingContext.request().uri());
    String topic = routingContext.request().getParam("topic");
    String start = routingContext.request().getParam("start");
    String end = routingContext.request().getParam("end");
    String limitStr = routingContext.request().getParam("limit");

    String query;
    int limit = limitStr == null ? 100 : Integer.valueOf(limitStr.trim());

    List<JsonObject> result;
    if (topic == null || start == null) {
      routingContext.response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .setStatusCode(400)
        .end();
      return;
    } else if (end == null) {
      query = "select * from sensor_history " +
        "where recorded_time >= STR_TO_DATE('" + start + "', '%Y-%m-%d %H:%i:%S') " +
        "and sensor_id in " +
        "(select distinct sensor_id from sensor_topic_map " +
        "where topic = '" + topic + "') order by recorded_time desc " +
        "limit " + limit + ";";
      logger.info("QUERY " + query);
      result = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).select(query);
    } else {
      query = "select * from sensor_history " +
        "where recorded_time between STR_TO_DATE('" + start + "', '%Y-%m-%d %H:%i:%S') " +
        "and STR_TO_DATE('" + end + "', '%Y-%m-%d %H:%i:%S') " +
        "and sensor_id in " +
        "(select distinct sensor_id from sensor_topic_map " +
        "where topic = '" + topic + "') " +
        "order by recorded_time desc " +
        "limit " + limit + ";";
      logger.info("QUERY " + query);
      result = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).select(query);
    }
    JsonArray arr = new JsonArray(result);
    JsonObject obj = new JsonObject().put("result", arr);
    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(200)
      .end(obj.encodePrettily());
  }

  private void getInstalledSensor(RoutingContext routingContext) {
    String limitStr = routingContext.request().getParam("limit");
    int limit = limitStr == null ? 100 : Integer.valueOf(limitStr.trim());
    logger.info("GET " + routingContext.request().uri());
    List<JsonObject> result = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).select("select * from sensor limit " + limit + ";");
    JsonArray arr = new JsonArray(result);
    JsonObject obj = new JsonObject().put("result", arr);
    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(200)
      .end(obj.encodePrettily());
  }

  private void getLocInfo(RoutingContext routingContext) {
    logger.info("GET " + routingContext.request().uri());
    String location = routingContext.request().getParam("location");
    String limitStr = routingContext.request().getParam("limit");
    int limit = limitStr == null ? 100 : Integer.valueOf(limitStr.trim());
    List<JsonObject> result = DBHelper
      .getInstance(ConstLib.SERVICE_PLATFORM)
      .select("select * from sensor where install_loc like '%" + location + "%' limit " + limit + ";");
    JsonArray arr = new JsonArray(result);
    JsonObject obj = new JsonObject().put("result", arr);
    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(200)
      .end(obj.encodePrettily());
  }

  private void actuationCommand(RoutingContext routingContext) {
    logger.info("POST " + routingContext.request().uri());
    JsonObject body = routingContext.getBodyAsJson();
    if (body.isEmpty() || !body.containsKey("sensor_id") || !body.containsKey("action_id") || !body.containsKey("app_id")) {
      routingContext.response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .setStatusCode(400)
        .end();
      return;
    }

    logger.info("sending to acutator");

    EventBus eb = vertx.eventBus();
    eb.send(ConstLib.ACTUATOR_ADDRESS, body.toString(), ar -> {
      logger.info("message send to actuator");
      if (ar.succeeded()) {
        String result = (String) ar.result().body();
        if (result.equals("Success")) {
          logger.info("Action request [" + body.getString("action_id") + "] on #" + body.getString("sensor_id") + " processed");
          routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .setStatusCode(200)
            .end("Action request processed");
          return;
        }
        logger.info("Action request [" + body.getString("action_id") + "] on #" + body.getString("sensor_id") + " failed due to " + ar.result().body());
        routingContext.response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .setStatusCode(200)
          .end("Action request failed");
        return;
      } else {
        logger.error("Action request [" + body.getString("action_id") + "] on #" + body.getString("sensor_id") + " failed due to " + ar.result().body());
        routingContext.response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .setStatusCode(500)
          .end("Action request failed");
        return;
      }
    });
  }
}