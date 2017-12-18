# fiware-ngsiv2-client-codegen

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>org.interiot</groupId>
    <artifactId>fiware-ngsiv2-client-codegen</artifactId>
    <version>1.3.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "org.interiot:fiware-ngsiv2-client-codegen:1.3.0"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/fiware-ngsiv2-client-codegen-1.3.0.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import org.interiot.fiware.ngsiv2.client.*;
import org.interiot.fiware.ngsiv2.client.auth.*;
import org.interiot.fiware.ngsiv2.client.model.*;
import org.interiot.fiware.ngsiv2.client.api.APIEntryPointApi;

import java.io.File;
import java.util.*;

public class APIEntryPointApiExample {

    public static void main(String[] args) {
        
        APIEntryPointApi apiInstance = new APIEntryPointApi();
        try {
            Resources result = apiInstance.retrieveAPIResources();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling APIEntryPointApi#retrieveAPIResources");
            e.printStackTrace();
        }
    }
}

```

## Documentation for API Endpoints

All URIs are relative to *http://localhost/fiware-orion*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*APIEntryPointApi* | [**retrieveAPIResources**](docs/APIEntryPointApi.md#retrieveAPIResources) | **GET** /v2 | 
*AllApi* | [**createANewContextProviderRegistration**](docs/AllApi.md#createANewContextProviderRegistration) | **POST** /v2/registrations | 
*AllApi* | [**createANewSubscription**](docs/AllApi.md#createANewSubscription) | **POST** /v2/subscriptions | 
*AllApi* | [**createDiscover**](docs/AllApi.md#createDiscover) | **POST** /v2/op/discover/ | 
*AllApi* | [**createEntity**](docs/AllApi.md#createEntity) | **POST** /v2/entities | 
*AllApi* | [**createNotifyContext**](docs/AllApi.md#createNotifyContext) | **POST** /v2/notifyContext | 
*AllApi* | [**createNotifyContextAvailability**](docs/AllApi.md#createNotifyContextAvailability) | **POST** /v2/notifyContextAvailability | 
*AllApi* | [**createQuery**](docs/AllApi.md#createQuery) | **POST** /v2/op/query | 
*AllApi* | [**createRegister**](docs/AllApi.md#createRegister) | **POST** /v2/op/register | 
*AllApi* | [**createSubscribeContext**](docs/AllApi.md#createSubscribeContext) | **POST** /v2/subscribeContext | 
*AllApi* | [**createSubscribeContextAvailability**](docs/AllApi.md#createSubscribeContextAvailability) | **POST** /v2/subscribeContextAvailability | 
*AllApi* | [**createUnsubscribeContext**](docs/AllApi.md#createUnsubscribeContext) | **POST** /v2/unsubscribeContext | 
*AllApi* | [**createUnsubscribeContextAvailability**](docs/AllApi.md#createUnsubscribeContextAvailability) | **POST** /v2/unsubscribeContextAvailability | 
*AllApi* | [**deleteContextProviderRegistration**](docs/AllApi.md#deleteContextProviderRegistration) | **DELETE** /v2/registrations/{registrationId} | 
*AllApi* | [**deleteSubscription**](docs/AllApi.md#deleteSubscription) | **DELETE** /v2/subscriptions/{subscriptionId} | 
*AllApi* | [**getAttributeData**](docs/AllApi.md#getAttributeData) | **GET** /v2/entities/{entityId}/attrs/{attrName} | 
*AllApi* | [**getAttributeValue**](docs/AllApi.md#getAttributeValue) | **GET** /v2/entities/{entityId}/attrs/{attrName}/value | 
*AllApi* | [**listEntities**](docs/AllApi.md#listEntities) | **GET** /v2/entities | 
*AllApi* | [**removeASingleAttribute**](docs/AllApi.md#removeASingleAttribute) | **DELETE** /v2/entities/{entityId}/attrs/{attrName} | 
*AllApi* | [**removeEntity**](docs/AllApi.md#removeEntity) | **DELETE** /v2/entities/{entityId} | 
*AllApi* | [**retrieveAPIResources**](docs/AllApi.md#retrieveAPIResources) | **GET** /v2 | 
*AllApi* | [**retrieveContextProviderRegistration**](docs/AllApi.md#retrieveContextProviderRegistration) | **GET** /v2/registrations/{registrationId} | 
*AllApi* | [**retrieveEntity**](docs/AllApi.md#retrieveEntity) | **GET** /v2/entities/{entityId} | 
*AllApi* | [**retrieveEntityAttributes**](docs/AllApi.md#retrieveEntityAttributes) | **GET** /v2/entities/{entityId}/attrs | 
*AllApi* | [**retrieveEntityType**](docs/AllApi.md#retrieveEntityType) | **GET** /v2/types/{entityType} | 
*AllApi* | [**retrieveEntityTypes**](docs/AllApi.md#retrieveEntityTypes) | **GET** /v2/types/ | 
*AllApi* | [**retrieveRegistrations**](docs/AllApi.md#retrieveRegistrations) | **GET** /v2/registrations | 
*AllApi* | [**retrieveSubscription**](docs/AllApi.md#retrieveSubscription) | **GET** /v2/subscriptions/{subscriptionId} | 
*AllApi* | [**retrieveSubscriptions**](docs/AllApi.md#retrieveSubscriptions) | **GET** /v2/subscriptions | 
*AllApi* | [**update**](docs/AllApi.md#update) | **POST** /v2/op/update | 
*AllApi* | [**updateAttributeData**](docs/AllApi.md#updateAttributeData) | **PUT** /v2/entities/{entityId}/attrs/{attrName} | 
*AllApi* | [**updateContextAvailabilitySubscription**](docs/AllApi.md#updateContextAvailabilitySubscription) | **POST** /v2/updateContextAvailabilitySubscription | 
*AllApi* | [**updateContextSubscription**](docs/AllApi.md#updateContextSubscription) | **POST** /v2/updateContextSubscription | 
*AllApi* | [**updateOrAppendEntityAttributes**](docs/AllApi.md#updateOrAppendEntityAttributes) | **POST** /v2/entities/{entityId}/attrs | 
*AllApi* | [**updateReplaceAllEntityAttributes**](docs/AllApi.md#updateReplaceAllEntityAttributes) | **PUT** /v2/entities/{entityId}/attrs | 
*AttributeValueApi* | [**getAttributeValue**](docs/AttributeValueApi.md#getAttributeValue) | **GET** /v2/entities/{entityId}/attrs/{attrName}/value | 
*AttributesApi* | [**getAttributeData**](docs/AttributesApi.md#getAttributeData) | **GET** /v2/entities/{entityId}/attrs/{attrName} | 
*AttributesApi* | [**removeASingleAttribute**](docs/AttributesApi.md#removeASingleAttribute) | **DELETE** /v2/entities/{entityId}/attrs/{attrName} | 
*AttributesApi* | [**updateAttributeData**](docs/AttributesApi.md#updateAttributeData) | **PUT** /v2/entities/{entityId}/attrs/{attrName} | 
*BatchOperationsApi* | [**createDiscover**](docs/BatchOperationsApi.md#createDiscover) | **POST** /v2/op/discover/ | 
*BatchOperationsApi* | [**createQuery**](docs/BatchOperationsApi.md#createQuery) | **POST** /v2/op/query | 
*BatchOperationsApi* | [**createRegister**](docs/BatchOperationsApi.md#createRegister) | **POST** /v2/op/register | 
*BatchOperationsApi* | [**update**](docs/BatchOperationsApi.md#update) | **POST** /v2/op/update | 
*EntitiesApi* | [**createEntity**](docs/EntitiesApi.md#createEntity) | **POST** /v2/entities | 
*EntitiesApi* | [**listEntities**](docs/EntitiesApi.md#listEntities) | **GET** /v2/entities | 
*EntitiesApi* | [**removeEntity**](docs/EntitiesApi.md#removeEntity) | **DELETE** /v2/entities/{entityId} | 
*EntitiesApi* | [**retrieveEntity**](docs/EntitiesApi.md#retrieveEntity) | **GET** /v2/entities/{entityId} | 
*EntitiesApi* | [**retrieveEntityAttributes**](docs/EntitiesApi.md#retrieveEntityAttributes) | **GET** /v2/entities/{entityId}/attrs | 
*EntitiesApi* | [**updateOrAppendEntityAttributes**](docs/EntitiesApi.md#updateOrAppendEntityAttributes) | **POST** /v2/entities/{entityId}/attrs | 
*EntitiesApi* | [**updateReplaceAllEntityAttributes**](docs/EntitiesApi.md#updateReplaceAllEntityAttributes) | **PUT** /v2/entities/{entityId}/attrs | 
*OMANGSIOperationsApi* | [**createNotifyContext**](docs/OMANGSIOperationsApi.md#createNotifyContext) | **POST** /v2/notifyContext | 
*OMANGSIOperationsApi* | [**createNotifyContextAvailability**](docs/OMANGSIOperationsApi.md#createNotifyContextAvailability) | **POST** /v2/notifyContextAvailability | 
*OMANGSIOperationsApi* | [**createSubscribeContext**](docs/OMANGSIOperationsApi.md#createSubscribeContext) | **POST** /v2/subscribeContext | 
*OMANGSIOperationsApi* | [**createSubscribeContextAvailability**](docs/OMANGSIOperationsApi.md#createSubscribeContextAvailability) | **POST** /v2/subscribeContextAvailability | 
*OMANGSIOperationsApi* | [**createUnsubscribeContext**](docs/OMANGSIOperationsApi.md#createUnsubscribeContext) | **POST** /v2/unsubscribeContext | 
*OMANGSIOperationsApi* | [**createUnsubscribeContextAvailability**](docs/OMANGSIOperationsApi.md#createUnsubscribeContextAvailability) | **POST** /v2/unsubscribeContextAvailability | 
*OMANGSIOperationsApi* | [**updateContextAvailabilitySubscription**](docs/OMANGSIOperationsApi.md#updateContextAvailabilitySubscription) | **POST** /v2/updateContextAvailabilitySubscription | 
*OMANGSIOperationsApi* | [**updateContextSubscription**](docs/OMANGSIOperationsApi.md#updateContextSubscription) | **POST** /v2/updateContextSubscription | 
*RegistrationsApi* | [**createANewContextProviderRegistration**](docs/RegistrationsApi.md#createANewContextProviderRegistration) | **POST** /v2/registrations | 
*RegistrationsApi* | [**deleteContextProviderRegistration**](docs/RegistrationsApi.md#deleteContextProviderRegistration) | **DELETE** /v2/registrations/{registrationId} | 
*RegistrationsApi* | [**retrieveContextProviderRegistration**](docs/RegistrationsApi.md#retrieveContextProviderRegistration) | **GET** /v2/registrations/{registrationId} | 
*RegistrationsApi* | [**retrieveRegistrations**](docs/RegistrationsApi.md#retrieveRegistrations) | **GET** /v2/registrations | 
*SubscriptionsApi* | [**createANewSubscription**](docs/SubscriptionsApi.md#createANewSubscription) | **POST** /v2/subscriptions | 
*SubscriptionsApi* | [**deleteSubscription**](docs/SubscriptionsApi.md#deleteSubscription) | **DELETE** /v2/subscriptions/{subscriptionId} | 
*SubscriptionsApi* | [**retrieveSubscription**](docs/SubscriptionsApi.md#retrieveSubscription) | **GET** /v2/subscriptions/{subscriptionId} | 
*SubscriptionsApi* | [**retrieveSubscriptions**](docs/SubscriptionsApi.md#retrieveSubscriptions) | **GET** /v2/subscriptions | 
*TypesApi* | [**retrieveEntityType**](docs/TypesApi.md#retrieveEntityType) | **GET** /v2/types/{entityType} | 
*TypesApi* | [**retrieveEntityTypes**](docs/TypesApi.md#retrieveEntityTypes) | **GET** /v2/types/ | 


## Documentation for Models

 - [Attribute](docs/Attribute.md)
 - [Attributes](docs/Attributes.md)
 - [Discoverrequest](docs/Discoverrequest.md)
 - [Entity](docs/Entity.md)
 - [EntityType](docs/EntityType.md)
 - [FullEntity](docs/FullEntity.md)
 - [Metadata](docs/Metadata.md)
 - [MetadataAttribute](docs/MetadataAttribute.md)
 - [Queryrequest](docs/Queryrequest.md)
 - [Queryresponse](docs/Queryresponse.md)
 - [Registerrequest](docs/Registerrequest.md)
 - [Registration](docs/Registration.md)
 - [Resources](docs/Resources.md)
 - [Subscription](docs/Subscription.md)
 - [TypeAttribute](docs/TypeAttribute.md)
 - [TypeAttributes](docs/TypeAttributes.md)
 - [UpdateRequest](docs/UpdateRequest.md)


## Documentation for Authorization

All endpoints do not require authorization.
Authentication schemes defined for the API:

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author



