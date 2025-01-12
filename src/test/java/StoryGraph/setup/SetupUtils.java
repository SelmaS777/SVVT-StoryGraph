package StoryGraph.setup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SetupUtils {

    private static final Logger log = LoggerFactory.getLogger(SetupUtils.class);
    protected static WebDriver webDriver;
    protected static WebDriverWait webDriverWait;
    protected static final String BASEURL = "https://app.thestorygraph.com";
    public static final String EMAIL = "multipurpose.beca@gmail.com";
    public static final String[] USERNAMES = {"multipurpose", "multipurpose1"};
    public static final String[] PASSWORDS = {"1111112", "111111"};
    public static final String[] MODES = {"Dark", "Light"};

    @BeforeAll
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "/C:/Users/selma/Downloads/chromedriver-win64 (2)/chromedriver-win64/chromedriver.exe/");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        webDriver = new ChromeDriver(options);
        webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    public void login(String email, String password) {
        webDriver.get("https://app.thestorygraph.com/users/sign_in");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10)); // Increased wait time

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_email"))); // Correct ID
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_password"))); // Correct ID
        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in-btn"))); // Correct ID

        emailField.sendKeys(email);
        passwordField.sendKeys(password);
        loginButton.click();

        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        webDriver.manage().window().maximize();

    }

    public void logout() throws InterruptedException {

        webDriver.get("https://app.thestorygraph.com/");
        WebElement logoutMenuButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"navbar\"]/div[1]/div/div[4]/div")));
        logoutMenuButton.click();

        WebElement logoutButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"user-menu-dropdown\"]/form/button")));
        logoutButton.click();
        Thread.sleep(2000);
    }

    public void signUp(String email, String username, String password) {

        webDriver.get(BASEURL+"/users/sign_in");

        WebElement signUpLink = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[1]/div/a")));
        signUpLink.click();

        webDriverWait.until(ExpectedConditions.urlToBe(BASEURL+"/users/sign_up"));

        WebElement emailField = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_email")));
        WebElement emailConfirmationField = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_email_confirmation")));
        WebElement usernameField = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_username")));
        WebElement passwordField = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_password")));
        WebElement passwordConfirmationField = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_password_confirmation")));
        WebElement signUpButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-up-btn")));

        emailField.sendKeys(email);
        emailConfirmationField.sendKeys(email);
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        passwordConfirmationField.sendKeys(password);

        signUpButton.click();
    }

    public void selectDate(String day, String month, String year) {

        WebElement dayDropdown = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[3]/div/div[1]/div[1]/div[2]/form/div[2]/select[1]")));
        dayDropdown.click();
        new Select(dayDropdown).selectByValue(day);

        WebElement monthDropdown = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[3]/div/div[1]/div[1]/div[2]/form/div[2]/select[2]")));
        monthDropdown.click();
        new Select(monthDropdown).selectByValue(month);

        WebElement yearDropdown = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[3]/div/div[1]/div[1]/div[2]/form/div[2]/select[3]")));
        yearDropdown.click();
        new Select(yearDropdown).selectByValue(year);

        webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[3]/div/div[1]/div[1]/div[2]/form/input[5]")).click();

    }

    public boolean compareDates(int day1, int month1, int year1, int day2, int month2, int year2) {
        if (year1 != year2) return year1 > year2;
        if (month1 != month2) return month1 > month2;
        return day1 > day2;
    }

    public void addBookToReadPile(String bookUrl, String day, String month, String year) throws InterruptedException {

        webDriver.get(bookUrl);
        Thread.sleep(1000);

        List<WebElement> buttons = webDriver.findElements(By.xpath("//button[@class='btn-dropdown expand-dropdown-button']"));
        WebElement secondButton = buttons.get(1);
        secondButton.click();
        Thread.sleep(500);

        WebElement dropdownContent = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[3]/div/div[1]/div[1]/div/div/div")));
        List<WebElement> buttons2 = dropdownContent.findElements(By.tagName("button"));
        buttons2.get(0).click();
        Thread.sleep(1000);

        List<WebElement> noReadDates = webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[contains(@href, '/edit-read-instance-from-book')]/p[contains(text(), 'No read date')]")));
        WebElement noReadDate = noReadDates.get(0);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", noReadDate);
        selectDate(day, month, year);

        Thread.sleep(1000);
    }

    public void removeBookFromRead(String bookUrl) throws InterruptedException{

        webDriver.get(bookUrl);
        Thread.sleep(1000);

        List<WebElement> buttons = webDriver.findElements(By.xpath("//button[@class='btn-dropdown expand-dropdown-button']"));
        WebElement secondButton = buttons.get(1);
        secondButton.click();
        Thread.sleep(1000);

        WebElement dropdownContent = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[3]/div/div[1]/div[1]/div/div/div")));
        List<WebElement> buttons2 = dropdownContent.findElements(By.tagName("button"));
        buttons2.get(3).click();
        Thread.sleep(1000);

        Alert alert = webDriver.switchTo().alert();
        alert.accept();

        Thread.sleep(1000);

    }

    public void scroll(int scrollAmount) throws InterruptedException {
        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, " + scrollAmount + ")");
        int sleepTime = Math.max(300, scrollAmount);
        Thread.sleep(sleepTime);
    }

    public int getMonthAsNumber(String month) {
        return switch (month.toLowerCase()) {
            case "january" -> 1;
            case "february" -> 2;
            case "march" -> 3;
            case "april" -> 4;
            case "may" -> 5;
            case "june" -> 6;
            case "july" -> 7;
            case "august" -> 8;
            case "september" -> 9;
            case "october" -> 10;
            case "november" -> 11;
            case "december" -> 12;
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };
    }

}
