package br.com.agibank.qa.api.tests;

import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.api.client.DogApiClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Dog API")
@Feature("Breed Images")
class BreedImagesTest {

  private static DogApiClient client;

  @BeforeAll
  static void setUp() {
    client = new DogApiClient();
  }

  @Test
  @DisplayName("Get images for valid breed returns 200 with image URLs")
  @Description("GET /breed/hound/images should return a list of valid image URLs")
  @Severity(SeverityLevel.CRITICAL)
  void getImagesForValidBreed() {
    Response response = client.getBreedImages("hound");
    List<String> images = response.jsonPath().getList("message");

    assertAll(
        () -> assertEquals(200, response.statusCode(), "Status code should be 200"),
        () -> assertEquals("success", response.jsonPath().getString("status")),
        () -> assertFalse(images.isEmpty(), "Image list should not be empty"));
  }

  @Test
  @DisplayName("Breed image URLs are valid and point to images.dog.ceo")
  @Description("All returned URLs should start with https://images.dog.ceo/breeds/")
  @Severity(SeverityLevel.NORMAL)
  void imageUrlsAreValid() {
    Response response = client.getBreedImages("hound");
    List<String> images = response.jsonPath().getList("message");

    images.forEach(
        url ->
            assertTrue(
                url.startsWith("https://images.dog.ceo/breeds/"),
                "URL should point to images.dog.ceo: " + url));
  }

  @Test
  @DisplayName("Breed image URLs contain the breed name")
  @Description("Image URLs for 'hound' should contain 'hound' in the path")
  @Severity(SeverityLevel.NORMAL)
  void imageUrlsContainBreedName() {
    String breed = "hound";
    Response response = client.getBreedImages(breed);
    List<String> images = response.jsonPath().getList("message");

    images.forEach(url -> assertTrue(url.contains(breed), "URL should contain breed name: " + url));
  }

  @Test
  @DisplayName("Get images for invalid breed returns 404")
  @Description("GET /breed/invalidbreed999/images should return status 404 with error message")
  @Severity(SeverityLevel.CRITICAL)
  void getImagesForInvalidBreedReturns404() {
    Response response = client.getBreedImages("invalidbreed999");

    assertAll(
        () -> assertEquals(404, response.statusCode(), "Status code should be 404"),
        () ->
            assertEquals(
                "error", response.jsonPath().getString("status"), "Status should be 'error'"),
        () ->
            assertNotNull(
                response.jsonPath().getString("message"), "Error message should not be null"));
  }
}
