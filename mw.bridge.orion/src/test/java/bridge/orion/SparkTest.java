package bridge.orion;

import eu.interiot.intermw.bridge.orion.OrionBridge;
import eu.interiot.intermw.commons.DefaultConfiguration;
import eu.interiot.intermw.commons.interfaces.Configuration;
import eu.interiot.intermw.commons.model.OntologyId;
import eu.interiot.intermw.commons.model.Platform;
import eu.interiot.intermw.commons.model.PlatformId;
import eu.interiot.intermw.commons.model.PlatformType;

public class SparkTest {
	
	static Configuration configuration;
	static Platform platform;
	static OrionBridge orionBridge;
	static String BASE_PATH;
	static String platformId;

	public static void main(String[] args) {
		try{
			platformId = "http://inter-iot.eu/example-platform127";
			platform = new Platform(new PlatformId(platformId), "Test", new PlatformType("FIWARE"), "http://www.w3.org/ns/sosa/Platform", new OntologyId("GOIoTP#SoftwarePlatform")); //baseUrl, Ontology
			configuration = new DefaultConfiguration("*.bridge.properties");
			orionBridge = new OrionBridge(configuration, platform);
		}
		catch(Exception e){}
	}

}
