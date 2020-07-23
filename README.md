# Fiware Bridge
Bridge for the Fiware platform.


## Getting stated

To configure the SIL with the FIWARE bridge properly the following files must be added inside a mounted docker volume:

* OrionBridge.properties (bridge configuration file, available in mw.bridge.orion/src/main/config)
* syntactic-translators-1.0.jar (library, available at https://git.activageproject.eu/Bridges_Binaries/Libraries)
* mw.bridge.orion-1.0.1-SNAPSHOT.jar (bridge jar file)


The FIWARE instance only requires Orion to be able to connect to the bridge, no other module is required nor any specific version in particular. The bridge communicates with the Context Broker using NGSI v2.



**Limitations of the current version**

* Authentication options supported by the bridge: authentication by tokens (using a proxy steelskin+keystone and an authentication server with Orion) - Currently not completely ready.
* Subscriptions are created using the entity Id (translated by the bridge). Other options for subscription are currently not supported by the bridge. 


**Limitations on the standard bridge**

The standard FIWARE bridge uses the Orion REST API to perform the discovery and CRUD operations on virtual devices. However, the subscription mechanism provided by FIWARE has limitations that can affect to the initial discovery process, especially on platforms with a large number of virtual devices. More especifically:
* the subscription response is limited to 1000 results. On platfors with iver 1000 virtual devices, the intial discovery will just discover the initial 1000 devices.
* the subscription is not returning information about the 'FIWARE_SERVICE' each device belongs to. This affects to platforms where virtual devices are spread over different services.

If any of these two restrictions apply, it is advisable to install a specific enabler for FIWARE that exposes Orion methods needed by the bridge and manages the initial discovery witouth losing information.

The enabler is available at https://git.activageproject.eu/AIOTES_integration/AIOTES-FIWARE-Discovery-Enabler 

This enable exposes a REST API that has to be made available to the bridge (there should be connectivity between both of them). The url to this API is setup when declaring the platform in the SIL.



### Bridge configuration 
 
Create OrionBridge.properties file with this content and save the file with this name. Modify the configuration following the indications below.

```
# Fiware
datetimeformat=yyyy-MM-dd'T'HH:mm:ss.SSSZ
dateformat=yyyy-MM-dd
deviceIdPrefix=http://inter-iot.eu/dev/

#Discovery
#entityTypes=type1,type2
#services=service1,service1

# Self-signed certificates
#certificate=/path-to-certificate/certificate.jks
#certificate-password=password

# Authentication
#token = keystone-token
```


**What to change?**

Change the value of **deviceIdPrefix** to customize your device identifiers. (Default value “http://inter-iot.eu/dev/”)
Uncomment and add your entity types and services to enable the discovery of your devices.

```
entityTypes=deviceType1,deviceType2
services=myService1,myService1
```

The following two options are only for FIWARE installations that make use of self-signed certificates. Uncomment and add the security certificate and password. If your FIWARE installation uses a valid certificate, do not use these options.

```
certificate=/trustStore.jks
certificate-password=mysecretpassword
```

Uncomment and add token if you use a security token (not likely)
If you don’t use service path, self-signed certificates or tokens leave the file as it is.


**Bridge configuration and device discovery**

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




### Build bridge

You need a machine with Docker to build the bridge and create the installation image.


Install bridges parent POM (you only need to do this the first time). Clone the "parent-bridge" repository https://git.activageproject.eu/Bridges/parent-bridge and use `mvn install`


To build the Fiware bridge:
1. Clone repository
2. Go to the mw.bridge.orion folder
3. Skip all unitary tests in the compilation and use the jar obtained. For compiling the bridge you must use a maven version >= 3.6.0.

`mvn clean package -DskipTests`



### Dependencies 

You have to copy into the INTER-MW library (i.e. lib directory) all runtime dependencies that are required by the bridge project and do not already exist there. Make sure that dependencies added to INTER-MW library do not cause dependency conflict. The mw.bridges.api dependency together with its sub-dependencies are already included in the INTER-MW library.

Dependencies required for FIWARE: 
 
Syntactic-translators-1.0.jar
https://git.activageproject.eu/Bridges_Binaries/Libraries


You can get a list of all dependencies available in INTER-MW library by listing lib directory content:

`$ docker exec <intermw-container> ls -l /usr/local/tomcat/webapps/ROOT/WEB-INF/lib`


To display dependency tree for the bridge project (runtime dependencies), run the following command:

`$ mvn dependency:tree -Dscope=runtime`



### Automatic bridge installation

The bridge can be installed using a Docker image. This image will copy the bridge, its dependencies and its configuration file in the proper directories of the intermw Docker container.


Build bridge and create installation docker image (from mw.bridge.orion directory):

`mvn clean package docker:build`


Install bridge (if intermw has been deployed from the intermw-ipsm-deployment directory):

`docker run -v  intermw-ipsm-deployment_intermw_config:/volume/config -v  intermw-ipsm-deployment_intermw_bin:/volume/bin mw.bridge.orion-mngt:latest`


Uninstall bridge:

`docker run -v  intermw-ipsm-deployment_intermw_config:/volume/config -v  intermw-ipsm-deployment_intermw_bin:/volume/bin --entrypoint "/mngt/script/uninstall.sh" mw.bridge.orion-mngt:latest`



## Testing
JUnit tests are provided with the bridge code. These tests can be adapted to test new functionalities.


## Further information

### Tutorial

**FIWARE platform registration**

In the case of FIWARE, the parameters to be Platforms are registered in the SIL using the POST /mw2mw/platforms operation. provided are:
* **platformId**: the id that will be assigned to the registered platform. It has to conform to the format “http://{DS_CODE}.inter-iot.eu/platforms/{id}”, where DS_CODE is the acronym for the deployment site in ActivAge (e.g., ‘DSVLC’) and ‘id’ is an internal identifier for the platform (eg, ‘fiware’)
* **type**: this is the bridge type to use (http://inter-iot.eu/FIWARE) . This label can be obtained from /get platform-types or from the Table of platform types on the main/general guide. Check that the expected platform type is shown using GET platform-types in the API to confirm that the FIWARE bridge has been integrated correctly.
* **baseEndpoint**: it refers to Orion’s address. It should be an URL (e.g., ‘http://{orion_ipaddress}:{orion_port}). If you are using the FiwareEnabler to enable discovery of large amount of devices, this baseEndpoint should be the url to the enabler REST API.
* **location**: internal attribute used by Inter-Iot to give the geographic location of the platform. This field is optional, but in case it is provided, it has to be an URL.
* **Name**: a label to identify the platform
* **downstreamInputXXX/upstreamInputXXX**: these fields are used to assignt the corresponding upstream and downstream alignments to your platform instance.
 
Example JSON object:
```
 {
  "platformId": "http://dsvlc.inter-iot.eu/platforms/fiware",
  "type": "http://inter-iot.eu/FIWARE",
  "baseEndpoint": "http://myorion:1026",
  "location": "http://dsvlc.inter-iot.eu/activage-server",
  "name": “VLC FIWARE production platform",
  "downstreamInputAlignmentName": "",
  "downstreamInputAlignmentVersion": "",
  "downstreamOutputAlignmentName": "",
  "downstreamOutputAlignmentVersion": "",
  "upstreamInputAlignmentName": "",
  "upstreamInputAlignmentVersion": "",
  "upstreamOutputAlignmentName": "",
  "upstreamOutputAlignmentVersion": ""
}
```


(If you have alignments, you should fill the name and version in this json following the indications from the general guide)

The REST API operation returns 202 (Accepted) response code. To make sure the process has executed successfully, check the response message and the logs.

**Registering devices**

When a platform is registered, the SIL performs automatically a device discovery process, provided it has been configured properly. However, the devices can be also manually registered in the SIL. This can be done using the operation POST /mw2mw/devices. This operation also allows the creation of virtual devices.

The convention used to generate device ids in FIWARE is the following:

“http://inter-iot.eu/dev/{service}/{type}/{entityId}#{servicePath}” 

Where:
* **Service**: it is the ‘Fiware-Service’ header that Orion needs to create the device at a specific tenant.
* **Type**: it denotes the entity type that will be used. This is specific to each deployment site
* **entityId**: this is the id that is assigned in Orion to the entity. It can be a number, a string or an urn. 
* **`servicePath`** (if you have servicePath): the ‘Fiware-ServicePath’ header that is passed to Orion. Remarks: suppress the initial “/”.


If the entity is given by the previous parameters already exists on Orion, only the AIoTES metadata is generated. Otherwise, the entity is created.

For example: 
“http://interiot.eu/dev/activage/Device/urn:ngsi-ld:device:presence001#provider01/installation001” 

In this case, we will create a new device with id “urn:ngsi-ld:device:presence001” in the service “activage” under the servicePath “/provider01/installation001”.

Example of POST data:

```
{
  "devices": [
	{
  	"deviceId": "http://dsvlc.interiot.eu/dev/activage/Device/urn:ngsi-ld:device:presence001#provider01/installation001",
  	"hostedBy": "http://dsvlc.inter-iot.eu/platforms/fiware",
  	"location": "http://dsvlc.inter-iot.eu/activage-server",
  	"name": "Presence Sensor 001"
	},
	...
  ]
}
```

The REST API operation returns 202 (Accepted) response code. To make sure the process has been executed successfully, check the response messages.


## License
The FIWARE bridge is licensed under [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
