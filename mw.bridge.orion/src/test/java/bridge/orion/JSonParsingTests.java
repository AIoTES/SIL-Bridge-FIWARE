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
package bridge.orion;

import org.apache.jena.atlas.lib.EscapeStr;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JSonParsingTests {

	@Before
	public void setUpAssets() {

	}

	@Test
	public void testWeighScale() {

		String weighScaleJson = String.join("\n", "{",
				"\"id\": " + "\"" + EscapeStr.stringEsc("ScaleTest") + "\"" + ",",
				"\"type\": " + "\"" + EscapeStr.stringEsc("WeighScale") + "\"" + ",",
				"\"" + EscapeStr.stringEsc("weight") + "\": " + "{", "     \"value\": " + "78" + ",",
				"     \"type\":" + "\"" + EscapeStr.stringEsc("Text") + "\"" + ",", "     \"metadata\": " + "{",
				"         \"unit\": " + "{",
				"              \"value\": " + "\"" + EscapeStr.stringEsc("Kg") + "\"" + ",",
				"              \"type\": " + "\"Text\"", "               },", "     \"irrelevantAttribute2\": " + "{",
				"              \"value\": " + "3.141" + ",", "              \"type\": " + "\"Float\"" + ",",
				"               \"metadata\": " + "{}", "               }", "      }", "}", "}");

		System.out.println(weighScaleJson);

		JsonParser parser = new JsonParser();
		JsonObject o = parser.parse(weighScaleJson).getAsJsonObject();

		System.out.println(o);
		System.out.println(o.get("id"));
		Assert.assertEquals("\"ScaleTest\"", o.get("id").toString());
		System.out.println(o.get("weight").getAsJsonObject().get("value"));
		Assert.assertEquals("78", o.get("weight").getAsJsonObject().get("value").toString());

	}

}
