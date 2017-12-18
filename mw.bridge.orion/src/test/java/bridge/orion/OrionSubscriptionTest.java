package bridge.orion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spark.Spark.post;

import eu.interiot.intermw.commons.model.*;
import eu.interiot.intermw.commons.model.PlatformType;
import org.apache.jena.atlas.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.interiot.intermw.bridge.Context;
import eu.interiot.intermw.bridge.exceptions.BridgeException;
import eu.interiot.intermw.bridge.orion.OrionBridge;
import eu.interiot.intermw.commons.exceptions.MiddlewareException;

public class OrionSubscriptionTest {
//TODO FF 20.9.2017, refactor
/*
	private final String ATTR_KEY_1 = "attr1";
	private final String ATTR_KEY_2 = "attr2";
	private final String ATTR_VALUE_1 = "attrvalue1";
	private final String ATTR_VALUE_2 = "attrvalue2";

	private final String ID_THING_ID_1 = "Thing-ID-1";
	private final String PROP_TYPE = "type";
	private final String ATTR_TYPE_TEXT = "Text";
	private final String THING_TYPE_1 = "idonthavetype";

	// Windows --> To get the host IP accessible by a Docker container, look up
	// the Ethernet vEthernet DockerNAT network
	private final String BASE_URL = "http://10.0.75.1:";
	private final String PORT = "4567";
	private final String CALLBACK_ENDPOINT = "/test";

	// Client under test
	private OrionBridge client;

	private Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

	// Control to execue intitialization just once
	private static boolean first = true;

	private boolean requestReceived = false;
	private String subscriptionID = null;

	// Thing1
	Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Before
	public void setUpListener() {
		if (!first) {
		} else {
			logger.info("Intial set up");

			first = !first;

			post(CALLBACK_ENDPOINT, (req, res) -> {
				log.info("<<<Request>>>: " + req.body().toString());

				JsonObject reqBody = new Gson().fromJson(req.body().toString(), JsonObject.class);
				subscriptionID = reqBody.get("subscriptionId").getAsString();

				requestReceived = true;
				return "Hello";
			});

			try {
				client = new OrionBridge(Context.getConfiguration(), new Platform(new PlatformId("Test_Orion"), "Test", new PlatformType("FIWARE"), null, null));
			} catch (MiddlewareException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void thingSubscription() {
		try {

			Thing thing1 = new Thing(new ThingId(ID_THING_ID_1));
			ThingAttribute attr = createThingAttribute(PROP_TYPE, THING_TYPE_1, ATTR_TYPE_TEXT);
			thing1.put(attr);

			// for (int i = 0; i < 20; i++) {
			// Thread.sleep(50);
			// }
			assertFalse(requestReceived);

			Log.info(this, "Creating " + thing1);
			client.create(thing1);

			Log.info(this, "Subscribing to thing1 events");
			client.subscribe(thing1, "MY_NEW_CONVERSATION_ID"+Math.random());

			Log.info(this, "Updating thing1 with " + ATTR_KEY_1);
			ThingAttribute attr1 = createThingAttribute(ATTR_KEY_1, ATTR_VALUE_1, ATTR_TYPE_TEXT);
			thing1.put(attr1);
			client.update(thing1);

			for (int i = 0; i < 20; i++) {
				Thread.sleep(50);
			}

			assertTrue(requestReceived);
			assertNotNull(subscriptionID);

			// client.unsubscribe(new SubscriptionId(subscriptionID));
			// client.delete(new ThingId(ID_THING_ID_1));

			 // try { client.cleanAllSubscriptions(); } catch (BridgeException
			 // e1) { e1.printStackTrace(); }
			 		} catch (BridgeException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void clearResources() {

		try {
			//client.delete(new ThingId(ID_THING_ID_1));
			client.cleanSubscriptions();
		} catch (BridgeException e1) {
			e1.printStackTrace();
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ThingAttribute createThingAttribute(String key, String value, String type) {
		ThingAttribute attr = new ThingAttribute(key, value, type);
		return attr;
	}
*/
}
