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
