# intermw_bridge_fiware


**General bridge features**
* Communication with the Context Broker using NGSi v2.
* Device discovery based on subscriptions. Device discovery can be configured using the properties entityTypes and services. To use device discovery the property entityTypes must contain the types of the entities to be discovered.
* Minimum virtual device creation: the entities are created with only id, type, service  and servicePath (can be updated using observe).


__TODO__
* actuate has not been tested (needs semantic alignment)
* query --> Currently implemented by ID and as a general query to all entities in Fiware. Not tested yet with semantic alignments.
* platformCreateDevices, platformUpdateDevices: test with semantic alignments.


### Automatic bridge installation

Install bridges parent POM.


Build bridge and create installation docker image (from mw.bridge.orion directory):

`mvn clean package docker:build`


Install bridge (if intermw has been deployed from the intermw-ipsm-deployment directory):

`docker run -v  intermw-ipsm-deployment_intermw_config:/volume/config -v  intermw-ipsm-deployment_intermw_bin:/volume/bin mw.bridge.orion-mngt:latest`


Uninstall bridge:

`docker run -v  intermw-ipsm-deployment_intermw_config:/volume/config -v  intermw-ipsm-deployment_intermw_bin:/volume/bin --entrypoint "/mngt/script/uninstall.sh" mw.bridge.orion-mngt:latest`