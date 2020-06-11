/*
 * Copyright 2016-2018 Universitat Politècnica de València
 * Copyright 2016-2018 Università della Calabria
 * Copyright 2016-2018 Prodevelop, SL
 * Copyright 2016-2018 Technische Universiteit Eindhoven
 * Copyright 2016-2018 Fundación de la Comunidad Valenciana para la
 * Investigación, Promoción y Estudios Comerciales de Valenciaport
 * Copyright 2016-2018 Rinicom Ltd
 * Copyright 2016-2018 Association pour le développement de la formation
 * professionnelle dans le transport
 * Copyright 2016-2018 Noatum Ports Valenciana, S.A.U.
 * Copyright 2016-2018 XLAB razvoj programske opreme in svetovanje d.o.o.
 * Copyright 2016-2018 Systems Research Institute Polish Academy of Sciences
 * Copyright 2016-2018 Azienda Sanitaria Locale TO5
 * Copyright 2016-2018 Alessandro Bassi Consulting SARL
 * Copyright 2016-2018 Neways Technologies B.V.
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.interiot.intermw.bridge.orion;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.interiot.intermw.commons.model.Platform;
import eu.interiot.message.Message;
import eu.interiot.message.MessageMetadata;
import eu.interiot.message.MessagePayload;
import eu.interiot.message.ID.EntityID;
import eu.interiot.message.managers.URI.URIManagerMessageMetadata.MessageTypesEnum;
import eu.interiot.message.metadata.PlatformMessageMetadata;
import eu.interiot.translators.syntax.FIWARE.FIWAREv2Translator;

public class OrionV2Utils {
	String url;
	private static CloseableHttpClient httpClient;
    private final static Logger logger = LoggerFactory.getLogger(OrionV2Utils.class);

    //Unsupported
    public static final String FIWARE_PLATFORM_REGISTER = "";
    public static final String FIWARE_PLATFORM_UNREGISTER = "";
    public static final String FIWARE_ENTITY_ACTUATION = "";
    
    //post
    private static final String FIWARE_ENTITY_REGISTER= "/v2/entities";
    //delete {entityId}
    private static final String FIWARE_ENTITY_UNREGISTER = "/v2/entities";
    //put
    private static final String FIWARE_ENTITY_UPDATE = "/v2/entities";
    //get
    private static final String FIWARE_ENTITY_DISCOVERY = "/v2/entities";    
    //post {entityId}
    private static final String FIWARE_ENTITY_OBSERVATION = "/v2/entities";
    
    //get {entityId}
    private static final String FIWARE_ENTITY_QUERY = "/v2/op/query";
    //post
    private static final String FIWARE_ENTITY_SUBSCRIBE = "/v2/subscriptions";
    //remove {subscriptionId}
    private static final String FIWARE_ENTITY_UNSUBSCRIBE = "/v2/subscriptions";
    
    public static final String URIoldssn = "http://purl.oclc.org/NET/ssnx/ssn#";
    
    // Types
    public static final String EntityTypeDevice = FIWAREv2Translator.FIWAREbaseURI + "Entity";
    public static final String EntityTypeSSNDevice = URIoldssn + "Device";
    
    public static final String propHasIdURI = FIWAREv2Translator.FIWAREbaseURI + "hasId";
    private static String deviceIdPrefix;  //from properties,  default value "http://inter-iot.eu/dev/"
        
    // Authentication
    static String token = null;
    static SSLContext customSslContext = null;
    
    public static void setDeviceIdPrefix(String prefix){
    	if (!prefix.endsWith("/")) prefix = prefix + "/"; // Just in case
    	deviceIdPrefix = prefix;
    }
    
    public static String getDeviceIdPrefix(){
    	return deviceIdPrefix;
    }
    
	public static String registerEntity(String baseUrl, String body, String service, String servicePath) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_REGISTER;
		return postToFiware(completeUrl, body, service, servicePath);
	}
	
	
	public static String unregisterEntity(String baseUrl, String entityId, String type, String service, String servicePath) throws IOException {
		// Add entity type to avoid ambiguity
		String completeUrl = baseUrl + FIWARE_ENTITY_UNREGISTER+"/"+entityId + "?type=" + type;
		return deleteInFiware(completeUrl, service, servicePath); 
	}
	
	
	public static String updateEntity(String baseUrl, String entityId, String type, String body, String service, String servicePath) throws IOException {
		// Add entity type to avoid ambiguity
		String completeUrl = baseUrl + FIWARE_ENTITY_UPDATE + "/" + entityId + "/attrs" + "?type=" + type;
		String completeBody = removeId(body);
		return putToFiware(completeUrl, completeBody, service, servicePath); 
	}
		
	public static String publishEntityObservation(String baseUrl, String entityId, String type, String body, String service, String servicePath) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_OBSERVATION+"/"+entityId+"/attrs" + "?type=" + type;
		return postToFiware(completeUrl,body, service, servicePath);
	}
	
	public static String discoverEntities(String baseUrl) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_DISCOVERY;
		return getFromFiware(completeUrl, "", ""); 
	}
	
	//TODO Check ontology alignment with Pawel/Kasia
	//TODO Build a shortcut to query a single entity by id
	
	public static String queryEntityById(String baseUrl, String entityId, String type, String service, String servicePath) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_QUERY;
		String body = buildJsonWithIdAndType(entityId, type);
		return postToFiware(completeUrl, body, service, servicePath);
	}
    
	public static String createSubscription(String baseUrl, String body, String service, String servicePath) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_SUBSCRIBE;
		return buildJsonWithSubscriptionId(postToFiware(completeUrl, body, service, servicePath)); 
	}

	public static String removeSubscription(String baseUrl, String subscriptionId, String service, String servicePath) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_UNSUBSCRIBE+"/"+subscriptionId;
		return deleteInFiware(completeUrl, service, servicePath); 
	}
	
    private static String postToFiware(String url, String body, String service, String servicePath) throws IOException{   	
    	
		if(customSslContext == null)  httpClient = HttpClientBuilder.create().build();
		else httpClient = HttpClientBuilder.create().setSSLContext(customSslContext).build();
        HttpPost httpPost = new HttpPost(url);        
        HttpEntity httpEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        httpPost.setEntity(httpEntity);   
        
        if (service != null && service !="") httpPost.setHeader("Fiware-Service", service);     
        if (servicePath != null && servicePath !="") httpPost.setHeader("Fiware-ServicePath", servicePath);
        if (token != null) httpPost.setHeader("x-auth-token", token);
        HttpResponse response = null;
        String responseBody = "";
		try {
			response = httpClient.execute(httpPost);
			if(response != null && response.getEntity() != null){
				responseBody = EntityUtils.toString(response.getEntity());
				if(responseBody == null || responseBody.length() == 0){
					if(url.contains(FIWARE_ENTITY_SUBSCRIBE)){
						responseBody = response.getHeaders("Location")[0].toString();
					}
				}
				logger.info(responseBody);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(httpClient!=null) {
				httpClient.close();
			}
		}		
        logger.info("Received response from the platform: {}", response.getStatusLine());
		return responseBody;
	}
    
    private static String getFromFiware(String url, String service, String servicePath) throws IOException{
    	if(customSslContext == null)  httpClient = HttpClientBuilder.create().build();
		else httpClient = HttpClientBuilder.create().setSSLContext(customSslContext).build();
        HttpGet httpGet = new HttpGet(url);        
        if (token != null) httpGet.setHeader("x-auth-token", token);
        if (service != null && service !="") httpGet.setHeader("Fiware-Service", service);     
        if (servicePath != null && servicePath !="") httpGet.setHeader("Fiware-ServicePath", servicePath);
        HttpResponse response = null;
        String responseBody = "";
		try {
			response = httpClient.execute(httpGet);
			if(response != null && response.getEntity() != null){
				responseBody = EntityUtils.toString(response.getEntity());
				logger.info(responseBody);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(httpClient!=null) {
				httpClient.close();
			}
		}		
        logger.info("Received response from the platform: {}", response.getStatusLine());        
		return responseBody;
	}
    
    private static String putToFiware(String url, String body, String service, String servicePath) throws IOException{
    	if(customSslContext == null)  httpClient = HttpClientBuilder.create().build();
		else httpClient = HttpClientBuilder.create().setSSLContext(customSslContext).build();
        HttpPut httpPut = new HttpPut(url); 
        HttpEntity httpEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        if (token != null) httpPut.setHeader("x-auth-token", token);
        if (service != null && service !="") httpPut.setHeader("Fiware-Service", service);     
        if (servicePath != null && servicePath !="") httpPut.setHeader("Fiware-ServicePath", servicePath);
        httpPut.setEntity(httpEntity);
        HttpResponse response = null;
        String responseBody = "";
        try {
			response = httpClient.execute(httpPut);
			if(response != null && response.getEntity() != null){
				responseBody = EntityUtils.toString(response.getEntity());
				logger.info(responseBody);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(httpClient!=null) {
				httpClient.close();
			}
		}		
        logger.info("Received response from the platform: {}", response.getStatusLine());
		return responseBody;
	}
    
    private static String deleteInFiware(String url, String service, String servicePath) throws IOException{
    	if(customSslContext == null)  httpClient = HttpClientBuilder.create().build();
		else httpClient = HttpClientBuilder.create().setSSLContext(customSslContext).build();
    	HttpDelete httpDelete = new HttpDelete(url);
    	if (token != null) httpDelete.setHeader("x-auth-token", token);
    	 if (service != null && service !="") httpDelete.setHeader("Fiware-Service", service);     
         if (servicePath != null && servicePath !="") httpDelete.setHeader("Fiware-ServicePath", servicePath);
    	HttpResponse response = null;
    	String responseBody = "";
    	try {
			response = httpClient.execute(httpDelete);
			if(response != null && response.getEntity() != null){
				responseBody = EntityUtils.toString(response.getEntity());
				logger.info(responseBody);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(httpClient!=null) {
				httpClient.close();
			}
		}
		return responseBody;
    }
    
    // Old implementation
//    public static String filterThingID(String thingId) {
//    	String filteredString = thingId;
//    	if (filteredString.contains("http://inter-iot.eu/dev/")) { // TODO: get value from properties
//			filteredString = thingId.replace("http://inter-iot.eu/dev/", "");
//		} 
//    	if (filteredString.contains("http://")) {
//			filteredString = filteredString.replace("://", "_");
//		}
//		if (filteredString.contains("/")) {
//			filteredString = filteredString.replace("/", "");
//		}
//		if (filteredString.contains("#")) {
//			filteredString = filteredString.replace("#", "+");
//		}
//		if (filteredString.contains(":")) {
//			filteredString = filteredString.replace(":", "");
//		}
//		return filteredString;
//	}
    
    // New algorithm
  public static String[] filterThingID(String thingId) {
	  /*Output:
	   * 0 - entity id
	   * 1 - entity type
	   * 2 - service (empty String for default value)
	   * 3 - servicePath (empty String for default value)
	   * */
	// "http://inter-iot.eu/dev/{service}/{type}/{entityId}#{servicePath}"
  	String filteredString = thingId;
  	String servicePath = "";
  	String service = "";
  	String type;
  	String id;
  	String[] result;
  	if (filteredString.contains(OrionV2Utils.deviceIdPrefix)) {
			filteredString = thingId.replace(OrionV2Utils.deviceIdPrefix, "");
			if(thingId.contains("#")){
				// Not the default servicePath
				String[] splitId = filteredString.split("#");
				servicePath = "/" + splitId[1]; // Only one "#" character allowed in deviceId.
				filteredString = splitId[0];
			}
			String[] splitId = filteredString.split("/");
			if(splitId.length>2) service = splitId[splitId.length - 3];
    		type = splitId[splitId.length - 2]; 
    		id = splitId[splitId.length - 1]; 
    		
    		result = new String[4];
    		result[0] = id;
    		result[1] = type;
    		result[2] = service;
    		result[3] = servicePath;
    		
		} else result = new String[]{thingId};
		
		return result;
  	
	}
    
    public static String createThingId(String id, String type, String service, String servicePath){
    	// "http://inter-iot.eu/dev/{service}/{type}/{entityId}#{servicePath}"
    	String thingId = OrionV2Utils.deviceIdPrefix;
    	if(!Strings.isNullOrEmpty(service)) thingId = thingId + service + "/";
    	thingId = thingId + type + "/";
    	thingId = thingId + id;
    	// Add "#{servicePath}" only if it is not the default value
    	if(!Strings.isNullOrEmpty(servicePath) && !servicePath.equals("/")){
    		if(servicePath.startsWith("/")) servicePath = servicePath.substring(1);
    		thingId = thingId + "#" + servicePath;
    	} 
    	return thingId;
    }
    
    public static String setDeviceId(String entity, String service, String servicePath){
    	JsonParser parser = new JsonParser();
    	JsonElement element = parser.parse(entity);
         
        if(element instanceof JsonArray){
        	JsonArray input = element.getAsJsonArray();
        	JsonArray output = new JsonArray();
        	Gson gson = new Gson();
        	for(int i=0; i<input.size(); i++){
        		 output.add(setDeviceId(input.get(i).getAsJsonObject(), service, servicePath));
        	}
        	return gson.toJson(output);
        }else{
        	return setDeviceId(element.getAsJsonObject(), service, servicePath).toString();
        }
    }
    
    public static JsonObject setDeviceId(JsonObject entity, String service, String servicePath){
    	String devId = entity.get("id").getAsString();
        String type = entity.get("type").getAsString();
        entity.addProperty("id", createThingId(devId, type, service, servicePath));
        return entity;
    }
	    
    public static String buildJsonWithIds(String... entityId){
    	JsonObject jsonObjectFinal = new JsonObject();
    	JsonArray jsonArray = new JsonArray();
    	for (String id : entityId) {
    		String[] entityID = filterThingID(id);
        	String transformedID = entityID[0];
        	    		
        	JsonObject jsonObjectId = new JsonObject(); 
        	jsonObjectId.addProperty("id", transformedID);  
        	// TODO: add type
        	jsonArray.add(jsonObjectId);        	
		}    	
    	jsonObjectFinal.add("entities", jsonArray);
    	
    	return jsonObjectFinal.toString();
    }
    
    public static String buildJsonWithTypes(String... entityType){
    	JsonObject jsonObjectFinal = new JsonObject();
    	JsonArray jsonArray = new JsonArray();
    	for (String type : entityType) {
    		
        	JsonObject jsonObjectId = new JsonObject(); 
        	jsonObjectId.addProperty("idPattern", ".*");
        	jsonObjectId.addProperty("type", type);  
        	// Add idPattern?
        	jsonArray.add(jsonObjectId);        	
		}    	
    	jsonObjectFinal.add("entities", jsonArray);
    	
    	return jsonObjectFinal.toString();
    }
    
    public static String buildJsonWithIdAndType(String entityId, String entityType){
    	JsonObject jsonObjectFinal = new JsonObject();
    	JsonArray jsonArray = new JsonArray();
        JsonObject jsonObjectId = new JsonObject(); 
        jsonObjectId.addProperty("id", entityId);  
        jsonObjectId.addProperty("type", entityType);
        jsonArray.add(jsonObjectId);        	    	
    	jsonObjectFinal.add("entities", jsonArray);
    	
    	return jsonObjectFinal.toString();
    }
        
    public static String buildJsonWithUrl(String body, String url){
    	JsonParser parser = new JsonParser();
    	JsonElement jsonBody = parser.parse(body);
    	if(jsonBody.isJsonObject()){
    		if(jsonBody.isJsonObject()){
    		    JsonObject jsonBodyObject = jsonBody.getAsJsonObject();    		    
    		    JsonObject jsonNotification = jsonBodyObject.getAsJsonObject("notification");
    		    if(jsonNotification.isJsonObject()){
    		    	JsonObject jsonHttp = jsonNotification.getAsJsonObject("http");
    		    	if(jsonHttp.isJsonObject()){
    		    		jsonHttp.addProperty("url", url);    		    		
        		    }
    		    }
    		}    		
    	}
    	return jsonBody.toString();	
    }
    
    public static String buildJsonWithSubscriptionId(String responseBody){
    	JsonObject jsonObjectFinal = new JsonObject();
    	String[] locationSplit = responseBody.split("/");
    	String location = locationSplit[locationSplit.length-1];
    	jsonObjectFinal.addProperty("id", location);
    	return jsonObjectFinal.toString();
    }
    
    public static String removeId(String body){
    	JsonParser parser = new JsonParser();
    	JsonElement jsonBody = parser.parse(body);
    	if(jsonBody.isJsonObject()){    		
		    JsonObject jsonBodyObject = jsonBody.getAsJsonObject();
		    jsonBodyObject.remove("id");    		  		
    	}
    	return jsonBody.toString();	
    }
    
    public static String getPlatformId(Platform platform){
		return platform.getPlatformId();
	}
    
    public static Set<String> getEntityIDsFromPayload(MessagePayload payload, String entityType) {
        Model model = payload.getJenaModel();
        return model.listStatements(new SimpleSelector(null, RDF.type, model.createResource(entityType))).toSet().stream().map(x -> x.getSubject().toString()).collect(Collectors.toSet());
    }
    
    public static Set<EntityID> getEntityIDsFromPayloadAsEntityIDSet(MessagePayload payload, String entityType) {
        Model model = payload.getJenaModel();
        return model.listStatements(new SimpleSelector(null, RDF.type, model.createResource(entityType))).toSet().stream().map(x -> new EntityID(x.getSubject().toString())).collect(Collectors.toSet());
    }
    
    public static Set<String> getEntityIds(Message message){
		return getEntityIDsFromPayload(message.getPayload(), EntityTypeDevice);
	}
    
    public static Set<EntityID> getEntityIdsAsEntityIDSet(Message message){
		return getEntityIDsFromPayloadAsEntityIDSet(message.getPayload(), EntityTypeDevice);
	}
    
    public static Set<String> getIdFromPayload(EntityID entityID, MessagePayload payload) {
        Model payloadModel = payload.getJenaModel();
        Set<String> names = new HashSet<>();
        Property hasName = payloadModel.createProperty(propHasIdURI);
        StmtIterator stmtIt = payloadModel.listStatements(new SimpleSelector(entityID.getJenaResource(), hasName, (RDFNode) null));
        while (stmtIt.hasNext()) {
            RDFNode node = stmtIt.next().getObject();
            if (node.isLiteral()) {
                names.add(node.asLiteral().getValue().toString());
            } else {
                names.add(node.toString());
            }
        }
        return names;
    }
    
//    public static Set<String> getPlatformIds(Message message){
//		Set<EntityID> entityIds = getEntityIDsFromPayloadAsEntityIDSet(message.getPayload(), EntityTypeDevice);
//		Set<String> deviceIds = new HashSet<>();
//		for (EntityID entityId : entityIds) {
//			deviceIds.addAll(getIdFromPayload(entityId, message.getPayload()));
//		}
//		return deviceIds;
//	}
    
    /**
     * Generate metadata for a test message device
     * @param messageResponse
     * @param messageTypesEnum
     */
    public static void generateDeviceMetadataToMessageResponse(Message messageResponse, MessageTypesEnum messageTypesEnum) {
    	MessageMetadata metadata = new MessageMetadata();
		metadata.initializeMetadata();
        metadata.setMessageType(messageTypesEnum);
        messageResponse.setMetadata(metadata);
    }
    
    /**
     * Generate metadata for a test message platform
     * @param messageResponsePlatform
     * @param platform
     */
    public static void generatePlatformMetadaToMessageResponse(Message messageResponsePlatform, MessageTypesEnum messageTypesEnum, String platform) {
    	PlatformMessageMetadata metadataPlatform = new MessageMetadata().asPlatformMessageMetadata();
        metadataPlatform.initializeMetadata();
        metadataPlatform.setMessageType(messageTypesEnum);
        metadataPlatform.addReceivingPlatformID(new EntityID(platform));
        messageResponsePlatform.setMetadata(metadataPlatform);
    }
    
    /**
     * 
     * @param messageResponse
     * @param BASE_PATH
     * @throws UnsupportedOperationException
     * @throws IOException
     */
    public static void generatePayloadToMessageResponse(Message messageResponse, String BASE_PATH) throws UnsupportedOperationException, IOException {
    	String responseBody = OrionV2Utils.discoverEntities(BASE_PATH);
		FIWAREv2Translator translator = new FIWAREv2Translator();
		// Create the model from the response JSON
		Model translatedModel = translator.toJenaModel(responseBody);
		// Create a new message payload for the response message
		MessagePayload responsePayload = new MessagePayload(translatedModel);
		// Attach the payload to the message
		messageResponse.setPayload(responsePayload);
    }
    
}
