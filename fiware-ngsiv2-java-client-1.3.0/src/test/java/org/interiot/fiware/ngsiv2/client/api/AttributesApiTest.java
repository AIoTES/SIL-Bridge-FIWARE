/*
 * FIWARE-NGSI v2 Specification
 * FIWARE-NGSI v2 Specification
 *
 * OpenAPI spec version: 
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.interiot.fiware.ngsiv2.client.api;

import org.interiot.fiware.ngsiv2.client.ApiException;
import org.interiot.fiware.ngsiv2.client.model.Attributes;
import org.interiot.fiware.ngsiv2.client.model.Attribute;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for AttributesApi
 */
public class AttributesApiTest {

    private final AttributesApi api = new AttributesApi();

    
    /**
     * 
     *
     * Returns a JSON object with the attribute data of the attribute. The object follows the JSON representation for attributes (described in \&quot;JSON Attribute Representation\&quot; section). Response: * Successful operation uses 20O OK. * Errors use a non-2xx and (optionally) an error payload. See subsection on \&quot;Error Responses\&quot; for   more details.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getAttributeDataTest() throws ApiException {
        String entityId = null;
        String attrName = null;
        String type = null;
        // Attributes response = api.getAttributeData(entityId, attrName, type);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Removes an entity attribute. Response: * Successful operation uses 204 No Content * Errors use a non-2xx and (optionally) an error payload. See subsection on \&quot;Error Responses\&quot; for   more details.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void removeASingleAttributeTest() throws ApiException {
        String entityId = null;
        String attrName = null;
        String type = null;
        // api.removeASingleAttribute(entityId, attrName, type);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * The request payload is an object representing the new attribute data. Previous attribute data is replaced by the one in the request. The object follows the JSON representation for attributes (described in \&quot;JSON Attribute Representation\&quot; section). Response: * Successful operation uses 204 No Content * Errors use a non-2xx and (optionally) an error payload. See subsection on \&quot;Error Responses\&quot; for   more details.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void updateAttributeDataTest() throws ApiException {
        String entityId = null;
        String attrName = null;
        Attribute body = null;
        String type = null;
        // api.updateAttributeData(entityId, attrName, body, type);

        // TODO: test validations
    }
    
}