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


package org.interiot.fiware.ngsiv2.client.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * Resources
 */

public class Resources   {
  @SerializedName("entities_url")
  private String entitiesUrl = null;

  @SerializedName("types_url")
  private String typesUrl = null;

  @SerializedName("subscriptions_url")
  private String subscriptionsUrl = null;

  @SerializedName("registrations_url")
  private String registrationsUrl = null;

  public Resources entitiesUrl(String entitiesUrl) {
    this.entitiesUrl = entitiesUrl;
    return this;
  }

   /**
   * URL which points to the entities resource
   * @return entitiesUrl
  **/
  @ApiModelProperty(example = "null", required = true, value = "URL which points to the entities resource")
  public String getEntitiesUrl() {
    return entitiesUrl;
  }

  public void setEntitiesUrl(String entitiesUrl) {
    this.entitiesUrl = entitiesUrl;
  }

  public Resources typesUrl(String typesUrl) {
    this.typesUrl = typesUrl;
    return this;
  }

   /**
   * URL which points to the types resource
   * @return typesUrl
  **/
  @ApiModelProperty(example = "null", required = true, value = "URL which points to the types resource")
  public String getTypesUrl() {
    return typesUrl;
  }

  public void setTypesUrl(String typesUrl) {
    this.typesUrl = typesUrl;
  }

  public Resources subscriptionsUrl(String subscriptionsUrl) {
    this.subscriptionsUrl = subscriptionsUrl;
    return this;
  }

   /**
   * URL which points to the  subscriptions resource
   * @return subscriptionsUrl
  **/
  @ApiModelProperty(example = "null", required = true, value = "URL which points to the  subscriptions resource")
  public String getSubscriptionsUrl() {
    return subscriptionsUrl;
  }

  public void setSubscriptionsUrl(String subscriptionsUrl) {
    this.subscriptionsUrl = subscriptionsUrl;
  }

  public Resources registrationsUrl(String registrationsUrl) {
    this.registrationsUrl = registrationsUrl;
    return this;
  }

   /**
   * URL which points to the  registrations resource
   * @return registrationsUrl
  **/
  @ApiModelProperty(example = "null", required = true, value = "URL which points to the  registrations resource")
  public String getRegistrationsUrl() {
    return registrationsUrl;
  }

  public void setRegistrationsUrl(String registrationsUrl) {
    this.registrationsUrl = registrationsUrl;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Resources resources = (Resources) o;
    return Objects.equals(this.entitiesUrl, resources.entitiesUrl) &&
        Objects.equals(this.typesUrl, resources.typesUrl) &&
        Objects.equals(this.subscriptionsUrl, resources.subscriptionsUrl) &&
        Objects.equals(this.registrationsUrl, resources.registrationsUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entitiesUrl, typesUrl, subscriptionsUrl, registrationsUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Resources {\n");
    
    sb.append("    entitiesUrl: ").append(toIndentedString(entitiesUrl)).append("\n");
    sb.append("    typesUrl: ").append(toIndentedString(typesUrl)).append("\n");
    sb.append("    subscriptionsUrl: ").append(toIndentedString(subscriptionsUrl)).append("\n");
    sb.append("    registrationsUrl: ").append(toIndentedString(registrationsUrl)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

