package br.com.agibank.qa.api.client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class DogApiClient {

  private static final String BASE_URL = "https://dog.ceo/api";

  private RequestSpecification baseRequest() {
    return RestAssured.given()
        .baseUri(BASE_URL)
        .filter(new AllureRestAssured())
        .header("Accept", "application/json");
  }

  @Step("GET /breeds/list/all - List all breeds")
  public Response listAllBreeds() {
    return baseRequest().get("/breeds/list/all");
  }

  @Step("GET /breed/{breed}/images - Get images for breed: {breed}")
  public Response getBreedImages(String breed) {
    return baseRequest().pathParam("breed", breed).get("/breed/{breed}/images");
  }

  @Step("GET /breeds/image/random - Get random image")
  public Response getRandomImage() {
    return baseRequest().get("/breeds/image/random");
  }
}
