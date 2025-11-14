package files;

import io.restassured.path.json.JsonPath;

public class ReUsableMethods {

	public static JsonPath rawToJson(String response) {
		JsonPath Js = new JsonPath(response);
		return Js;

	}

}
