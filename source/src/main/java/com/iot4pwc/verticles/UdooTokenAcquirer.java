package com.iot4pwc.verticles;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.MultiMap;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a token acquirer that retrives token from the UDOO platform.
 * The token is used for future retrival from the UDOO platform.
 */

public class UdooTokenAcquirer extends AbstractVerticle {
  Logger logger = LogManager.getLogger(UdooTokenAcquirer.class);
  
  public void start() {
	  setToken();
    // update token everyday
  	vertx.setPeriodic(ConstLib.ONEDAY, id -> {
	    setToken();
	  });
  }

  private void setToken() {
	  WebClient client = WebClient.create(vertx);
	  MultiMap form = MultiMap.caseInsensitiveMultiMap();
//	    form.set("username", System.getenv("UDOO_USERNAME"));
//	    form.set("password", System.getenv("UDOO_PASSWORD"));
	  form.set("username", "cmu4pwc");
	  form.set("password", "CMU4pwc.");
	  Future<String> tokenRequest = Future.future();
    client.postAbs(ConstLib.UDOO_ENDPOINT + "/token")
          .as(BodyCodec.jsonObject())
          .sendForm(form, ar -> {
            if (ar.succeeded()) {
              tokenRequest.complete(ar.result().body().getString("token"));
        	    EventBus eb = vertx.eventBus();
              eb.publish(ConstLib.UDOO_TOKEN_ADDRESS,tokenRequest.result());
              logger.info("Token is - " + tokenRequest.result());
            } else {
              logger.error("Something went wrong " + ar.cause().getMessage());
            }
          });
  }
  
}
