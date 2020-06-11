/*
 * Copyright 2016-2018 Universitat Politècnica de València
 * Copyright 2016-2018 Università della Calabria
 * Copyright 2016-2018 Prodevelop, SL
 * Copyright 2016-2018 Technische Universiteit Eindhoven
 * Copyright 2016-2018 Fundación de la Comunidad Valenciana para la
 * Investigación, Promoción y Estudios Comerciales de Valenciaport
 * Copyright 2016-2018 Rinicom Ltd
 * Copyright 2016-2018 Association pour le développement de la formation
 * professionnelle dans le transport
 * Copyright 2016-2018 Noatum Ports Valenciana, S.A.U.
 * Copyright 2016-2018 XLAB razvoj programske opreme in svetovanje d.o.o.
 * Copyright 2016-2018 Systems Research Institute Polish Academy of Sciences
 * Copyright 2016-2018 Azienda Sanitaria Locale TO5
 * Copyright 2016-2018 Alessandro Bassi Consulting SARL
 * Copyright 2016-2018 Neways Technologies B.V.
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.interiot.intermw.bridge.orion;

import java.util.List;

import eu.interiot.intermw.bridge.orion.enums.Options;

/**
 * 
 * @author <a href="mailto:mllorente@prodevelop.es">Miguel A. Llorente</a>
 * @author <a href="mailto:aromeu@prodevelop.es">Alberto Romeu</a>
 *
 */
public class Parameters{

	/**
	 * A list of elements. Retrieve entities whose ID matches
	 * one of the elements in the list. Incompatible with idPattern. Example:
	 * Boe_Idearium
	 */
	private List<String>  id;
	/**
	 * Comma-separated list of elements. Retrieve entities whose type matches
	 * one of the elements in the list. Incompatible with typePattern. Example:
	 * Room
	 * 
	 */
	private List<String> type;
	/**
	 * A correctly formated regular expression. Retrieve entities whose type
	 * matches the regular expression. Incompatible with type. Example: Room_.*.
	 * 
	 * 
	 */
	private String idPattern;
	/**
	 * A correctly formated regular expression. Retrieve entities whose type
	 * matches the regular expression. Incompatible with type. Example: Room_.*.
	 * 
	 */
	private String typePattern;
	/**
	 * Limits the number of entities to be retrieved Example: 20.
	 */
	private int limit;
	/**
	 * Establishes the offset from where entities are retrieved Example: 20.
	 */
	private int offset;
	/**
	 * Options dictionary Possible values: count , keyValues , values , unique .
	 */
	private Options options;
	/**
	 * A query expression, composed of a list of statements in a List. See
	 * Simple Query Language specification. Example: temperature>40.
	 * 
	 * TODO: link
	 * 
	 */
	private List<String> q;
	/**
	 * A query expression for attribute metadata, composed of a list of
	 * statements in a List. See Simple Query Language specification.
	 * 
	 * TODO: link
	 * 
	 */
	private String mq;
	/**
	 * List of attribute names whose data are to be included in the response.
	 * The attributes are retrieved in the order specified by this parameter. If
	 * this parameter is not included, the attributes are retrieved in arbitrary
	 * order. Example: seatNumber.
	 */
	private List<String> attrs;
	/**
	 * Criteria for ordering results. See "Ordering Results" section for
	 * details. Example: temperature,!speed.
	 * 
	 * The keyword geo:distance to order results by distance to a reference
	 * geometry when a "near" (georel=near) spatial relationship is used.A
	 * comma-separated list of attributes (including virtual attributes), e.g.
	 * temperature,!humidity. Results are ordered by the first attribute. On
	 * ties, the results are ordered by the second attribute and so on. A "!"
	 * before the attribute name means that the order is reversed.
	 */
	private String orderBy;

	/**
	 * TODO: Implement geo powers
	 * 
	 * private Object georel; private Object geometry; private Object coords;
	 * 
	 */

	public Parameters() {	
	}

	/**
	 * @return the id
	 */
	public List<String> getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(List<String> id) {
		this.id = id;
	}

	/**
	 * @return the type
	 * 
	 * 
	 */
	public List<String> getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 * 
	 * TODO: check incompatibilities
	 * 
	 */
	public void setType(List<String> type) {
		this.type = type;
	}

	/**
	 * @return the idPattern
	 */
	public String getIdPattern() {
		return idPattern;
	}

	/**
	 * @param idPattern the idPattern to set
	 * 
	 * TODO: check incompatibilities
	 * 
	 */
	public void setIdPattern(String idPattern) {
		this.idPattern = idPattern;
	}

	/**
	 * @return the typePattern
	 */
	public String getTypePattern() {
		return typePattern;
	}

	/**
	 * @param typePattern the typePattern to set
	 * 
	 * TODO: check incompatibilities
	 * 
	 */
	public void setTypePattern(String typePattern) {
		this.typePattern = typePattern;
	}

	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the options
	 */
	public Options getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(Options options) {
		this.options = options;
	}

	/**
	 * @return the q
	 */
	public List<String> getQ() {
		return q;
	}

	/**
	 * @param q the q to set
	 */
	public void setQ(List<String> q) {
		this.q = q;
	}

	/**
	 * @return the mq
	 */
	public String getMq() {
		return mq;
	}

	/**
	 * @param mq the mq to set
	 */
	public void setMq(String mq) {
		this.mq = mq;
	}

	/**
	 * @return the attrs
	 */
	public List<String> getAttrs() {
		return attrs;
	}

	/**
	 * @param attrs the attrs to set
	 */
	public void setAttrs(List<String> attrs) {
		this.attrs = attrs;
	}

	/**
	 * @return the orderBy
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * @param orderBy the orderBy to set
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
		
}
