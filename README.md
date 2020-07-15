# Fiware Bridge
Bridge for the Fiware platform.


**General bridge features**
* Communication with the Context Broker using NGSi v2.
* Device discovery based on subscriptions. Device discovery can be configured using the properties entityTypes and services. To use device discovery the property entityTypes must contain the types of the entities to be discovered.
* Minimum virtual device creation: the entities are created with only id, type, service  and servicePath (can be updated using observe).


__TODO__
* actuate has not been tested (needs semantic alignment)
* query --> Currently implemented by ID and as a general query to all entities in Fiware. Not tested yet with semantic alignments.
* platformCreateDevices, platformUpdateDevices: test with semantic alignments.


## Build
Build with Maven using

mvn clean package -DskipTests


## Limitations on the standard bridge
The standard FIWARE bridge uses the Orion REST API to perform the discovery and CRUD operations on virtual devices. However, the subscription mechanism provided by FIWARE has limitations that can affect to the initial discovery process, especially on platforms with a large number of virtual devices. More especifically:
* the subscription response is limited to 1000 results. On platfors with iver 1000 virtual devices, the intial discovery will just discover the initial 1000 devices.
* the subscription is not returning information about the 'FIWARE_SERVICE' each device belongs to. This affects to platforms where virtual devices are spread over different services.

If any of these two restrictions apply, it is advisable to install a specific enabler for FIWARE that exposes Orion methods needed by the bridge and manages the initial discovery witouth losing information.

The enabler is available at https://git.activageproject.eu/AIOTES_integration/AIOTES-FIWARE-Discovery-Enabler 

This enable exposes a REST API that has to be made available to the bridge (there should be connectivity between both of them). The url to this API is setup when declaring the platform in the SIL.


## Configuration and use
Configuration in OrionBridgeBridge.properties file.

Fiware bridge guide: https://docs.google.com/document/d/1SyBYlofBnNEeWZaLIXphYsxuhKN-taToy4O1pStub5I/edit


### Bridge configuration and device discovery
The automatic registration of the devices when the Fiware platform is registered depends on a correct configuration of the bridge. The following configuration parameters are needed for the device discovery:
* entityTypes: a list (separated by commas) of the entity types that represent devices in the Fiware platform.
* services:  a list (separated by commas) of the Fiware services where the devices of interest are located. If this property is not set and the device discovery is enabled, only the default Fiware service will be used in the discovery.
* discoveryEnabler: true or false (default). If true, use discovery enabler (the base URL of the discovery enabler is provided as the "baseEndpoint" value when the Fiware platform is registered).
* discoveryInterval: time interval for querying the discovery enabler about new devices (in ms).
* discoveryToken: token for the discovery enabler.


The bridge can use two different methods for the device discovery, depending on how it is configured:
* If entityTypes is not set, the device discovery is disabled.
* If entityTypes is set and discoveryEnabler is not set (or false), the device discovery method based on subscriptions is used. This method is not advisable if there is a considerable amount of devices in the platform (>500 approximately).
* If both entityTypes is set and discoveryEnabler is true, the device discovery method based on a discovery enabled is used. This method relies on the use of a custom discovery enabler on the Fiware platform. The properties discoveryInterval and discoveryToken must be also set to the proper values in this case. The discovery enabler will act as a proxy for Orion and its URL must be used as base endpoint for Fiware when the platform is registered.



### Automatic bridge installation
You need a machine with Docker to build the bridge and create the installation image.


Install bridges parent POM (you only need to do this the first time). Clone the "parent-bridge" repository and use `mvn install`


Build bridge and create installation docker image (from mw.bridge.orion directory):

`mvn clean package docker:build`


Install bridge (if intermw has been deployed from the intermw-ipsm-deployment directory):

`docker run -v  intermw-ipsm-deployment_intermw_config:/volume/config -v  intermw-ipsm-deployment_intermw_bin:/volume/bin mw.bridge.orion-mngt:latest`


Uninstall bridge:

`docker run -v  intermw-ipsm-deployment_intermw_config:/volume/config -v  intermw-ipsm-deployment_intermw_bin:/volume/bin --entrypoint "/mngt/script/uninstall.sh" mw.bridge.orion-mngt:latest`