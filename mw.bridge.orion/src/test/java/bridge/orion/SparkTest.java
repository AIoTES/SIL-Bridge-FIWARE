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
package bridge.orion;

import java.net.URL;

import eu.interiot.intermw.bridge.BridgeConfiguration;
import eu.interiot.intermw.bridge.orion.OrionBridge;
import eu.interiot.intermw.commons.DefaultConfiguration;
//import eu.interiot.intermw.commons.model.OntologyId;
import eu.interiot.intermw.commons.model.Platform;
//import eu.interiot.intermw.commons.model.PlatformId;
//import eu.interiot.intermw.commons.model.PlatformType;

public class SparkTest {
	
	static BridgeConfiguration configuration;
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
//			configuration = new DefaultConfiguration("*.bridge.properties");
	        configuration = new BridgeConfiguration("OrionBridge.properties", "http://inter-iot.eu/example-platform1", new DefaultConfiguration("intermw.properties"));
			orionBridge = new OrionBridge(configuration, platform);
		}
		catch(Exception e){}
	}

}
