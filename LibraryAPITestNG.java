
/** * -----------------------------------------------------------------------------
=======
/**
 * -----------------------------------------------------------------------------
>>>>>>> b57ba591b94af9eb7a957ecbcc1b3962ef282230
 * Project      : Library API Automation
 * Test Class   : LibraryAPITestNG
 * Author       : Santosh Patil
 * Created On   : 30-10-2025
 * Description  : This TestNG-based automation script performs CRUD operations 
 *                on the Library API using RestAssured.
 *                
 *                It includes:
 *                  - Adding multiple books using data-driven approach (Faker)
 *                  - Deleting added books using dynamically captured IDs
 *                  - Assertion validations and detailed logging for failures
 * -----------------------------------------------------------------------------*/

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;

import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;

import files.ReUsableMethods;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class LibraryAPITestNG {

	// List to store dynamically generated book IDs for deletion later
	ArrayList<String> ids = new ArrayList<String>();

	
	/* * NOTE: If this test suite is executed in parallel mode (via TestNG or
=======
	/*
	 * NOTE: If this test suite is executed in parallel mode (via TestNG or
>>>>>>> b57ba591b94af9eb7a957ecbcc1b3962ef282230
	 * Jenkins), the shared 'ids' list may be accessed concurrently by multiple
	 * threads. In that case, consider using: Collections.synchronizedList(new
	 * ArrayList<>()) or CopyOnWriteArrayList for thread-safety.  List<String> ids = Collections.synchronizedList(new ArrayList<>());  */

	/** * -------------------------------------------------------------------------
	 * Test Name : addbook 
	 * Description : Adds new book entries to the Library API
	 * using dynamic data. Data Source : Data provided
	 * by @DataProvider(getDataForAddBook) Validations : - Verifies HTTP 200 status
	 * code - Asserts success message in response - Validates returned ID matches
	 * expected (isbn + aisle)
	 * ------------------------------------------------------------------------- */
	@Test(dataProvider = "getDataForAddBook",
			description = "Verify that AddBook API successfully adds new books with valid details")
	public void addbook(String author, String isbn, String name, String aisle) throws JsonProcessingException {
		
		
		// data structure for add operations
		HashMap<String, Object> book = new HashMap<>();
		// Preparing payload using HashMap
		book.put("isbn", isbn);
		book.put("aisle", aisle);
		book.put("name", name);
		book.put("author", author);

		// Set base URI for API under test
		RestAssured.baseURI = "http://216.10.245.166";

		// Send POST request to add book
		Response response = given()
				.header("Content-Type", "application/json")
				.body(book)
				.when()
				.post("/Library/Addbook.php")
				.then()
				.extract()
				.response();
		SoftAssert softAssert = new SoftAssert();
		// Assertion 1: Validate HTTP status code
		softAssert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 but got: " + response.getStatusCode() );

		// Convert response to JsonPath for parsing
		//String responseBody = response.asString();
		JsonPath js = ReUsableMethods.rawToJson(response.asString());
		String id = js.get("ID");
		ids.add(id); // Store ID for later operations

		// Log book ID in TestNG report
		Reporter.log("Book added successfully with ID: " + id);

		// Assertion 2: Validate success message
		String msg = js.get("Msg");
		softAssert.assertEquals(msg, "successfully added", "Unexpected response message: " + msg);
		
		// Assertion 3: Validate ID matches expected format
		softAssert.assertEquals(id, isbn + aisle, "Book ID mismatch.");

		// Handle assertion failures gracefully with detailed reporting
		try {
			softAssert.assertAll();
		} catch (AssertionError e) {
			String logMessage = "AddBook assertion failed: " + e.getMessage().replace("\n", " ").replaceAll("\\s+", " ")
					+ "\nBook: " + book + " | ID: " + isbn + aisle + "\nResponse: " + response.getStatusCode();
			System.err.println(logMessage);
			Reporter.log(logMessage, true);
			throw e;
		}
	}
	
	/**
	 * -------------------------------------------------------------------------
	 * Test Name : getBook 
	 * Description : Get book details of previously added books using stored
	 * book IDs. Dependency : Depends on successful execution of addbook() Data
	 * Source : Data provided by @DataProvider(getId) 
	 * Validations : - Verifies HTTP 200 status code
	 * -------------------------------------------------------------------------
	 */
	@Test( dataProvider = "getId", dependsOnMethods= {"addbook" }, description = "Verify that GetBook API retirves correct book details for the specified ID")
	public void getBook(String id) {
		Response getResponse = given().queryParam("ID", id)
				.when()
				.get("Library/GetBook.php")
				.then()
				.extract()
				.response();
		int statusCode = getResponse.getStatusCode();
		SoftAssert softAssert = new SoftAssert();
		// Assertion 3: Validate ID matches expected format
		softAssert.assertEquals(statusCode, 200, "Expected status code 200 but got: " + statusCode);
		
		try {
			softAssert.assertAll();
		} catch (AssertionError e) {
			String logMessage = "GetBook assertion failed: " + e.getMessage().replace("\n", " ").replaceAll("\\s+", " ")
					+ "\n ID: " + id;
			System.err.println(logMessage);
			Reporter.log(logMessage, true);
			throw e;
		}
	}
	
	/** * -------------------------------------------------------------------------
	 * Test Name : delbook 
	 * Description : Deletes previously added books using stored
	 * book IDs. Dependency : Depends on successful execution of addbook() Data
	 * Source : Data provided by @DataProvider(getId) Validations : - Verifies HTTP
	 * 200 status code
	 * ------------------------------------------------------------------------- */
	
	@Test(dataProvider = "getId", dependsOnMethods = {
			"addbook" }, priority = 3, description = "Verify that DeleteBook API successfully deletes a book using a valid book ID")
	public void delbook(String id) {
		
		// Delete request payload
		String deletePayload = "{\r\n" + "    \"ID\": \"" + id + "\"\r\n" + "}";

		// Send POST request to delete book
		Response delResponse = given()
				.header("Content-Type", "application/json")
				.body(deletePayload)
				.when()
				.post("/Library/DeleteBook.php")
				.then()
				.extract()
				.response();

		// Assertion: Validate HTTP 200 status code
		int statusCode = delResponse.getStatusCode();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(statusCode, 200);

		// Handle assertion failures with proper logging
		try {
			softAssert.assertAll();
		} catch (AssertionError e) {
			String consoleMessage = "Delete assertion failed: " + e.getMessage().strip().replaceAll("\\s+", " ") + "\n"
					+ "ID: " + id;

			String logMessage = "Delete assertion failed: " + e.getMessage().strip().replaceAll("\\s+", " ") + "<br>\n"
					+ "ID: " + id;

			System.err.println(consoleMessage);
			Reporter.log(logMessage, false);
			throw e;
		}
	}

	/**	 * -------------------------------------------------------------------------
	 * Data Provider : getId Description : Supplies dynamically generated Book IDs
	 * for deletion tests.
	 * -------------------------------------------------------------------------*/
	@DataProvider(name = "getId")
	public Object[][] getIds() {
		Object[][] data = new Object[ids.size()][1];
		for (int i = 0; i < ids.size(); i++) {
			data[i][0] = ids.get(i); // Each row represents a single book ID
		}
		return data;
	}

	/*** -------------------------------------------------------------------------
	 * Data Provider : getDataForAddBook Description : Generates dynamic book data
	 * using Faker library. Purpose : To perform data-driven testing by adding
	 * multiple books with random but valid data.
	 * ------------------------------------------------------------------------- */
	@DataProvider(name = "getDataForAddBook")
	public Object[][] getData1() {
		int numberOfTestCases = 12; // Number of records to create
		Object[][] data = new Object[numberOfTestCases][4];
		Faker faker = new Faker();

		int j = 0;
		do {
			data[j][0] = faker.book().author(); // Author
			data[j][1] = faker.book().genre(); // ISBN
			data[j][2] = faker.book().title(); // Book Name
			data[j][3] = faker.code().gtin8().toString(); // Aisle (unique numeric string)
			j++;
		} while (j < numberOfTestCases);

		return data;
	}
}
