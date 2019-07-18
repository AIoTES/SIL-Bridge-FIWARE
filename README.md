# intermw_bridge_fiware


**General bridge features**
* Communication with the Context Broker using NGSi v2.
* Device discovery based on subscriptions. Device discovery can be configured using the properties entityTypes and services. To use device discovery the property entityTypes must contain the types of the entities to be discovered.
* Minimum virtual device creation: the entities are created with only id, type, service  and servicePath (can be updated using observe).


__TODO__
* actuate has not been tested (needs semantic alignment)
* query --> Currently implemented by ID and as a general query to all entities in Fiware. Not tested yet with semantic alignments.
* platformCreateDevices, platformUpdateDevices: test with semantic alignments.
