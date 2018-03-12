package bridge.orion;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import eu.interiot.intermw.bridge.orion.OrionV2Utils;
import eu.interiot.intermw.bridge.orion.OrionBridge;
import eu.interiot.intermw.commons.DefaultConfiguration;
import eu.interiot.intermw.commons.exceptions.MiddlewareException;
import eu.interiot.intermw.commons.interfaces.Configuration;
import eu.interiot.intermw.commons.model.Platform;
import eu.interiot.message.EntityID;
import eu.interiot.message.Message;
import eu.interiot.message.exceptions.MessageException;
import eu.interiot.translators.syntax.IllegalSyntaxException;
import eu.interiot.translators.syntax.FIWARE.FIWAREv2Translator;

public class OrionPropertiesTest {

	Configuration configuration;
	Platform platform;

	/*
	 * @Before public void init() { try { configuration = new
	 * DefaultConfiguration("bridge.properties"); platform = new
	 * Platform("FIWARE_TEST", ) } catch (MiddlewareException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 */

	@Test
	public void tests() {

		Configuration configuration;
		OrionBridge orionBridge;
		try {			
			configuration = new DefaultConfiguration("*.bridge.properties");
			orionBridge = new OrionBridge(configuration, platform);
			
			// create Message objects from serialized messages
			URL url1 = Resources.getResource("messages/platform-register.json");
			String platformRegisterJson = Resources.toString(url1, Charsets.UTF_8);
			System.out.println(platformRegisterJson);
			Message platformRegisterMsg = new Message(platformRegisterJson);
			// create Platform object using platform-register message
			EntityID platformId = platformRegisterMsg.getMetadata().asPlatformMessageMetadata().getReceivingPlatformIDs().iterator().next();
			Platform platform = new Platform(platformId.toString(), platformRegisterMsg.getPayload());
			
			URL url2 = Resources.getResource("messages/thing-register-FIWARE.json");
			String thingRegisterJson = Resources.toString(url2, Charsets.UTF_8);
			System.out.println(thingRegisterJson);
			Message thingRegisterMsg = new Message(thingRegisterJson);
			
			URL url3 = Resources.getResource("messages/thing-query-FIWARE.json");
			String thingQueryJson1 = Resources.toString(url3, Charsets.UTF_8);
			System.out.println(thingQueryJson1);
			Message thingQueryMsg = new Message(thingQueryJson1);
			
			orionBridge = new OrionBridge(configuration, platform);

			String basepath = configuration.getProperty("orion-base-path");
			assertNotNull(basepath);

			//orionBridge.send(thingRegisterMsg);
			orionBridge.send(thingQueryMsg);
			//FIWAREv2Translator translator = new FIWAREv2Translator();			
			//String body = translator.toFormatX(thingRegisterMsg.getPayload().getJenaModel());
			
			//String body = "{body}";
			//assertNotNull(body);

			//OrionV2Utils.registerEntity(basepath, body);
			//OrionV2Utils.createSubscription(basepath, body);
			
			/*
			 * // register platform orionBridge.send(platformRegisterMsg); Message
			 * responseMsg = publisher.retrieveMessage(); Set<MessageTypesEnum>
			 * messageTypesEnumSet = responseMsg.getMetadata().getMessageTypes();
			 * assertTrue(messageTypesEnumSet.contains(MessageTypesEnum.RESPONSE));
			 * assertTrue(messageTypesEnumSet.contains(MessageTypesEnum.PLATFORM_REGISTER));
			 */
			
		} catch (MiddlewareException | IOException | MessageException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
