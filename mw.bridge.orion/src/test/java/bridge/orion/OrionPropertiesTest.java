package bridge.orion;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import eu.interiot.intermw.bridge.orion.FiwareUtils;
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
			
			URL url2 = Resources.getResource("messages/thing-register.json");
			String platformRegisterJson2 = Resources.toString(url1, Charsets.UTF_8);
			System.out.println(platformRegisterJson2);
			Message platformRegisterMsg2 = new Message(platformRegisterJson);


			
			OrionBridge bridge = new OrionBridge(configuration, platform);

			String basepath = configuration.getProperty("orion-base-path");
			assertNotNull(basepath);

			FIWAREv2Translator translator = new FIWAREv2Translator();
			
			String body = translator.toFormatX(platformRegisterMsg2.getMetadata().getJenaModel());
			
			//String body = "{body}";
			assertNotNull(body);

			FiwareUtils.postToFiware(basepath + FiwareUtils.FIWARE_CREATE, body);
			
			/*
			 * // register platform orionBridge.send(platformRegisterMsg); Message
			 * responseMsg = publisher.retrieveMessage(); Set<MessageTypesEnum>
			 * messageTypesEnumSet = responseMsg.getMetadata().getMessageTypes();
			 * assertTrue(messageTypesEnumSet.contains(MessageTypesEnum.RESPONSE));
			 * assertTrue(messageTypesEnumSet.contains(MessageTypesEnum.PLATFORM_REGISTER));
			 */
			
		} catch (MiddlewareException | IOException | MessageException | IllegalSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
