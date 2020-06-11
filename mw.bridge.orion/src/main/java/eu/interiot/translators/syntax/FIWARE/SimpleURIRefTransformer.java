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
package eu.interiot.translators.syntax.FIWARE;

/**
 * INTER-IoT. Interoperability of IoT Platforms.
 * INTER-IoT is a R&D project which has received funding from the European
 * Union<92>s Horizon 2020 research and innovation programme under grant
 * agreement No 687283.
 * <p>
 * Copyright (C) 2016-2018, by (Author's company of this file):
 * - Systems Research Institute Polish Academy of Sciences
 * <p>
 * <p>
 * For more information, contact:
 * - @author <a href="mailto:pawel.szmeja@ibspan.waw.pl">Paweł Szmeja</a>
 * - Project coordinator:  <a href="mailto:coordinator@inter-iot.eu"></a>
 * <p>
 * <p>
 * This code is licensed under the EPL license, available at the root
 * application directory.
 */

import eu.interiot.translators.syntax.transformer.JenaTransformer;
import org.apache.jena.rdf.model.*;

import java.util.Set;

/**
 * Converts (both way) values of reference type (ones that refer to another entity) to RDF Resource references
 * <p>
 * e.g. a FIWARE JSON attribute X {type: "Ref", value: "Device1"} will be converted to RDF X [ hasValue <http://base.uri#Device1>]
 * All URIs are configurable.
 */
public class SimpleURIRefTransformer extends JenaTransformer {
    //TODO: Implement and document

    //The FIWARE type that denotes a reference
    private String referenceType = "Ref";

    private String baseURI = "http://inter-iot.eu/syntax/FIWAREv2#";

    private String hasTypeURI = baseURI + "hasType";
    private String hasAttrValueURI = baseURI + "hasAttrValue";

    public SimpleURIRefTransformer() {

    }

    public SimpleURIRefTransformer(String referenceType, String baseURI, String hasTypeURI, String hasValueURI) {
        this.referenceType = referenceType;
        this.baseURI = baseURI;
        this.hasTypeURI = hasTypeURI;
        this.hasAttrValueURI = hasValueURI;
    }

    @Override
    public Model transformTowardsFormatX(Model input) {
        Model model = ModelFactory.createDefaultModel().add(input);
        Property hasType = model.createProperty(hasTypeURI);
        Property hasValue = model.createProperty(hasAttrValueURI);

        Set<Statement> refTypeStatements = model.listStatements(new SimpleSelector(null,
                hasType,
                referenceType)
        ).toSet();

        for (Statement typeStmt : refTypeStatements) {
            Set<Statement> valueStatements = model.listStatements(new SimpleSelector(
                    typeStmt.getSubject(),
                    hasValue,
                    (String) null
            )).toSet();

            for (Statement valStmt : valueStatements) {
                //Check if the value is a resource
                if (valStmt.getObject().isResource()) {
                    String uri = valStmt.getObject().asResource().getURI();
                    //Check if we can remove the base URI
                    if (uri.startsWith(baseURI)) {
                        //Add new statement
                        model.add(valStmt.getSubject(),
                                valStmt.getPredicate(),
                                uri.substring(baseURI.length()));
                        //Remove old statement
                        model.remove(valStmt);
                    }
                }
            }
        }
        return model;
    }

    @Override
    public Model transformTowardsINTERMW(Model input) {
        Model model = ModelFactory.createDefaultModel().add(input);
        Property hasType = model.createProperty(hasTypeURI);
        Property hasValue = model.createProperty(hasAttrValueURI);
        Set<Statement> refTypeStatements = model.listStatements(new SimpleSelector(null,
                hasType,
                referenceType)
        ).toSet();

        for (Statement typeStmt : refTypeStatements) {
            Set<Statement> valueStatements = model.listStatements(new SimpleSelector(
                    typeStmt.getSubject(),
                    hasValue,
                    (String) null
            )).toSet();

            for (Statement valStmt : valueStatements) {
                //Check if the value is a literal
                if (valStmt.getObject().isLiteral()) {
                    String fragment = valStmt.getObject().asLiteral().getValue().toString();
                    //Check if we can add the base URI
                    if (isURIFragment(fragment) && !isURI(fragment)) {
                        //Add new statement
                        model.add(valStmt.getSubject(),
                                valStmt.getPredicate(),
                                model.createResource(addBaseURI(fragment)));
                        //Remove old statement
                        model.remove(valStmt);
                    }
                }
            }
        }
        return model;
    }

    private String addBaseURI(String fragment) {
        return baseURI + fragment;
    }

    private boolean isURIFragment(String fragment) {
        //TODO: Check and verify proper URI fragment format (e.g. no spaces)
        return !fragment.contains(" ");
    }

    private boolean isURI(String uri) {
        return uri.startsWith("http://") && uri.length() > 7;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public String getHasTypeURI() {
        return hasTypeURI;
    }

    public void setHasTypeURI(String hasTypeURI) {
        this.hasTypeURI = hasTypeURI;
    }

    public String getHasAttrValueURI() {
        return hasAttrValueURI;
    }

    public void setHasAttrValueURI(String hasAttrValueURI) {
        this.hasAttrValueURI = hasAttrValueURI;
    }
}
