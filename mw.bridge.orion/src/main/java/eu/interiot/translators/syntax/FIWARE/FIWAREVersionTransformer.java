package eu.interiot.translators.syntax.FIWARE;

import eu.interiot.translators.syntax.transformer.FormatXTransformer;

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

/**
 * Transforms FIWARE JSON between NGSIv1 and NGSIv2
 */
public class FIWAREVersionTransformer extends FormatXTransformer<String> {
    @Override
    public String transformTowardsFormatX(String input) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String transformTowardsINTERMW(String input) {
        throw new UnsupportedOperationException();
    }
}
