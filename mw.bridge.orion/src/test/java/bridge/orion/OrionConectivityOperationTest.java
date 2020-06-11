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

//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.logging.ConsoleHandler;
//import java.util.logging.Handler;
//import java.util.logging.Level;
//import java.util.logging.Logger;

//import org.junit.Before;
//import org.junit.Test;

//import com.google.common.collect.ImmutableMap;
//import com.spotify.docker.client.DefaultDockerClient;
//import com.spotify.docker.client.DockerClient;
//import com.spotify.docker.client.DockerClient.ListContainersParam;
//import com.spotify.docker.client.DockerException;
//import com.spotify.docker.client.messages.Container;
//import com.spotify.docker.client.messages.ContainerConfig;
//import com.spotify.docker.client.messages.HostConfig;
//import com.spotify.docker.client.messages.Image;
//import com.spotify.docker.client.messages.PortBinding;

//import eu.interiot.intermw.bridge.Context;
//import eu.interiot.intermw.bridge.orion.OrionBridge;
//import eu.interiot.intermw.commons.exceptions.ContextException;
//import eu.interiot.intermw.commons.exceptions.MiddlewareException;
//import eu.interiot.intermw.commons.model.Platform;
//import eu.interiot.intermw.commons.model.PlatformId;
//import eu.interiot.intermw.commons.model.PlatformType;
//import eu.interiot.intermw.commons.model.Thing;
//import eu.interiot.intermw.commons.model.ThingAttribute;
//import eu.interiot.intermw.commons.model.ThingId;

//public class OrionConectivityOperationTest {

//	private final String ATTR_KEY_1 = "attr1";
//	private final String ATTR_KEY_2 = "attr2";
//	private final String ATTR_VALUE_1 = "attrvalue1";
//	private final String ATTR_VALUE_2 = "attrvalue2";

//	private final String ID_THING_ID_1 = "+Thing_ID_1";
//	private final String PROP_TYPE = "type";
//	private final String ATTR_TYPE_TEXT = "Text";
//	private final String THING_TYPE_1 = "idonthavetype";

//	private OrionBridge client;
//	private Thing thing;

//	private final String FIWARE_IMAGE = "fiware/orion:latest";
//	private final String ORION_NAME = "orion-test-container";
//	private final String MONGO_IMAGE = "mongo:3.2";
//	private final String MONGO_NAME = "mongo-test-container";
//	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
//	private Handler consoleHandler = new ConsoleHandler();

	// Control only executed once
//	private static boolean first = true;

//	@Before
//	public void createAssets() throws ContextException {

//		if (first) {
//			// setupLog();
//			// setUpDocker();
//		}
//		try {
//			client = new OrionBridge(Context.getConfiguration(), new Platform(new PlatformId("http://inter-iot.eu/Test_Orion"), "Test", new PlatformType("FIWARE"), null, null));
//		} catch (MiddlewareException e) {
//			e.printStackTrace();
//		}

//		thing = new Thing(new ThingId(ID_THING_ID_1));

//		ThingAttribute attr = createThingAttribute(PROP_TYPE, THING_TYPE_1, ATTR_TYPE_TEXT);
//		thing.put(attr);

//		ThingAttribute attr1 = createThingAttribute(ATTR_KEY_1, ATTR_VALUE_1, "Text");
//		thing.put(attr1);

//		ThingAttribute attr2 = createThingAttribute(ATTR_KEY_2, ATTR_VALUE_2, "Text");
//		thing.put(attr2);

//	}

	/**
	 * See
	 * https://github.com/spotify/docker-client/blob/master/docs/user_manual.md#creating-a-docker-client
	 */
//	private void setUpDocker() {

//		first = false;

//		DockerClient docker = null;
//		List<Container> containers = null;

//		try {

//			docker = DefaultDockerClient.fromEnv().build();
//			if (docker != null) {
//				log.info("Docker created");
//			}

			// setUpContainer(docker, MONGO_IMAGE, MONGO_NAME);

//			containers = docker.listContainers(ListContainersParam.allContainers());
//			Container orion = null;

//			System.out.println("Pulling");
//			docker.pull(FIWARE_IMAGE);
			// Create container
			// +" -dbhost mongo"
//			HostConfig hostConfig = HostConfig.builder()
//					.portBindings(ImmutableMap.of("1026", Arrays.asList(PortBinding.of("", "1026"))))
//					.links("orioncluster_mongo_1:orioncluster_mongo_1").build();
//			final ContainerConfig config = ContainerConfig.builder().image(FIWARE_IMAGE).hostConfig(hostConfig)
//					.exposedPorts("1026").cmd("-dbhost 172.17.0.3:27017").build();


			// Map<String, List<PortBinding>> portBindings = new HashMap<String,
			// PortBinding>();
			// you can leave the host IP empty for the PortBinding.of first
			// parameter
			// portBindings.put("80/tcp", new ArrayList(PortBinding.of("",
			// "8080")));
			// HostConfig hostConfig =
			// HostConfig.builder().portBindings(portBindings).networkMode("bridge").build();
			// dockerClient.startContainer(containerId, hostConfig);

//			System.out.println("Creating container");
//			System.out.println(config.toString());
//			docker.createContainer(config, ORION_NAME);
//			System.out.println("Starting container");
//			docker.startContainer(ORION_NAME);

//			log.info("Starting " + FIWARE_IMAGE);
//			System.out.println("Starting " + FIWARE_IMAGE);
//			docker.startContainer(FIWARE_IMAGE);

//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		/*
		 * if (containers != null) { for (Container container : containers) {
		 * log.info("ToString-->" + container.toString()); log.info("Image-->" +
		 * container.image()); log.info("Container-->" + container.id()); if
		 * (container.image().equals(FIWARE_IMAGE)) {
		 * System.out.println("Found"); orion = container; break; } } if (orion
		 * != null) { log.info("Orion container exists. Starting.");
		 * 
		 * docker.startContainer(orion.id());
		 * 
		 * } else { log.info("Orion container does not exists."); List<Image>
		 * imageList = new ArrayList<>();
		 * 
		 * imageList = (ArrayList<Image>) docker.listImages();
		 * 
		 * log.info("Pulling " + FIWARE_IMAGE); // Pull image
		 * docker.pull(FIWARE_IMAGE); // Create container final ContainerConfig
		 * config = ContainerConfig.builder().image(FIWARE_IMAGE)
		 * .exposedPorts("1026/tcp").build();
		 * 
		 * docker.createContainer(config, ORION_NAME);
		 * docker.startContainer(ORION_NAME);
		 * 
		 * log.info("Starting " + FIWARE_IMAGE);
		 * docker.startContainer(FIWARE_IMAGE);
		 * 
		 * }
		 * 
		 * 
		 * 
		 * }
		 * 
		 * } catch (DockerCertificateException e1) { // TODO Auto-generated
		 * catch block e1.printStackTrace(); } catch (DockerException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } finally { // Stop all runnning containers try
		 * { List<Container> containerList = docker.listContainers(); for
		 * (Container container : containerList) {
		 * //docker.stopContainer(container.id(), 10); } } catch
		 * (DockerException | InterruptedException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 * 
		 * }
		 */

//	}

//	private void setUpContainer(DockerClient docker, String image, String name, String... ports) {
//		List<Container> containers = null;

//		try {
//			containers = docker.listContainers(ListContainersParam.allContainers());
//			Container container = null;
//			if (containers != null) {
//				for (Container containerItem : containers) {
//					log.info("ToString-->" + containerItem.toString());
//					if (containerItem.image().equals(image)) {
//						System.out.println("Found");
//						container = containerItem;
//						break;
//					}
//				}
//				if (container != null) {
//					log.info("The " + image + " container exists. Starting.");
//					docker.startContainer(container.id());
//				} else {

//					log.info("The " + image + " container does not exists. Creating.");
//					List<Image> imageList = new ArrayList<>();
//					imageList = (ArrayList<Image>) docker.listImages();
//					log.info("Pulling " + image);
					// Pull image
//					docker.pull(image);
//					// Create container
//					ContainerConfig config = null;
//					if (ports != null) {
//						config = ContainerConfig.builder().image(image).exposedPorts(ports).build();
//					} else {
//						config = ContainerConfig.builder().image(image).build();
//					}

//					docker.createContainer(config, name);
//					docker.startContainer(name);

//					log.info("Starting " + image);
//					docker.startContainer(image);

//				}
//			}
//		} catch (DockerException | InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//	}

//	private void setupLog() {
//		consoleHandler.setLevel(Level.FINER);
//		log.addHandler(consoleHandler);
//	}

//	@Test
//	public void testCreateThing() {
//TODO FF 20.9.2017, refactor
/*		try {
			client.create(thing);
		} catch (BridgeException e) {
			log.info("createEntity");
			log.info(e.getCause() + " -  -  - " + e.getMessage());
			e.printStackTrace();
		}
*/
//	}

//	@Test
//	public void testReadThing() {
//TODO FF 20.9.2017, refactor
		/*		OrionQuery query = new OrionQuery();
		query.setThingId(new ThingId(ID_THING_ID_1));
		try {
			log.info("Read test ");
			log.info(client.read(query).toString());
		} catch (BridgeException e) {
			log.info("readEntity");
			log.info(e.getCause() + " -  -  - " + e.getMessage());
			e.printStackTrace();
		}
*/
//	}

//	@Test
//	public void testupdateThing() throws Exception {
//TODO FF 20.9.2017, refactor
/*
		testCreateThing();

		ThingAttribute attr = createThingAttribute(ATTR_KEY_1, "a_new_value", "Text");
		thing.put(attr);
		try {
			client.update(thing);
		} catch (BridgeException e) {
			log.info("updateEntity");
			log.info(e.getCause() + " -  -  - " + e.getMessage());
			e.printStackTrace();
		}
		OrionQuery query = new OrionQuery();
		query.setThingId(new ThingId(ID_THING_ID_1));
		Thing thingNew = null;
		try {
			thingNew = client.read(query);
		} catch (BridgeException e) {
			log.info("readEntity");
			log.info(e.getCause() + " -  -  - " + e.getMessage());
			e.printStackTrace();
		}

		Assert.assertNotNull(thingNew);
		Assert.assertNotNull(thingNew.getAttribute(ATTR_KEY_1));
		Assert.assertEquals("a_new_value", thingNew.getAttributeValue(ATTR_KEY_1));
*/
//	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	protected ThingAttribute createThingAttribute(String key, String value, String type) {
//		ThingAttribute attr = new ThingAttribute(key, value, type);
//		return attr;
//	}

//	@Test
//	public void testDeleteAssets() {
//TODO FF 20.9.2017, refactor
/*		try {
			client.delete(new ThingId(ID_THING_ID_1));
		} catch (BridgeException e) {
			log.info("deleteEntity");
			log.info(e.getCause() + " -  -  - " + e.getMessage());
			e.printStackTrace();
		}*/
//	}

//}
