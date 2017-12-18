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
import org.interiot.fiware.ngsiv2.client.model.Resources;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for APIEntryPointApi
 */
public class APIEntryPointApiTest {

    private final APIEntryPointApi api = new APIEntryPointApi();

    
    /**
     * 
     *
     * This resource does not have any attributes. Instead it offers the initial API affordances in the form of the links in the JSON body. It is recommended to follow the “url” link values, [Link](https://tools.ietf.org/html/rfc5988) or Location headers where applicable to retrieve resources. Instead of constructing your own URLs, to keep your client decoupled from implementation details.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void retrieveAPIResourcesTest() throws ApiException {
        // Resources response = api.retrieveAPIResources();

        // TODO: test validations
    }
    
}