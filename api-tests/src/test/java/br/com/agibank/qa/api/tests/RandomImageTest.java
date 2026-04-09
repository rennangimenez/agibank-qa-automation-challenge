package br.com.agibank.qa.api.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.api.client.DogApiClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Dog API")
@Feature("Random Image")
class RandomImageTest {

  private static DogApiClient client;

  @BeforeAll
  static void setUp() {
    client = new DogApiClient();
  }

  @Test
  @DisplayName("Random image returns 200 with a valid image URL")
  @Description("GET /breeds/image/random should return status 200 and a single image URL")
  @Severity(SeverityLevel.BLOCKER)
  void randomImageReturns200WithValidUrl() {
    Response response = client.getRandomImage();
    String imageUrl = response.jsonPath().getString("message");

    assertAll(
        () -> assertEquals(200, response.statusCode(), "Status code should be 200"),
        () -> assertEquals("success", response.jsonPath().getString("status")),
        () -> assertNotNull(imageUrl, "Image URL should not be null"),
        () ->
            assertTrue(
                imageUrl.startsWith("https://images.dog.ceo/breeds/"),
                "URL should point to images.dog.ceo"));
  }

  @Test
  @DisplayName("Random image URL has valid image extension")
  @Description("The returned image URL should end with a valid image file extension")
  @Severity(SeverityLevel.NORMAL)
  void randomImageHasValidExtension() {
    Response response = client.getRandomImage();
    String imageUrl = response.jsonPath().getString("message");

    assertTrue(
        imageUrl.matches(".*\\.(jpg|jpeg|png|gif|webp)$"),
        "URL should end with a valid image extension: " + imageUrl);
  }

  @Test
  @DisplayName("Random image response matches JSON schema")
  @Description("The response body should conform to the expected JSON schema")
  @Severity(SeverityLevel.CRITICAL)
  void responseMatchesJsonSchema() {
    Response response = client.getRandomImage();

    assertThat(
        response.body().asString(),
        matchesJsonSchemaInClasspath("schemas/image-response-schema.json"));
  }
}
