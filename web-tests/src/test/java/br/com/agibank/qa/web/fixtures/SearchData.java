package br.com.agibank.qa.web.fixtures;

public final class SearchData {

  private SearchData() {}

  public static final String VALID_TERM = "empréstimo";
  public static final String NONEXISTENT_TERM = "xyzqwerty999";

  public static final String SQL_INJECTION = "'; DROP TABLE users;--";
  public static final String XSS_SCRIPT = "<script>alert('xss')</script>";
  public static final String HTML_INJECTION = "<h1>Injected</h1>";
  public static final String SPECIAL_CHARACTERS = "<>&\"'%\\";
  public static final int LONG_INPUT_LENGTH = 5000;

  public static String longInput() {
    return "a".repeat(LONG_INPUT_LENGTH);
  }
}
