package com.iot4pwc.verticles;

import java.util.List;

import com.iot4pwc.components.DBHelper;
import com.iot4pwc.components.tables.DBTable;
import com.iot4pwc.components.tables.SensorHistoryTable;
import com.iot4pwc.constants.ConstLib;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RESTService extends AbstractVerticle {
	private final Logger logger = LoggerFactory.getLogger(RESTService.class);
	@Override
	public void start(){
		//EventBus eb = vertx.eventBus();
		Router router = Router.router(vertx);
		System.out.println(RESTService.class.getName()+" : Initializing RESTful service running on port 8080");
		/**
		 * GET /data?topic=topic&start=starttime&end=endtime  -- topic related data
		 * GET /data/getSensor?sensorid=sensorid                        -- all sensor installed
		 * GET /data/getLocation?location=location                        -- all sensor installed in certain location
		 * POST /data  {sensorid:sensorid, action:command}
		 */
		router.route().handler(BodyHandler.create());
		router.get("/data").handler(this::getSensorHistory);
		router.get("/data/getSensor").handler(this::getInstalledSensor);
		router.get("/data/getLocation").handler(this::getLocInfo);
		router.post("/actuate").handler(this::actuationCommand);

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
		System.out.println(RESTService.class.getName()+" : RESTful service running on port 8080");
	}

	private void getSensorHistory(RoutingContext routingContext) {
		System.out.println(RESTService.class.getName()+" : GET " + routingContext.request().uri());
		String topic = routingContext.request().getParam("topic");
		String start = routingContext.request().getParam("start");
		String end = routingContext.request().getParam("end");
		
		String query = "";
		if(topic == null || start == null){
			routingContext.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(400)
			.end();
		}else if(end == null){
			query = "select * from sensor_history where recorded_time > " + start + "and sensor_id in (select distinct sensor_id from sensor_topic_map where topic = "+ topic + ");";
	        /* Invoke using the event bus. */
	        /*vertx.eventBus().send(ConstLib.,
	                HelloWorldOperations.SAY_HELLO_WORLD.toString(), response -> {

	            if (response.succeeded()) {
	                 Send the result from HelloWorldService to the http connection. 
	                httpRequest.response().end(response.result().body().toString());
	            } else {
	                logger.error("Can't send message to hello service", response.cause());
	                httpRequest.response().setStatusCode(500).end(response.cause().getMessage());
	            }
	        });*/
			DBHelper db = new DBHelper();
			DBTable history = new SensorHistoryTable().getInstance();
			List<JsonObject> result = db.select(query, history);
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
	}
	
	private void getInstalledSensor(RoutingContext routingContext){
		System.out.println(RESTService.class.getName()+" : GET " + routingContext.request().uri());
	}
	
	private void getLocInfo(RoutingContext routingContext){
		System.out.println(RESTService.class.getName()+" : GET " + routingContext.request().uri());
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
