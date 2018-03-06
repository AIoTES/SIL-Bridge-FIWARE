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

public class FiwareUtils {

	private static CloseableHttpClient httpClient;
	String url;
    private final static Logger logger = LoggerFactory.getLogger(FiwareUtils.class);

    //Unsupported
    public static final String FIWARE_PLATFORM_REGISTER = "";
    public static final String FIWARE_PLATFORM_UNREGISTER = "";
    public static final String FIWARE_ENTITY_ACTUATION = "";
    
    //post
    public static final String FIWARE_ENTITY_REGISTER= "/v2/entities";
    //delete {entityId}
    public static final String FIWARE_ENTITY_UNREGISTER = "/v2/entities";
    //post {entityId}
    public static final String FIWARE_ENTITY_OBSERVATION = "/v2/entities";
    //get
    public static final String FIWARE_ENTITY_DISCOVERY = "/v2/entities";
    //get {entityId}
    public static final String FIWARE_ENTITY_QUERY = "/v2/entities";
    //post
    public static final String FIWARE_ENTITY_SUBSCRIBE = "/v2/subscriptions";
    //remove {subscriptionId}
    public static final String FIWARE_ENTITY_UNSUBSCRIBE = "/v2/subscriptions/";
       	
	public static HttpResponse registerEntity(String url, String body) {
		return null; 
	}
	
	public static HttpResponse unregisterEntity(String url, String entityId) {
		return null; 
	}
	
	public static HttpResponse publishEntityObservation(String url, String entityId ,String body) {
		return null; 
	}
	
	public static HttpResponse discoverEntities(String url) {
		return null; 
	}
	
	public static HttpResponse queryEntityById(String url, String entityId) {
		return null; 
	}
    
	public static HttpResponse createSubscription(String url, String body) {
		return null; 
	}

	public static HttpResponse removeSubscription(String url, String subscriptionId) {
		return null; 
	}
	
	
	
    public static HttpResponse postToFiware(String url, String body) throws IOException{
    		
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
    
    public static HttpResponse getFromFiware(String url) throws IOException{
		
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
    
    public static HttpResponse deteleInFiware(String url) throws IOException{
    	
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
	
}
