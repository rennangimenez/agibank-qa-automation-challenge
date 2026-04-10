package br.com.agibank.qa.api.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.api.client.DogApiClient;
import br.com.agibank.qa.api.fixtures.BreedData;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Dog API")
@Feature("Breed List")
class BreedListTest {

  private static DogApiClient client;

  @BeforeAll
  static void setUp() {
    client = new DogApiClient();
  }

  @Test
  @DisplayName("List all breeds returns 200 with success status")
  @Description("GET /breeds/list/all should return status 200 and status field 'success'")
  @Severity(SeverityLevel.BLOCKER)
  void listAllBreedsReturns200() {
    // Act
    Response response = client.listAllBreeds();

    // Assert
    assertAll(
        () -> assertEquals(200, response.statusCode(), "Status code should be 200"),
        () ->
            assertEquals(
                "success", response.jsonPath().getString("status"), "Status should be 'success'"));
  }

  @Test
  @DisplayName("Breed list contains known breeds")
  @Description("The breed list should contain well-known breeds like bulldog, labrador, and hound")
  @Severity(SeverityLevel.CRITICAL)
  void breedListContainsKnownBreeds() {
    // Act
    Response response = client.listAllBreeds();
    Map<String, Object> breeds = response.jsonPath().getMap("message");

    // Assert
    assertFalse(breeds.isEmpty(), "Breed list should not be empty");
    BreedData.KNOWN_BREEDS.forEach(
        expected -> assertTrue(breeds.containsKey(expected), "Should contain " + expected));
  }

  @Test
  @DisplayName("Sub-breeds are returned as arrays")
  @Description("Each breed's sub-breeds should be a list (even if empty)")
  @Severity(SeverityLevel.NORMAL)
  void subBreedsAreArrays() {
    // Act
    Response response = client.listAllBreeds();
    Map<String, Object> breeds = response.jsonPath().getMap("message");

    // Assert
    breeds.forEach(
        (breed, subBreeds) ->
            assertInstanceOf(
                java.util.List.class,
                subBreeds,
                "Sub-breeds for '" + breed + "' should be a list"));
  }

  @Test
  @DisplayName("Breed list response matches JSON schema")
  @Description("The response body should conform to the expected JSON schema")
  @Severity(SeverityLevel.CRITICAL)
  void responseMatchesJsonSchema() {
    // Act
    Response response = client.listAllBreeds();

    // Assert
    assertThat(
        response.body().asString(), matchesJsonSchemaInClasspath("schemas/breed-list-schema.json"));
  }
}
