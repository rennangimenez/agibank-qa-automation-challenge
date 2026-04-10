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
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("🐕 Dog API")
@Feature("📋 Listagem de Raças")
@Owner("rennan")
@Link(name = "Dog API Docs", url = "https://dog.ceo/dog-api/")
@DisplayName("📋 Listagem de Raças")
class BreedListTest {

  private static DogApiClient client;

  @BeforeAll
  static void setUp() {
    client = new DogApiClient();
  }

  @Test
  @DisplayName("✅ Listar todas as raças retorna 200")
  @Description(
      "A requisição GET /breeds/list/all deve retornar status HTTP 200 "
          + "e o campo 'status' com valor 'success', confirmando que a API está operacional.")
  @Severity(SeverityLevel.BLOCKER)
  @Story("📋 Listar Todas as Raças")
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
  @DisplayName("🐶 Lista contém raças conhecidas (bulldog, labrador, hound)")
  @Description(
      "A lista de raças retornada deve conter raças amplamente conhecidas como "
          + "bulldog, labrador e hound, garantindo integridade dos dados.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("📋 Listar Todas as Raças")
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
  @DisplayName("🔀 Sub-raças são retornadas como listas")
  @Description(
      "Cada raça deve ter suas sub-raças retornadas como uma lista (mesmo que vazia), "
          + "garantindo consistência na estrutura de dados da API.")
  @Severity(SeverityLevel.NORMAL)
  @Story("🔀 Estrutura de Sub-Raças")
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
  @DisplayName("📄 Resposta segue o contrato JSON Schema")
  @Description(
      "O corpo da resposta deve estar em conformidade com o JSON Schema esperado, "
          + "validando o contrato da API para evitar breaking changes.")
  @Severity(SeverityLevel.CRITICAL)
  @Story("📄 Validação de Contrato")
  void responseMatchesJsonSchema() {
    // Act
    Response response = client.listAllBreeds();

    // Assert
    assertThat(
        response.body().asString(), matchesJsonSchemaInClasspath("schemas/breed-list-schema.json"));
  }
}
