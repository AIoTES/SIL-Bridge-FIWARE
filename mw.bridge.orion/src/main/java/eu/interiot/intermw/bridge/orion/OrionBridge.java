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

import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.interiot.intermw.bridge.abstracts.AbstractBridge;
import eu.interiot.intermw.commons.exceptions.MiddlewareException;
import eu.interiot.intermw.commons.interfaces.Configuration;
import eu.interiot.intermw.commons.model.Platform;
import eu.interiot.message.Message;
import eu.interiot.message.MessagePayload;
import eu.interiot.message.ID.EntityID;
import eu.interiot.message.managers.URI.URIManagerMessageMetadata.MessageTypesEnum;
import eu.interiot.message.utils.MessageUtils;
import eu.interiot.translators.syntax.FIWARE.FIWAREv2Translator;

@eu.interiot.intermw.bridge.annotations.Bridge(platformType = "FIWARE")
public class OrionBridge extends AbstractBridge {

	private final static String PROPERTIES_PREFIX = "orion-";
	private final String BASE_PATH;
	private String callbackAddres;

	private final Logger logger = LoggerFactory.getLogger(OrionBridge.class);

	public OrionBridge(Configuration configuration, Platform platform) throws MiddlewareException {
		super(configuration, platform);
		BASE_PATH = configuration.getProperty(PROPERTIES_PREFIX + "base-path");
		callbackAddres = configuration.getProperty(PROPERTIES_PREFIX + "callback-address");

	}
	
	@Override
	public Message registerPlatform(Message message) {
		Message responseMessage = MessageUtils.createResponseMessage(message);
		logger.info("Registering platform {}...", OrionV2Utils.getPlatformId(platform));
		logger.info("Platform {} has been registered.", OrionV2Utils.getPlatformId(platform));
		responseMessage.getMetadata().setStatus("OK");
		return responseMessage;
	}

	@Override
	public Message unrecognized(Message message) {
		logger.info("Unrecognized message type.");
		Message responseMessage = MessageUtils.createResponseMessage(message);
		responseMessage.getMetadata().setStatus("OK");
		return responseMessage;
	}

	@Override
	public Message unregisterPlatform(Message message) {
		Message responseMessage = MessageUtils.createResponseMessage(message);
		logger.info("Unregistering platform {}...", OrionV2Utils.getPlatformId(platform));
		logger.info("Platform {} has been unregistered.", OrionV2Utils.getPlatformId(platform));
		responseMessage.getMetadata().setStatus("OK");
		return responseMessage;
	}
		
	@Override
	public Message platformCreateDevice(Message message) {
		Message responseMessage = MessageUtils.createResponseMessage(message);
		try {
			logger.info("Creating devices...");
			FIWAREv2Translator translator = new FIWAREv2Translator();
			String body = translator.toFormatX(message.getPayload().getJenaModel());
			String responseBody = OrionV2Utils.registerEntity(BASE_PATH, body);			
			logger.info("Device/s {} has/have been created.", OrionV2Utils.getEntityIds(message));
			// Get the Model from the response
			Model translatedModel = translator.toJenaModel(responseBody);			
			// Create a new message payload for the response message
			MessagePayload responsePayload = new MessagePayload(translatedModel);
			// Attach the payload to the message
			responseMessage.setPayload(responsePayload);
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
	public Message platformDeleteDevice(Message message) {
		Message responseMessage = MessageUtils.createResponseMessage(message);
		try {
			logger.info("Removing devices...");
			Set<String> deviceIds = OrionV2Utils.getEntityIds(message);
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
	public Message platformUpdateDevice(Message message) {
		Message responseMessage = MessageUtils.createResponseMessage(message);
		logger.info("Updating devices...");
		Set<String> deviceIds = OrionV2Utils.getEntityIds(message);
		Set<EntityID> deviceIdsAsEntities = OrionV2Utils.getEntityIdsAsEntityIDSet(message);
		for(String deviceId : deviceIds){
			try {
				FIWAREv2Translator translator = new FIWAREv2Translator();
				String body = translator.toFormatX(message.getPayload().getJenaModel());
				String responseBody = OrionV2Utils.updateEntity(BASE_PATH, deviceId, body);
				// Get the Model from the response
				Model translatedModel = translator.toJenaModel(responseBody);			
				// Create a new message payload for the response message
				MessagePayload responsePayload = new MessagePayload(translatedModel);
				// Attach the payload to the message
				responseMessage.setPayload(responsePayload);
				logger.info("Device {} has been updated.", deviceId);
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
		Message responseMessage = MessageUtils.createResponseMessage(message);
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
		Message responseMessage = MessageUtils.createResponseMessage(message);
		try{
			Set<String> deviceIds = OrionV2Utils.getEntityIds(message);
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
	
	@SuppressWarnings("unused")
	@Override
	public Message actuate(Message message) {
		Message responseMessage = MessageUtils.createResponseMessage(message);
		Set<String> deviceIds = OrionV2Utils.getEntityIds(message);
		for(String deviceId : deviceIds){
			try {
				FIWAREv2Translator translator = new FIWAREv2Translator();
				String body = translator.toFormatX(message.getPayload().getJenaModel());
				// TODO Which is the API method that implements actuations?
				//OrionV2Utils.doAction(BASE_PATH, deviceId, body);					
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
		Message responseMessage = MessageUtils.createResponseMessage(message);
		Set<String> deviceIds = OrionV2Utils.getEntityIds(message);
		for(String deviceId : deviceIds){
			try {
				FIWAREv2Translator translator = new FIWAREv2Translator();
				String body = translator.toFormatX(message.getPayload().getJenaModel());
				String responseBody = OrionV2Utils.publishEntityObservation(BASE_PATH, deviceId, body);
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
		}			
		return responseMessage; 
	}
	
	@Override
	public Message error(Message message) {
		logger.info("Error occured in {}...", message);
		Message responseMessage = MessageUtils.createResponseMessage(message);
		responseMessage.getMetadata().setStatus("KO");
		responseMessage.getMetadata().setMessageType(MessageTypesEnum.ERROR);
		return responseMessage;
	}
	
	@Override
	public Message subscribe(Message message) {
		Message responseMessage = MessageUtils.createResponseMessage(message);
		try{
			FIWAREv2Translator translator = new FIWAREv2Translator();
			// Translate the message into Fiware JSON
			String requestBody = translator.toFormatX(message.getPayload().getJenaModel());
			// Add the callback address to the body 
			requestBody = OrionV2Utils.buildJsonWithUrl(requestBody, callbackAddres);
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
		Message responseMessage = MessageUtils.createResponseMessage(message);
		try{
			Set<String> entityIds = OrionV2Utils.getEntityIds(message);
			for (String entityId : entityIds) {
				logger.info("Unsubscribing {}...", entityId);
				String responseBody = OrionV2Utils.removeSubscription(BASE_PATH, entityId);
				// Get the Model from the response
				FIWAREv2Translator translator = new FIWAREv2Translator();
				Model translatedModel = translator.toJenaModel(responseBody);			
				// Create a new message payload for the response message
				MessagePayload responsePayload = new MessagePayload(translatedModel);
				// Attach the payload to the message
				responseMessage.setPayload(responsePayload);
			}
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
}
