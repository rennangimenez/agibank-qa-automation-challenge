package br.com.agibank.qa.api.tests;

import static org.junit.jupiter.api.Assertions.*;

import br.com.agibank.qa.api.client.DogApiClient;
import br.com.agibank.qa.api.fixtures.BreedData;
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
@Feature("Edge Cases")
class EdgeCaseTest {

  private static DogApiClient client;

  @BeforeAll
  static void setUp() {
    client = new DogApiClient();
  }

  @Test
  @DisplayName("Breed name with special characters returns 404")
  @Description("Breed names containing @, #, $ should return a 404 error")
  @Severity(SeverityLevel.NORMAL)
  void breedWithSpecialCharactersReturns404() {
    // Arrange
    String breed = BreedData.SPECIAL_CHARS_BREED;

    // Act
    Response response = client.getBreedImages(breed);

    // Assert
    assertAll(
        () -> assertEquals(404, response.statusCode(), "Should return 404 for special characters"),
        () ->
            assertEquals(
                "error", response.jsonPath().getString("status"), "Status should be 'error'"));
  }

  @Test
  @DisplayName("Numeric breed name returns 404")
  @Description("Using a numeric value as breed name should return 404")
  @Severity(SeverityLevel.NORMAL)
  void numericBreedNameReturns404() {
    // Arrange
    String breed = BreedData.NUMERIC_BREED;

    // Act
    Response response = client.getBreedImages(breed);

    // Assert
    assertEquals(404, response.statusCode(), "Should return 404 for numeric breed");
  }

  @Test
  @DisplayName("Very long breed name returns 404")
  @Description("A breed name exceeding 100 characters should return 404 gracefully")
  @Severity(SeverityLevel.NORMAL)
  void veryLongBreedNameReturns404() {
    // Arrange
    String breed = BreedData.longBreedName();

    // Act
    Response response = client.getBreedImages(breed);

    // Assert
    assertTrue(
        response.statusCode() == 404 || response.statusCode() == 414,
        "Should return 404 or 414 for very long breed name, got " + response.statusCode());
  }

  @Test
  @DisplayName("Breed name with unicode characters returns 404")
  @Description("Unicode characters in breed name should be handled without server crash")
  @Severity(SeverityLevel.NORMAL)
  void unicodeBreedNameIsHandled() {
    // Arrange
    String breed = BreedData.UNICODE_BREED;

    // Act
    Response response = client.getBreedImages(breed);

    // Assert
    assertTrue(
        response.statusCode() < 500,
        "Should not return 5xx for unicode breed name, got " + response.statusCode());
  }

  @Test
  @DisplayName("Multiple random images returns correct count")
  @Description("GET /breeds/image/random/5 should return exactly 5 image URLs")
  @Severity(SeverityLevel.CRITICAL)
  void multipleRandomImagesReturnsCorrectCount() {
    // Arrange
    int count = BreedData.RANDOM_IMAGE_COUNT;

    // Act
    Response response = client.getMultipleRandomImages(count);
    List<String> images = response.jsonPath().getList("message");

    // Assert
    assertAll(
        () -> assertEquals(200, response.statusCode(), "Status code should be 200"),
        () -> assertEquals("success", response.jsonPath().getString("status")),
        () -> assertEquals(count, images.size(), "Should return exactly " + count + " images"));
  }

  @Test
  @DisplayName("Random image with count zero returns empty or error")
  @Description("Requesting 0 random images should be handled gracefully")
  @Severity(SeverityLevel.NORMAL)
  void randomImagesWithZeroCount() {
    // Act
    Response response = client.getMultipleRandomImages(0);

    // Assert
    assertTrue(
        response.statusCode() == 200 || response.statusCode() == 400,
        "Should return 200 or 400 for zero count, got " + response.statusCode());
  }

  @Test
  @DisplayName("Random image with negative count is handled")
  @Description("Requesting a negative number of random images should not cause a server error")
  @Severity(SeverityLevel.NORMAL)
  void randomImagesWithNegativeCount() {
    // Act
    Response response = client.getMultipleRandomImages(-1);

    // Assert
    assertTrue(
        response.statusCode() < 500,
        "Should not return 5xx for negative count, got " + response.statusCode());
  }

  @Test
  @DisplayName("Sub-breed images endpoint returns valid results")
  @Description("GET /breed/hound/afghan/images should return images for the afghan hound sub-breed")
  @Severity(SeverityLevel.CRITICAL)
  void subBreedImagesReturnsResults() {
    // Arrange
    String parent = BreedData.VALID_SUB_BREED_PARENT;
    String sub = BreedData.VALID_SUB_BREED;

    // Act
    Response response = client.getSubBreedImages(parent, sub);
    List<String> images = response.jsonPath().getList("message");

    // Assert
    assertAll(
        () -> assertEquals(200, response.statusCode(), "Status code should be 200"),
        () -> assertEquals("success", response.jsonPath().getString("status")),
        () -> assertFalse(images.isEmpty(), "Image list should not be empty"),
        () ->
            assertTrue(
                images.get(0).contains(parent + "-" + sub),
                "Image URL should contain sub-breed path"));
  }

  @Test
  @DisplayName("Invalid sub-breed returns 404")
  @Description("GET /breed/hound/invalidsubbreed/images should return 404")
  @Severity(SeverityLevel.NORMAL)
  void invalidSubBreedReturns404() {
    // Arrange
    String parent = BreedData.VALID_SUB_BREED_PARENT;
    String sub = BreedData.INVALID_SUB_BREED;

    // Act
    Response response = client.getSubBreedImages(parent, sub);

    // Assert
    assertAll(
        () -> assertEquals(404, response.statusCode(), "Status code should be 404"),
        () ->
            assertEquals(
                "error", response.jsonPath().getString("status"), "Status should be 'error'"));
  }
}
