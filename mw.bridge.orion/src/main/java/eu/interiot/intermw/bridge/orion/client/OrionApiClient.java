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
 * - @author <a href="mailto:aromeu@prodevelop.es">Alberto Romeu</a>  
 * - Project coordinator:  <a href="mailto:coordinator@inter-iot.eu"></a>
 *  
 *
 *    This code is licensed under the EPL license, available at the root
 *    application directory.
 */
package eu.interiot.intermw.bridge.orion.client;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import org.interiot.fiware.ngsiv2.client.ApiCallback;
import org.interiot.fiware.ngsiv2.client.ApiClient;
import org.interiot.fiware.ngsiv2.client.ApiException;
import org.interiot.fiware.ngsiv2.client.ApiResponse;
import org.interiot.fiware.ngsiv2.client.api.AllApi;
import org.interiot.fiware.ngsiv2.client.model.Attribute;
import org.interiot.fiware.ngsiv2.client.model.Attributes;
import org.interiot.fiware.ngsiv2.client.model.Discoverrequest;
import org.interiot.fiware.ngsiv2.client.model.Entity;
import org.interiot.fiware.ngsiv2.client.model.EntityType;
import org.interiot.fiware.ngsiv2.client.model.Queryrequest;
import org.interiot.fiware.ngsiv2.client.model.Queryresponse;
import org.interiot.fiware.ngsiv2.client.model.Registerrequest;
import org.interiot.fiware.ngsiv2.client.model.Registration;
import org.interiot.fiware.ngsiv2.client.model.Resources;
import org.interiot.fiware.ngsiv2.client.model.Subscription;
import org.interiot.fiware.ngsiv2.client.model.UpdateRequest;

import com.squareup.okhttp.Call;

/**
 * Swagger generator forces a Content-Type header and some FIWARE endpoints
 * doesn't work with a Content-Type
 * 
 * Solution? Extending the Swagger generated
 * {@link org.interiot.fiware.ngsiv2.client.ApiClient} to force FIWARE
 * restrictions
 * 
 * Most of the methods are overriden from {@link ApiClient} or delegated to
 * {@link AllApi}
 * 
 * @author <a href="mailto:aromeu@prodevelop.es">Alberto Romeu</a>
 * @author <a href="mailto:mllorente@prodevelop.es">Miguel A. Llorente</a>
 *
 */
public class OrionApiClient extends org.interiot.fiware.ngsiv2.client.ApiClient {

	private static final String DEFAULT_URL = "http://localhost:1026";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String USER_AGENT = "interiot/1.0.0/java";
	private static final boolean DEBUGGING = false;

	public static final String PROP_URL = "url";
	public static final String PROP_DATE_TIME_FORMAT = "datetimeformat";
	public static final String PROP_DATE_FORMAT = "dateformat";
	public static final String PROP_USER_AGENT = "useragent";
	public static final String PROP_DEBUGGING = "debugging";

	private AllApi orionApi;
	private Properties properties;

	public OrionApiClient(Properties properties) {
		super();
		this.properties = properties;

		String url = this.properties.getOrDefault(PROP_URL, DEFAULT_URL).toString();
		setBasePath(url);

		String dateTimeFormat = this.properties.getOrDefault(PROP_DATE_TIME_FORMAT, DATE_TIME_FORMAT).toString();
		setDatetimeFormat(new SimpleDateFormat(dateTimeFormat));

		String dateFormat = this.properties.getOrDefault(PROP_DATE_FORMAT, DATE_FORMAT).toString();
		setDateFormat(new SimpleDateFormat(dateFormat));

		String userAgent = this.properties.getOrDefault(PROP_USER_AGENT, USER_AGENT).toString();
		setUserAgent(userAgent);

		boolean debugging = Boolean.valueOf(this.properties.getOrDefault(PROP_DEBUGGING, DEBUGGING).toString());
		setDebugging(debugging);

		orionApi = new AllApi(this);
		
		System.out.println("CREATING CONNECTION TO ORION: "+url);
		
	}
	
	public OrionApiClient(Properties properties, String URL) {
		super();
		this.properties = properties;

		String url = URL;
		setBasePath(url);

		String dateTimeFormat = this.properties.getOrDefault(PROP_DATE_TIME_FORMAT, DATE_TIME_FORMAT).toString();
		setDatetimeFormat(new SimpleDateFormat(dateTimeFormat));

		String dateFormat = this.properties.getOrDefault(PROP_DATE_FORMAT, DATE_FORMAT).toString();
		setDateFormat(new SimpleDateFormat(dateFormat));

		String userAgent = this.properties.getOrDefault(PROP_USER_AGENT, USER_AGENT).toString();
		setUserAgent(userAgent);

		boolean debugging = Boolean.valueOf(this.properties.getOrDefault(PROP_DEBUGGING, DEBUGGING).toString());
		setDebugging(debugging);

		orionApi = new AllApi(this);
		
		System.out.println("CREATING CONNECTION TO ORION: "+url);
		
	}

	@Override
	public String selectHeaderContentType(String[] contentTypes) {
		if (contentTypes != null && contentTypes.length > 0) {
			return super.selectHeaderContentType(contentTypes);
		}

		return null;
	}

	@Override
	public String selectHeaderAccept(String[] accepts) {
		if (accepts != null && accepts.length > 0) {
			return super.selectHeaderContentType(accepts);
		}

		return null;
	}

	public void createANewContextProviderRegistration(Registration body) throws ApiException {
		orionApi.createANewContextProviderRegistration(body);
	}

	public ApiResponse<Void> createANewContextProviderRegistrationWithHttpInfo(Registration body) throws ApiException {
		return orionApi.createANewContextProviderRegistrationWithHttpInfo(body);
	}

	public Call createANewContextProviderRegistrationAsync(Registration body, ApiCallback<Void> callback)
			throws ApiException {
		return orionApi.createANewContextProviderRegistrationAsync(body, callback);
	}

	public void createANewSubscription(Subscription body) throws ApiException {
		orionApi.createANewSubscription(body);
	}

	public ApiResponse<Void> createANewSubscriptionWithHttpInfo(Subscription body) throws ApiException {
		return orionApi.createANewSubscriptionWithHttpInfo(body);
	}

	public Call createANewSubscriptionAsync(Subscription body, ApiCallback<Void> callback) throws ApiException {
		return orionApi.createANewSubscriptionAsync(body, callback);
	}

	public List<Registration> createDiscover(Discoverrequest body, Double limit, Double offset, String options)
			throws ApiException {
		return orionApi.createDiscover(body, limit, offset, options);
	}

	public ApiResponse<List<Registration>> createDiscoverWithHttpInfo(Discoverrequest body, Double limit, Double offset,
			String options) throws ApiException {
		return orionApi.createDiscoverWithHttpInfo(body, limit, offset, options);
	}

	public Call createDiscoverAsync(Discoverrequest body, Double limit, Double offset, String options,
			ApiCallback<List<Registration>> callback) throws ApiException {
		return orionApi.createDiscoverAsync(body, limit, offset, options, callback);
	}

	public void createEntity(Entity body, String options) throws ApiException {
		orionApi.createEntity(body, options);
	}

	public ApiResponse<Void> createEntityWithHttpInfo(Entity body, String options) throws ApiException {
		return orionApi.createEntityWithHttpInfo(body, options);
	}

	public Call createEntityAsync(Entity body, String options, ApiCallback<Void> callback) throws ApiException {
		return orionApi.createEntityAsync(body, options, callback);
	}

	public void createNotifyContext() throws ApiException {
		orionApi.createNotifyContext();
	}

	public ApiResponse<Void> createNotifyContextWithHttpInfo() throws ApiException {
		return orionApi.createNotifyContextWithHttpInfo();
	}

	public Call createNotifyContextAsync(ApiCallback<Void> callback) throws ApiException {
		return orionApi.createNotifyContextAsync(callback);
	}

	public void createNotifyContextAvailability() throws ApiException {
		orionApi.createNotifyContextAvailability();
	}

	public ApiResponse<Void> createNotifyContextAvailabilityWithHttpInfo() throws ApiException {
		return orionApi.createNotifyContextAvailabilityWithHttpInfo();
	}

	public Call createNotifyContextAvailabilityAsync(ApiCallback<Void> callback) throws ApiException {
		return orionApi.createNotifyContextAvailabilityAsync(callback);
	}

	public List<Queryresponse> createQuery(Queryrequest body, Double limit, Double offset, String orderBy,
			String options) throws ApiException {
		return orionApi.createQuery(body, limit, offset, orderBy, options);
	}

	public ApiResponse<List<Queryresponse>> createQueryWithHttpInfo(Queryrequest body, Double limit, Double offset,
			String orderBy, String options) throws ApiException {
		return orionApi.createQueryWithHttpInfo(body, limit, offset, orderBy, options);
	}

	public Call createQueryAsync(Queryrequest body, Double limit, Double offset, String orderBy, String options,
			ApiCallback<List<Queryresponse>> callback) throws ApiException {
		return orionApi.createQueryAsync(body, limit, offset, orderBy, options, callback);
	}

	public List<String> createRegister(Registerrequest body) throws ApiException {
		return orionApi.createRegister(body);
	}

	public ApiResponse<List<String>> createRegisterWithHttpInfo(Registerrequest body) throws ApiException {
		return orionApi.createRegisterWithHttpInfo(body);
	}

	public Call createRegisterAsync(Registerrequest body, ApiCallback<List<String>> callback) throws ApiException {
		return orionApi.createRegisterAsync(body, callback);
	}

	public void createSubscribeContext() throws ApiException {
		orionApi.createSubscribeContext();
	}

	public ApiResponse<Void> createSubscribeContextWithHttpInfo() throws ApiException {
		return orionApi.createSubscribeContextWithHttpInfo();
	}

	public Call createSubscribeContextAsync(ApiCallback<Void> callback) throws ApiException {
		return orionApi.createSubscribeContextAsync(callback);
	}

	public void createSubscribeContextAvailability() throws ApiException {
		orionApi.createSubscribeContextAvailability();
	}

	public ApiResponse<Void> createSubscribeContextAvailabilityWithHttpInfo() throws ApiException {
		return orionApi.createSubscribeContextAvailabilityWithHttpInfo();
	}

	public Call createSubscribeContextAvailabilityAsync(ApiCallback<Void> callback) throws ApiException {
		return orionApi.createSubscribeContextAvailabilityAsync(callback);
	}

	public void createUnsubscribeContext() throws ApiException {
		orionApi.createUnsubscribeContext();
	}

	public ApiResponse<Void> createUnsubscribeContextWithHttpInfo() throws ApiException {
		return orionApi.createUnsubscribeContextWithHttpInfo();
	}

	public Call createUnsubscribeContextAsync(ApiCallback<Void> callback) throws ApiException {
		return orionApi.createUnsubscribeContextAsync(callback);
	}

	public void createUnsubscribeContextAvailability() throws ApiException {
		orionApi.createUnsubscribeContextAvailability();
	}

	public ApiResponse<Void> createUnsubscribeContextAvailabilityWithHttpInfo() throws ApiException {
		return orionApi.createUnsubscribeContextAvailabilityWithHttpInfo();
	}

	public Call createUnsubscribeContextAvailabilityAsync(ApiCallback<Void> callback) throws ApiException {
		return orionApi.createUnsubscribeContextAvailabilityAsync(callback);
	}

	public void deleteContextProviderRegistration(String registrationId) throws ApiException {
		orionApi.deleteContextProviderRegistration(registrationId);
	}

	public ApiResponse<Void> deleteContextProviderRegistrationWithHttpInfo(String registrationId) throws ApiException {
		return orionApi.deleteContextProviderRegistrationWithHttpInfo(registrationId);
	}

	public Call deleteContextProviderRegistrationAsync(String registrationId, ApiCallback<Void> callback)
			throws ApiException {
		return orionApi.deleteContextProviderRegistrationAsync(registrationId, callback);
	}

	public void deleteSubscription(String subscriptionId) throws ApiException {
		orionApi.deleteSubscription(subscriptionId);
	}

	public ApiResponse<Void> deleteSubscriptionWithHttpInfo(String subscriptionId) throws ApiException {
		return orionApi.deleteSubscriptionWithHttpInfo(subscriptionId);
	}

	public Call deleteSubscriptionAsync(String subscriptionId, ApiCallback<Void> callback) throws ApiException {
		return orionApi.deleteSubscriptionAsync(subscriptionId, callback);
	}

	public Attributes getAttributeData(String entityId, String attrName, String type) throws ApiException {
		return orionApi.getAttributeData(entityId, attrName, type);
	}

	public ApiResponse<Attributes> getAttributeDataWithHttpInfo(String entityId, String attrName, String type)
			throws ApiException {
		return orionApi.getAttributeDataWithHttpInfo(entityId, attrName, type);
	}

	public Call getAttributeDataAsync(String entityId, String attrName, String type, ApiCallback<Attributes> callback)
			throws ApiException {
		return orionApi.getAttributeDataAsync(entityId, attrName, type, callback);
	}

	public void getAttributeValue(String entityId, String attrName, String type) throws ApiException {
		orionApi.getAttributeValue(entityId, attrName, type);
	}

	public ApiResponse<Void> getAttributeValueWithHttpInfo(String entityId, String attrName, String type)
			throws ApiException {
		return orionApi.getAttributeValueWithHttpInfo(entityId, attrName, type);
	}

	public Call getAttributeValueAsync(String entityId, String attrName, String type, ApiCallback<Void> callback)
			throws ApiException {
		return orionApi.getAttributeValueAsync(entityId, attrName, type, callback);
	}

	public List<Entity> listEntities(String id, String type, String idPattern, String typePattern, String q, String mq,
			String georel, String geometry, String coords, Double limit, Double offset, String attrs, String orderBy,
			String options) throws ApiException {
		return orionApi.listEntities(id, type, idPattern, typePattern, q, mq, georel, geometry, coords, limit, offset,
				attrs, orderBy, options);
	}

	public ApiResponse<List<Entity>> listEntitiesWithHttpInfo(String id, String type, String idPattern,
			String typePattern, String q, String mq, String georel, String geometry, String coords, Double limit,
			Double offset, String attrs, String orderBy, String options) throws ApiException {
		return orionApi.listEntitiesWithHttpInfo(id, type, idPattern, typePattern, q, mq, georel, geometry, coords,
				limit, offset, attrs, orderBy, options);
	}

	public Call listEntitiesAsync(String id, String type, String idPattern, String typePattern, String q, String mq,
			String georel, String geometry, String coords, Double limit, Double offset, String attrs, String orderBy,
			String options, ApiCallback<List<Entity>> callback) throws ApiException {
		return orionApi.listEntitiesAsync(id, type, idPattern, typePattern, q, mq, georel, geometry, coords, limit,
				offset, attrs, orderBy, options, callback);
	}

	public void removeASingleAttribute(String entityId, String attrName, String type) throws ApiException {
		orionApi.removeASingleAttribute(entityId, attrName, type);
	}

	public ApiResponse<Void> removeASingleAttributeWithHttpInfo(String entityId, String attrName, String type)
			throws ApiException {
		return orionApi.removeASingleAttributeWithHttpInfo(entityId, attrName, type);
	}

	public Call removeASingleAttributeAsync(String entityId, String attrName, String type, ApiCallback<Void> callback)
			throws ApiException {
		return orionApi.removeASingleAttributeAsync(entityId, attrName, type, callback);
	}

	public void removeEntity(String entityId, String type) throws ApiException {
		orionApi.removeEntity(entityId, type);
	}

	public ApiResponse<Void> removeEntityWithHttpInfo(String entityId, String type) throws ApiException {
		return orionApi.removeEntityWithHttpInfo(entityId, type);
	}

	public Call removeEntityAsync(String entityId, String type, ApiCallback<Void> callback) throws ApiException {
		return orionApi.removeEntityAsync(entityId, type, callback);
	}

	public Resources retrieveAPIResources() throws ApiException {
		return orionApi.retrieveAPIResources();
	}

	public ApiResponse<Resources> retrieveAPIResourcesWithHttpInfo() throws ApiException {
		return orionApi.retrieveAPIResourcesWithHttpInfo();
	}

	public Call retrieveAPIResourcesAsync(ApiCallback<Resources> callback) throws ApiException {
		return orionApi.retrieveAPIResourcesAsync(callback);
	}

	public Registration retrieveContextProviderRegistration(String registrationId) throws ApiException {
		return orionApi.retrieveContextProviderRegistration(registrationId);
	}

	public ApiResponse<Registration> retrieveContextProviderRegistrationWithHttpInfo(String registrationId)
			throws ApiException {
		return orionApi.retrieveContextProviderRegistrationWithHttpInfo(registrationId);
	}

	public Call retrieveContextProviderRegistrationAsync(String registrationId, ApiCallback<Registration> callback)
			throws ApiException {
		return orionApi.retrieveContextProviderRegistrationAsync(registrationId, callback);
	}

	public Entity retrieveEntity(String entityId, String type, String attrs, String options) throws ApiException {
		return orionApi.retrieveEntity(entityId, type, attrs, options);
	}

	public ApiResponse<Entity> retrieveEntityWithHttpInfo(String entityId, String type, String attrs, String options)
			throws ApiException {
		return orionApi.retrieveEntityWithHttpInfo(entityId, type, attrs, options);
	}

	public Call retrieveEntityAsync(String entityId, String type, String attrs, String options,
			ApiCallback<Entity> callback) throws ApiException {
		return orionApi.retrieveEntityAsync(entityId, type, attrs, options, callback);
	}

	public Attributes retrieveEntityAttributes(String entityId, String type, String attrs, String options)
			throws ApiException {
		return orionApi.retrieveEntityAttributes(entityId, type, attrs, options);
	}

	public ApiResponse<Attributes> retrieveEntityAttributesWithHttpInfo(String entityId, String type, String attrs,
			String options) throws ApiException {
		return orionApi.retrieveEntityAttributesWithHttpInfo(entityId, type, attrs, options);
	}

	public Call retrieveEntityAttributesAsync(String entityId, String type, String attrs, String options,
			ApiCallback<Attributes> callback) throws ApiException {
		return orionApi.retrieveEntityAttributesAsync(entityId, type, attrs, options, callback);
	}

	public EntityType retrieveEntityType(String entityType) throws ApiException {
		return orionApi.retrieveEntityType(entityType);
	}

	public ApiResponse<EntityType> retrieveEntityTypeWithHttpInfo(String entityType) throws ApiException {
		return orionApi.retrieveEntityTypeWithHttpInfo(entityType);
	}

	public Call retrieveEntityTypeAsync(String entityType, ApiCallback<EntityType> callback) throws ApiException {
		return orionApi.retrieveEntityTypeAsync(entityType, callback);
	}

	public List<EntityType> retrieveEntityTypes(Double limit, Double offset, String options) throws ApiException {
		return orionApi.retrieveEntityTypes(limit, offset, options);
	}

	public ApiResponse<List<EntityType>> retrieveEntityTypesWithHttpInfo(Double limit, Double offset, String options)
			throws ApiException {
		return orionApi.retrieveEntityTypesWithHttpInfo(limit, offset, options);
	}

	public Call retrieveEntityTypesAsync(Double limit, Double offset, String options,
			ApiCallback<List<EntityType>> callback) throws ApiException {
		return orionApi.retrieveEntityTypesAsync(limit, offset, options, callback);
	}

	public List<Registration> retrieveRegistrations() throws ApiException {
		return orionApi.retrieveRegistrations();
	}

	public ApiResponse<List<Registration>> retrieveRegistrationsWithHttpInfo() throws ApiException {
		return orionApi.retrieveRegistrationsWithHttpInfo();
	}

	public Call retrieveRegistrationsAsync(ApiCallback<List<Registration>> callback) throws ApiException {
		return orionApi.retrieveRegistrationsAsync(callback);
	}

	public Subscription retrieveSubscription(String subscriptionId) throws ApiException {
		return orionApi.retrieveSubscription(subscriptionId);
	}

	public ApiResponse<Subscription> retrieveSubscriptionWithHttpInfo(String subscriptionId) throws ApiException {
		return orionApi.retrieveSubscriptionWithHttpInfo(subscriptionId);
	}

	public Call retrieveSubscriptionAsync(String subscriptionId, ApiCallback<Subscription> callback)
			throws ApiException {
		return orionApi.retrieveSubscriptionAsync(subscriptionId, callback);
	}

	public List<Subscription> retrieveSubscriptions(Double limit, Double offset, String options) throws ApiException {
		return orionApi.retrieveSubscriptions(limit, offset, options);
	}

	public ApiResponse<List<Subscription>> retrieveSubscriptionsWithHttpInfo(Double limit, Double offset,
			String options) throws ApiException {
		return orionApi.retrieveSubscriptionsWithHttpInfo(limit, offset, options);
	}

	public Call retrieveSubscriptionsAsync(Double limit, Double offset, String options,
			ApiCallback<List<Subscription>> callback) throws ApiException {
		return orionApi.retrieveSubscriptionsAsync(limit, offset, options, callback);
	}

	public void update(UpdateRequest body, String options) throws ApiException {
		orionApi.update(body, options);
	}

	public ApiResponse<Void> updateWithHttpInfo(UpdateRequest body, String options) throws ApiException {
		return orionApi.updateWithHttpInfo(body, options);
	}

	public Call updateAsync(UpdateRequest body, String options, ApiCallback<Void> callback) throws ApiException {
		return orionApi.updateAsync(body, options, callback);
	}

	public void updateAttributeData(String entityId, String attrName, Attribute body, String type) throws ApiException {
		orionApi.updateAttributeData(entityId, attrName, body, type);
	}

	public ApiResponse<Void> updateAttributeDataWithHttpInfo(String entityId, String attrName, Attribute body,
			String type) throws ApiException {
		return orionApi.updateAttributeDataWithHttpInfo(entityId, attrName, body, type);
	}

	public Call updateAttributeDataAsync(String entityId, String attrName, Attribute body, String type,
			ApiCallback<Void> callback) throws ApiException {
		return orionApi.updateAttributeDataAsync(entityId, attrName, body, type, callback);
	}

	public void updateContextAvailabilitySubscription() throws ApiException {
		orionApi.updateContextAvailabilitySubscription();
	}

	public ApiResponse<Void> updateContextAvailabilitySubscriptionWithHttpInfo() throws ApiException {
		return orionApi.updateContextAvailabilitySubscriptionWithHttpInfo();
	}

	public Call updateContextAvailabilitySubscriptionAsync(ApiCallback<Void> callback) throws ApiException {
		return orionApi.updateContextAvailabilitySubscriptionAsync(callback);
	}

	public void updateContextSubscription() throws ApiException {
		orionApi.updateContextSubscription();
	}

	public ApiResponse<Void> updateContextSubscriptionWithHttpInfo() throws ApiException {
		return orionApi.updateContextSubscriptionWithHttpInfo();
	}

	public Call updateContextSubscriptionAsync(ApiCallback<Void> callback) throws ApiException {
		return orionApi.updateContextSubscriptionAsync(callback);
	}

	public void updateOrAppendEntityAttributes(String entityId, Attributes body, String type, String options)
			throws ApiException {
		orionApi.updateOrAppendEntityAttributes(entityId, body, type, options);
	}

	public ApiResponse<Void> updateOrAppendEntityAttributesWithHttpInfo(String entityId, Attributes body, String type,
			String options) throws ApiException {
		return orionApi.updateOrAppendEntityAttributesWithHttpInfo(entityId, body, type, options);
	}

	public Call updateOrAppendEntityAttributesAsync(String entityId, Attributes body, String type, String options,
			ApiCallback<Void> callback) throws ApiException {
		return orionApi.updateOrAppendEntityAttributesAsync(entityId, body, type, options, callback);
	}

	public void updateReplaceAllEntityAttributes(String entityId, Attributes body, String type, String options)
			throws ApiException {
		orionApi.updateReplaceAllEntityAttributes(entityId, body, type, options);
	}

	public ApiResponse<Void> updateReplaceAllEntityAttributesWithHttpInfo(String entityId, Attributes body, String type,
			String options) throws ApiException {
		return orionApi.updateReplaceAllEntityAttributesWithHttpInfo(entityId, body, type, options);
	}

	public Call updateReplaceAllEntityAttributesAsync(String entityId, Attributes body, String type, String options,
			ApiCallback<Void> callback) throws ApiException {
		return orionApi.updateReplaceAllEntityAttributesAsync(entityId, body, type, options, callback);
	}
}
