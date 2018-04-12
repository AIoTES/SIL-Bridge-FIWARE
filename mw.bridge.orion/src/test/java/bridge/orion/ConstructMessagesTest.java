package bridge.orion;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
	
	protected boolean idsTransformation = false;
	protected final String TEST_FILE_PATH = "messagesV2/";
	protected final String TEST_COMPLETE_FILE_PATH = "src/test/resources/" + TEST_FILE_PATH;
	protected final String TEST_FILE_NAME = "tmp_response_file.json";
	
	@Test
	public void buildAndTest() throws MiddlewareException {
		//TODO - Must configure a real platform for tests and conect it to Docker Bridge Orion
		platformId = "http://inter-iot.eu/example-platform127";
		platform = new Platform(new PlatformId(platformId), "Test", new PlatformType("FIWARE"), "http://www.w3.org/ns/sosa/Platform", new OntologyId("GOIoTP#SoftwarePlatform")); //baseUrl, Ontology
		configuration = new DefaultConfiguration("*.bridge.properties");
		orionBridge = new OrionBridge(configuration, platform);
		BASE_PATH = configuration.getProperty("orion-base-path");
		
		orionBridge.setTestMode(true);
		orionBridge.setTestResultFilePath(TEST_COMPLETE_FILE_PATH);
		orionBridge.setTestResultFileName(TEST_FILE_NAME);
		
		List<MessageTest> messages = new ArrayList<MessageTest> ();
		messages.add(new MessageTest(MessageTypesEnum.PLATFORM_CREATE_DEVICE, "06_platform_create_device.json"));
		messages.add(new MessageTest(MessageTypesEnum.LIST_DEVICES, "05_list_devices.json"));
		messages.add(new MessageTest(MessageTypesEnum.PLATFORM_UPDATE_DEVICE, "08_platform_update_device.json"));
		messages.add(new MessageTest(MessageTypesEnum.QUERY, "04_query.json"));
		messages.add(new MessageTest(MessageTypesEnum.SUBSCRIBE, "02_susbcribe.json"));
		messages.add(new MessageTest(MessageTypesEnum.UNSUBSCRIBE, TEST_FILE_NAME));
		messages.add(new MessageTest(MessageTypesEnum.PLATFORM_DELETE_DEVICE, "07_platform_delete_device.json"));
		messages.add(new MessageTest(MessageTypesEnum.LIST_DEVICES, "05_list_devices.json"));
		
		//Set json fiware for test
		//Map<MessageTypesEnum, String> mapJsons = new HashMap<MessageTypesEnum,String>();
		//mapJsons.put(MessageTypesEnum.PLATFORM_REGISTER, "messagesV2/00_platform_register.json");
		//mapJsons.put(MessageTypesEnum.PLATFORM_UNREGISTER, "messagesV2/01_platform_unregister.json");
		//mapJsons.put(MessageTypesEnum.QUERY, "messagesV2/04_query.json");
		//mapJsons.put(MessageTypesEnum.OBSERVATION, "messagesV2/09_observation.json");
		//mapJsons.put(MessageTypesEnum.ACTUATION, "messagesV2/10_actuation.json");
		//mapJsons.put(MessageTypesEnum.ERROR, "messagesV2/11_error.json");
		
		//Iterate the Map
		messages.forEach(entry -> {
		    Message messageResponsePlatform = new Message();
		    URL url = Resources.getResource(TEST_FILE_PATH + entry.getFilePath());
		    
		    try{
		    	
		    	Scanner scanner = new Scanner(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString() + "/" + TEST_COMPLETE_FILE_PATH + entry.getFilePath()), StandardCharsets.UTF_8.name());
		    	String fileContent = scanner.useDelimiter("\\A").next();
		    	scanner.close();
		    	
			    if(!Resources.toString(url, Charsets.UTF_8).contains("@graph")){
			    	//Generate Metadata for MessageResponse
				    OrionV2Utils.generatePlatformMetadaToMessageResponse(messageResponsePlatform, entry.getMessageType(), platformId);
					
					try {
						//Generate Payload for MessageResponse
						generatePayloadToMessageResponse(messageResponsePlatform, BASE_PATH, fileContent);
						System.out.println("---- Printing " + entry.getFilePath() + " ----");
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
			    }
			    // It is already a JSON-LD FIWARE message
			    else{
			    	String platformJson = Resources.toString(url, Charsets.UTF_8);
					System.out.println(platformJson);
					Message platformMsg = new Message(platformJson);
					orionBridge.send(platformMsg);
			    }
		    }
		    catch (Exception e) {
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
	
	class MessageTest{
		MessageTypesEnum messageType;
		String filePath = null;
		String responseFilePath;
		
		
		MessageTest(MessageTypesEnum messageType, String filePath){
			this(messageType, filePath, null);
		}
		
		MessageTest(MessageTypesEnum messageType, String filePath, String responseFilePath){
			this.messageType = messageType;
			this.filePath = filePath;
			this.responseFilePath = responseFilePath;
		}

		public MessageTypesEnum getMessageType() {
			return messageType;
		}

		public void setMessageType(MessageTypesEnum messageType) {
			this.messageType = messageType;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
		
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
