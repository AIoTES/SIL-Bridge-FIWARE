/**
 * INTER-IoT. Interoperability of IoT Platforms.
 * INTER-IoT is a R&D project which has received funding from the European 
 * Unionâ€™s Horizon 2020 research and innovation programme under grant 
 * agreement No 687283.
 * 
 * Copyright (C) 2016-2018, by (Author's company of this file):
 * - Prodevelop S.L.
 * 
 *
 * For more information, contact:
 * - @author <a href="mailto:mllorente@prodevelop.es">Miguel A. Llorente</a>  
 * - Project coordinator:  <a href="mailto:coordinator@inter-iot.eu"></a>
 *  
 *
 *    This code is licensed under the ASL license, available at the root
 *    application directory.
 */
package eu.interiot.intermw.bridge.orion;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.interiot.intermw.bridge.BridgeConfiguration;
import eu.interiot.intermw.bridge.abstracts.AbstractBridge;
import eu.interiot.intermw.comm.broker.exceptions.BrokerException;
import eu.interiot.intermw.commons.exceptions.MiddlewareException;
import eu.interiot.intermw.commons.model.IoTDevice;
import eu.interiot.intermw.commons.model.Platform;
import eu.interiot.intermw.commons.requests.PlatformCreateDeviceReq;
import eu.interiot.intermw.commons.requests.SubscribeReq;
import eu.interiot.message.Message;
import eu.interiot.message.MessageMetadata;
import eu.interiot.message.MessagePayload;
import eu.interiot.message.ID.EntityID;
import eu.interiot.message.exceptions.payload.PayloadException;
import eu.interiot.message.managers.URI.URIManagerMessageMetadata;
import eu.interiot.message.managers.URI.URIManagerMessageMetadata.MessageTypesEnum;
import eu.interiot.message.metadata.PlatformMessageMetadata;
import eu.interiot.message.payload.types.IoTDevicePayload;
import eu.interiot.translators.syntax.FIWARE.FIWAREv2Translator;
import spark.Request;
import spark.Spark;

@eu.interiot.intermw.bridge.annotations.Bridge(platformType = "http://inter-iot.eu/FIWARE")
public class OrionBridge extends AbstractBridge {

//	private final static String PROPERTIES_PREFIX = "orion-";
	private String BASE_PATH;
	private String callbackAddress;
	private Map<String, List<String[]>> subscriptionIds = new HashMap<String,List<String[]>>();

	private final Logger logger = LoggerFactory.getLogger(OrionBridge.class);
	
	protected boolean testMode = false;
	protected String testResultFilePath;
	protected String testResultFileName;
	
	// TrustStore for self-signed certificates
    private String trustStore;
	private String trustStorePass;
	
	// Discovery
	private String[] types;
	private String[] services;
	private List<String[]> discoverySubscriptions = new ArrayList<String[]>();

	public OrionBridge(BridgeConfiguration configuration, Platform platform) throws MiddlewareException {
		super(configuration, platform);
		try{
			String url = platform.getBaseEndpoint().toString(); // Get base path from the Register Platform message
			if (url.endsWith("/")) url = url.substring(0, url.length()-1); // Just in case
			BASE_PATH = url;
			callbackAddress = this.bridgeCallbackUrl.toString(); // Same base callback address for all bridges
		
			// Raise the server
//			get(callbackAddress, (req, res) -> testServerGet(req));
//			post(callbackAddress,(req, res) -> publishObservationToIntermw(req));

			// Discovery
			String entityTypes = configuration.getProperty("entityTypes");
			String definedServices = configuration.getProperty("services");
			
			if(entityTypes != null){
				types = entityTypes.replaceAll(" ", "").split(",");
			} else{
				// Disable discovery if types == null or try to discover every entity?
				types = null;
			}
			
			if(definedServices != null){
				services = definedServices.replaceAll(" ", "").split(",");
			} else{
				// Use only default Fiware Service
				services = new String[]{""};
			}
					
			
			// For self-signed certificates
		    trustStore = configuration.getProperty("certificate");
	        trustStorePass = configuration.getProperty("certificate-password");
			if(BASE_PATH.startsWith("https") && trustStore != null) setCustomTrustStore(trustStore, trustStorePass);
			// Authentication token
//			OrionV2Utils.token = platform.getEncryptedPassword(); 
			OrionV2Utils.token = configuration.getProperty("token");; // TODO: improve this
			
		} catch (Exception e) {
		    throw new MiddlewareException(
				    "Failed to read UAALBridge configuration: "
					    + e.getMessage());
			}
	}
	
	@Override
	public Message registerPlatform(Message message) {
		Message responseMessage = createResponseMessage(message);
		logger.info("Registering platform {}...", OrionV2Utils.getPlatformId(platform));
		logger.info("Platform {} has been registered.", OrionV2Utils.getPlatformId(platform));
		responseMessage.getMetadata().setStatus("OK");
		return responseMessage;
	}

	@Override
	public Message unrecognized(Message message) {
		logger.info("Unrecognized message type.");
		Message responseMessage = createResponseMessage(message);
		responseMessage.getMetadata().setStatus("OK");
		return responseMessage;
	}

	@Override
	public Message unregisterPlatform(Message message) {
		Message responseMessage = createResponseMessage(message);
		stopDeviceDiscovery();
		logger.info("Unregistering platform {}...", OrionV2Utils.getPlatformId(platform));
		logger.info("Platform {} has been unregistered.", OrionV2Utils.getPlatformId(platform));
		responseMessage.getMetadata().setStatus("OK");
		return responseMessage;
	}
	
	
	@Override
	public Message updatePlatform(Message message) throws Exception {
		logger.info("Updating platform {}...", OrionV2Utils.getPlatformId(platform));
		Message responseMessage = createResponseMessage(message);
		String url = platform.getBaseEndpoint().toString(); // Get base path from the Update Platform message
		if(url!=null){
			if (url.endsWith("/")) url = url.substring(0, url.length()-1); // Just in case
			BASE_PATH = url;
		}
		// TODO: set new user and password (if needed)
		// DO ANYTHING ELSE?
		responseMessage.getMetadata().setStatus("OK");
		return responseMessage;
	}
		
	@Override
	public Message platformCreateDevices(Message message) {
		// TODO: use FIware translator
		// TODO: test with semantic translation
		Message responseMessage = createResponseMessage(message);
		try {
			logger.info("Creating devices...");
			FIWAREv2Translator translator = new FIWAREv2Translator();
//			String body = translator.toFormatX(message.getPayload().getJenaModel());
			
			PlatformCreateDeviceReq req = new PlatformCreateDeviceReq(message);
			// TODO: FIND A BETTER WAY TO DO THIS
			for (IoTDevice iotDevice : req.getDevices()) {
	            logger.debug("Sending create-device (start-to-manage) request to the platform for device {}...", iotDevice.getDeviceId());
	            
	            String thingId = iotDevice.getDeviceId();
			    String[] entityID = OrionV2Utils.filterThingID(thingId);
	        	String transformedID = entityID[0];
			    String type = entityID[1];
			    
	        	JsonObject deviceObject = new JsonObject(); 
	        	deviceObject.addProperty("id", transformedID);
	        	deviceObject.addProperty("type", type);
	            OrionV2Utils.registerEntity(BASE_PATH, deviceObject.toString(), entityID[2], entityID[3]);
				logger.info("Device {} has been created.", thingId);
	        }
						
//			String responseBody = OrionV2Utils.registerEntity(BASE_PATH, body);			
			// Get the Model from the response
//			Model translatedModel = translator.toJenaModel(responseBody);			
			// Create a new message payload for the response message
//			MessagePayload responsePayload = new MessagePayload(translatedModel);
			// Attach the payload to the message
//			responseMessage.setPayload(responsePayload);
			responseMessage.getMetadata().setStatus("OK");
		} 
		catch (Exception e) {
			logger.error("Error creating devices: " + e.getMessage());
			responseMessage.getMetadata().setStatus("KO");
			responseMessage.getMetadata().setMessageType(MessageTypesEnum.ERROR);
			responseMessage.getMetadata().asErrorMessageMetadata().setExceptionStackTrace(e);
		}
		return responseMessage;
	}
	
	@Override
	public Message platformDeleteDevices(Message message) {
		Message responseMessage = createResponseMessage(message);
		try {
			logger.info("Removing devices...");
			IoTDevicePayload reqIoTDevicePayload = message.getPayloadAsGOIoTPPayload().asIoTDevicePayload();
	        Set<EntityID> deviceIds = reqIoTDevicePayload.getIoTDevices();
			for(EntityID entityId : deviceIds){
				String deviceId = entityId.toString();
			    String[] entityID = OrionV2Utils.filterThingID(deviceId);
	        	String transformedID = entityID[0];
			    String type = entityID[1];
			    
				String responseBody = OrionV2Utils.unregisterEntity(BASE_PATH, transformedID, type, entityID[2], entityID[3]); // Type added to avoid ambiguity
				// Get the Model from the response
				FIWAREv2Translator translator = new FIWAREv2Translator();
				Model translatedModel = translator.toJenaModel(responseBody);			
				// Create a new message payload for the response message
				MessagePayload responsePayload = new MessagePayload(translatedModel);
				// Attach the payload to the message
				responseMessage.setPayload(responsePayload);
				logger.info("Device {} has been removed.", transformedID);
			}
			responseMessage.getMetadata().setStatus("OK");
		} 
		catch (Exception e) {
			logger.error("Error removing devices: " + e.getMessage());
			responseMessage.getMetadata().setStatus("KO");
			responseMessage.getMetadata().setMessageType(MessageTypesEnum.ERROR);
			responseMessage.getMetadata().asErrorMessageMetadata().setExceptionStackTrace(e);
		}
		return responseMessage;
	}
	
	@Override
	/**
	 * Overrides all existing attributes and deletes all the existing ones, so the update should contain all the current attributes
	 */
	public Message platformUpdateDevices(Message message) {
		Message responseMessage = createResponseMessage(message);
		logger.info("Updating devices...");
		IoTDevicePayload reqIoTDevicePayload = message.getPayloadAsGOIoTPPayload().asIoTDevicePayload();
        Set<EntityID> deviceIds = reqIoTDevicePayload.getIoTDevices();
		for(EntityID entityId : deviceIds){
			String deviceId = entityId.toString();
			try {
				FIWAREv2Translator translator = new FIWAREv2Translator();
				String body = translator.toFormatX(message.getPayload().getJenaModel());
			    String[] entityID = OrionV2Utils.filterThingID(deviceId);
			    String transformedId = entityID[0];
			    String type = entityID[1];
			    
				String responseBody = OrionV2Utils.updateEntity(BASE_PATH, transformedId, type, body, entityID[2], entityID[3]);
				// Get the Model from the response
				Model translatedModel = translator.toJenaModel(responseBody);			
				// Create a new message payload for the response message
				MessagePayload responsePayload = new MessagePayload(translatedModel);
				// Attach the payload to the message
				responseMessage.setPayload(responsePayload);
				logger.info("Device {} has been updated.", transformedId);
			} 
			catch (Exception e) {
				logger.error("Error updating device {}: " + e.getMessage(), deviceId);
				responseMessage.getMetadata().setStatus("KO");
				responseMessage.getMetadata().setMessageType(MessageTypesEnum.ERROR);
				responseMessage.getMetadata().asErrorMessageMetadata().setExceptionStackTrace(e);
			}
		}					
		return responseMessage; 
	}
	
	@Override
	public Message listDevices(Message message) {
		// TODO: UPDATE DISCOVERY QUERY (and update registry with new devices)
		Message responseMessage = createResponseMessage(message);
		logger.debug("ListDevices started...");
		String conversationId = message.getMetadata().getConversationId().orElse(null);
		
		if(types != null){
			try{
				/// Initialize device registry
				Message deviceRegistryInitializeMessage = new Message();
				PlatformMessageMetadata metadata = new MessageMetadata().asPlatformMessageMetadata();
		        metadata.initializeMetadata();
		        metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.DEVICE_REGISTRY_INITIALIZE);
		        metadata.setSenderPlatformId(new EntityID(platform.getPlatformId()));
//		        metadata.setConversationId(conversationId); 
//		        MessagePayload devicePayload = new MessagePayload(translatedModel);
		        
		        deviceRegistryInitializeMessage.setMetadata(metadata);
//		        deviceRegistryInitializeMessage.setPayload(devicePayload);
		        deviceRegistryInitializeMessage.setPayload(new MessagePayload());
		        publisher.publish(deviceRegistryInitializeMessage);
		        logger.debug("Device_Registry_Initialize message has been published upstream.");
				
				// Start device discovery
				deviceDiscovery(conversationId);
				responseMessage.getMetadata().setStatus("OK");
			} catch (Exception e) {
				logger.error("Error in query: " + e.getMessage());
				responseMessage.getMetadata().setStatus("KO");
				responseMessage.getMetadata().setMessageType(MessageTypesEnum.ERROR);
				responseMessage.getMetadata().asErrorMessageMetadata().setExceptionStackTrace(e);
			}		
		}		
		return responseMessage;
	}
	
	@Override
	public Message query(Message message) {
		Message responseMessage = createResponseMessage(message);
		try{
			IoTDevicePayload reqIoTDevicePayload = message.getPayloadAsGOIoTPPayload().asIoTDevicePayload();
	        Set<EntityID> reqIoTDevices = reqIoTDevicePayload.getIoTDevices();
	        String responseBody = null;
	        FIWAREv2Translator translator = new FIWAREv2Translator();
	        if (reqIoTDevices.isEmpty()) {
	        	// Query all devices
	        	// TODO: change query to Orion (same as device discovery)
	        	String response = OrionV2Utils.discoverEntities(BASE_PATH);
//				logger.info(responseBody);
	        	// Create a new message payload with the information about the device
	        	// Set proper ids
	        	JsonParser parser = new JsonParser();
	        	JsonArray array = parser.parse(response).getAsJsonArray();
	        	for(int i = 0; i<array.size(); i++){
		            JsonObject deviceObject = array.get(i).getAsJsonObject();
		            JsonObject updatedDevice = OrionV2Utils.setDeviceId(deviceObject, "", ""); // TODO: add service and servicePath
		            array.set(i, updatedDevice);
	        	}
            	responseBody = array.toString();
	            
	        }else{
	        	// Query only selected devices
	            for (EntityID reqIoTDevice : reqIoTDevices) {
	            	String deviceId = reqIoTDevice.toString();
	            	
				    String[] entityID = OrionV2Utils.filterThingID(deviceId);
				    String transformedId = entityID[0];
				    String type = entityID[1];
	            	
				    String response = OrionV2Utils.queryEntityById(BASE_PATH, transformedId, type, entityID[2], entityID[3]);
				    
	            	if (response.startsWith("[")) response=response.substring(1, response.length()-1); // JSON object, not JSON array
	            	logger.info(response);	
	            	// Set proper deviceID value
	            	responseBody = OrionV2Utils.setDeviceId(response, entityID[2], entityID[3]);
	            }
			}
	        // Create the model from the response JSON
			Model translatedModel = translator.toJenaModel(responseBody);
			// Create a new message payload for the response message
			MessagePayload responsePayload = new MessagePayload(translatedModel);
			// Attach the payload to the message
			responseMessage.setPayload(responsePayload);
			// Set the OK status
			responseMessage.getMetadata().setStatus("OK");
			logger.debug(responseMessage.serializeToJSONLD());
		}
		catch (Exception e) {
			logger.error("Error in query: " + e.getMessage());
			responseMessage.getMetadata().setStatus("KO");
			responseMessage.getMetadata().setMessageType(MessageTypesEnum.ERROR);
			responseMessage.getMetadata().asErrorMessageMetadata().setExceptionStackTrace(e);
		}
		return responseMessage;
	}
	
	@Override
	public Message actuate(Message message) {
		Message responseMessage = createResponseMessage(message);
		logger.info("Sending actuation data...");
		Set<String> deviceIds = OrionV2Utils.getEntityIds(message);
		for(String deviceId : deviceIds){
			try {
				FIWAREv2Translator translator = new FIWAREv2Translator();
				String body = translator.toFormatX(message.getPayload().getJenaModel());
				// Update entity in Fiware
			    String[] entityID = OrionV2Utils.filterThingID(deviceId);
			    String transformedId = entityID[0];
			    String type = entityID[1];
			    JsonParser parser = new JsonParser();
			    JsonObject bodyObject = parser.parse(body).getAsJsonObject();
//			    if(bodyObject.has("id")){
//			    	bodyObject.addProperty("id", transformedId); // Set correct id value
//			    }
				
			    bodyObject.remove("id");
			    bodyObject.remove("type");
			    body = bodyObject.toString();			    
			    	
			    String responseBody = OrionV2Utils.updateEntity(BASE_PATH, transformedId, type, body, entityID[2], entityID[3]);
			} 
			catch (Exception e) {
				logger.error("Error in actuate: " + e.getMessage());
				responseMessage.getMetadata().setStatus("KO");
				responseMessage.getMetadata().setMessageType(MessageTypesEnum.ERROR);
				responseMessage.getMetadata().asErrorMessageMetadata().setExceptionStackTrace(e);
			}
		}					
		return responseMessage; 
	}
	
	@Override
	public Message observe(Message message) {
		Message responseMessage = createResponseMessage(message);
//		Set<String> deviceIds = OrionV2Utils.getEntityIds(message);
//		for(String deviceId : deviceIds){
			try {
				FIWAREv2Translator translator = new FIWAREv2Translator();
				String body = translator.toFormatX(message.getPayload().getJenaModel());
				
				logger.debug("Observation data: " + body);
				JsonParser parser = new JsonParser();
				JsonObject data = parser.parse(body).getAsJsonObject();
			    String deviceId = data.get("id").getAsString();
			    // Extract data from deviceId
			    String[] entityID = OrionV2Utils.filterThingID(deviceId);
			    String transformedId = entityID[0];
			    String type = entityID[1];
			    
			    logger.debug("DeviceId: " + deviceId);
			    data.remove("id");
			    data.remove("type");
			    String responseBody = OrionV2Utils.publishEntityObservation(BASE_PATH, transformedId, type, data.toString(), entityID[2], entityID[3]);
				
				// Get the Model from the response
				Model translatedModel = translator.toJenaModel(responseBody);			
				// Create a new message payload for the response message
				MessagePayload responsePayload = new MessagePayload(translatedModel);
				// Attach the payload to the message
				responseMessage.setPayload(responsePayload);
			} 
			catch (Exception e) {
				logger.error("Error in observe: " + e.getMessage());
				responseMessage.getMetadata().setStatus("KO");
				responseMessage.getMetadata().setMessageType(MessageTypesEnum.ERROR);
				responseMessage.getMetadata().asErrorMessageMetadata().setExceptionStackTrace(e);
			}
//		}			
		return responseMessage; 
	}
	
	@Override
	public Message error(Message message) {
		logger.info("Error occured in {}...", message);
		Message responseMessage = createResponseMessage(message);
		responseMessage.getMetadata().setStatus("KO");
		responseMessage.getMetadata().setMessageType(MessageTypesEnum.ERROR);
		return responseMessage;
	}
	
	@Override
	public Message subscribe(Message message) {
		Message responseMessage = createResponseMessage(message);
		try{
			// Translate the message into Fiware JSON
			List<String> entities;
			SubscribeReq subsreq = new SubscribeReq(message);
			entities = subsreq.getDeviceIds();
			
			if (entities.isEmpty()) {
	            throw new PayloadException("No entities of type Device found in the Payload.");
	        } 
			
			JsonParser parser = new JsonParser();
			
		    String conversationId = message.getMetadata().getConversationId().orElse(null);
		    logger.debug("Subscribing to things using conversationId {}...", conversationId);
		    List<String[]> subIds = new ArrayList<String[]>();
		    Map<String, String> completeIds = new HashMap<String,String>();
		    
		    for (String deviceId : entities) {
		    	String[] entityID = OrionV2Utils.filterThingID(deviceId);
			    String transformedId = entityID[0];
			    String type = entityID[1];
			    JsonObject subjectObject = parser.parse(OrionV2Utils.buildJsonWithIdAndType(transformedId, type)).getAsJsonObject();
			    JsonObject urlObject = new JsonObject();
			    JsonObject notificationObject = new JsonObject();
			    notificationObject.add("http", urlObject);
			    JsonObject subscription = new JsonObject();
			    subscription.add("subject", subjectObject);
			    subscription.add("notification", notificationObject);
			    // TODO: add "expires" attribute
			    String requestBody = subscription.toString();
			    
			    // Change the message callback address of the body for the address where the bridge is listening after a subscription 
				requestBody = OrionV2Utils.buildJsonWithUrl(requestBody, callbackAddress.concat("/" + conversationId));
				
				// Create the subscription
				String responseBody = OrionV2Utils.createSubscription(BASE_PATH, requestBody, entityID[2], entityID[3]);
				
				String subscriptionId = parser.parse(responseBody).getAsJsonObject().get("id").getAsString();
				// Keep track of the subscription ids
				if(subscriptionId != null){
					subIds.add(new String[]{subscriptionId, entityID[2]}); // Subscription Id and Service
					completeIds.put(subscriptionId, deviceId); // Full device id for translated messages
				} 
				//Test
				if(testMode && testResultFilePath != null && testResultFileName != null){
					OutputStream os = new FileOutputStream(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString() + "/" + testResultFilePath + testResultFileName).toString());
					os.write(responseBody.getBytes());
					os.flush();
					os.close();
				}
		    }
		    
		    subscriptionIds.put(conversationId, subIds); // SUBSCRIPTION ID IS NEEDED FOR UNSUBSCRIBE METHOD. UNSUBSCRIBE MESSAGE CONTAINS CONVERSATIONID
						
			Spark.post(conversationId, (req, response) -> {
				 Message callbackMessage = new Message();
		         try{
					 // Metadata
			         PlatformMessageMetadata metadata = new MessageMetadata().asPlatformMessageMetadata();
			         metadata.initializeMetadata();
			         metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.OBSERVATION);
//			         metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.RESPONSE);
			         metadata.setSenderPlatformId(new EntityID(platform.getPlatformId()));
			         metadata.setConversationId(conversationId);
			         callbackMessage.setMetadata(metadata);
			         
			         JsonObject bodyObject = parser.parse(req.body()).getAsJsonObject();
					 JsonObject body = bodyObject.get("data").getAsJsonArray().get(0).getAsJsonObject();
					 // Put full device Id value
			         String subscriptionId = bodyObject.get("subscriptionId").getAsString();
			         body.addProperty("id", completeIds.get(subscriptionId));
					 
			         FIWAREv2Translator translator2 = new FIWAREv2Translator();
			         Model transformedModel = translator2.toJenaModelTransformed(body.toString());
			
			         //Finish creating the message
			         MessagePayload messagePayload = new MessagePayload(transformedModel);
			         callbackMessage.setPayload(messagePayload);
			         
			         System.out.println(callbackMessage.serializeToJSONLD());
			
			         // Send to InterIoT
			         publisher.publish(callbackMessage);
		         }
		         catch(Exception e){
		        	 return "500-KO";
		         }
		         return "200-OK";
	        });
			

		}
		catch (Exception e){ 
			logger.error("Error subscribing: " + e.getMessage());
			responseMessage.getMetadata().setStatus("KO");
			responseMessage.getMetadata().setMessageType(MessageTypesEnum.ERROR);
			responseMessage.getMetadata().asErrorMessageMetadata().setExceptionStackTrace(e);
		}
		return responseMessage;
	}

	@Override
	public Message unsubscribe(Message message) {
		Message responseMessage = createResponseMessage(message);
		try{
			String conversationId = message.getMetadata().asPlatformMessageMetadata().getSubscriptionId().get();
			logger.info("Unsubscribing from things in conversation {}...", conversationId);
						
			List<String[]> subId = subscriptionIds.get(conversationId); // RETRIEVE SUBSCRIPTION IDs AND SERVICES
			for (String[] subscription : subId){
				String subscriptionId = subscription[0];
				String service = subscription[1];
				try{
					OrionV2Utils.removeSubscription(BASE_PATH, subscriptionId, service, "");
				}catch (Exception e){
					logger.error("Error unsubscribing: " + e.getMessage());
					e.printStackTrace();
				}
			}
			subscriptionIds.remove(conversationId);
			
			responseMessage.getMetadata().setStatus("OK");
		}
		catch (Exception e){ 
			logger.error("Error unsubscribing: " + e.getMessage());
			responseMessage.getMetadata().setStatus("KO");
			responseMessage.getMetadata().setMessageType(MessageTypesEnum.ERROR);
			responseMessage.getMetadata().asErrorMessageMetadata().setExceptionStackTrace(e);
		}
		return responseMessage;
	}

	public boolean isTestMode() {
		return testMode;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	public String getTestResultFilePath() {
		return testResultFilePath;
	}

	public void setTestResultFilePath(String testResultFilePath) {
		this.testResultFilePath = testResultFilePath;
	}

	public String getTestResultFileName() {
		return testResultFileName;
	}

	public void setTestResultFileName(String testResultFileName) {
		this.testResultFileName = testResultFileName;
	}
	
	private String testServerGet(Request req){
		logger.info("Petición get recibida");
		return "Petición get recibida";
	}
		
	// For self-signed certificates
    private void setCustomTrustStore(String trustStore, String trustStorePass) throws Exception{
    	// TO AVOID PROBLEMS WITH SSL SELF-SIGNED CERTIFICATES
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			// Using null here initialises the TMF with the default trust store.
			tmf.init((KeyStore) null);

			// Get hold of the default trust manager
			X509TrustManager defaultTm = null;
			for (TrustManager tm : tmf.getTrustManagers()) {
			    if (tm instanceof X509TrustManager) {
			        defaultTm = (X509TrustManager) tm;
			        break;
			    }
			}

			FileInputStream myKeys = new FileInputStream(trustStore);

			// Custom trust store
			KeyStore myTrustStore = KeyStore.getInstance("JKS");
			myTrustStore.load(myKeys, trustStorePass.toCharArray());
			myKeys.close();
			tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(myTrustStore);
			
			// Get hold of the default trust manager
			X509TrustManager myTm = null;
			for (TrustManager tm : tmf.getTrustManagers()) {
			    if (tm instanceof X509TrustManager) {
			        myTm = (X509TrustManager) tm;
			        break;
			    }
			}

			// Wrap it in your own class.
			final X509TrustManager finalDefaultTm = defaultTm;
			final X509TrustManager finalMyTm = myTm;
			X509TrustManager customTm = new X509TrustManager() {
			    @Override
			    public X509Certificate[] getAcceptedIssuers() {
			        // If you're planning to use client-cert auth,
			        // merge results from "defaultTm" and "myTm".
			        return finalDefaultTm.getAcceptedIssuers();
			    }

			    @Override
			    public void checkServerTrusted(X509Certificate[] chain,
			            String authType) throws CertificateException {
			        try {
			            finalMyTm.checkServerTrusted(chain, authType);
			        } catch (CertificateException e) {
			            // This will throw another CertificateException if this fails too.
			            finalDefaultTm.checkServerTrusted(chain, authType);
			        }
			    }

			    @Override
			    public void checkClientTrusted(X509Certificate[] chain,
			            String authType) throws CertificateException {
			        // If you're planning to use client-cert auth,
			        // do the same as checking the server.
			        finalDefaultTm.checkClientTrusted(chain, authType);
			    }
			};
			
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { customTm }, null);
			OrionV2Utils.customSslContext = sslContext;
			
    }
	
    
    private void deviceDiscovery(String conversationId) throws Exception{
			// Discover all the registered devices
//			String responseBody = OrionV2Utils.discoverEntities(BASE_PATH);
//			logger.info(responseBody);
			FIWAREv2Translator translator = new FIWAREv2Translator();
            JsonParser parser = new JsonParser();
            
            // Create subscriptions by service and entity type
            for (String service : services) {
            	for (String type : types) {
            		JsonObject subjectObject = parser.parse(OrionV2Utils.buildJsonWithTypes(type)).getAsJsonObject();
            		JsonObject urlObject = new JsonObject();
            		JsonObject notificationObject = new JsonObject();
            		notificationObject.add("http", urlObject);
            		JsonObject subscription = new JsonObject();
            		subscription.add("subject", subjectObject);
            		subscription.add("notification", notificationObject);
            		// TODO: add "expires" attribute
            		String requestBody = subscription.toString();
            		
            		// Change the message callback address of the body for the address where the bridge is listening after a subscription 
            		requestBody = OrionV2Utils.buildJsonWithUrl(requestBody, callbackAddress.concat("/" + conversationId));
            		
            		// Create the subscription
            		String responseBody = OrionV2Utils.createSubscription(BASE_PATH, requestBody, service, ""); // Any sevicePath
				
            		String subscriptionId = parser.parse(responseBody).getAsJsonObject().get("id").getAsString();
            		// Keep track of the subscription ids
            		if(subscriptionId != null){
						discoverySubscriptions.add(new String[]{subscriptionId, service});
            		} 
            	}
            }
            
            // Add devices and keep device registry up to date
			Spark.post(conversationId, (req, response) -> { // Unique endpoint
		         try{
		        	JsonObject bodyObject = parser.parse(req.body()).getAsJsonObject();
		        	JsonArray devices = bodyObject.get("data").getAsJsonArray();
		        	String responseService = req.headers("Fiware-Service");
		        	String responseServicePath = req.headers("Fiware-ServicePath");
		        	String[] servicePath = responseServicePath.split(","); // List of servicePath values
		 			for(int i = 0; i < devices.size(); i++){
		 				String path;
		 				if(servicePath.length > 1)	path = servicePath[i];
		 				else path = servicePath[0];
		 				// Metadata
						Message addDeviceMessage = new Message();
						PlatformMessageMetadata metadata = new MessageMetadata().asPlatformMessageMetadata();
			            metadata.initializeMetadata();
			            metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.DEVICE_ADD_OR_UPDATE);
			            metadata.setSenderPlatformId(new EntityID(platform.getPlatformId()));
//			            metadata2.setConversationId(conversationId); 
			            // Create a new message payload with the information about the device
			            JsonObject deviceObject = devices.get(i).getAsJsonObject();
			            deviceObject = OrionV2Utils.setDeviceId(deviceObject, responseService, path);
			            Model deviceModel = translator.toJenaModel(deviceObject.toString());	            
			    		MessagePayload devicePayload = new MessagePayload(deviceModel);
			            
			            addDeviceMessage.setMetadata(metadata);
			            addDeviceMessage.setPayload(devicePayload);
			            
			            publisher.publish(addDeviceMessage);
			            logger.debug("Device_Add_Or_Update message has been published upstream.");
					}
					logger.debug(devices.size() + " new devices have been added to the registry");
		         }
		         catch(Exception e){
		        	 return "500-KO";
		         }
		         return "200-OK";
	        });
    }
    
    private void stopDeviceDiscovery(){
    	// Remove subscriptions
    	for (String[] subscription : discoverySubscriptions){
    		String subscriptionId = subscription[0];
    		String service = subscription[1];
			try{
				OrionV2Utils.removeSubscription(BASE_PATH, subscriptionId, service, "");
			}catch (Exception e){
				logger.error("Error unsubscribing: " + e.getMessage());
				e.printStackTrace();
			}
		}
    }
    
//	private String publishObservationToIntermw(Request req){
//		 Message callbackMessage = new Message();
//         try{
//			 // Metadata
//	         PlatformMessageMetadata metadata = new MessageMetadata().asPlatformMessageMetadata();
//	         metadata.initializeMetadata();
//	         metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.OBSERVATION);
//	         metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.RESPONSE);
//	         metadata.setSenderPlatformId(new EntityID(platform.getId().getId()));
//	         metadata.setConversationId(conversationId);
//	         callbackMessage.setMetadata(metadata);
//	         
//	         String body = req.body();
//	         FIWAREv2Translator translator = new FIWAREv2Translator();
//	         Model transformedModel = translator.toJenaModelTransformed(body);
//	
//	         //Finish creating the message
//	         MessagePayload messagePayload = new MessagePayload(transformedModel);
//	         callbackMessage.setPayload(messagePayload);
//	         
//	         System.out.println(callbackMessage.serializeToJSONLD());
//	
//	         // Send to InterIoT
//	         publisher.publish(callbackMessage);
//         }
//         catch(Exception e){
//        	 return "500-KO";
//         }
//         return "200-OK";
//	}
}
