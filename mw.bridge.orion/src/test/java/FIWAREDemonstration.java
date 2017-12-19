import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.interiot.translators.syntax.FIWARE.FIWAREv2Translator;
import eu.interiot.translators.syntax.FIWARE.SillyJenaTransformer;
import eu.interiot.translators.syntax.FIWARE.SimpleURIRefTransformer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;

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
public class FIWAREDemonstration {

    public static void main(String[] args) throws Exception {

        File resourcesDirectory = new File("src/test/resources/FIWARE/v2");

        FilenameFilter jsonFilenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(".json")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        File[] jsonFiles = resourcesDirectory.listFiles(jsonFilenameFilter);

        FIWAREv2Translator translator = new FIWAREv2Translator();
//        translator.getJenaTransformers().add(new SillyJenaTransformer());
//        translator.getJenaTransformers().add(new SimpleURIRefTransformer());

        for(File f : jsonFiles){
            System.out.println("************************");
            System.out.println("+++ Input file: " + f.getAbsolutePath() + " +++");
            System.out.println();

            String fileContents = new String(Files.readAllBytes(f.toPath()));

            System.out.println(fileContents);

            System.out.println();
            System.out.println("+++ RDF output: +++");
            System.out.println();

            //Translate towards INTER-MW and apply transformers

            Model jenaModel = translator.toJenaModelTransformed(fileContents);

            System.out.println(translator.printJenaModel(jenaModel, Lang.N3));

            System.out.println();
            System.out.println("+++ Reverse translation: +++");

            //Reverse the translation
            String jsonString = translator.toFormatXTransformed(jenaModel);

            System.out.println();
            System.out.println(translator.prettifyJsonString(jsonString));
            System.out.println();

        }

    }
}
