# intermw_bridge_fiware


**General bridge features**
* Communication with the Context Broker using NGSi v2.
* Device discovery based on subscriptions. Device discovery can be configured using the properties entityTypes and services. To use device discovery the property entityTypes must contain the types of the entities to be discovered.
* Minimum virtual device creation: the entities are created with only id, type, service  and servicePath (can be updated using observe).


__TODO__
* actuate has not been tested (needs semantic alignment)
* query --> Currently implemented by ID and as a general query to all entities in Fiware. Not tested yet with semantic alignments.
* platformCreateDevices, platformUpdateDevices: test with semantic alignments.


**Bridge configuration and device discovery**
The automatic registration of the devices when the Fiware platform is registered depends on a correct configuration of the bridge. The following configuration parameters are needed for the device discovery:
* entityTypes: a list (separated by commas) of the entity types that represent devices in the Fiware platform.
* services:  a list (separated by commas) of the Fiware services where the devices of interest are located. If this property is not set and the device discovery is enabled, only the default Fiware service will be used in the discovery.
* discoveryUrl: base URL of the discovery enabler.
* discoveryInterval: time interval for querying the discovery enabler about new devices (in ms).
* discoveryToken: token for the discovery enabler.


The bridge can use two different methods for the device discovery, depending on how it is configured:
* If entityTypes is not set, the device discovery is disabled.
* If entityTypes is set and discoveryUrl is not set, the device discovery method based on subscriptions is used. This method is not advisable if there is a considerable amount of devices in the platform (>500 approximately).
* If both entityTypes and discoveryUrl are set, the device discovery method based on a discovery enabled is used. This method relies on the use of a custom discovery enabler on the Fiware platform. The properties discoveryInterval and discoveryToken must be also set to the proper values in this case. 



### Automatic bridge installation

Install bridges parent POM.


Build bridge and create installation docker image (from mw.bridge.orion directory):

`mvn clean package docker:build`


Install bridge (if intermw has been deployed from the intermw-ipsm-deployment directory):

`docker run -v  intermw-ipsm-deployment_intermw_config:/volume/config -v  intermw-ipsm-deployment_intermw_bin:/volume/bin mw.bridge.orion-mngt:latest`


Uninstall bridge:

`docker run -v  intermw-ipsm-deployment_intermw_config:/volume/config -v  intermw-ipsm-deployment_intermw_bin:/volume/bin --entrypoint "/mngt/script/uninstall.sh" mw.bridge.orion-mngt:latest`