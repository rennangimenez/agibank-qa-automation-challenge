package br.com.agibank.qa.api.tests;

import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.api.client.DogApiClient;
import br.com.agibank.qa.api.fixtures.BreedData;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Dog API")
@Feature("Breed Images")
@Owner("rennan")
@Link(name = "Dog API Docs", url = "https://dog.ceo/dog-api/")
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
  @Story("Valid Breed Images")
  void getImagesForValidBreed() {
    // Arrange
    String breed = BreedData.VALID_BREED;

    // Act
    Response response = client.getBreedImages(breed);
    List<String> images = response.jsonPath().getList("message");

    // Assert
    assertAll(
        () -> assertEquals(200, response.statusCode(), "Status code should be 200"),
        () -> assertEquals("success", response.jsonPath().getString("status")),
        () -> assertFalse(images.isEmpty(), "Image list should not be empty"));
  }

  @Test
  @DisplayName("Breed image URLs are valid and point to images.dog.ceo")
  @Description("All returned URLs should start with https://images.dog.ceo/breeds/")
  @Severity(SeverityLevel.NORMAL)
  @Story("Valid Breed Images")
  void imageUrlsAreValid() {
    // Arrange
    String breed = BreedData.VALID_BREED;

    // Act
    Response response = client.getBreedImages(breed);
    List<String> images = response.jsonPath().getList("message");

    // Assert
    images.forEach(
        url ->
            assertTrue(
                url.startsWith(BreedData.IMAGE_URL_PREFIX),
                "URL should point to images.dog.ceo: " + url));
  }

  @Test
  @DisplayName("Breed image URLs contain the breed name")
  @Description("Image URLs for 'hound' should contain 'hound' in the path")
  @Severity(SeverityLevel.NORMAL)
  @Story("Valid Breed Images")
  void imageUrlsContainBreedName() {
    // Arrange
    String breed = BreedData.VALID_BREED;

    // Act
    Response response = client.getBreedImages(breed);
    List<String> images = response.jsonPath().getList("message");

    // Assert
    images.forEach(url -> assertTrue(url.contains(breed), "URL should contain breed name: " + url));
  }

  @Test
  @DisplayName("Get images for invalid breed returns 404")
  @Description("GET /breed/invalidbreed999/images should return status 404 with error message")
  @Severity(SeverityLevel.CRITICAL)
  @Story("Invalid Breed Handling")
  void getImagesForInvalidBreedReturns404() {
    // Arrange
    String breed = BreedData.INVALID_BREED;

    // Act
    Response response = client.getBreedImages(breed);

    // Assert
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
