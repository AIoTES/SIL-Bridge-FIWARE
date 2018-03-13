/**
 * INTER-IoT. Interoperability of IoT Platforms.
 * INTER-IoT is a R&D project which has received funding from the European 
 * Union’s Horizon 2020 research and innovation programme under grant 
 * agreement No 687283.
 * 
 * Copyright (C) 2016-2018, by (Author's company of this file):
 * - Prodevelop S.L.
 * 
 *
 * For more information, contact:
 * - @author <a href="mailto:mllorente@prodevelop.es">Miguel A. Llorente</a>  
 * - Project coordinator:  <a href="mailto:coordinator@inter-iot.eu"></a>
 *  
 *
 *    This code is licensed under the ASL license, available at the root
 *    application directory.
 */
package eu.interiot.intermw.bridge.orion;

import static spark.Spark.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.jena.rdf.model.Model;
import org.interiot.fiware.ngsiv2.client.ApiException;
import org.interiot.fiware.ngsiv2.client.model.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.interiot.intermw.bridge.abstracts.AbstractBridge;
import eu.interiot.intermw.bridge.exceptions.BridgeException;
import eu.interiot.intermw.bridge.orion.client.OrionApiClient;
import eu.interiot.intermw.comm.broker.exceptions.BrokerException;
import eu.interiot.intermw.commons.Context;
import eu.interiot.intermw.commons.exceptions.IllegalActionException;
import eu.interiot.intermw.commons.exceptions.MiddlewareException;
import eu.interiot.intermw.commons.exceptions.UnknownActionException;
import eu.interiot.intermw.commons.exceptions.UnsupportedActionException;
import eu.interiot.intermw.commons.interfaces.Configuration;
import eu.interiot.intermw.commons.interfaces.MwFactory;
import eu.interiot.intermw.commons.model.Platform;
import eu.interiot.intermw.commons.model.SubscriptionId;
import eu.interiot.message.EntityID;
import eu.interiot.message.Message;
import eu.interiot.message.MessageMetadata;
import eu.interiot.message.MessagePayload;
import eu.interiot.message.URI.URIManagerMessageMetadata;
import eu.interiot.message.exceptions.MessageException;
import eu.interiot.message.exceptions.payload.PayloadException;
import eu.interiot.message.metaTypes.PlatformMessageMetadata;
import eu.interiot.message.utils.INTERMWDemoUtils;
import eu.interiot.translators.syntax.IllegalSyntaxException;
import eu.interiot.translators.syntax.FIWARE.FIWAREv2Translator;

@eu.interiot.intermw.bridge.annotations.Bridge(platformType = "FIWARE")
public class OrionBridge extends AbstractBridge {

	private final String NO_TYPE_TEXT = "no_type";
	private final String TYPE_KEY = "type";
	private final int ENTITY_EXISTS_ERROR_CODE = 422;

	private final static String DEFAULT_ATTRIBUTE_TYPE = "Text";
	private final static String PROPERTIES_PREFIX = "orion-";

	private final String BASE_PATH;

	private OrionApiClient client;
	private MwFactory mwFactory;
	private int callbackPort;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private CloseableHttpClient httpClient;
	// private Broker broker;
	// private Publisher<Message> publisher;

	private final Logger logger = LoggerFactory.getLogger(OrionBridge.class);

	public OrionBridge(Configuration configuration, Platform platform) throws MiddlewareException {
		super(configuration, platform);
		// FIXME make bridges multi-instance
		client = new OrionApiClient(configuration.getProperties(PROPERTIES_PREFIX));
		mwFactory = Context.mwFactory();
		// TODO commons context and broker context. This is very confusing
		// broker = eu.interiot.intermw.comm.broker.Context.getBroker();
		BASE_PATH = configuration.getProperty("orion-base-path");
		callbackPort = Integer.parseInt(configuration.getProperty("bridge-callback-port"));

	}

	public OrionBridge(Configuration configuration, Platform platform, String URL) throws MiddlewareException {
		super(configuration, platform);
		// FIXME make bridges multi-instance
		client = new OrionApiClient(configuration.getProperties(PROPERTIES_PREFIX), URL);
		BASE_PATH = configuration.getProperty("orion-base-path");

		mwFactory = Context.mwFactory();
		// TODO commons context and broker context. This is very confusing
		// broker = eu.interiot.intermw.comm.broker.Context.getBroker();
		callbackPort = Integer.parseInt(configuration.getProperty("bridge-callback-port"));
	}

	private void create(String thingId, MessagePayload payload) throws BridgeException {

		// Transform to a compatible ID in FIWARE
		// String transformedID = filterThingID(thingId);
		// XXX Question SRIPAS --> DO we need a new for each bridge instance??
		FIWAREv2Translator fiwareTranslator = new FIWAREv2Translator();

		// Properties properties = configuration.getProperties();

		String url = null;
		try {
			url = new URL(configuration.getProperty("orion-url")).toString();
		} catch (Exception e) {
			throw new BridgeException("Failed to read Example bridge configuration: " + e.getMessage());
		}

		String body = null;
		try {
			body = fiwareTranslator.toFormatX(payload.getJenaModel());
			OrionV2Utils.registerEntity(BASE_PATH, body);

		} catch (IllegalSyntaxException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/**
		 * 
		 * try {
		 * 
		 * //Orion library has a class 'Entity' representing a device/thing/entity
		 * capable to produce data. Entity fiwareEntity = new Entity();
		 * fiwareEntity.setId(transformedID);
		 * 
		 * 
		 * //TODO FF, 20-09-2017, Commented out, as things were sent without attributes.
		 * //All hardcoded, but should work.... fiwareEntity.setType(NO_TYPE_TEXT);
		 * Attributes attributes = new Attributes(); Attribute attr = new Attribute();
		 * attr.setType("no_type"); attr.setValue("no_attributes");
		 * attributes.put("no_attributes", attr); /* if
		 * (interiotThing.containsKey(TYPE_KEY)) {
		 * fiwareEntity.setType(interiotThing.getAttributeValue(TYPE_KEY)); } else {
		 * fiwareEntity.setType(NO_TYPE_TEXT); } // hack for demo in Geneva. Actually,
		 * no creation should come // without attributes. if
		 * (interiotThing.getAttributes().size() == 0) { interiotThing.put(new
		 * ThingAttribute("no_attributes", "no_attributes", "no_type")); }
		 * 
		 * Attributes attributes = createAttributes(interiotThing);
		 */

		/*
		 * try { client.createEntity(fiwareEntity, null);
		 * client.updateOrAppendEntityAttributes(fiwareEntity.getId(), attributes,
		 * fiwareEntity.getType(), null); } catch (ApiException e) { if (e.getCode() !=
		 * ENTITY_EXISTS_ERROR_CODE) { throw new BridgeException(e); } } } catch
		 * (Exception e) { throw new BridgeException(e); }
		 * 
		 */
	}

	/*
	 * private Thing read(Query query) throws BridgeException { try { OrionQuery
	 * _query = (OrionQuery) query;
	 * 
	 * Entity retrievedEntity; Attributes retrievedAttributes;
	 * 
	 * retrievedEntity = client.retrieveEntity(query.getEntityId(), query.getType(),
	 * query.getAttributesInCommaSeparatedString(),
	 * _query.getOptionsInCommaSeparatedString());
	 * 
	 * retrievedAttributes = client.retrieveEntityAttributes(query.getEntityId(),
	 * query.getType(), query.getAttributesInCommaSeparatedString(),
	 * _query.getOptionsInCommaSeparatedString());
	 * 
	 * return fiwareEntityToInteriotThing(retrievedEntity, retrievedAttributes); }
	 * catch (Exception e) { throw new BridgeException(e); } }
	 * 
	 */

	private void publishObservation(String thingId, Message message) throws BridgeException, MessageException {
		// FIXME HACK: getAttributeFromPayload assumes that
		// there is an observation inside the payload
		// It will not work otherwise
		String key = INTERMWDemoUtils.getAttrKeyToUpdateFromPayload(message.getPayload());
		String value = INTERMWDemoUtils.getAttrValueToUpdateFromPayload(message.getPayload());
		String type = INTERMWDemoUtils.getAttrTypeToUpdateFromPayload(message.getPayload());

		String transformedID = OrionV2Utils.filterThingID(thingId);

		// Transform to a compatible ID in FIWARE
		// String transformedID = filterThingID(thingId);
		// XXX Question SRIPAS --> DO we need a new for each bridge instance??
		FIWAREv2Translator fiwareTranslator = new FIWAREv2Translator();

		// Properties properties = configuration.getProperties();

		String body = null;
		try {
			body = fiwareTranslator.toFormatX(message.getPayload().getJenaModel());
			OrionV2Utils.publishEntityObservation(BASE_PATH, transformedID, body);
		} catch (IllegalSyntaxException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/*
		 * try { Entity fiwareEntity = new Entity(); fiwareEntity.setId(transformedID);
		 * 
		 * Attributes attributes = new Attributes(); Attribute attr = new Attribute();
		 * if(type == null || type.equals("")) type = DEFAULT_ATTRIBUTE_TYPE;
		 * attr.setType(type); attr.setValue(value);
		 * attributes.put(filterThingAttrKey(key), attr);
		 * 
		 * client.updateOrAppendEntityAttributes(fiwareEntity.getId(), attributes,
		 * fiwareEntity.getType(), null);
		 * 
		 * } catch (Exception e) { throw new BridgeException(e); }
		 */
	}

	private String filterThingAttrKey(String key) {

		// Check http://telefonicaid.github.io/fiware-orion/api/v2/stable/ Field
		// syntax restrictions
		// &, ?, / and #.
		String newKey;
		if (key.contains("http://inter-iot.eu/ex/")) {
			newKey = key.replace("http://inter-iot.eu/ex/", "");
		} else if (key.contains("&")) {
			newKey = key.replace("&", "_AND_");
		} else if (key.contains("?")) {
			newKey = key.replace("?", "_QUESTION_");
		} else if (key.contains("/")) {
			newKey = key.replace("/", "_SLASH_");
		} else if (key.contains("#")) {
			newKey = key.replace("#", "_NUMBERSIGN_");
		} else {
			newKey = key;
		}

		return newKey;
	}

	/**
	 * How to implement the functionality of deleting only one type with the ID???
	 * 
	 * @param thingId
	 * @throws IOException
	 */
	private void delete(String thingId) throws BridgeException, IOException {

		String transformedID = OrionV2Utils.filterThingID(thingId);
		OrionV2Utils.unregisterEntity(BASE_PATH, transformedID);
		
		/*
		try {
			client.removeEntity(transformedID, null);
		} catch (Exception e) {
			throw new BridgeException(e);
		}
		*/
	}

	private void subscribe(String thingID, String conversationId) throws BridgeException {
		try {
			Subscription subscription = new Subscription();

			subscription.setSubject(new Subject(new Entities[] { new Entities(thingID, null) }));

			UUID randomUniqueEndpoint = UUID.randomUUID();
			String generateURLCallback = BASE_PATH + ":" + callbackPort + "/" + randomUniqueEndpoint;

			// publisher =
			// broker.createPublisher(BrokerTopics.BRIDGE_IPSM.getTopicName() +
			// "_" + platform.getId().getId(), Message.class);

			post("/" + randomUniqueEndpoint, (req, res) -> {
				Message callbackMessage = new Message();
				PlatformMessageMetadata metadata = new MessageMetadata().asPlatformMessageMetadata();
				metadata.initializeMetadata();
				// conversationID must be overriden, because the answer to a
				// subscription must share the same cID
				metadata.setConversationId(conversationId);
				metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.OBSERVATION);
				metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.RESPONSE);
				// metadata.setSenderPlatformId(URI.create(platform.getId().getId())); OLD
				// MESSAGING CODE
				metadata.setSenderPlatformId(new EntityID(platform.getId().getId()));
				callbackMessage.setMetadata(metadata);
				// A test
				Gson gson = new Gson();
				NotifyContextRequest ncr = gson.fromJson(req.body(), NotifyContextRequest.class);
				System.out.println(ncr.toString());

				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse(req.body()).getAsJsonObject();

				String id = jsonObject.get("id").getAsString();

				// FIXME The field "weight is not generic, we must find a way to
				// make this process generic"
				Float value = jsonObject.get("weight").getAsJsonObject().get("value").getAsFloat();

				// Random UUID for the observation sice is not provided by the
				// platform
				// Sets the timestamp in the callback since is not provided by
				// FIWARE
				MessagePayload payload = INTERMWDemoUtils.createWeightObservationPayload(UUID.randomUUID().toString(),
						Calendar.getInstance(), id, value);
				callbackMessage.setPayload(payload);
				publisher.publish(callbackMessage);
				return "200-OK";

			});

			Notification notification = new Notification(new URL(generateURLCallback));
			subscription.setNotification(notification);
			client.createANewSubscription(subscription);

		} catch (Exception e) {
			throw new BridgeException(e);
		}

	}

	private static String initHttpListener() {

		return null;

	}

	private void unsubscribe(SubscriptionId subscriptionId) throws BridgeException {
		try {
			client.deleteSubscription(subscriptionId.getId());
		} catch (ApiException e) {
			throw new BridgeException(e);
		}
	}

	/**
	 * XXX NOT TESTED public void cleanAllSubscriptons() throws BridgeException {
	 * while (cleanSubscriptions()); }
	 */

	/**
	 * 
	 * @return true if there are still pending subscriptions to delete
	 * @throws BridgeException
	 */
	private void cleanSubscriptions() throws BridgeException {
		// FIXME retrieveSubscriptions method fails when gets parameters.
		// Whithout parameters, it acts by default, returning 10 first
		// subscriptions.

		int i = 0;

		try {
			List<Subscription> subscriptions = client.retrieveSubscriptions(null, null, null);
			log.info("Retrieved " + subscriptions.size() + " subscriptions.");
			for (Subscription subscription : subscriptions) {
				log.info("Deleting: " + subscription.getId());
				unsubscribe(new SubscriptionId(subscription.getId()));
				i++;
			}

		} catch (ApiException e) {
			throw new BridgeException(e);
		}

	}

	/**
	 * TODO this will be replaced by IPSM calls
	 * 
	 * @return
	 */
	/*
	 * private Thing fiwareEntityToInteriotThing(Entity entity, Attributes
	 * retrievedAttributes) { Thing interiotThing = mwFactory.createThing(new
	 * ThingId(entity.getId()));
	 * 
	 * if (entity.getType() != null) { ThingAttribute attribute =
	 * mwFactory.createThingAttribute(TYPE_KEY, entity.getType());
	 * interiotThing.put(attribute); }
	 * 
	 * for (String attributeKey : retrievedAttributes.keySet()) { ThingAttribute
	 * attribute = mwFactory.createThingAttribute(attributeKey,
	 * retrievedAttributes.get(attributeKey).getValue());
	 * interiotThing.put(attribute); }
	 * 
	 * return interiotThing; }
	 * 
	 * protected Attributes createAttributes(Thing interiotThing) { Attributes
	 * attributes = new Attributes(); for (ThingAttribute attribute : interiotThing)
	 * { if (attribute.getKey() != null && attribute.getKey().equals(TYPE_KEY)) {
	 * continue; }
	 * 
	 * Attribute attr = new Attribute(); attr.setType(getType(attribute));
	 * attr.setValue(attribute.getValue().toString());
	 * attributes.put(attribute.getKey(), attr); }
	 * 
	 * return attributes; }
	 * 
	 * protected String getType(ThingAttribute attribute) { String type =
	 * attribute.getType();
	 * 
	 * if (StringUtils.isEmpty(type)) { type = DEFAULT_ATTRIBUTE_TYPE; }
	 * 
	 * return type; }
	 */
	class Subject {
		public Entities[] entities;

		public Subject(Entities[] entities) {
			this.entities = entities;
		}
	}

	class Entities {
		public String idPattern;
		public String type;

		public Entities(String idPattern, String type) {
			this.idPattern = idPattern;
			this.type = type;
		}
	}

	class Notification {
		public URL http;

		public Notification(URL url) {
			this.http = url;
		}
	}

	class URL {
		public String url;

		public URL(String url) {
			this.url = url;
		}
	}

	private void subscribeById(String conversationId, String... thingIds) throws BridgeException {
		for (String thingId : thingIds) {
			subscribe(thingId, conversationId);
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public void send(Message message) throws BridgeException {
		Set<URIManagerMessageMetadata.MessageTypesEnum> messageTypesEnumSet = message.getMetadata().getMessageTypes();
		try {
			if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.THING_REGISTER)) {
				registerThing(message);
			} 
			else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.THING_UNREGISTER)) {
				unregisterThing(message);
			} 
			else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.THING_UPDATE)
					&& messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.OBSERVATION)) {
				updateThing(message);
			}	 
			else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.QUERY)) {
				query(message);
			} 
			else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.SUBSCRIBE)) {
				subscribe(message);
			} 
			else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.UNSUBSCRIBE)) {
				unsubscribe(message);
			} 
			else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.DISCOVERY)) {
				discover(message);
			} 
			else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.PLATFORM_REGISTER)) {
				registerPlatform(message);
			} 
			else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.PLATFORM_UNREGISTER)) {
				unregisterPlatform(message);
			} 
			else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.UNRECOGNIZED)) {
				throw new UnknownActionException("The action is labelled as UNRECOGNIZED and thus is unprocessable by component " + this.getClass().getName() + " in platform " + platform.getId().getId());
			} 
			else {
				throw new UnknownActionException("The message type is not properly handled and can't be processed" + this.getClass().getName() + " in platform " + platform.getId().getId());
			}
			// TODO For now we create a generic response message. Think
			// about sending a specific status

			/*
			Message responseMessage = createResponseMessage(message);
			try {
				publisher.publish(responseMessage);
			} catch (BrokerException e) {
				log.error("Error publishing response");
				throw new MessageException("error publishing response", e);
			}
			*/

		} catch (Exception e) {
			throw new BridgeException(e.toString());
		}

	}

	@Override
	public void discover(Message message) {
		try {
			// get the body from the http response. HttpResponse comes from the http request
			// to the FIWATRE Platform.
			String responseBody = OrionV2Utils.discoverEntities(BASE_PATH).getEntity().getContent().toString();
			// create a new internal response message for INTER-MW
			Message responseMessage = createResponseMessage(message);
			// instantiates a FIWARE syntactic translator
			FIWAREv2Translator translator = new FIWAREv2Translator();
			// translate from plain FIWARE-json to FIWARE json-ld. Gets a JENA (RDF) Model
			Model translatedModel = translator.toJenaModel(responseBody);
			// create a new message payload for the response message
			MessagePayload responsePayload = new MessagePayload(translatedModel);
			// attach the payload to the message
			responseMessage.setPayload(responsePayload);
			// publish the message to INTER-MW. The publisher is global (and it is tested)
			publisher.publish(responseMessage);
		} 
		catch (IOException | BrokerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void query(Message message) {
		// TODO Review url and change the method's entityId
		Set<String> entityIds = INTERMWDemoUtils.getEntityIDsFromPayload(message.getPayload(), "http://inter-iot.eu/syntax/FIWAREv2#Entity");
		for (String entityId : entityIds) {
			try {
				HttpResponse response = OrionV2Utils.queryEntityById(BASE_PATH, entityId);
				logger.info(response.toString());
			} catch (IOException e) {
				logger.error("Error on query: " + e.getMessage());
			}
		}
	}

	@Override
	public void registerPlatform(Message message) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void unregisterPlatform(Message message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerThing(Message message) {
		try {
			// TODO Discuss with pawel EntityTypeDevice to enum
			Set<String> entityIds = INTERMWDemoUtils.getEntityIDsFromPayload(message.getPayload(), INTERMWDemoUtils.EntityTypeDevice);
	
			// XXX Discuss with matevz, flavio At this point we can
			// do two things, iterate over the list and do atomic
			// creations or introduce a bulk creation method
			// For the moment. It is implemented the first option,
			// in order to preserve the Bridge API TODO consider
			// changing it
			// TODO This type will be changed when the new message classes are finished
			entityIds = INTERMWDemoUtils.getEntityIDsFromPayload(message.getPayload(), "http://inter-iot.eu/syntax/FIWAREv2#Entity");
			
			for (String entityId : entityIds) {
				// TODO Loop over the payload to include possible
				// observations in the creation or a schema of the
				// 'Thing'				
				create(entityId, message.getPayload());				
			}
		} 
		catch (BridgeException e) {
			logger.error("Error registering thing: " + e.getMessage());
		}
	}
	
	@Override
	public void updateThing(Message message) {
		try{
			// TODO Discuss with pawel EntityTypeDevice to enum
			Set<String> entityIds = INTERMWDemoUtils.getEntityIDsFromPayload(message.getPayload(), "http://inter-iot.eu/syntax/FIWAREv2#Entity");

			for(String entityId : entityIds){
				System.out.println("Updating thing:" + entityId);
				publishObservation(entityId, message);
			}
		}
		catch (Exception e){
			logger.error("Error updating thing: " + e.getMessage());
		}
	}
	
	@Override
	public void unregisterThing(Message message) {
		// Get the entity id to which be subscribed
		// (to be retrieved from the message)
		//String mockEntityId = "myFalseEntityId";
		try {
			// TODO Discuss with pawel EntityTypeDevice to enum
			Set<String> entityIds = INTERMWDemoUtils.getEntityIDsFromPayload(message.getPayload(), "http://inter-iot.eu/syntax/FIWAREv2#Entity");
			
			for (String entityId : entityIds) {
				// TODO Loop over the payload to include possible
				// observations in the creation or a schema of the
				// 'Thing'				
				delete(entityId);				
			}
		} 
		catch (Exception e) {
			logger.error("Error unregistering thing: " + e.getMessage());
		}

	}

	@Override
	public void subscribe(Message message) {
		FIWAREv2Translator translator = new FIWAREv2Translator();
		String responseBody = null;
		Message responseMessage = null;
		try {
			//Get the entityIds from the payload. Will be ready in Messages v0.7
			ArrayList<String> myFalseEntityList = new ArrayList<>();
			String requestBody = translator.toFormatX(message.getPayload().getJenaModel());
			responseBody = OrionV2Utils.createSubscription(BASE_PATH, requestBody).getEntity().toString();
			// translate from plain FIWARE-json to FIWARE json-ld. Gets a JENA (RDF) Model
			Model translatedModel = translator.toJenaModel(responseBody);
			// create a new message payload for the response message
			MessagePayload responsePayload = new MessagePayload(translatedModel);
			// attach the payload to the message
			responseMessage.setPayload(responsePayload);
			// publish the message to INTER-MW. The publisher is global (and it is tested)
			publisher.publish(responseMessage);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void unsubscribe(Message message) {
		// TODO Auto-generated method stub
		try{
			String conversationID = message.getMetadata().getConversationId().orElse("");
			unsubscribe(new SubscriptionId(conversationID));
		}
		catch (Exception e){ 
			logger.error("Error unsubscribing: " + e.getMessage());
		}
	}
}
