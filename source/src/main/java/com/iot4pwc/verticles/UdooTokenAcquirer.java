package com.iot4pwc.verticles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;

/**
 * This is a token acquirer that retrives token from the UDOO platform.
 * The token is used for future retrival from the UDOO platform.
 */

public class UdooTokenAcquirer extends AbstractVerticle {
  Logger logger = LogManager.getLogger(UdooTokenAcquirer.class);
  
  /**
   * Start the verticle to acquire token from Udoo platform.
   * Reset the token everyday
   */
  public void start() {
	  setToken();
  	vertx.setPeriodic(ConstLib.ONEDAY, id -> {
	    setToken();
	  });
  }

  /**
   * Send post request to Udoo platform, set token String and send to evernt bus
   */
  private void setToken() {
    WebClient client = WebClient.create(vertx,
    new WebClientOptions()
        .setTrustAll(true)
        .setSsl(true)
        .setPemTrustOptions(new PemTrustOptions().addCertPath(ConstLib.UDOO_CLOUD_CERT))
        .setFollowRedirects(true)
);
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
