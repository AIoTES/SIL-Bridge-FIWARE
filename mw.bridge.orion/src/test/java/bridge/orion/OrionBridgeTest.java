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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.interiot.intermw.bridge.BridgeConfiguration;
import eu.interiot.intermw.bridge.orion.OrionBridge;
import eu.interiot.intermw.commons.DefaultConfiguration;
import eu.interiot.intermw.commons.exceptions.MiddlewareException;
import eu.interiot.intermw.commons.interfaces.Configuration;
import eu.interiot.intermw.commons.model.Platform;
import eu.interiot.message.Message;
import eu.interiot.message.ID.EntityID;
import eu.interiot.message.exceptions.MessageException;

public class OrionBridgeTest {

	Configuration configuration;
	Platform platform;

	/*
	 * @Before public void init() { try { configuration = new
	 * DefaultConfiguration("bridge.properties"); platform = new
	 * Platform("FIWARE_TEST", ) } catch (MiddlewareException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 */

	@Test
	public void tests() {

		BridgeConfiguration configuration;
		OrionBridge orionBridge;
		try {			
//			configuration = new DefaultConfiguration("*.bridge.properties");
			configuration = new BridgeConfiguration("OrionBridge.properties", "http://inter-iot.eu/example-platform1", new DefaultConfiguration("intermw.properties"));
			orionBridge = new OrionBridge(configuration, platform);
			
			// create Message objects from serialized messages
			URL url1 = Resources.getResource("messages/platform-register.json");
			String platformRegisterJson = Resources.toString(url1, Charsets.UTF_8);
			System.out.println(platformRegisterJson);
			Message platformRegisterMsg = new Message(platformRegisterJson);
			// create Platform object using platform-register message
			EntityID platformId = platformRegisterMsg.getMetadata().asPlatformMessageMetadata().getReceivingPlatformIDs().iterator().next();
//			Platform platform = new Platform(platformId.toString(), platformRegisterMsg.getPayload());
			Platform platform = new Platform();
			platform.setPlatformId(platformId.toString());
			 // SHOULD GET THESE VALUES FROM THE MESSAGE (AND SOME OF THEM FROM PROPERTIES)
	        platform.setClientId("test");
	        platform.setName("Example Platform #1");
	        platform.setType("FIWARE");
	        platform.setBaseEndpoint(new URL("http://localhost:4569/"));
	        platform.setLocation("http://test.inter-iot.eu/TestLocation");
			
			URL url2 = Resources.getResource("messages/thing-register-FIWARE.json");
			String thingRegisterJson = Resources.toString(url2, Charsets.UTF_8);
			System.out.println(thingRegisterJson);
			Message thingRegisterMsg = new Message(thingRegisterJson);
			
			URL url3 = Resources.getResource("messages/thing-query-FIWARE.json");
			String thingQueryJson1 = Resources.toString(url3, Charsets.UTF_8);
			System.out.println(thingQueryJson1);
			Message thingQueryMsg = new Message(thingQueryJson1);
			
			URL url4 = Resources.getResource("messages/thing-unregister-FIWARE.json");
			String thingUnregisterJson1 = Resources.toString(url4, Charsets.UTF_8);
			System.out.println(thingUnregisterJson1);
			Message thingUnregisterMsg = new Message(thingUnregisterJson1);
			
			URL url5 = Resources.getResource("messagesV2/08_platform_update_device2.json");
			String thingUpdateJson = Resources.toString(url5, Charsets.UTF_8);
			System.out.println(thingUpdateJson);
			Message thingUpdateMsg = new Message(thingUpdateJson);			
			
			orionBridge = new OrionBridge(configuration, platform);

			String basepath = configuration.getProperty("orion-base-path");
			assertNotNull(basepath);

			//orionBridge.send(thingRegisterMsg);
			//orionBridge.send(thingQueryMsg);
			//orionBridge.send(thingUnregisterMsg);
//			orionBridge.send(thingUpdateMsg);
			orionBridge.process(thingUpdateMsg);
			//FIWAREv2Translator translator = new FIWAREv2Translator();			
			//String body = translator.toFormatX(thingRegisterMsg.getPayload().getJenaModel());
			
			//String body = "{body}";
			//assertNotNull(body);

			//OrionV2Utils.registerEntity(basepath, body);
			//OrionV2Utils.createSubscription(basepath, body);
			
			/*
			 * // register platform orionBridge.send(platformRegisterMsg); Message
			 * responseMsg = publisher.retrieveMessage(); Set<MessageTypesEnum>
			 * messageTypesEnumSet = responseMsg.getMetadata().getMessageTypes();
			 * assertTrue(messageTypesEnumSet.contains(MessageTypesEnum.RESPONSE));
			 * assertTrue(messageTypesEnumSet.contains(MessageTypesEnum.PLATFORM_REGISTER));
			 */
			
		} catch (MiddlewareException | IOException | MessageException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//@Test
	public void TestBuildUrl(){
		String body = "{\r\n" + 
				"  \"description\": \"A subscription to get info about Room1\",\r\n" + 
				"  \"subject\": {\r\n" + 
				"    \"entities\": [\r\n" + 
				"      {\r\n" + 
				"        \"id\": \"Room1\",\r\n" + 
				"        \"type\": \"Room\"\r\n" + 
				"      }\r\n" + 
				"    ],\r\n" + 
				"    \"condition\": {\r\n" + 
				"      \"attrs\": [\r\n" + 
				"        \"pressure\"\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  },\r\n" + 
				"  \"notification\": {\r\n" + 
				"    \"http\": {\r\n" + 
				"      \"url\": \"http://localhost:1028/accumulate\"\r\n" + 
				"    },\r\n" + 
				"    \"attrs\": [\r\n" + 
				"      \"temperature\"\r\n" + 
				"    ]\r\n" + 
				"  },\r\n" + 
				"  \"expires\": \"2040-01-01T14:00:00.00Z\",\r\n" + 
				"  \"throttling\": 5\r\n" + 
				"}";
		String url = "http://localhost14antesdelpunto:4569";
		
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
    		System.out.println(jsonBody.toString());
    	}
	}

}
