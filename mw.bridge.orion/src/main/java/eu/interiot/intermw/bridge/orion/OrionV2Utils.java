package eu.interiot.intermw.bridge.orion;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
       	
	public static String registerEntity(String baseUrl, String body) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_REGISTER;
		return postToFiware(completeUrl, body);
	}
	
	public static String unregisterEntity(String baseUrl, String entityId) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_UNREGISTER+"/"+entityId;
		return deleteInFiware(completeUrl); 
	}
	
	public static String updateEntity(String baseUrl, String entityId, String body) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_UPDATE + "/" + entityId + "/attrs" ;
		String completeBody = removeId(body);
		return putToFiware(completeUrl, completeBody); 
	}
	
	public static String publishEntityObservation(String baseUrl, String entityId ,String body) throws IOException {
//		String completeUrl = baseUrl + FIWARE_ENTITY_OBSERVATION+"/"+entityId;
//		return putToFiware(completeUrl,body);
		String completeUrl = baseUrl + FIWARE_ENTITY_OBSERVATION+"/"+entityId+"/attrs";
		return postToFiware(completeUrl,body);
	}
	
	public static String discoverEntities(String baseUrl) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_DISCOVERY;
		return getFromFiware(completeUrl); 
	}
	
	//TODO Check ontology alignment with Pawel/Kasia
	//TODO Build a shortcut to query a single entity by id
	public static String queryEntityById(String baseUrl, String entityId) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_QUERY;
		String body = buildJsonWithIds(entityId);
		return postToFiware(completeUrl, body); 
	}
    
	public static String createSubscription(String baseUrl, String body) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_SUBSCRIBE;
		return buildJsonWithSubscriptionId(postToFiware(completeUrl, body)); 
	}

	public static String removeSubscription(String baseUrl, String subscriptionId) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_UNSUBSCRIBE+"/"+subscriptionId;
		return deleteInFiware(completeUrl); 
	}
	
    private static String postToFiware(String url, String body) throws IOException{
    		
		httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);        
        HttpEntity httpEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        httpPost.setEntity(httpEntity);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(httpClient!=null) {
				httpClient.close();
			}
		}		
        logger.info("Received response from the platform: {}", response.getStatusLine());
		return responseBody;
	}
    
    private static String getFromFiware(String url) throws IOException{
		httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);        
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
    
    private static String putToFiware(String url, String body) throws IOException{
		httpClient = HttpClientBuilder.create().build();
        HttpPut httpPut = new HttpPut(url); 
        HttpEntity httpEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
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
    
    private static String deleteInFiware(String url) throws IOException{
    	httpClient = HttpClientBuilder.create().build();
    	HttpDelete httpDelete = new HttpDelete(url);
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
    
    public static String filterThingID(String thingId) {
    	String filteredString = thingId;
		// Check algorithm is optimal+
    	if (filteredString.contains("http://inter-iot.eu/dev/")) {
			filteredString = thingId.replace("http://inter-iot.eu/dev/", "");
		} 
    	if (filteredString.contains("http://")) {
			filteredString = filteredString.replace("://", "_");
		}
		if (filteredString.contains("/")) {
			filteredString = filteredString.replace("/", "");
		}
		if (filteredString.contains("#")) {
			filteredString = filteredString.replace("#", "+");
		}
		if (filteredString.contains(":")) {
			filteredString = filteredString.replace(":", "");
		}
		return filteredString;
	}
	    
    public static String buildJsonWithIds(String... entityId){
    	JsonObject jsonObjectFinal = new JsonObject();
    	JsonArray jsonArray = new JsonArray();
    	for (String id : entityId) {
    		String transformedID = filterThingID(id);
        	
        	JsonObject jsonObjectId = new JsonObject(); 
        	jsonObjectId.addProperty("id", transformedID);       	
        	jsonArray.add(jsonObjectId);        	
		}    	
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
//		return platform.getId().getId();
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
    
    public static Set<String> getPlatformIds(Message message){
		Set<EntityID> entityIds = getEntityIDsFromPayloadAsEntityIDSet(message.getPayload(), EntityTypeDevice);
		Set<String> deviceIds = new HashSet<>();
		for (EntityID entityId : entityIds) {
			deviceIds.addAll(getIdFromPayload(entityId, message.getPayload()));
		}
		return deviceIds;
	}
    
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
