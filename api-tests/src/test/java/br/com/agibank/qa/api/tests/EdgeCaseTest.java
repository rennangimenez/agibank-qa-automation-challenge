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
@Feature("⚠️ Casos Extremos")
@Owner("rennan")
@Link(name = "Dog API Docs", url = "https://dog.ceo/dog-api/")
@DisplayName("⚠️ Casos Extremos")
class EdgeCaseTest {

  private static DogApiClient client;

  @BeforeAll
  static void setUp() {
    client = new DogApiClient();
  }

  @Test
  @DisplayName("🔣 Nome de raça com caracteres especiais retorna 404")
  @Description(
      "Nomes de raça contendo @, #, $ devem retornar erro 404, "
          + "validando a rejeição de entradas com caracteres inválidos.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🚫 Nomes de Raça Inválidos")
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
  @DisplayName("🔢 Nome numérico de raça retorna 404")
  @Description("Usar um valor numérico como nome de raça deve retornar 404.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🚫 Nomes de Raça Inválidos")
  void numericBreedNameReturns404() {
    // Arrange
    String breed = BreedData.NUMERIC_BREED;

    // Act
    Response response = client.getBreedImages(breed);

    // Assert
    assertEquals(404, response.statusCode(), "Should return 404 for numeric breed");
  }

  @Test
  @DisplayName("📏 Nome de raça extremamente longo retorna 404")
  @Description(
      "Um nome de raça com mais de 100 caracteres deve retornar 404 (ou 414) "
          + "de forma estável, sem causar erro no servidor.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🚫 Nomes de Raça Inválidos")
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
  @DisplayName("🌍 Nome de raça com caracteres Unicode é tratado")
  @Description(
      "Caracteres Unicode no nome da raça devem ser tratados sem causar crash no servidor "
          + "(não deve retornar 5xx).")
  @Severity(SeverityLevel.NORMAL)
  @Story("🚫 Nomes de Raça Inválidos")
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
  @DisplayName("🎲 Múltiplas imagens aleatórias retorna quantidade correta")
  @Description(
      "A requisição GET /breeds/image/random/5 deve retornar exatamente 5 URLs de imagem, "
          + "validando a paginação do endpoint.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("🎲 Múltiplas Imagens Aleatórias")
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
  @DisplayName("0️⃣ Requisição com contagem zero é tratada")
  @Description("Solicitar 0 imagens aleatórias deve ser tratado de forma estável pela API.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🎲 Múltiplas Imagens Aleatórias")
  void randomImagesWithZeroCount() {
    // Act
    Response response = client.getMultipleRandomImages(0);

    // Assert
    assertTrue(
        response.statusCode() == 200 || response.statusCode() == 400,
        "Should return 200 or 400 for zero count, got " + response.statusCode());
  }

  @Test
  @DisplayName("➖ Contagem negativa de imagens não causa erro no servidor")
  @Description(
      "Solicitar um número negativo de imagens aleatórias não deve causar erro 5xx no servidor.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🎲 Múltiplas Imagens Aleatórias")
  void randomImagesWithNegativeCount() {
    // Act
    Response response = client.getMultipleRandomImages(-1);

    // Assert
    assertTrue(
        response.statusCode() < 500,
        "Should not return 5xx for negative count, got " + response.statusCode());
  }

  @Test
  @DisplayName("🐾 Imagens de sub-raça retornam resultados válidos")
  @Description(
      "A requisição GET /breed/hound/afghan/images deve retornar imagens da sub-raça "
          + "afghan hound, com URLs contendo o path correto.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("🐾 Imagens de Sub-Raça")
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
  @DisplayName("❌ Sub-raça inválida retorna 404")
  @Description(
      "A requisição GET /breed/hound/invalidsubbreed/images deve retornar 404, "
          + "validando o tratamento de sub-raças inexistentes.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🐾 Imagens de Sub-Raça")
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
