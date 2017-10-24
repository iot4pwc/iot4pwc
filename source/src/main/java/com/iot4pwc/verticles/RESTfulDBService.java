package com.iot4pwc.verticles;

import java.util.ArrayList;
import java.util.List;

import com.iot4pwc.components.helpers.DBHelper;

import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RESTfulDBService extends AbstractVerticle {
	@Override
	public void start(){
    WorkerExecutor executor = vertx.createSharedWorkerExecutor(
      ConstLib.RESTFUL_DB_SERVICE_POOL,
      ConstLib.RESTFUL_DB_SERVICE_POOL_SIZE
    );
    executor.executeBlocking (future -> {
      Router router = Router.router(vertx);
      System.out.println(RESTfulDBService.class.getName()+" : Initializing RESTful service running on port 8080");
      /**
       * GET /data?topic=topic&start=starttime&end=endtime&limit=limit  -- topic related data
       * GET /data/getSensor?limit=limit                       		  -- all sensor installed
       * GET /data/getLocation?location=location&limit=limit                        -- all sensor installed in certain location
       * POST /data  {sensorid:sensorid, action:command}
       */
      router.route().handler(BodyHandler.create());
      router.get("/data").handler(this::getSensorHistory);
		router.get("/data/getSensor").handler(this::getInstalledSensor);
		router.get("/data/getLocation").handler(this::getLocInfo);
		router.post("/actuate").handler(this::actuationCommand);

      vertx.createHttpServer().requestHandler(router::accept).listen(8080);
      System.out.println(RESTfulDBService.class.getName()+" : RESTful service running on port 8080");
      future.complete();
    }, res -> {
      // TODO: add response
    });


	}

	private void getSensorHistory(RoutingContext routingContext) {
		System.out.println(RESTfulDBService.class.getName()+" : GET " + routingContext.request().uri());
		String topic = routingContext.request().getParam("topic");
		String start = routingContext.request().getParam("start");
		String end = routingContext.request().getParam("end");
		String limitStr = routingContext.request().getParam("limit");

    String query = "";
    DBHelper db = new DBHelper();
    int limit = limitStr == null ? 100 : Integer.valueOf(limitStr.trim());

    List<JsonObject> result = new ArrayList<>();
    if(topic == null || start == null){
      routingContext.response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .setStatusCode(400)
        .end();
      return;
    }else if(end == null){
      query = "select * from sensor_history where recorded_time >= STR_TO_DATE('" + start + "', '%Y-%m-%d %H:%i:%S') and sensor_id in (select distinct sensor_id from sensor_topic_map where topic = '"+ topic + "') order by recorded_time desc limit "+ limit +";";
      System.out.println(query);
      result = db.select(query);
    }else {
      query = "select * from sensor_history where recorded_time between STR_TO_DATE('" + start + "', '%Y-%m-%d %H:%i:%S') and STR_TO_DATE('" + end + "', '%Y-%m-%d %H:%i:%S') and sensor_id in (select distinct sensor_id from sensor_topic_map where topic = '" + topic + "') order by recorded_time desc limit "+ limit +";";
      System.out.println(query);
      result = db.select(query);
    }
    JsonArray arr = new JsonArray(result);
    JsonObject obj = new JsonObject().put("result", arr);
    db.closeConnection();
    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(200)
      .end(obj.encodePrettily());
	}

	private void getInstalledSensor(RoutingContext routingContext){
    String limitStr = routingContext.request().getParam("limit");
    int limit = limitStr == null ? 100 : Integer.valueOf(limitStr.trim());
		System.out.println(RESTfulDBService.class.getName()+" : GET " + routingContext.request().uri());
		DBHelper db = new DBHelper();
		List<JsonObject> result = db.select("select * from sensor limit "+ limit + ";");
		JsonArray arr = new JsonArray(result);
		JsonObject obj = new JsonObject().put("result", arr);
    db.closeConnection();
		routingContext.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(200)
			.end(obj.encodePrettily());
	}

	private void getLocInfo(RoutingContext routingContext){
		System.out.println(RESTfulDBService.class.getName()+" : GET " + routingContext.request().uri());
		String location = routingContext.request().getParam("location");
    String limitStr = routingContext.request().getParam("limit");
    int limit = limitStr == null ? 100 : Integer.valueOf(limitStr.trim());
		DBHelper db = new DBHelper();
		List<JsonObject> result = db.select("select * from sensor where install_loc like '%" + location + "%' limit "+ limit +";");
    JsonArray arr = new JsonArray(result);
    JsonObject obj = new JsonObject().put("result", arr);
    db.closeConnection();
    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(200)
      .end(obj.encodePrettily());
	}

	private void actuationCommand(RoutingContext routingContext){
		System.out.println(RESTfulDBService.class.getName()+" : POST " + routingContext.request().uri());
		JsonObject body = routingContext.getBodyAsJson();
		if(body.isEmpty() || !body.containsKey("sensorid") || !body.containsKey("action")){
			routingContext.response()
				.putHeader("content-type", "application/json; charset=utf-8")
				.setStatusCode(400)
				.end();
			return;
		}

		String sensorid = body.getString("sensorid");
		String action = body.getString("action");
		System.out.println(RESTfulDBService.class.getName()+" : Action request ["+ action + "] on #" + sensorid + " processed");
		routingContext.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(200)
			.end();
	}
}