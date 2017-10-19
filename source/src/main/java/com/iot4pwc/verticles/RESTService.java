package com.iot4pwc.verticles;

import java.util.ArrayList;
import java.util.List;

import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.constants.ConstLib;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RESTService extends AbstractVerticle {
	private static final String DB_USER = "iot4pwc";
	private static final String DB_PWD = "Heinz123!";
	@Override
	public void start(){
		Router router = Router.router(vertx);
		System.out.println(RESTService.class.getName()+" : Initializing RESTful service running on port 8080");
		/**
		 * GET /data?topic=topic&start=starttime&end=endtime  -- topic related data
		 * GET /data/getInstalledSensor                       		  -- all sensor installed
		 * GET /data/getLocation?location=location                        -- all sensor installed in certain location
		 * POST /data  {sensorid:sensorid, action:command}
		 */
		router.route().handler(BodyHandler.create());
		router.get("/data").handler(this::getSensorHistory);
		router.get("/data/getSensor").handler(this::getInstalledSensor);
		router.get("/data/getLocation").handler(this::getLocInfo);
		router.post("/actuate").handler(this::actuationCommand);

		vertx.createHttpServer().requestHandler(router::accept).listen(80);
		System.out.println(RESTService.class.getName()+" : RESTful service running on port 8080");
	}

	private void getSensorHistory(RoutingContext routingContext) {
		System.out.println(RESTService.class.getName()+" : GET " + routingContext.request().uri());
		String topic = routingContext.request().getParam("topic");
		String start = routingContext.request().getParam("start");
		String end = routingContext.request().getParam("end");

		String query = "";
		DBHelper db = new DBHelper();
		List<JsonObject> result = new ArrayList<>();
		if(topic == null || start == null){
			routingContext.response()
				.putHeader("content-type", "application/json; charset=utf-8")
				.setStatusCode(400)
				.end();
		}else if(end == null){
			query = "select * from sensor_history where recorded_time > UNIX_TIMESTAMP(STR_TO_DATE('" + start + "', '%Y-%M-%d')) and sensor_id in (select distinct sensor_id from sensor_topic_map where topic = '"+ topic + "');";
			System.out.println(query);
			result = db.select(query);
		}else{
			query = "select * from sensor_history where recorded_time between UNIX_TIMESTAMP(STR_TO_DATE('" + start + "', '%Y-%M-%d')) and UNIX_TIMESTAMP(STR_TO_DATE('" + end + "', '%Y-%M-%d')) and sensor_id in (select distinct sensor_id from sensor_topic_map where topic = '"+ topic + "');";
			System.out.println(query);
		}
		StringBuilder sb = new StringBuilder();
		if(result != null){
			for(JsonObject json:result){
				sb.append(json.toString()+"\n");
			}
		}
		routingContext.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(200)
			.end(sb.toString());
	}

	private void getInstalledSensor(RoutingContext routingContext){
		System.out.println(RESTService.class.getName()+" : GET " + routingContext.request().uri());
		DBHelper db = new DBHelper();
		List<JsonObject> result = db.select("select * from sensor");
		StringBuilder sb = new StringBuilder();
		if(result != null){
			for(JsonObject json:result){
				sb.append(json.toString()+"\n");
			}
		}
		routingContext.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(200)
			.end(sb.toString());
	}

	private void getLocInfo(RoutingContext routingContext){
		System.out.println(RESTService.class.getName()+" : GET " + routingContext.request().uri());
		String location = routingContext.request().getParam("location");
		DBHelper db = new DBHelper();
		List<JsonObject> result = db.select("select * from sensor where install_loc like '%" + location + "%';");
		StringBuilder sb = new StringBuilder();
		if(result != null){
			for(JsonObject json:result){
				sb.append(json.toString()+"\n");
			}
		}
		routingContext.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(200)
			.end(sb.toString());
	}

	private void actuationCommand(RoutingContext routingContext){
		System.out.println(RESTService.class.getName()+" : POST " + routingContext.request().uri());
		JsonObject body = routingContext.getBodyAsJson();
		if(body.isEmpty() || !body.containsKey("sensorid") || !body.containsKey("action")){
			routingContext.response()
				.putHeader("content-type", "application/json; charset=utf-8")
				.setStatusCode(400)
				.end();
		}

		String sensorid = body.getString("sensorid");
		String action = body.getString("action");
		System.out.println(RESTService.class.getName()+" : Action request ["+ action + "] on #" + sensorid + " processed");
		routingContext.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(200)
			.end();
	}
}