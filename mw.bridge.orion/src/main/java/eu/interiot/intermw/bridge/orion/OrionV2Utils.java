package eu.interiot.intermw.bridge.orion;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrionV2Utils {

	private static CloseableHttpClient httpClient;
	String url;
    private final static Logger logger = LoggerFactory.getLogger(OrionV2Utils.class);

    //Unsupported
    public static final String FIWARE_PLATFORM_REGISTER = "";
    public static final String FIWARE_PLATFORM_UNREGISTER = "";
    public static final String FIWARE_ENTITY_ACTUATION = "";
    
    //post
    private static final String FIWARE_ENTITY_REGISTER= "/v2/entities";
    //delete {entityId}
    private static final String FIWARE_ENTITY_UNREGISTER = "/v2/entities";
    //post {entityId}
    private static final String FIWARE_ENTITY_OBSERVATION = "/v2/entities";
    //get
    private static final String FIWARE_ENTITY_DISCOVERY = "/v2/entities";
    //get {entityId}
    private static final String FIWARE_ENTITY_QUERY = "/v2/op/query";
    //post
    private static final String FIWARE_ENTITY_SUBSCRIBE = "/v2/subscriptions";
    //remove {subscriptionId}
    private static final String FIWARE_ENTITY_UNSUBSCRIBE = "/v2/subscriptions/";
       	
	public static HttpResponse registerEntity(String baseUrl, String body) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_REGISTER;
		return postToFiware(completeUrl, body);
	}
	
	public static HttpResponse unregisterEntity(String baseUrl, String entityId) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_UNREGISTER+"/"+entityId;
		return deteleInFiware(completeUrl); 
	}
	
	public static HttpResponse publishEntityObservation(String baseUrl, String entityId ,String body) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_OBSERVATION+"/"+entityId;
		return deteleInFiware(completeUrl); 
	}
	
	public static HttpResponse discoverEntities(String baseUrl) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_DISCOVERY;
		return getFromFiware(completeUrl); 
	}
	
	//TODO Check ontology alignment with Pawel/Kasia
	//TODO Build a shortcut to query a single entity by id
	public static HttpResponse queryEntityById(String baseUrl, String body) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_QUERY+"/"+body;
		return postToFiware(completeUrl, body); 
	}
    
	
	public static HttpResponse createSubscription(String baseUrl, String body) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_SUBSCRIBE;
		return postToFiware(completeUrl, body); 
	}

	public static HttpResponse removeSubscription(String baseUrl, String subscriptionId) throws IOException {
		String completeUrl = baseUrl + FIWARE_ENTITY_UNSUBSCRIBE+"/"+subscriptionId;
		return deteleInFiware(completeUrl); 
	}
	
	
	
    private static HttpResponse postToFiware(String url, String body) throws IOException{
    		
		httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);        
        HttpEntity httpEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        httpPost.setEntity(httpEntity);
        HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			System.out.println(response.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(httpClient!=null) {
				httpClient.close();
			}
		}
		
        logger.debug("Received response from the platform: {}", response.getStatusLine());
		return response;

	}
    
    private static HttpResponse getFromFiware(String url) throws IOException{
		
		httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);        
        HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			System.out.println(response.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(httpClient!=null) {
				httpClient.close();
			}
		}
		
        logger.debug("Received response from the platform: {}", response.getStatusLine());
		return response;

	}
    
    private static HttpResponse deteleInFiware(String url) throws IOException{
    	
    	httpClient = HttpClientBuilder.create().build();
    	HttpDelete httpDelete = new HttpDelete(url);
    	HttpResponse response = null;
		try {
			response = httpClient.execute(httpDelete);
			System.out.println(response.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(httpClient!=null) {
				httpClient.close();
			}
		}
		return response;
    }
    
    public static String filterThingID(String thingId) {
		String filteredString;
		// Check algorithm is optimal+
		if (thingId.contains("http://inter-iot.eu/dev/")) {
			filteredString = thingId.replace("http://inter-iot.eu/dev/", "");
			return filteredString;
		} else if (thingId.contains("/")) {
			filteredString = thingId.replace("/", "%");
			return filteredString;
		} else if (thingId.contains("#")) {
			filteredString = thingId.replace("#", "+");
			return filteredString;
		} else {
			return thingId;
		}
	}
	
}
