/**
 * INTER-IoT. Interoperability of IoT Platforms.
 * INTER-IoT is a R&D project which has received funding from the European 
 * Union’s Horizon 2020 research and innovation programme under grant 
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
import java.util.HashMap;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.interiot.intermw.bridge.BridgeConfiguration;
import eu.interiot.intermw.bridge.abstracts.AbstractBridge;
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
import eu.interiot.translators.syntax.FIWARE.FIWAREv2Translator;
import spark.Request;
import spark.Spark;

@eu.interiot.intermw.bridge.annotations.Bridge(platformType = "http://inter-iot.eu/FIWARE")
public class OrionBridge extends AbstractBridge {

//	private final static String PROPERTIES_PREFIX = "orion-";
	private String BASE_PATH;
	private String callbackAddress;
	private Map<String,String> subscriptionIds = new HashMap<String,String>();

	private final Logger logger = LoggerFactory.getLogger(OrionBridge.class);
	
	protected boolean testMode = false;
	protected String testResultFilePath;
	protected String testResultFileName;
	
	// TrustStore for self-signed certificates
    private String trustStore;
	private String trustStorePass;

	public OrionBridge(BridgeConfiguration configuration, Platform platform) throws MiddlewareException {
		super(configuration, platform);
		try{
			String url = platform.getBaseEndpoint().toString(); // Get base path from the Register Platform message
			if (url.endsWith("/")) url = url.substring(0, url.length()-1); // Just in case
			BASE_PATH = url;
			callbackAddress = this.bridgeCallbackUrl.toString(); // Same base callback address for all bridges
		
			// Raise the server
//			String callbackAddress = configuration.getProperty("bridge.callback.subscription.context");
//			get(callbackAddress, (req, res) -> testServerGet(req));
//			post(callbackAddress,(req, res) -> publishObservationToIntermw(req));
			OrionV2Utils.service = configuration.getProperty("service");
			OrionV2Utils.servicePath = configuration.getProperty("servicePath");
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
			    String transformedID = OrionV2Utils.filterThingID(thingId);
	        	
	        	JsonObject deviceObject = new JsonObject(); 
	        	deviceObject.addProperty("id", transformedID);
	        	deviceObject.addProperty("type", "device"); // TODO: CHANGE THIS. The message does not contain entity type information
	            OrionV2Utils.registerEntity(BASE_PATH, deviceObject.toString());
	        }
			
			
//			String responseBody = OrionV2Utils.registerEntity(BASE_PATH, body);			
			logger.info("Device/s {} has/have been created.", OrionV2Utils.getPlatformIds(message));
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
			Set<String> deviceIds = OrionV2Utils.getPlatformIds(message);
			for(String deviceId : deviceIds){
				String transformedId = OrionV2Utils.filterThingID(deviceId);
				String responseBody = OrionV2Utils.unregisterEntity(BASE_PATH, transformedId);
				// Get the Model from the response
				FIWAREv2Translator translator = new FIWAREv2Translator();
				Model translatedModel = translator.toJenaModel(responseBody);			
				// Create a new message payload for the response message
				MessagePayload responsePayload = new MessagePayload(translatedModel);
				// Attach the payload to the message
				responseMessage.setPayload(responsePayload);
				logger.info("Device {} has been removed.", transformedId);
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
		Set<String> deviceIds = OrionV2Utils.getPlatformIds(message);
		for(String deviceId : deviceIds){
			try {
				FIWAREv2Translator translator = new FIWAREv2Translator();
				String body = translator.toFormatX(message.getPayload().getJenaModel());
				String transformedId = OrionV2Utils.filterThingID(deviceId);
				String responseBody = OrionV2Utils.updateEntity(BASE_PATH, transformedId, body);
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
		Message responseMessage = createResponseMessage(message);
		logger.debug("ListDevices started...");
		try{
			// Discover all the registered devices
			String responseBody = OrionV2Utils.discoverEntities(BASE_PATH);
			logger.info(responseBody);
			FIWAREv2Translator translator = new FIWAREv2Translator();
			// Create the model from the response JSON
			Model translatedModel = translator.toJenaModel(responseBody);
			// Create a new message payload for the response message
			MessagePayload responsePayload = new MessagePayload(translatedModel);
			// Attach the payload to the message
			responseMessage.setPayload(responsePayload);
			// Set the OK status
			responseMessage.getMetadata().setStatus("OK");
			
			/// Initialize device registry
			Message deviceRegistryInitializeMessage = new Message();
			PlatformMessageMetadata metadata = new MessageMetadata().asPlatformMessageMetadata();
            metadata.initializeMetadata();
            metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.DEVICE_REGISTRY_INITIALIZE);
            metadata.setSenderPlatformId(new EntityID(platform.getPlatformId()));
//            metadata.setConversationId(conversationId); 
            MessagePayload devicePayload = new MessagePayload(translatedModel);
            
            deviceRegistryInitializeMessage.setMetadata(metadata);
            deviceRegistryInitializeMessage.setPayload(devicePayload);
            publisher.publish(deviceRegistryInitializeMessage);
            logger.debug("Device_Registry_Initialize message has been published upstream.");
            
            // TODO: KEEP DEVICE REGISTRY UP TO DATE
			
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
	public Message query(Message message) {
		Message responseMessage = createResponseMessage(message);
		try{
			Set<String> deviceIds = OrionV2Utils.getPlatformIds(message);
			for(String deviceId : deviceIds){
				String responseBody = OrionV2Utils.queryEntityById(BASE_PATH, deviceId);
				logger.info(responseBody);	
				//String responseBody = response.getEntity().getContent().toString();
				FIWAREv2Translator translator = new FIWAREv2Translator();
				// Create the model from the response JSON
				Model translatedModel = translator.toJenaModel(responseBody);
				// Create a new message payload for the response message
				MessagePayload responsePayload = new MessagePayload(translatedModel);
				// Attach the payload to the message
				responseMessage.setPayload(responsePayload);
			}
			// Set the OK status
			responseMessage.getMetadata().setStatus("OK");
			// Publish the message to INTER-MW. The publisher is global (and it is tested)
			publisher.publish(responseMessage);
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
				String transformedId = OrionV2Utils.filterThingID(deviceId);
				String responseBody = OrionV2Utils.updateEntity(BASE_PATH, transformedId, body);	
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
			    JsonObject bodyObject = parser.parse(body).getAsJsonObject();
			    JsonObject data = bodyObject.get("data").getAsJsonArray().get(0).getAsJsonObject();
			    String deviceId = data.get("id").getAsString();
			    logger.debug("DeviceId: " + deviceId);
			    data.remove("id");
			    data.remove("type");
			    String responseBody = OrionV2Utils.publishEntityObservation(BASE_PATH, deviceId, data.toString());
				
//				String responseBody = OrionV2Utils.publishEntityObservation(BASE_PATH, deviceId, body);
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
			FIWAREv2Translator translator = new FIWAREv2Translator();
			// Translate the message into Fiware JSON
			
			List<String> entities;
			SubscribeReq subsreq = new SubscribeReq(message);
			entities = subsreq.getDeviceIds();
			
			if (entities.isEmpty()) {
	            throw new PayloadException("No entities of type Device found in the Payload.");
	        } else if (entities.size() > 1) {
	            throw new PayloadException("Only one device is supported by Subscribe operation.");
	        }
			
			String thingId = entities.iterator().next();
		    String conversationId = message.getMetadata().getConversationId().orElse(null);
		    logger.debug("Subscribing to thing {} using conversationId {}...", thingId, conversationId);
			
		    JsonParser parser = new JsonParser();
		    JsonObject subjectObject = parser.parse(OrionV2Utils.buildJsonWithIds(thingId)).getAsJsonObject();
		    JsonObject urlObject = new JsonObject();
		    JsonObject notificationObject = new JsonObject();
		    notificationObject.add("http", urlObject);
		    JsonObject subscription = new JsonObject();
		    subscription.add("subject", subjectObject);
		    subscription.add("notification", notificationObject);
		    String requestBody = subscription.toString();
												
			// Change the message callback address of the body for the address where the bridge is listening after a subscription 
			requestBody = OrionV2Utils.buildJsonWithUrl(requestBody, callbackAddress.concat("/" + conversationId));
			
			// Create the subscription
			String responseBody = OrionV2Utils.createSubscription(BASE_PATH, requestBody);
			// Get the Model from the response
			Model translatedModel = translator.toJenaModel(responseBody);			
			// Create a new message payload for the response message
			MessagePayload responsePayload = new MessagePayload(translatedModel);
			// Attach the payload to the message
			responseMessage.setPayload(responsePayload);
			// Set OK status
			responseMessage.getMetadata().setStatus("OK");
			// If test, save the subscription id in order to be able to unsubscribe
			
			String subscriptionId = parser.parse(responseBody).getAsJsonObject().get("id").getAsString();
			subscriptionIds.put(conversationId, subscriptionId); // SUBSCRIPTION ID IS NEEDED FOR UNSUBSCRIBE METHOD. UNSUBSCRIBE MESSAGE CONTAINS CONVERSATIONID
			
			Spark.post(conversationId, (req, response) -> { // SOFIA2 sends data using a HTTP PUT query
				 Message callbackMessage = new Message();
		         try{
					 // Metadata
			         PlatformMessageMetadata metadata = new MessageMetadata().asPlatformMessageMetadata();
			         metadata.initializeMetadata();
			         metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.OBSERVATION);
			         metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.RESPONSE);
			         metadata.setSenderPlatformId(new EntityID(platform.getPlatformId()));
			         metadata.setConversationId(conversationId);
			         callbackMessage.setMetadata(metadata);
			         
			         String body = req.body();
			         FIWAREv2Translator translator2 = new FIWAREv2Translator();
			         Model transformedModel = translator2.toJenaModelTransformed(body);
			
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
			
			if(testMode && testResultFilePath != null && testResultFileName != null){
				OutputStream os = new FileOutputStream(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString() + "/" + testResultFilePath + testResultFileName).toString());
				os.write(responseBody.getBytes());
				os.flush();
				os.close();
			}
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
			String subId = subscriptionIds.get(conversationId); // RETRIEVE SUBSCRIPTION IDs
			String responseBody = OrionV2Utils.removeSubscription(BASE_PATH, subId);
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
		logger.info("Petici�n get recibida");
		return "Petici�n get recibida";
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
