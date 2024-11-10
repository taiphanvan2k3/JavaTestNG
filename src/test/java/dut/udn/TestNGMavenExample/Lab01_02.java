package dut.udn.TestNGMavenExample;

import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.annotations.Test;

public class Lab01_02 {
  @Test
  public void LoginGuru() {
	  System.setProperty("webdriver.edge.driver", "D:\\edgedriver_win64\\msedgedriver.exe");
	  EdgeOptions options = new EdgeOptions();
	  WebDriver driver = new EdgeDriver(options);
	  
	  try {
          // Mở trang web
          driver.get("https://demo.guru99.com/test/login.html");

          // Tìm phần tử có ID là "email" và nhập giá trị
          WebElement emailField = driver.findElement(By.id("email"));
          emailField.sendKeys("admin@gmail.com");
          
          WebElement passwordField = driver.findElement(By.id("passwd"));
          passwordField.sendKeys("admin");
          
          WebElement submitBtn = driver.findElement(By.id("SubmitLogin"));
          submitBtn.click();
          
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
