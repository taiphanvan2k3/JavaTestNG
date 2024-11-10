package dut.udn.TestNGMavenExample;

import java.time.Duration;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class Lab01_01 {
	@Test
	public void RegisterAccount() {
		System.setProperty("webdriver.edge.driver", "D:\\edgedriver_win64\\msedgedriver.exe");
		EdgeOptions options = new EdgeOptions();
		WebDriver driver = new EdgeDriver(options);

		try {
			// Mở trang web
			driver.get("https://demo.guru99.com/test/newtours/register.php");

			driver.findElement(By.id("email")).sendKeys("taiphanvan2403");
			driver.findElement(By.cssSelector("input[name='password']")).sendKeys("admin@123");
			driver.findElement(By.cssSelector("input[name='confirmPassword']")).sendKeys("admin@123");
			
			driver.findElement(By.cssSelector("input[name='submit']")).click();
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("a")));
			
			WebElement loginPageLink = driver.findElement(By.cssSelector("a[href='login.php']"));
			loginPageLink.click();
			
			driver.findElement(By.cssSelector("input[name='userName']")).sendKeys("taiphanvan2403");
			driver.findElement(By.cssSelector("input[name='password']")).sendKeys("admin@123");
			driver.findElement(By.cssSelector("input[name='submit']")).click();
			
			// Tạm dừng để xem nội dung
			System.out.println("Press Enter to continue...");
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine(); // Chờ người dùng nhấn phím Enter
			scanner.close();
		} finally {
			// Đóng trình duyệt
			driver.quit();
		}
	}
}
