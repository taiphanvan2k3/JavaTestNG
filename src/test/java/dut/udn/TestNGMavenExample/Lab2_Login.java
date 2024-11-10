package dut.udn.TestNGMavenExample;

import java.nio.file.Paths;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Lab2_Login {
	@Test
	public void TestLogin() {
		System.setProperty("webdriver.edge.driver",
				Paths.get(System.getProperty("user.dir"), "test_resources", "msedgedriver.exe").toString());

		EdgeOptions options = new EdgeOptions();
		// options.addArguments("headless"); // Thêm chế độ headless
		// options.addArguments("disable-gpu"); // Tắt GPU (tùy chọn giúp ổn định hơn)

		WebDriver driver = new EdgeDriver(options);
		try {
			// Mở trang web
			driver.get("https://phptravels.net/login");

			WebElement emailField = driver.findElement(By.id("email"));
			WebElement passwordField = driver.findElement(By.id("password"));

			/*
			 * user@phptravels.com/demouser a/a a@gmail.com/a
			 */
			String email = "user@phptravels.com";
			String password = "demouser";

			emailField.sendKeys(email);
			passwordField.sendKeys(password);

			JavascriptExecutor js = (JavascriptExecutor) driver;
			Boolean isValid = (Boolean) js.executeScript("return arguments[0].checkValidity();", emailField);

			if (!isValid) {
				String validationMessage = (String) js.executeScript("return arguments[0].validationMessage;",
						emailField);
				Assert.fail(validationMessage);
			}

			driver.findElement(By.id("submitBTN")).click();

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
			try {
				String expectedUrl = "https://phptravels.net/dashboard";
				wait.until(ExpectedConditions.urlToBe(expectedUrl));

				String currentUrl = driver.getCurrentUrl();
				Assert.assertEquals(expectedUrl, currentUrl, "Login success");
			} catch (Exception e) {
				Assert.fail("Login fail");
			}
		} finally {
			driver.quit();
		}
	}
}
