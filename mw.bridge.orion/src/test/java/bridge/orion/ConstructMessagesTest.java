package bridge.orion;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.interiot.intermw.bridge.exceptions.BridgeException;
import eu.interiot.intermw.bridge.orion.OrionBridge;
import eu.interiot.intermw.bridge.orion.OrionV2Utils;
import eu.interiot.intermw.commons.DefaultConfiguration;
import eu.interiot.intermw.commons.exceptions.MiddlewareException;
import eu.interiot.intermw.commons.interfaces.Configuration;
import eu.interiot.intermw.commons.model.OntologyId;
import eu.interiot.intermw.commons.model.Platform;
import eu.interiot.intermw.commons.model.PlatformId;
import eu.interiot.intermw.commons.model.PlatformType;
import eu.interiot.message.Message;
import eu.interiot.message.MessagePayload;
import eu.interiot.message.managers.URI.URIManagerMessageMetadata.MessageTypesEnum;
import eu.interiot.translators.syntax.FIWARE.FIWAREv2Translator;

public class ConstructMessagesTest {
	Configuration configuration;
	Platform platform;
	OrionBridge orionBridge;
	String BASE_PATH;
	String platformId;
	
	@Test
	public void test() throws MiddlewareException {
		//TODO - Must configure a real platform for tests and conect it to Docker Bridge Orion
		platformId = "http://inter-iot.eu/example-platform127";
		platform = new Platform(new PlatformId(platformId), "Test", new PlatformType("FIWARE"), "http://www.w3.org/ns/sosa/Platform", new OntologyId("GOIoTP#SoftwarePlatform")); //baseUrl, Ontology
		configuration = new DefaultConfiguration("*.bridge.properties");
		orionBridge = new OrionBridge(configuration, platform);
		BASE_PATH = configuration.getProperty("orion-base-path");
		
		//Set json fiware for test
		Map<MessageTypesEnum, String> mapJsons = new HashMap<MessageTypesEnum,String>();
		//mapJsons.put(MessageTypesEnum.PLATFORM_REGISTER, "messagesV2/00_platform_register.json");
		//mapJsons.put(MessageTypesEnum.PLATFORM_UNREGISTER, "messagesV2/01_platform_unregister.json");
		//mapJsons.put(MessageTypesEnum.SUBSCRIBE, "messagesV2/02_susbcribe.json");
		//mapJsons.put(MessageTypesEnum.UNSUBSCRIBE, "messagesV2/03_unsusbcribe.json");
		//mapJsons.put(MessageTypesEnum.PLATFORM_CREATE_DEVICE, "messagesV2/06_platform_create_device.json");
		//mapJsons.put(MessageTypesEnum.PLATFORM_CREATE_DEVICE, "messagesV2/06_platform_create_device2.json");
		mapJsons.put(MessageTypesEnum.PLATFORM_UPDATE_DEVICE, "messagesV2/08_platform_update_device.json");
		//mapJsons.put(MessageTypesEnum.LIST_DEVICES, "messagesV2/05_list_devices.json");
//		mapJsons.put(MessageTypesEnum.QUERY, "messagesV2/04_query.json");
//		mapJsons.put(MessageTypesEnum.PLATFORM_DELETE_DEVICE, "messagesV2/07_platform_delete_device.json");
//		mapJsons.put(MessageTypesEnum.OBSERVATION, "messagesV2/09_observation.json");
//		mapJsons.put(MessageTypesEnum.ACTUATION, "messagesV2/10_actuation.json");
//		mapJsons.put(MessageTypesEnum.ERROR, "messagesV2/11_error.json");
		
		//Iterate the Map
		mapJsons.entrySet().forEach(entry -> {
		    Message messageResponsePlatform = new Message();
		    URL url = Resources.getResource(entry.getValue());
		    
		    //Generate Metadata for MessageResponse
		    OrionV2Utils.generatePlatformMetadaToMessageResponse(messageResponsePlatform, entry.getKey(), platformId);
			
			try {
				//Generate Payload for MessageResponse
				generatePayloadToMessageResponse(messageResponsePlatform, BASE_PATH,Resources.toString(url, Charsets.UTF_8));
				System.out.println("---- Printing "+entry.getValue()+" ----");
				System.out.println(messageResponsePlatform.serializeToJSONLD());
			} catch (UnsupportedOperationException | IOException e) {
				e.printStackTrace();
			}
			
			try {
				//Test the message generated
				orionBridge.send(messageResponsePlatform);
			} catch (BridgeException e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * return pretty json usgin prettyPrinting of Gson
	 * @param json
	 * @return
	 */
	public String printPrettyJson(String json) {
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(json).getAsJsonObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		return gson.toJson(jsonObject);
	}
	
	/**
	 * 
	 * @param messageResponse
	 * @param BASE_PATH
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	public static void generatePayloadToMessageResponse(Message messageResponse, String BASE_PATH, String responseBody) throws UnsupportedOperationException, IOException {
		FIWAREv2Translator translator = new FIWAREv2Translator();
		// Create the model from the response JSON
		Model translatedModel = translator.toJenaModel(responseBody);
		// Create a new message payload for the response message
		MessagePayload responsePayload = new MessagePayload(translatedModel);
		// Attach the payload to the message
		messageResponse.setPayload(responsePayload);
    }
}
