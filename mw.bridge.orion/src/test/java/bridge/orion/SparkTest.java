package bridge.orion;

import java.net.URL;

import eu.interiot.intermw.bridge.orion.OrionBridge;
import eu.interiot.intermw.commons.DefaultConfiguration;
import eu.interiot.intermw.commons.interfaces.Configuration;
//import eu.interiot.intermw.commons.model.OntologyId;
import eu.interiot.intermw.commons.model.Platform;
//import eu.interiot.intermw.commons.model.PlatformId;
//import eu.interiot.intermw.commons.model.PlatformType;

public class SparkTest {
	
	static Configuration configuration;
	static Platform platform;
	static OrionBridge orionBridge;
	static String BASE_PATH;
	static String platformId;

	public static void main(String[] args) {
		try{
			platformId = "http://inter-iot.eu/example-platform127";
//			platform = new Platform(new PlatformId(platformId), "Test", new PlatformType("FIWARE"), "http://www.w3.org/ns/sosa/Platform", new OntologyId("GOIoTP#SoftwarePlatform")); //baseUrl, Ontology
			platform = new Platform();
			 // SHOULD GET THESE VALUES FROM THE MESSAGE (AND SOME OF THEM FROM PROPERTIES)
			platform.setPlatformId(platformId);
	        platform.setClientId("test");
	        platform.setName("Example Platform #1");
	        platform.setType("FIWARE");
	        platform.setBaseEndpoint(new URL("http://localhost:4569/"));
	        platform.setLocation("http://test.inter-iot.eu/TestLocation");
			configuration = new DefaultConfiguration("*.bridge.properties");
			orionBridge = new OrionBridge(configuration, platform);
		}
		catch(Exception e){}
	}

}
