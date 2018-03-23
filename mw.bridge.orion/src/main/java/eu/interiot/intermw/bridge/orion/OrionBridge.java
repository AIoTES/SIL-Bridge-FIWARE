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

import org.apache.http.HttpResponse;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.interiot.intermw.bridge.abstracts.AbstractBridge;
import eu.interiot.intermw.commons.exceptions.MiddlewareException;
import eu.interiot.intermw.commons.interfaces.Configuration;
import eu.interiot.intermw.commons.model.Platform;
import eu.interiot.message.Message;
import eu.interiot.message.MessagePayload;
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
		logger.debug("Registering platform {}...", OrionV2Utils.getPlatformId(platform));
		logger.debug("Platform {} has been registered.", OrionV2Utils.getPlatformId(platform));
		responseMessage.getMetadata().setStatus("OK");
		return responseMessage;
	}

	@Override
	public Message unrecognized(Message message) {
		logger.debug("Unrecognized message type.");
		Message responseMessage = MessageUtils.createResponseMessage(message);
		responseMessage.getMetadata().setStatus("OK");
		return responseMessage;
	}

	@Override
	public Message unregisterPlatform(Message message) {
		Message responseMessage = MessageUtils.createResponseMessage(message);
		logger.debug("Unregistering platform {}...", OrionV2Utils.getPlatformId(platform));
		logger.debug("Platform {} has been unregistered.", OrionV2Utils.getPlatformId(platform));
		responseMessage.getMetadata().setStatus("OK");
		return responseMessage;
	}
		
	@Override
	public Message platformCreateDevice(Message message) {
		Message responseMessage = MessageUtils.createResponseMessage(message);
		try {
			logger.debug("Creating devices...");
			FIWAREv2Translator fiwareTranslator = new FIWAREv2Translator();
			String body = fiwareTranslator.toFormatX(message.getPayload().getJenaModel());
			OrionV2Utils.registerEntity(BASE_PATH, body);			
			logger.debug("Device/s {} has/have been created.", OrionV2Utils.getEntityIds(message));
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
			logger.debug("Removing devices...");
			Set<String> deviceIds = OrionV2Utils.getEntityIds(message);
			for(String deviceId : deviceIds){
				String transformedId = OrionV2Utils.filterThingID(deviceId);
				OrionV2Utils.unregisterEntity(BASE_PATH, transformedId);
				logger.debug("Device {} has been removed.", transformedId);
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
		logger.debug("Updating devices...");
		Set<String> deviceIds = OrionV2Utils.getEntityIds(message);
		for(String deviceId : deviceIds){
			try {
				FIWAREv2Translator translator = new FIWAREv2Translator();
				String body = translator.toFormatX(message.getPayload().getJenaModel());
				OrionV2Utils.updateEntity(BASE_PATH, deviceId, body);	
				logger.debug("Device {} has been updated.", deviceId);
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
			String responseBody = OrionV2Utils.discoverEntities(BASE_PATH).getEntity().getContent().toString();
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
				HttpResponse response = OrionV2Utils.queryEntityById(BASE_PATH, deviceId);
				logger.info(response.toString());	
				String responseBody = response.getEntity().getContent().toString();
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
				OrionV2Utils.publishEntityObservation(BASE_PATH, deviceId, body);					
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
		logger.debug("Error occured in {}...", message);
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
			String responseBody = OrionV2Utils.createSubscription(BASE_PATH, requestBody).getEntity().toString();
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
				logger.debug("Unsubscribing {}...", entityId);
				OrionV2Utils.removeSubscription(BASE_PATH, entityId);
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
