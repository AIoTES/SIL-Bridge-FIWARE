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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

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

//TODO: Remove this file

/**
 * A class for testing transformers
 */
public class SillyJenaTransformer extends JenaTransformer {
    @Override
    public Model transformTowardsFormatX(Model input) {
        Model model = ModelFactory.createDefaultModel().add(input);
//        model.addLiteral(
//                model.createResource("http://silly.string#Resource"),
//                model.createProperty("http://silly.string#isSilly"),
//                true
//                );
//
//        RDFList RDFlist = model.createList(new RDFNode[] {
//                model.createLiteral("A"),
//                model.createLiteral("B"),
//                model.createLiteral("C")
//                });
//
//        RDFList rdfList = model.createList(new RDFNode[] {
//                model.createLiteral("test"),
//                model.createLiteral("1"),
//                model.createResource("http://silly.string#Element"),
//        });
//
//        input.add(model.createResource("http://silly.string#Resource"),
//                model.createProperty("http://silly.string#hasArray"),
//                RDFlist
//                );

        model.addLiteral(
                model.createResource("http://silly.string#Resource"),
                model.createProperty("http://silly.string#isSilly"),
                true
        );

        return model;
    }

    @Override
    public Model transformTowardsINTERMW(Model input) {
        Model model = ModelFactory.createDefaultModel().add(input);
        model.addLiteral(
                model.createResource("http://silly.string#Resource"),
                model.createProperty("http://silly.string#isSilly"),
                true
        );

        return model;
    }
}
