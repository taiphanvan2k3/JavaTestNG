package selenium.example.lab02;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import lab02.ExcelUtils;
import lab02.LoginData;
import lab02.ResponseInfo;

public class MultiLoginTest {
	private WebDriver driver;
	private final String FILE_PATH = Paths.get(System.getProperty("user.dir"), "src/test/resources", "login_data.xlsx")
			.toString();

	private Set<LoginData> logs;

	// Đọc từng hàng dữ liệu từ sheet LOGIN_DATA
	private LoginData data;

	@BeforeClass
	public void init() throws IOException {
		System.setProperty("webdriver.edge.driver",
				Paths.get(System.getProperty("user.dir"), "src/test/resources", "msedgedriver.exe").toString());

		// Dùng LinkedHashSet để đảm bảo thứ tự dữ liệu test không bị thay đổi
		logs = new LinkedHashSet<>();
	}

	@BeforeMethod
	public void setUp() {
		EdgeOptions options = new EdgeOptions();
		// options.addArguments("headless"); // Thêm chế độ headless
		// options.addArguments("disable-gpu"); // Tắt GPU (tùy chọn giúp ổn định hơn)

		driver = new EdgeDriver(options);
		driver.get("https://phptravels.net/login");

		data = new LoginData();
	}

	@AfterClass
	public void destroy() throws IOException {
		// Sau khi đã thực hiện tất cả test case
		// Ghi dữ liệu vào file Excel
		LoginData.writeLog(FILE_PATH, "LOGIN_TEST_RESULT", logs);
	}

	@DataProvider(name = "loginData")
	public Object[][] data() throws IOException {
		XSSFWorkbook workbook = ExcelUtils.getWorkbook(FILE_PATH);
		XSSFSheet sheet = workbook.getSheet("LOGIN_DATA");
		Object[][] data = ExcelUtils.readSheetData(sheet);

		// In dữ liệu ra console
		for (Object[] row : data) {
			System.out.println(Arrays.toString(row));
		}

		return data;
	}

	@Test(dataProvider = "loginData")
	public void TestLogin(String email, String password, String expected) {
		ResponseInfo responseInfo = processLogin(email, password);

		data.setEmail(email);
		data.setPassword(password);
		data.setTestMethod("TestLogin");
		data.setAction("Test login function");
		data.setLogTime(new Date());
		data.setExpected(expected);

		String actual = responseInfo.isSuccess() ? "Login Success" : responseInfo.getErrorType();
		data.setActual(actual);

		String detailedMessage = responseInfo.getDetailedMessage();
		if (detailedMessage != null) {
			detailedMessage = detailedMessage.trim().equals(":") ? "Invalid Login: Please check your emal and password"
					: responseInfo.getDetailedMessage();
		}
		data.setException(detailedMessage);
		data.setStatus(actual.equals(expected) ? "PASSED" : "FAILED");

		logs.add(data);
		Assert.assertEquals(actual, expected);
	}

	private ResponseInfo processLogin(String email, String password) {
		WebElement emailField = driver.findElement(By.id("email"));
		WebElement passwordField = driver.findElement(By.id("password"));

		emailField.sendKeys(email);
		passwordField.sendKeys(password);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		Boolean isValid = (Boolean) js.executeScript("return arguments[0].checkValidity();", emailField);

		ResponseInfo response = new ResponseInfo();

		if (!isValid) {
			String validationMessage = (String) js.executeScript("return arguments[0].validationMessage;", emailField);
			response.setSuccess(false);
			response.setErrorType("Invalid Input");
			response.setDetailedMessage(validationMessage);

			driver.quit();
			return response;
		}

		driver.findElement(By.id("submitBTN")).click();
		try {
			try {
				new WebDriverWait(driver, Duration.ofSeconds(6))
						.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".vt-card.error")));
				WebElement toast = driver.findElement(By.cssSelector(".vt-card.error"));
				if (toast != null) {
					// Lấy nội dung tiêu đề thông báo (h4) và mô tả (p)
					WebElement header = toast.findElement(By.cssSelector("h4"));
					WebElement description = toast.findElement(By.cssSelector("p"));

					// In ra nội dung
					String headerText = header.getText();
					String descriptionText = description.getText();

					response.setSuccess(false);
					response.setErrorType("Login Fail");
					response.setDetailedMessage(headerText + ": " + descriptionText);
					return response;
				}

			} catch (Exception e) {
			}

			String expectedUrl = "https://phptravels.net/dashboard";
			String actualUrl = driver.getCurrentUrl();

			if (!expectedUrl.equals(actualUrl)) {
				response.setSuccess(false);
				response.setErrorType("Login Fail");
				response.setDetailedMessage("Redirect to " + actualUrl);
				return response;
			}

			response.setSuccess(true);
			return response;
		} finally {
			driver.quit();
		}
	}
}
