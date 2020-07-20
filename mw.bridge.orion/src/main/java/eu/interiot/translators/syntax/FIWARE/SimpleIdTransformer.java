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

import eu.interiot.translators.syntax.transformer.JenaTransformer;
import org.apache.jena.rdf.model.*;

import java.util.Set;

/**
 * For more information, contact:
 * - @author <a href="mailto:pawel.szmeja@ibspan.waw.pl">Paweł Szmeja</a>
 */
public class SimpleIdTransformer extends JenaTransformer {


    private String baseURI = "http://inter-iot.eu/syntax/FIWAREv2#";
    private String hasIdURI = baseURI + "hasId";

    public SimpleIdTransformer() {
    	
    }
    
    @Override
    public Model transformTowardsFormatX(Model input) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public Model transformTowardsINTERMW(Model input) {
        Model model = ModelFactory.createDefaultModel().add(input);
      
        Property hasId = model.createProperty(hasIdURI);
        Resource newId = null;
        Resource currentGeneratedId = null;
        Set<Statement> idStatements = model.listStatements(new SimpleSelector(null, hasId, null, null)).toSet();
        for (Statement idStmt : idStatements) {
        	newId = model.createResource((isURI(idStmt.getObject().asLiteral().toString()) ? "" : "http://") + idStmt.getObject().asLiteral().toString());
        	currentGeneratedId = idStmt.getSubject();
        }
        
        Set<Statement> currentIdStatements = model.listStatements(new SimpleSelector(currentGeneratedId, null, null, null)).toSet();
        for (Statement idStmt : currentIdStatements) {
            model.add(newId, idStmt.getPredicate(), idStmt.getObject());
            model.remove(idStmt);
        }
        return model;
    }

    private boolean isURI(String uri) {
        return uri.startsWith("http://") && uri.length() > 7;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }
}
