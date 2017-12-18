package bridge.orion;

import static spark.Spark.*;

import org.junit.Test;

import spark.Spark;

public class SparkServerTest {

	@Test
	public void serverTest() {
		Spark.port(8080);
		get("/test", (req, res) -> {
			org.apache.jena.atlas.logging.Log.info(this, "<<<<<<Get request>>>>>>");
			return "Hello";
		});
		post("/test", (req, res) -> {
			org.apache.jena.atlas.logging.Log.info(this, "<<<<<<Post request>>>>>>");
			return "Hello";
		});
		int i = 0;
		while (i < 1000) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
	}

}
