package dut.udn.TestNGMavenExample;

import java.util.Scanner;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.annotations.Test;

public class SearchGoogleTest {
  @Test
  public void Login() {
	  System.setProperty("webdriver.edge.driver", "D:\\edgedriver_win64\\msedgedriver.exe");
	  EdgeOptions options = new EdgeOptions();
	  WebDriver driver = new EdgeDriver(options);
	  
	  try {
          // Mở trang web
          driver.get("https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/?form=MA13LH#installation");

          // Lấy và in ra tiêu đề trang
          System.out.println("Page title is: " + driver.getTitle());
          
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
