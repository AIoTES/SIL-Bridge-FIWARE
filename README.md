# intermw_bridge_fiware

__TODO__
* registerPlatform // unregisterPlatform --> Not clear what bridges have to do here. It is an operation that must be performed by the INTER-MW itself. What must be returned (platformId, ontology, devices? ) need to be clarfied.
* setStatus --> To be fixed in all the bridges
* subsrcription --> Currently there is a proof of concept. The subscriptions must create unique endpoints to listen. Also, it must manage a system drop/restart.
* Check network status in the bridge and report
* actuate is not a valid method for FIWARE
* observe is implemented, although it does the same job as update when the message is received from platform, why not simplifying?


**General bridge features**

* query --> Currently only done to an ID. This is the very minimum requirement. We need to set up a complete query mechanism.
* getCapabilities --> a method to return the supported methods
* checkPlatform to indicate conectivity for the INTER-MW based clients