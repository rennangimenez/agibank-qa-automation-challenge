package br.com.agibank.qa.web.extensions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ScreenshotOnResultExtension implements AfterTestExecutionCallback {

  private static final ExtensionContext.Namespace NS =
      ExtensionContext.Namespace.create(ScreenshotOnResultExtension.class);

  public static void registerPage(ExtensionContext context, Page page) {
    context.getStore(NS).put("page", page);
  }

  public static void registerTracingStarted(ExtensionContext context) {
    context.getStore(NS).put("tracing", Boolean.TRUE);
  }

  private Optional<Page> getPage(ExtensionContext context) {
    return Optional.ofNullable((Page) context.getStore(NS).get("page"));
  }

  private boolean isTracingStarted(ExtensionContext context) {
    return Boolean.TRUE.equals(context.getStore(NS).get("tracing"));
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    boolean failed = context.getExecutionException().isPresent();

    getPage(context)
        .ifPresent(
            page -> {
              if (failed) {
                attachScreenshot(page, "Screenshot (Failure)");
              } else {
                attachScreenshot(page, "Screenshot (Success)");
              }
              Allure.addAttachment("Final URL", "text/plain", page.url());
              Allure.addAttachment("Page Title", "text/plain", page.title());
              stopTracing(context, failed);
            });
  }

  private void attachScreenshot(Page page, String name) {
    try {
      byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
      Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), ".png");
    } catch (Exception e) {
      Allure.addAttachment(
          name + " - capture failed", "text/plain", "Could not capture screenshot: " + e);
    }
  }

  private void stopTracing(ExtensionContext context, boolean attach) {
    if (!isTracingStarted(context)) {
      return;
    }
    getPage(context)
        .ifPresent(
            page -> {
              Path tracePath = null;
              try {
                tracePath = Files.createTempFile("trace-", ".zip");
                page.context().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                if (attach) {
                  Allure.addAttachment(
                      "Playwright Trace",
                      "application/zip",
                      Files.newInputStream(tracePath),
                      ".zip");
                }
              } catch (Exception e) {
                Allure.addAttachment(
                    "Trace capture failed", "text/plain", "Could not capture trace: " + e);
              } finally {
                if (tracePath != null) {
                  try {
                    Files.deleteIfExists(tracePath);
                  } catch (IOException ignored) {
                  }
                }
                context.getStore(NS).remove("tracing");
              }
            });
  }
}
