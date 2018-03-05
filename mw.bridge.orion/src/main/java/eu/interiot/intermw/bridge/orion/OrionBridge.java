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
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.interiot.fiware.ngsiv2.client.ApiException;
import org.interiot.fiware.ngsiv2.client.model.Attribute;
import org.interiot.fiware.ngsiv2.client.model.Attributes;
import org.interiot.fiware.ngsiv2.client.model.Entity;
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
		client = new OrionApiClient(configuration.getProperties(PROPERTIES_PREFIX),URL);
		BASE_PATH = configuration.getProperty("orion-base-path");

		mwFactory = Context.mwFactory();
		// TODO commons context and broker context. This is very confusing
		// broker = eu.interiot.intermw.comm.broker.Context.getBroker();
		callbackPort = Integer.parseInt(configuration.getProperty("bridge-callback-port"));
	}

	
	private void create(String thingId, MessagePayload payload) throws BridgeException {
		
		// Transform to a compatible ID in FIWARE
		//String transformedID = filterThingID(thingId);
		//XXX Question SRIPAS --> DO we need a new  for each bridge instance??
		FIWAREv2Translator fiwareTranslator = new FIWAREv2Translator();
		
		//Properties properties = configuration.getProperties();

		String url = null;
        try {
            url = new URL(configuration.getProperty("orion-url")).toString();
        } 
        catch (Exception e) {
            throw new BridgeException("Failed to read Example bridge configuration: " + e.getMessage());
        }
        
		String body = null; 
		try {
			body = fiwareTranslator.toFormatX(payload.getJenaModel());			
		} catch (IllegalSyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	
		try {
			String fiwareCreate = BASE_PATH+FiwareUtils.FIWARE_CREATE;
			FiwareUtils.postToFiware(fiwareCreate,body);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
/**		
		
		try {

			//Orion library has a class 'Entity' representing a device/thing/entity capable to produce data.
			Entity fiwareEntity = new Entity();
			fiwareEntity.setId(transformedID);
				

			//TODO FF, 20-09-2017, Commented out, as things were sent without attributes.
			//All hardcoded, but should work....
			fiwareEntity.setType(NO_TYPE_TEXT);
			Attributes attributes = new Attributes();
			Attribute attr = new Attribute();
			attr.setType("no_type");
			attr.setValue("no_attributes");
			attributes.put("no_attributes", attr);
			/*
			if (interiotThing.containsKey(TYPE_KEY)) {
				fiwareEntity.setType(interiotThing.getAttributeValue(TYPE_KEY));
			} else {
				fiwareEntity.setType(NO_TYPE_TEXT);
			}
			// hack for demo in Geneva. Actually, no creation should come
			// without attributes.
			if (interiotThing.getAttributes().size() == 0) {
				interiotThing.put(new ThingAttribute("no_attributes", "no_attributes", "no_type"));
			}

			Attributes attributes = createAttributes(interiotThing);
*/
		
		/*
			try {
				client.createEntity(fiwareEntity, null);
				client.updateOrAppendEntityAttributes(fiwareEntity.getId(), attributes, fiwareEntity.getType(), null);
			} catch (ApiException e) {
				if (e.getCode() != ENTITY_EXISTS_ERROR_CODE) {
					throw new BridgeException(e);
				}
			}
		} catch (Exception e) {
			throw new BridgeException(e);
		}

		*/
}




	/**
	 * Replace forbidden characters in FIWARE to be compatible
	 * @param thingId
	 * @return
	 */
	private String filterThingID(String thingId) {
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

	/*
	private Thing read(Query query) throws BridgeException {
		try {
			OrionQuery _query = (OrionQuery) query;

			Entity retrievedEntity;
			Attributes retrievedAttributes;

			retrievedEntity = client.retrieveEntity(query.getEntityId(), query.getType(),
					query.getAttributesInCommaSeparatedString(), _query.getOptionsInCommaSeparatedString());

			retrievedAttributes = client.retrieveEntityAttributes(query.getEntityId(), query.getType(),
					query.getAttributesInCommaSeparatedString(), _query.getOptionsInCommaSeparatedString());

			return fiwareEntityToInteriotThing(retrievedEntity, retrievedAttributes);
		} catch (Exception e) {
			throw new BridgeException(e);
		}
	}

*/
	private void update(String thingId, Message message) throws BridgeException, MessageException {
		// FIXME HACK: getAttributeFromPayload assumes that
		// there is an observation inside the payload
		// It will not work otherwise
		String key = INTERMWDemoUtils.getAttrKeyToUpdateFromPayload(message.getPayload());
		String value = INTERMWDemoUtils.getAttrValueToUpdateFromPayload(message.getPayload());
		String type = INTERMWDemoUtils.getAttrTypeToUpdateFromPayload(message.getPayload());

		String transformedID = filterThingID(thingId);

		try {
			Entity fiwareEntity = new Entity();
			fiwareEntity.setId(transformedID);

			Attributes attributes = new Attributes();
			Attribute attr = new Attribute();
			if(type == null || type.equals(""))
				type = DEFAULT_ATTRIBUTE_TYPE;
			attr.setType(type);
			attr.setValue(value);
			attributes.put(filterThingAttrKey(key), attr);

			client.updateOrAppendEntityAttributes(fiwareEntity.getId(), attributes, fiwareEntity.getType(), null);
		} catch (Exception e) {
			throw new BridgeException(e);
		}
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
			}
			else {
				newKey = key;
			}

		return newKey;
	}

	/**
	 * How to implement the functionality of deleting only one type with the
	 * ID???
	 * 
	 * @param thingId
	 */
	private void delete(String thingId) throws BridgeException {

		String transformedID = filterThingID(thingId);

		try {
			client.removeEntity(transformedID, null);
		} catch (Exception e) {
			throw new BridgeException(e);
		}
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
				//metadata.setSenderPlatformId(URI.create(platform.getId().getId()));	OLD MESSAGING CODE
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
	 * XXX NOT TESTED public void cleanAllSubscriptons() throws BridgeException
	 * { while (cleanSubscriptions()); }
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
	private Thing fiwareEntityToInteriotThing(Entity entity, Attributes retrievedAttributes) {
		Thing interiotThing = mwFactory.createThing(new ThingId(entity.getId()));

		if (entity.getType() != null) {
			ThingAttribute attribute = mwFactory.createThingAttribute(TYPE_KEY, entity.getType());
			interiotThing.put(attribute);
		}

		for (String attributeKey : retrievedAttributes.keySet()) {
			ThingAttribute attribute = mwFactory.createThingAttribute(attributeKey,
					retrievedAttributes.get(attributeKey).getValue());
			interiotThing.put(attribute);
		}

		return interiotThing;
	}

	protected Attributes createAttributes(Thing interiotThing) {
		Attributes attributes = new Attributes();
		for (ThingAttribute attribute : interiotThing) {
			if (attribute.getKey() != null && attribute.getKey().equals(TYPE_KEY)) {
				continue;
			}

			Attribute attr = new Attribute();
			attr.setType(getType(attribute));
			attr.setValue(attribute.getValue().toString());
			attributes.put(attribute.getKey(), attr);
		}

		return attributes;
	}

	protected String getType(ThingAttribute attribute) {
		String type = attribute.getType();

		if (StringUtils.isEmpty(type)) {
			type = DEFAULT_ATTRIBUTE_TYPE;
		}

		return type;
	}
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
				// TODO Discuss with pawel EntityTypeDevice to enum
				Set<String> entityIds = INTERMWDemoUtils.getEntityIDsFromPayload(message.getPayload(),
						INTERMWDemoUtils.EntityTypeDevice);

				// XXX Discuss with matevz, flavio At this point we can
				// do two things, iterate over the list and do atomic
				// creations or introduce a bulk creation method
				// For the moment. It is implemented the first option,
				// in order to preserve the Bridge API TODO consider
				// changing it

				for (String entityId : entityIds) {
					// TODO Loop over the payload to include possible
					// observations in the creation or a schema of the
					// 'Thing'
					create(entityId, message.getPayload());
				}
			} else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.THING_UNREGISTER)) {
				Set<String> entityIds = INTERMWDemoUtils.getEntityIDsFromPayload(message.getPayload(),
						INTERMWDemoUtils.EntityTypeDevice);
				for (String entityId : entityIds) {
					delete(entityId);
				}
			} else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.THING_UPDATE)) {
				// FIXME This implementation assumes that only one
				// attribute is updated at once
				// FIXME This code is for the demo only. It will break
				// otherwise.
				if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.OBSERVATION)) {
					Set<String> entities = INTERMWDemoUtils.getEntityIDsFromPayload(message.getPayload(),
							INTERMWDemoUtils.EntityTypeDevice);
					if (entities.isEmpty())
						throw new BridgeException("No entities of type Device found in the Payload");
					String entity = entities.iterator().next();

					System.out.println("Updating thing:" + entity);
					update(entity, message);
				}

			} else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.QUERY)) {
				// XXX Improve the Query definition

				Set<String> entities = INTERMWDemoUtils.getEntityIDsFromPayload(message.getPayload(),
						INTERMWDemoUtils.EntityTypeDevice);
				if (entities.isEmpty())
					throw new PayloadException("No entities of type Device found in the Payload");

				throw new BridgeException("QUERY not implemented");
				/*String entity = entities.iterator().next();

				Query q = new DefaultQueryImpl(new ThingId(entity));
				read(q);*/

			} else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.SUBSCRIBE)) {
				// Assuming the subscribing to one thing
				Set<String> entities = INTERMWDemoUtils.getEntityIDsFromPayload(message.getPayload(),
						INTERMWDemoUtils.EntityTypeDevice);
				if (entities.isEmpty())
					throw new PayloadException("No entities of type Device found in the Payload");

				String entity = entities.iterator().next();
				subscribe(entity,
						message.getMetadata().getConversationId().orElse(""));

			} else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.UNSUBSCRIBE)) {
				String conversationID = message.getMetadata().getConversationId().orElse("");
				unsubscribe(new SubscriptionId(conversationID));
			} else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.DISCOVERY)) {
				throw new UnsupportedActionException("The action DISCOVERY is currently unsupported");
			} else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.PLATFORM_REGISTER)) {

			} else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.PLATFORM_UNREGISTER)) {
				throw new IllegalActionException("The action PLATFORM UNREGISTER is not allowed here");
			} else if (messageTypesEnumSet.contains(URIManagerMessageMetadata.MessageTypesEnum.UNRECOGNIZED)) {
				throw new UnknownActionException(
						"The action is labelled as UNRECOGNIZED and thus is unprocessable by component "
								+ this.getClass().getName() + " in platform " + platform.getId().getId());
			} else {
				throw new UnknownActionException(
						"The message type is not properly handled and can't be processed"
								+ this.getClass().getName() + " in platform " + platform.getId().getId());
			}
			// TODO For now we create a generic response message. Think
			// about sending a specific status

			Message responseMessage = createResponseMessage(message);
			try {
				publisher.publish(responseMessage);
			} catch (BrokerException e) {
				log.error("Error publishing response");
				throw new MessageException("error publishing response", e);
			}

		} catch (MessageException | UnsupportedActionException | IllegalActionException | UnknownActionException e) {
			throw new BridgeException(e.toString());
		}


	}


}
