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

import org.interiot.fiware.ngsiv2.client.ApiCallback;
import org.interiot.fiware.ngsiv2.client.ApiClient;
import org.interiot.fiware.ngsiv2.client.ApiException;
import org.interiot.fiware.ngsiv2.client.ApiResponse;
import org.interiot.fiware.ngsiv2.client.Configuration;
import org.interiot.fiware.ngsiv2.client.Pair;
import org.interiot.fiware.ngsiv2.client.ProgressRequestBody;
import org.interiot.fiware.ngsiv2.client.ProgressResponseBody;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import org.interiot.fiware.ngsiv2.client.model.EntityType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypesApi {
    private ApiClient apiClient;

    public TypesApi() {
        this(Configuration.getDefaultApiClient());
    }

    public TypesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /* Build call for retrieveEntityType */
    private com.squareup.okhttp.Call retrieveEntityTypeCall(String entityType, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'entityType' is set
        if (entityType == null) {
            throw new ApiException("Missing the required parameter 'entityType' when calling retrieveEntityType(Async)");
        }
        

        // create path and map variables
        String localVarPath = "/v2/types/{entityType}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "entityType" + "\\}", apiClient.escapeString(entityType.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    /**
     * 
     * This operation returns a JSON object with information about the type: * &#x60;attrs&#x60; : the set of attribute names along with all the entities of such type, represented in   a JSON object whose keys are the attribute names and whose values contain information of such   attributes (in particular a list of the types used by attributes with that name along with all the   entities). * &#x60;count&#x60; : the number of entities belonging to that type. Response code: * Successful operation uses 200 OK * Errors use a non-2xx and (optionally) an error payload. See subsection on \&quot;Error Responses\&quot; for   more details.
     * @param entityType Entity Type (required)
     * @return EntityType
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public EntityType retrieveEntityType(String entityType) throws ApiException {
        ApiResponse<EntityType> resp = retrieveEntityTypeWithHttpInfo(entityType);
        return resp.getData();
    }

    /**
     * 
     * This operation returns a JSON object with information about the type: * &#x60;attrs&#x60; : the set of attribute names along with all the entities of such type, represented in   a JSON object whose keys are the attribute names and whose values contain information of such   attributes (in particular a list of the types used by attributes with that name along with all the   entities). * &#x60;count&#x60; : the number of entities belonging to that type. Response code: * Successful operation uses 200 OK * Errors use a non-2xx and (optionally) an error payload. See subsection on \&quot;Error Responses\&quot; for   more details.
     * @param entityType Entity Type (required)
     * @return ApiResponse&lt;EntityType&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<EntityType> retrieveEntityTypeWithHttpInfo(String entityType) throws ApiException {
        com.squareup.okhttp.Call call = retrieveEntityTypeCall(entityType, null, null);
        Type localVarReturnType = new TypeToken<EntityType>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * This operation returns a JSON object with information about the type: * &#x60;attrs&#x60; : the set of attribute names along with all the entities of such type, represented in   a JSON object whose keys are the attribute names and whose values contain information of such   attributes (in particular a list of the types used by attributes with that name along with all the   entities). * &#x60;count&#x60; : the number of entities belonging to that type. Response code: * Successful operation uses 200 OK * Errors use a non-2xx and (optionally) an error payload. See subsection on \&quot;Error Responses\&quot; for   more details.
     * @param entityType Entity Type (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call retrieveEntityTypeAsync(String entityType, final ApiCallback<EntityType> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = retrieveEntityTypeCall(entityType, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<EntityType>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for retrieveEntityTypes */
    private com.squareup.okhttp.Call retrieveEntityTypesCall(Double limit, Double offset, String options, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        

        // create path and map variables
        String localVarPath = "/v2/types/".replaceAll("\\{format\\}","json");

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (limit != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "limit", limit));
        if (offset != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "offset", offset));
        if (options != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "options", options));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    /**
     * 
     * If the &#x60;values&#x60; option is not in use, this operation returns a JSON array with the entity types. Each element is a JSON object with information about the type: * &#x60;type&#x60; : the entity type name. * &#x60;attrs&#x60; : the set of attribute names along with all the entities of such type, represented in   a JSON object whose keys are the attribute names and whose values contain information of such   attributes (in particular a list of the types used by attributes with that name along with all the   entities). * &#x60;count&#x60; : the number of entities belonging to that type. If the &#x60;values&#x60; option is used, the operation returns a JSON array with a list of entity type names as strings. Results are ordered by entity &#x60;type&#x60; in alphabetical order. Response code: * Successful operation uses 200 OK * Errors use a non-2xx and (optionally) an error payload. See subsection on \&quot;Error Responses\&quot; for   more details.
     * @param limit Limit the number of types to be retrieved. (optional)
     * @param offset Skip a number of records. (optional)
     * @param options Options dictionary. (optional)
     * @return List&lt;EntityType&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<EntityType> retrieveEntityTypes(Double limit, Double offset, String options) throws ApiException {
        ApiResponse<List<EntityType>> resp = retrieveEntityTypesWithHttpInfo(limit, offset, options);
        return resp.getData();
    }

    /**
     * 
     * If the &#x60;values&#x60; option is not in use, this operation returns a JSON array with the entity types. Each element is a JSON object with information about the type: * &#x60;type&#x60; : the entity type name. * &#x60;attrs&#x60; : the set of attribute names along with all the entities of such type, represented in   a JSON object whose keys are the attribute names and whose values contain information of such   attributes (in particular a list of the types used by attributes with that name along with all the   entities). * &#x60;count&#x60; : the number of entities belonging to that type. If the &#x60;values&#x60; option is used, the operation returns a JSON array with a list of entity type names as strings. Results are ordered by entity &#x60;type&#x60; in alphabetical order. Response code: * Successful operation uses 200 OK * Errors use a non-2xx and (optionally) an error payload. See subsection on \&quot;Error Responses\&quot; for   more details.
     * @param limit Limit the number of types to be retrieved. (optional)
     * @param offset Skip a number of records. (optional)
     * @param options Options dictionary. (optional)
     * @return ApiResponse&lt;List&lt;EntityType&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<EntityType>> retrieveEntityTypesWithHttpInfo(Double limit, Double offset, String options) throws ApiException {
        com.squareup.okhttp.Call call = retrieveEntityTypesCall(limit, offset, options, null, null);
        Type localVarReturnType = new TypeToken<List<EntityType>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * If the &#x60;values&#x60; option is not in use, this operation returns a JSON array with the entity types. Each element is a JSON object with information about the type: * &#x60;type&#x60; : the entity type name. * &#x60;attrs&#x60; : the set of attribute names along with all the entities of such type, represented in   a JSON object whose keys are the attribute names and whose values contain information of such   attributes (in particular a list of the types used by attributes with that name along with all the   entities). * &#x60;count&#x60; : the number of entities belonging to that type. If the &#x60;values&#x60; option is used, the operation returns a JSON array with a list of entity type names as strings. Results are ordered by entity &#x60;type&#x60; in alphabetical order. Response code: * Successful operation uses 200 OK * Errors use a non-2xx and (optionally) an error payload. See subsection on \&quot;Error Responses\&quot; for   more details.
     * @param limit Limit the number of types to be retrieved. (optional)
     * @param offset Skip a number of records. (optional)
     * @param options Options dictionary. (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call retrieveEntityTypesAsync(Double limit, Double offset, String options, final ApiCallback<List<EntityType>> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = retrieveEntityTypesCall(limit, offset, options, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<EntityType>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}