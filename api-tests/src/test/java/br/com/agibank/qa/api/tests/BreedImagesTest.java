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

@Epic("🐕 Dog API")
@Feature("🖼️ Imagens por Raça")
@Owner("rennan")
@Link(name = "Dog API Docs", url = "https://dog.ceo/dog-api/")
@DisplayName("🖼️ Imagens por Raça")
class BreedImagesTest {

  private static DogApiClient client;

  @BeforeAll
  static void setUp() {
    client = new DogApiClient();
  }

  @Test
  @DisplayName("✅ Buscar imagens de raça válida retorna 200 com URLs")
  @Description(
      "A requisição GET /breed/hound/images deve retornar uma lista de URLs de imagens válidas, "
          + "confirmando que o endpoint de imagens está funcional.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("✅ Imagens de Raça Válida")
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
  @DisplayName("🔗 URLs das imagens são válidas e apontam para images.dog.ceo")
  @Description(
      "Todas as URLs retornadas devem começar com https://images.dog.ceo/breeds/, "
          + "garantindo que os links apontam para o CDN correto.")
  @Severity(SeverityLevel.NORMAL)
  @Story("✅ Imagens de Raça Válida")
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
  @DisplayName("🏷️ URLs das imagens contêm o nome da raça")
  @Description(
      "As URLs de imagens para 'hound' devem conter 'hound' no caminho, "
          + "assegurando que as imagens correspondem à raça solicitada.")
  @Severity(SeverityLevel.NORMAL)
  @Story("✅ Imagens de Raça Válida")
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
  @DisplayName("❌ Raça inválida retorna 404")
  @Description(
      "A requisição GET /breed/invalidbreed999/images deve retornar status 404 "
          + "com mensagem de erro, validando o tratamento de raças inexistentes.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("❌ Raça Inválida")
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
