package eu.interiot.intermw.bridge.orion;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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

    public static final String FIWARE_CREATE = "/v2/entities"; 
    
	
	public static void postToFiware(String url, String body) throws IOException{
    	
		httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);        
        HttpEntity httpEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        httpPost.setEntity(httpEntity);
        HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(httpClient!=null) {
				httpClient.close();
			}
		}
		
        logger.debug("Received response from the platform: {}", response.getStatusLine());
	
	}
	
}
