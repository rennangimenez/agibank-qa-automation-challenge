package br.com.agibank.qa.api.fixtures;

public final class SecurityPayloads {

  private SecurityPayloads() {}

  public static final String SQL_INJECTION = "' OR '1'='1";
  public static final String PATH_TRAVERSAL = "../../../etc/passwd";
  public static final String XSS_SCRIPT = "<script>alert(1)</script>";
  public static final String MALICIOUS_CONTENT_TYPE =
      "application/xml; <!ENTITY xxe SYSTEM 'file:///etc/passwd'>";
  public static final int OVERSIZED_HEADER_LENGTH = 8000;

  public static String oversizedHeaderValue() {
    return "A".repeat(OVERSIZED_HEADER_LENGTH);
  }
}
