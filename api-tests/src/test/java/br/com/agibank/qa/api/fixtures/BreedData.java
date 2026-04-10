package br.com.agibank.qa.api.fixtures;

import java.util.List;

public final class BreedData {

  private BreedData() {}

  public static final String VALID_BREED = "hound";
  public static final String INVALID_BREED = "invalidbreed999";
  public static final String SPECIAL_CHARS_BREED = "bull@dog#123";
  public static final String NUMERIC_BREED = "12345";
  public static final String UNICODE_BREED = "café☕\uD83D\uDC15";

  public static final String VALID_SUB_BREED_PARENT = "hound";
  public static final String VALID_SUB_BREED = "afghan";
  public static final String INVALID_SUB_BREED = "invalidsubbreed999";

  public static final List<String> KNOWN_BREEDS = List.of("bulldog", "labrador", "hound");

  public static final int RANDOM_IMAGE_COUNT = 5;
  public static final int LONG_BREED_LENGTH = 200;
  public static final String IMAGE_URL_PREFIX = "https://images.dog.ceo/breeds/";
  public static final String IMAGE_EXTENSION_PATTERN = ".*\\.(jpg|jpeg|png|gif|webp)$";

  public static String longBreedName() {
    return "a".repeat(LONG_BREED_LENGTH);
  }
}
