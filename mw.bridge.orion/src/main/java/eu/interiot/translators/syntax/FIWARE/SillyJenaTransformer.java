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
