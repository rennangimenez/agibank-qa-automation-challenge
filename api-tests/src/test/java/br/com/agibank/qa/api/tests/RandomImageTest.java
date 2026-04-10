package br.com.agibank.qa.api.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("🐕 Dog API")
@Feature("🎲 Imagem Aleatória")
@Owner("rennan")
@Link(name = "Dog API Docs", url = "https://dog.ceo/dog-api/")
@DisplayName("🎲 Imagem Aleatória")
class RandomImageTest {

  private static DogApiClient client;

  @BeforeAll
  static void setUp() {
    client = new DogApiClient();
  }

  @Test
  @DisplayName("✅ Imagem aleatória retorna 200 com URL válida")
  @Description(
      "A requisição GET /breeds/image/random deve retornar status 200 e uma URL de imagem "
          + "válida apontando para o CDN images.dog.ceo.")
  @Severity(SeverityLevel.BLOCKER)
  @Story("🎯 Imagem Aleatória Única")
  void randomImageReturns200WithValidUrl() {
    // Act
    Response response = client.getRandomImage();
    String imageUrl = response.jsonPath().getString("message");

    // Assert
    assertAll(
        () -> assertEquals(200, response.statusCode(), "Status code should be 200"),
        () -> assertEquals("success", response.jsonPath().getString("status")),
        () -> assertNotNull(imageUrl, "Image URL should not be null"),
        () ->
            assertTrue(
                imageUrl.startsWith(BreedData.IMAGE_URL_PREFIX),
                "URL should point to images.dog.ceo"));
  }

  @Test
  @DisplayName("🖼️ URL da imagem tem extensão válida (.jpg, .png, etc.)")
  @Description(
      "A URL retornada deve terminar com uma extensão de arquivo de imagem válida, "
          + "como .jpg, .png ou .gif.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🎯 Imagem Aleatória Única")
  void randomImageHasValidExtension() {
    // Act
    Response response = client.getRandomImage();
    String imageUrl = response.jsonPath().getString("message");

    // Assert
    assertTrue(
        imageUrl.matches(BreedData.IMAGE_EXTENSION_PATTERN),
        "URL should end with a valid image extension: " + imageUrl);
  }

  @Test
  @DisplayName("📄 Resposta segue o contrato JSON Schema")
  @Description(
      "O corpo da resposta deve estar em conformidade com o JSON Schema esperado, "
          + "validando o contrato da API para evitar breaking changes.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("📄 Validação de Contrato")
  void responseMatchesJsonSchema() {
    // Act
    Response response = client.getRandomImage();

    // Assert
    assertThat(
        response.body().asString(),
        matchesJsonSchemaInClasspath("schemas/image-response-schema.json"));
  }
}
