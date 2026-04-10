package br.com.agibank.qa.web.extensions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ScreenshotOnResultExtension implements AfterTestExecutionCallback {

  private static final ThreadLocal<Page> CURRENT_PAGE = new ThreadLocal<>();
  private static final ThreadLocal<Boolean> TRACING_STARTED = new ThreadLocal<>();

  public static void setPage(Page page) {
    CURRENT_PAGE.set(page);
  }

  public static void setTracingStarted(boolean started) {
    TRACING_STARTED.set(started);
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    Page page = CURRENT_PAGE.get();
    if (page == null) {
      return;
    }

    boolean failed = context.getExecutionException().isPresent();

    if (failed) {
      attachScreenshot(page, "Screenshot (Failure)");
    } else {
      attachScreenshot(page, "Screenshot (Success)");
    }
    Allure.addAttachment("Final URL", "text/plain", page.url());
    Allure.addAttachment("Page Title", "text/plain", page.title());
    stopTracing(page, failed);
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

  private void stopTracing(Page page, boolean attach) {
    if (!Boolean.TRUE.equals(TRACING_STARTED.get())) {
      return;
    }
    Path tracePath = null;
    try {
      tracePath = Files.createTempFile("trace-", ".zip");
      page.context().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
      if (attach) {
        Allure.addAttachment(
            "Playwright Trace", "application/zip", Files.newInputStream(tracePath), ".zip");
      }
    } catch (Exception e) {
      Allure.addAttachment("Trace capture failed", "text/plain", "Could not capture trace: " + e);
    } finally {
      if (tracePath != null) {
        try {
          Files.deleteIfExists(tracePath);
        } catch (IOException ignored) {
        }
      }
      TRACING_STARTED.remove();
    }
  }
}
