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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import lab02.ExcelUtils;
import lab02.ResponseInfo;
import lab02.SignUpData;

public class MultiSignupTest {
    private WebDriver driver;
    private final String FILE_PATH = Paths
            .get(System.getProperty("user.dir"), "src/test/resources", "login_data.xlsx")
            .toString();

    private Set<SignUpData> logs;

    // Đọc từng hàng dữ liệu từ sheet LOGIN_DATA
    private SignUpData data;

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
        options.addArguments("--start-maximized", "--disable-blink-features=AutomationControlled");
        driver = new EdgeDriver(options);
        driver.get("https://phptravels.net/signup");

        data = new SignUpData();
    }

    @AfterClass
    public void destroy() throws IOException {
        // Sau khi đã thực hiện tất cả test case
        // Ghi dữ liệu vào file Excel
        SignUpData.writeLog(FILE_PATH, "SIGNUP_TEST_RESULT", logs);
    }

    @DataProvider(name = "signUpData")
    public Object[][] data() throws IOException {
        XSSFWorkbook workbook = ExcelUtils.getWorkbook(FILE_PATH);
        XSSFSheet sheet = workbook.getSheet("SIGNUP_DATA");
        Object[][] data = ExcelUtils.readSheetData(sheet);

        // In dữ liệu ra console
        for (Object[] row : data) {
            System.out.println(Arrays.toString(row));
        }

        return data;
    }

    @Test(dataProvider = "signUpData")
    public void testSignUp(String firstName, String lastName, String country, String phone, String email,
            String password, String expected) {
        ResponseInfo response = processSignUp(firstName, lastName, country, phone, email, password, expected);

        data.setFirstName(firstName);
        data.setLastName(lastName);
        data.setCountry(country);
        data.setPhone(phone);
        data.setEmail(email);
        data.setPassword(password);
        data.setTestMethod("testSignUp");
        data.setExpected(expected);
        data.setAction("Test signup function");
		data.setLogTime(new Date());

        String actual = response.isSuccess() ? "SignUp Success" : response.getErrorType();
        data.setActual(actual);
        data.setException(response.getDetailedMessage());
        data.setStatus(actual.equals(expected) ? "PASSED" : "FAILED");

        logs.add(data);
        Assert.assertEquals(actual, expected);
    }

    public ResponseInfo processSignUp(String firstName, String lastName, String country, String phone,
            String email,
            String password, String expected) {
        try {
            driver.findElement(By.id("cookie_stop")).click();
            WebElement firstNameField = driver.findElement(By.id("firstname"));
            WebElement lastNameField = driver.findElement(By.id("last_name"));
            WebElement phoneField = driver.findElement(By.id("phone"));
            WebElement emailField = driver.findElement(By.id("user_email"));
            WebElement passwordField = driver.findElement(By.id("password"));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.querySelector('header').style.display = 'none';");
            firstNameField.sendKeys(firstName);
            lastNameField.sendKeys(lastName);
            phoneField.sendKeys(phone);
            emailField.sendKeys(email);
            passwordField.sendKeys(password);

            // Lựa chọn country
            WebElement dropdownButton = driver.findElement(By.cssSelector(".dropdown-toggle.btn-light"));
            dropdownButton.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(By.id("bs-select-1-" + country)));
            option.click();

            // Chuyển vào iframe chứa reCAPTCHA
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(
                    ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector("iframe[title='reCAPTCHA']")));

            // Chờ cho checkbox reCAPTCHA có thể click được
            WebElement recaptchaCheckbox = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector(".recaptcha-checkbox-border")));

            // Di chuyển chuột đến checkbox trước khi click
            Actions actions = new Actions(driver);

            js.executeScript("arguments[0].scrollIntoView(true);", recaptchaCheckbox);
            actions.moveToElement(recaptchaCheckbox).perform();
            driver.switchTo().defaultContent();

            // Click vào nút Đăng ký
            WebElement btnSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
            // xoá disable của btnSubmit
            js.executeScript("arguments[0].removeAttribute('disabled');", btnSubmit);

            Thread.sleep(1000);

            btnSubmit.click();

            // Check sau 3s mà chưa chuyển trang thì báo lỗi
            // Chờ trang reload hoàn tất và toast xuất hiện

            ResponseInfo response = new lab02.ResponseInfo();

            try {
                new WebDriverWait(driver, Duration.ofSeconds(3)).until(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".vt-card.error")));

                WebElement toast = driver.findElement(By.cssSelector(".vt-card.error"));
                if (toast != null) {
                    // Lấy nội dung tiêu đề thông báo (h4) và mô tả (p)
                    WebElement header = toast.findElement(By.cssSelector("h4"));
                    WebElement description = toast.findElement(By.cssSelector("p"));

                    // In ra nội dung
                    String headerText = header.getText();
                    String descriptionText = description.getText();

                    response.setSuccess(false);
                    response.setErrorType("SignUp Fail");
                    response.setDetailedMessage(headerText + ": " + descriptionText);
                    return response;
                }

            } catch (Exception e) {
                // Nếu không tìm thấy toast thì tiếp tục kiểm tra URL
            }

            String expectedUrl = "https://phptravels.net/signup_success";
            String actualUrl = driver.getCurrentUrl();

            if (!actualUrl.equals(expectedUrl)) {
                response.setSuccess(false);
                response.setErrorType("Invalid Input");
                return response;
            }

            response.setSuccess(true);

            return response;
        } catch (Exception e) {
            ResponseInfo response = new ResponseInfo();
            response.setSuccess(false);
            response.setErrorType("Exception");
            response.setDetailedMessage(e.getMessage());
            return response;
        } finally {
            // Đóng trình duyệt
            driver.quit();
        }
    }
}