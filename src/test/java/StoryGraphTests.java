import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.openqa.selenium.By.className;

public class StoryGraphTests {

    private static WebDriver webDriver;
    private static String baseUrl;
    private static WebDriverWait webDriverWait;
    private static JavascriptExecutor javascriptExecutor;

    private static final String[] USERNAMES = {"multipurpose1", "multipurpose"};
    private static final String[] PASSWORDS = {"111111", "1111112"};

    private static final String EMAIL = "multipurpose.beca@gmail.com";
    private static final String[] MODES = {"Dark", "Light"};

    @BeforeAll
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:/Users/selma/Downloads/chromedriver-win64 (2)/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        webDriver = new ChromeDriver(options);
        baseUrl = "https://app.thestorygraph.com/";
        webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        javascriptExecutor = (JavascriptExecutor) webDriver;
    }

    @AfterAll
    public static void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    private void scrollToY(int val) {
        javascriptExecutor.executeScript("window.scrollTo(0, arguments[0]);", val);
    }

    public void login(String email, String password) {
        webDriver.get("https://app.thestorygraph.com/users/sign_in");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='user_email']")));
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='user_password']")));
        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='sign-in-btn']")));

        assertTrue(emailField.isDisplayed(), "Email field should be visible on the login page.");
        assertTrue(passwordField.isDisplayed(), "Password field should be visible on the login page.");
        assertTrue(loginButton.isDisplayed(), "Login button should be visible on the login page.");

        emailField.sendKeys(email);
        passwordField.sendKeys(password);
        loginButton.click();

        try {
            WebElement cookieCloseButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click(); // Click the close button if the banner exists
            System.out.println("Cookie banner closed.");
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
            System.out.println("Cookie banner not visible, proceeding.");
        }
    }

    public void logout() {
        webDriver.get("https://app.thestorygraph.com/");
        WebElement logoutMenuButton = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/nav/div[1]/div/div[3]/button/div")));
        logoutMenuButton.click();

        WebElement logoutButton = new WebDriverWait(webDriver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/nav/div[2]/div[2]/div/form/button")));
        logoutButton.click();
    }

    private void selectDate(String day, String month, String year) {
        try {
            WebElement dayDropdown = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/div/div[1]/div[1]/div[2]/form/div[2]/select[1]")));
            dayDropdown.click();
            new Select(dayDropdown).selectByValue(day);

            WebElement monthDropdown = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/div/div[1]/div[1]/div[2]/form/div[2]/select[2]")));
            monthDropdown.click();
            new Select(monthDropdown).selectByValue(month);

            WebElement yearDropdown = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/div/div[1]/div[1]/div[2]/form/div[2]/select[3]")));
            yearDropdown.click();
            new Select(yearDropdown).selectByValue(year);

            webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/div/div[1]/div[1]/div[2]/form/input[5]"))).click();
        } catch (Exception e) {
            System.out.println("Error selecting date: " + e.getMessage());
        }
    }

    private void addBookToReadPile(String bookUrl, String day, String month, String year) throws InterruptedException {
        webDriver.get(bookUrl);

        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        Thread.sleep(1000);

        List<WebElement> buttons = webDriver.findElements(By.xpath("//button[@class='btn-dropdown expand-dropdown-button']"));
        WebElement secondButton = buttons.get(1);
        secondButton.click();
        Thread.sleep(1000);

        WebElement dropdownContent = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/div/div[1]/div[1]/div/div/div")));
        List<WebElement> buttons2 = dropdownContent.findElements(By.tagName("button"));
        if (!buttons2.isEmpty()) {
            buttons2.get(0).click();
        } else {
            System.out.println("No buttons found in the dropdown.");
        }

        Thread.sleep(2000);
        List<WebElement> noReadDates = webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[contains(@href, '/edit-read-instance-from-book')]/p[contains(text(), 'No read date')]")));
        WebElement noReadDate = noReadDates.get(0);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", noReadDate);

        webDriver.manage().window().maximize();
        Thread.sleep(1500);

        selectDate(day, month, year);
        Thread.sleep(200);
    }

    @Test
    public void testSignInNonExistantUser() {
        webDriver.get("https://app.thestorygraph.com/users/sign_in");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        // Verify login page elements
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='user_email']")));
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='user_password']")));
        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='sign-in-btn']")));

        assertTrue(emailField.isDisplayed(), "Email field should be visible on the login page.");
        assertTrue(passwordField.isDisplayed(), "Password field should be visible on the login page.");
        assertTrue(loginButton.isDisplayed(), "Login button should be visible on the login page.");

        // Perform login with test credentials
        emailField.sendKeys("svvttest@test.com");
        passwordField.sendKeys("SVVTTest123");
        loginButton.click();

        // Verify successful login by checking redirection or dashboard presence
        //wait.until(ExpectedConditions.urlContains("dashboard"));
        String currentUrl = webDriver.getCurrentUrl();
        assertFalse(currentUrl.contains("dashboard"), "User should be redirected to the dashboard after login.");
    }

    @Test
    public void testSignInValidUser() {
        login(EMAIL, PASSWORDS[0]);
    }

    @Test
    public void testSignInInvalidUser() {
        login("invaliduser@gmail.com", "wrongpassword");

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.contains("users/sign_in"), "User should stay on the login page after failed login attempt.");
    }

    @Test
    public void testLogout() {
        login(EMAIL, PASSWORDS[0]);

        logout();
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Log in')]")));
        assertTrue(loginButton.isDisplayed(), "User should be redirected to the login page after logout.");
    }

    public void signUp(String email, String emailConfirmation, String username, String password) {
        webDriver.get("https://app.thestorygraph.com/users/sign_in");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        WebElement signUpLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[1]/div/a")));
        signUpLink.click();

        wait.until(ExpectedConditions.urlToBe("https://app.thestorygraph.com/users/sign_up"));

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='user_email']")));
        WebElement emailConfirmationField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='user_email_confirmation']")));
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='user_username']")));
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='user_password']")));
        WebElement passwordConfirmationField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='user_password_confirmation']")));
        WebElement signUpButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='sign-up-btn']")));

        emailField.sendKeys(email);
        emailConfirmationField.sendKeys(emailConfirmation);
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        passwordConfirmationField.sendKeys(password);

        signUpButton.click();
    }

    @Test
    public void testSignUpIncorrectUsername() {
        signUp(EMAIL, EMAIL, "multipurpose.beca", PASSWORDS[0]);
    }

    @Test
    public void testSignUp() throws InterruptedException {
        signUp(EMAIL, EMAIL, USERNAMES[0], PASSWORDS[0]);

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe("https://app.thestorygraph.com/import-goodreads-0"));

        WebElement noThanksButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[5]/span/a[2]/button")));
        noThanksButton.click();

        wait.until(ExpectedConditions.urlToBe("https://app.thestorygraph.com/onboarding-community-preferences"));

        scrollToY(200);
        Thread.sleep(1000);

        WebElement saveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/div/div[2]/button/span[1]")));
        saveButton.click();

        wait.until(ExpectedConditions.urlToBe("https://app.thestorygraph.com/onboarding-audio-preferences-0"));

        WebElement noButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[4]/span/a[2]/button")));
        noButton.click();

        wait.until(ExpectedConditions.urlToBe("https://app.thestorygraph.com/onboarding-launch-pad"));

        String finalUrl = webDriver.getCurrentUrl();
        assertEquals("https://app.thestorygraph.com/onboarding-launch-pad", finalUrl, "User should be redirected to the launch pad after completing onboarding.");
    }

    @ParameterizedTest
    @CsvSource({"Romeo and Juliet, romeo", "Pride and Prejudice, pride", "The Fellowship of the Ring, ring", "Emma, emma", "Tale of Two Cities, cities"})
    public void testSearchForExistingBooks(String searchTermInput, String expectedPartialResult) throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/");

        WebElement searchInput = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/nav/div[1]/div/div[2]/div/div/form/input")));
        searchInput.sendKeys(searchTermInput);
        searchInput.sendKeys(Keys.ENTER);

        WebElement searchResult = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'results')]//h1/a")));
        String productTitle = searchResult.getText();

        assertTrue(productTitle.toLowerCase().contains(expectedPartialResult), "Search results should contain the search term anywhere in the title.");

        Thread.sleep(3000);
    }

    @ParameterizedTest
    @CsvSource({
            "lajkrnblarjbn, lajkrnblarjbn",
            "aerijbargunpq, aerijbargunpq",
            "pairrugbqirug, pairrugbqirug",
            "airurgbqiuvr, airurgbqiuvr",
            "arpeiguapivuna, arpeiguapivuna"
    })
    public void testSearchForNonExistingBooks(String searchTermInput, String expectedText) throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/");

        WebElement searchInput = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search")));
        searchInput.sendKeys(searchTermInput);
        searchInput.sendKeys(Keys.ENTER);

        Thread.sleep(1000);

        WebElement noResultsParagraph;
        String noResultsText = "";

        // First attempt with the primary XPath
        try {
            noResultsParagraph = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div[1]/p")));
            noResultsText = noResultsParagraph.getText();
        } catch (TimeoutException e) {
            // If the first XPath fails, try the second one
            try {
                noResultsParagraph = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/p")));
                noResultsText = noResultsParagraph.getText();
            } catch (TimeoutException e2) {
                // Both XPath attempts failed, handle the fallback scenario
                System.out.println("No results paragraph not found with either XPath.");
            }
        }

        assertEquals("There's nothing on The StoryGraph matching \"" + expectedText + "\".", noResultsText, "No results message should match expected text.");

        Thread.sleep(3000);
    }

    @Test
    public void testFilterByMood() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));

        WebElement filterButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[2]/span")));
        filterButton.click();
        Thread.sleep(1000);

        WebElement moodSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mood_adventurous")));
        moodSelect.click();

        /*Select moodSelect = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mood_adventurous"))));
        moodSelect.selectByValue("adventurous");*/

        WebElement moodSelect1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mood_dark")));
        moodSelect1.click();

        /*moodSelect = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mood_dark"))));
        moodSelect.selectByValue("dark");*/

        scrollToY(300);
        Thread.sleep(1000);

        WebElement filterButtonApply = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/div[5]/div[2]/input")));
        filterButtonApply.click();

        Thread.sleep(5000);

        scrollToY(1200);
        Thread.sleep(1000);

        List<WebElement> filteredResults = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/div[3]/div[1]")));

        boolean foundMood = false;

        for (WebElement moodElement : filteredResults) {
            String moodText = moodElement.getText().toLowerCase();
            if (moodText.contains("adventurous") || moodText.contains("dark")) {
                foundMood = true;
                break;
            }
        }
        assertTrue(foundMood, "Expected mood 'adventurous' or 'dark' not found.");
    }

    @Test
    public void testFilterByPace() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));

        WebElement filterButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[2]/span")));
        filterButton.click();
        Thread.sleep(1000);

        WebElement paceSelectMedium = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pace_medium")));
        paceSelectMedium.click();

        WebElement paceSelectFast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pace_fast")));
        paceSelectFast.click();

        scrollToY(300);
        Thread.sleep(1000);

        WebElement filterButtonApply = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/div[5]/div[2]/input")));
        filterButtonApply.click();

        Thread.sleep(5000);

        scrollToY(1200);
        Thread.sleep(1000);

        List<WebElement> filteredResults = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/div[3]/div[1]")));

        boolean foundPace = false;

        for (WebElement paceElement : filteredResults) {
            String paceText = paceElement.getText().toLowerCase();
            if (paceText.contains("medium-paced") || paceText.contains("fast-paced")) {
                foundPace = true;
                break;
            }
        }

        assertTrue(foundPace, "Expected pace 'medium-paced' or 'fast-paced' not found.");
    }

    @Test
    public void testFilterByGenres() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        WebElement filterButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[2]/span")));
        filterButton.click();
        Thread.sleep(1000);

        scrollToY(500);
        Thread.sleep(1000);

        WebElement genreDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/span[1]/span[1]/span/ul")));
        genreDropdown.click();

        WebElement sciFiOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Science Fiction']")));
        sciFiOption.click();
        Thread.sleep(500);

        WebElement fantasyOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Fantasy']")));
        fantasyOption.click();
        Thread.sleep(500);

        WebElement randomClick = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]")));
        randomClick.click();

        scrollToY(800);

        Thread.sleep(1000);

        WebElement filterButtonApply = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/div[5]/div[2]/input")));
        filterButtonApply.click();

        Thread.sleep(3000);

        scrollToY(1200);

        List<WebElement> filteredResults = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/div[3]/div[1]")));

        boolean foundGenre = false;

        for (WebElement book : filteredResults) {
            String genresText = book.getText().toLowerCase();
            if (genresText.contains("science fiction") || genresText.contains("fantasy")) {
                foundGenre = true;
                break;
            }
        }
        assertTrue(foundGenre, "Expected genre 'Science Fiction' or 'Fantasy' not found.");
    }

    @Test
    public void testOrderByPagesHighToLow() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");

        WebElement lastUpdatedButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/button")));
        lastUpdatedButton.click();

        WebElement dropdownOption = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/div/input[3]")));
        dropdownOption.click();

        Thread.sleep(3000);

        WebElement firstResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/p")));
        WebElement secondResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[2]/div[1]/div[2]/div[2]/div[1]/p")));

        int firstValue = Integer.parseInt(firstResultElement.getText().split(" ")[0]);
        int secondValue = Integer.parseInt(secondResultElement.getText().split(" ")[0]);

        System.out.println("First value: " + firstValue);
        System.out.println("Second value: " + secondValue);

        assertTrue(firstValue >= secondValue, "Results should be sorted by 'Last Updated' in descending order");
    }

    @Test
    public void testOrderByPagesLowToHighFantasy() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");

        WebElement lastUpdatedButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/button")));
        lastUpdatedButton.click();

        WebElement dropdownOption = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/div/input[3]")));
        dropdownOption.click();

        Thread.sleep(3000);

        WebElement firstResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/p")));
        WebElement secondResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[2]/div[1]/div[2]/div[2]/div[1]/p")));

        int firstValue = Integer.parseInt(firstResultElement.getText().split(" ")[0]);
        int secondValue = Integer.parseInt(secondResultElement.getText().split(" ")[0]);

        System.out.println("First value: " + firstValue);
        System.out.println("Second value: " + secondValue);

        assertTrue(firstValue >= secondValue, "Results should be sorted by 'Last Updated' in descending order");
    }

    @Test
    public void testOrderByLastUpdated() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");

        WebElement lastUpdatedButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/button")));
        lastUpdatedButton.click();

        WebElement dropdownOption = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/div/input[3]")));
        dropdownOption.click();

        Thread.sleep(3000);

        WebElement firstResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/p")));
        WebElement secondResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[2]/div[1]/div[2]/div[2]/div[1]/p")));

        int firstValue = Integer.parseInt(firstResultElement.getText().split(" ")[0]);
        int secondValue = Integer.parseInt(secondResultElement.getText().split(" ")[0]);

        System.out.println("First value: " + firstValue);
        System.out.println("Second value: " + secondValue);

        assertTrue(firstValue >= secondValue, "Results should be sorted by 'Last Updated' in descending order");
    }

    @Test
    public void testCompareDatesFromLatestFirstFantsyOption() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        webDriver.manage().window().maximize();

        Thread.sleep(1000);

        // Wait until the filter button is clickable by locating it using a CSS selector
        WebElement filterButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("span.font-semibold.align-middle")
        ));

        // Perform the click action on the filter button
        filterButton.click();

        Thread.sleep(1000);

        scrollToY(500);
        Thread.sleep(1000);

        WebElement searchField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input.select2-search__field")
        ));
        searchField.sendKeys("Fantasy"); // Replace 'YourGenre' with the desired genre

        /*WebElement genreDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/span[1]/span[1]/span/ul")));
        genreDropdown.click();

        WebElement fantasyOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Fantasy']")));
        fantasyOption.click();*/
        Thread.sleep(500);

        WebElement randomClickButton = webDriver.findElement(By.cssSelector("p.font-semibold.mt-5.mb-3"));
        randomClickButton.click();


        scrollToY(800);

        Thread.sleep(1000);

        WebElement filterButtonApply = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.filter-btn.primary-btn[value='Filter']")));
        filterButtonApply.click();

        Thread.sleep(3000);

        scrollToY(1100);

        WebElement lastUpdatedButton = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn-dropdown.expand-dropdown-button"))
        );
        lastUpdatedButton.click();

        WebElement dropdownOption = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("input.btn-dropdown.filter-btn.sort-button[value='Pub Date: Latest first']"))
        );
        dropdownOption.click();

        Thread.sleep(3000);

        scrollToY(1350);

        Thread.sleep(3000);

        WebElement firstPublicationDateReveal = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("span.underline.decoration-darkerGrey.cursor-pointer.hover\\:text-cyan-700")
                )
        );
        firstPublicationDateReveal.click();

        Thread.sleep(5000);

        WebElement infoDiv = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(className("edition-info")));
        System.out.println("infoDiv: " + infoDiv);

        int pCount = infoDiv.findElements(By.tagName("p")).size();

        WebElement publicationDateElement = infoDiv.findElements(By.tagName("p")).get(pCount - 1);
        String dateTextFirst = publicationDateElement.getText().trim();
        String[] datePartsFirst = dateTextFirst.split(" ");

        int dayFirst = Integer.parseInt(datePartsFirst[2]);
        int monthFirst = getMonthAsNumber(datePartsFirst[3]);
        int yearFirst = Integer.parseInt(datePartsFirst[4]);

        /*System.out.println("Date text: " + dateTextFirst);
        System.out.println("Day: " + dayFirst);
        System.out.println("Month: " + monthFirst);
        System.out.println("Year: " + yearFirst);*/

        Thread.sleep(3000);

        scrollToY(1600);

        WebElement elementContainingFirstPub = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'first pub')]"))
        );

// Scroll the element into view
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", elementContainingFirstPub);

// Click the element
        elementContainingFirstPub.click();

        Thread.sleep(5000);

        List<WebElement> infoDivs = webDriver.findElements(By.className("edition-info"));
        WebElement infoDiv1 = infoDivs.get(2);

        int pCount1 = infoDiv1.findElements(By.tagName("p")).size();

        WebElement publicationDateElement1 = infoDiv1.findElements(By.tagName("p")).get(pCount1 - 1);
        String dateTextSecond = publicationDateElement1.getText().trim();
        String[] datePartsSecond = dateTextSecond.split(" ");

        int daySecond = Integer.parseInt(datePartsSecond[2]);
        int monthSecond = getMonthAsNumber(datePartsSecond[3]);
        int yearSecond = Integer.parseInt(datePartsSecond[4]);

        /*System.out.println("Date text: " + dateTextSecond);
        System.out.println("Day: " + daySecond);
        System.out.println("Month: " + monthSecond);
        System.out.println("Year: " + yearSecond);*/

        boolean isDateFirstGreater = compareDates(dayFirst, monthFirst, yearFirst, daySecond, monthSecond, yearSecond);

        assertTrue(isDateFirstGreater, "The first publication's date should be more recent than the second publication's date");
    }

    private int getMonthAsNumber(String month) {
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

    private boolean compareDates(int day1, int month1, int year1, int day2, int month2, int year2) {
        if (year1 != year2) return year1 > year2;
        if (month1 != month2) return month1 > month2;
        return day1 > day2;
    }

    @Test
    public void testVerifyBookAddedToRead() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(2000);

        webDriver.get("https://app.thestorygraph.com/books/96e8fd00-733d-4686-9e41-d388149c438b");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        scrollToY(200);
        Thread.sleep(1000);

        WebElement dropdownOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[5]/div[1]/div[2]/div[4]/div[1]/div[1]/div/form/button")));
        dropdownOption.click();

        webDriver.get("https://app.thestorygraph.com/to-read/" + USERNAMES[0]);

        List<WebElement> booksList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div[1]/div[4]/div/a/button")
        ));

        String expectedBookTitle = "The Title of the Book You Clicked";
        boolean bookFound = booksList.stream()
                .anyMatch(book -> book.getText().equalsIgnoreCase(expectedBookTitle));

        assertTrue(bookFound, "The book should be listed in the 'To Read' section.");
    }

    @Test
    public void testChangeUsername() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);

        webDriver.get("https://app.thestorygraph.com/profile/edit/" + USERNAMES[0]);
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        WebElement usernameInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//html/body/div[1]/div/main/div/div[3]/div/form/div[2]/input")));
        usernameInput.clear();
        usernameInput.sendKeys(USERNAMES[1]);

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 500)");

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/button")));
        saveButton.click();
        Thread.sleep(1000);
        webDriver.navigate().refresh();
        Thread.sleep(2000);
        assertTrue(webDriver.getCurrentUrl().contains("https://app.thestorygraph.com/profile/"+USERNAMES[1]), "The URL did not reflect the updated username.");
    }

    @Test
    public void testToggleVisibility() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(3000);

        webDriver.get("https://app.thestorygraph.com/profile/edit/" + USERNAMES[0]);

        WebElement visibilityDropdown = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/div[6]/select"));
        Select dropdown = new Select(visibilityDropdown);

        dropdown.selectByVisibleText("Community");

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 300)");

        WebElement saveButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/button"));
        saveButton.click();

        Thread.sleep(1000);

        webDriver.get("https://app.thestorygraph.com/profile/edit/" + USERNAMES[0]);
        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 300)");
        visibilityDropdown = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/div[6]/select"));
        dropdown = new Select(visibilityDropdown);

        String selectedOption = dropdown.getFirstSelectedOption().getText();
        assertEquals("Community", selectedOption, "The visibility option was not updated correctly.");
    }

    @Test
    public void testChangePassword() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(3000);

        webDriver.get("https://app.thestorygraph.com/profile/edit/" + USERNAMES[0]);

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 600)");
        WebElement changePasswordButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/a[1]/button")));
        changePasswordButton.click();

        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        WebElement currentPasswordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_current_password")));
        WebElement newPasswordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_password")));
        WebElement confirmPasswordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_password_confirmation")));
        WebElement saveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div/form/div[4]/button")));

        currentPasswordField.sendKeys(PASSWORDS[0]);
        newPasswordField.sendKeys(PASSWORDS[1]);
        confirmPasswordField.sendKeys(PASSWORDS[1]);
        saveButton.click();

        Thread.sleep(1000);

        logout();
        Thread.sleep(2000);
        login(EMAIL, PASSWORDS[1]);
        Thread.sleep(2000);
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.equals("https://app.thestorygraph.com/"));

        logout();
        Thread.sleep(2000);

        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(2000);
        String currentUrl1 = webDriver.getCurrentUrl();
        assertFalse(currentUrl1.equals("https://app.thestorygraph.com/"));
    }

    @Test
    public void testBookLog() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(3000);

        addBookToReadPile("https://app.thestorygraph.com/books/e0f01a40-b8fb-472c-998d-853fadf00a67", "8", "1", "2025");

        Thread.sleep(3000);
        WebElement stats = webDriver.findElement(By.xpath("/html/body/div[1]/nav/div[1]/div/div[1]/div[2]/a[2]"));
        stats.click();

        Thread.sleep(2000);
        WebElement number = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[2]/a"));
        WebElement great = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[3]"));

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 300)");
        Thread.sleep(1000);

        assertEquals("2 books", number.getText());
        assertEquals("Brilliant! You are ahead by 1 book!", great.getText());
    }

    @Test
    public void testBookLogLastYear() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(2000);
        addBookToReadPile("https://app.thestorygraph.com/books/d93d6f72-8d62-4294-b087-91632fac143a", "7", "2", "2024");

        Thread.sleep(2000);
        WebElement stats = webDriver.findElement(By.xpath("/html/body/div[1]/nav/div[1]/div/div[1]/div[2]/a[2]"));
        stats.click();

        Thread.sleep(2000);
        WebElement number = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[2]/a"));
        WebElement great = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[3]"));

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 300)");
        Thread.sleep(1000);

        assertNotEquals("3 books", number.getText());
        assertEquals("Brilliant! You are ahead by 1 book!", great.getText());
    }

    @Test
    public void testPaperToAudio() throws InterruptedException{
        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(2000);

        webDriver.get("https://app.thestorygraph.com/books/e0f01a40-b8fb-472c-998d-853fadf00a67");
        WebElement moreInfo = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[2]/p/span/span[2]"));
        moreInfo.click();
        String oldFormat = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[2]/div[2]/p[2]")).getText();

        WebElement editions = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[2]/p/a"));
        editions.click();
        Thread.sleep(2000);
        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 600)");
        Thread.sleep(2000);

        WebElement switchEdition = webDriver.findElement(By.xpath("//*[@id=\"book_94aaa555-1ff2-46c0-8de6-77e86d3a7120\"]/div[1]/div[1]/div[2]/div[2]/div/div[2]/form[2]/button"));
        switchEdition.click();

        webDriver.get("https://app.thestorygraph.com/books-read/" + USERNAMES[0]);

        Thread.sleep(1500);
        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 300)");
        Thread.sleep(1000);

        webDriver.navigate().refresh();

        WebElement moreInfo1 = webDriver.findElement(By.xpath("//*[@id=\"book_94aaa555-1ff2-46c0-8de6-77e86d3a7120\"]/div[1]/div[1]/div[2]/div[1]/p/span/span[2]"));
        moreInfo1.click();

        String newFormat = webDriver.findElement(By.xpath("//*[@id=\"book_94aaa555-1ff2-46c0-8de6-77e86d3a7120\"]/div[1]/div[1]/div[2]/div[1]/div[2]/p[2]")).getText();
        assertNotEquals(oldFormat,newFormat);
    }

    @Test
    public void testMultiPreferences() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(2000);

        webDriver.get("https://app.thestorygraph.com/preferences/edit/" + USERNAMES[0]);
        webDriver.manage().window().maximize();
        Thread.sleep(1000);

        Select timeZone = new Select(webDriver.findElement(By.id("user_time_zone")));
        timeZone.selectByValue("Hawaii");
        String expectedTimeZone = "(GMT-10:00) Hawaii";

        Thread.sleep(1000);

        scrollToY(500);
        Thread.sleep(1000);

        WebElement languagesClick = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/form/div/div[1]/div[5]/span/span[1]/span/ul"));
        languagesClick.click();
        WebElement arabicOption = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Arabic']")));
        arabicOption.click();
        String expectedLanguage = "Arabic";
        Thread.sleep(1000);

        scrollToY(1600);
        Thread.sleep(1000);

        WebElement friendsDropdown = webDriver.findElement(By.id("user_follow_setting"));
        friendsDropdown.click();
        WebElement friendsSelect = webDriver.findElement(By.xpath("//*[@id=\"user_friends_setting\"]/option[3]"));
        friendsSelect.click();
        String expectedFriendsSetting = "Friends";

        Select buddyReads = new Select(webDriver.findElement(By.id("user_buddy_reads_setting")));
        buddyReads.selectByValue("anybody");
        String expectedBuddyReads = "Anybody";

        scrollToY(2200);

        Select ownedBooks = new Select(webDriver.findElement(By.id("user_owned_books_visibility_setting")));
        ownedBooks.selectByValue("friends_and_following");
        String expectedOwnedBooksVisibility = "Friends & People I follow";

        Thread.sleep(1000);

        WebElement updateButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/form/div/div[2]/button"));
        updateButton.click();

        webDriver.get("https://app.thestorygraph.com/preferences/edit/" + USERNAMES[0]);
        Thread.sleep(2000);

        Select actualTimeZone = new Select(webDriver.findElement(By.id("user_time_zone")));
        String actualTimeZoneValue = actualTimeZone.getFirstSelectedOption().getText();
        assertEquals(expectedTimeZone, actualTimeZoneValue);

        scrollToY(500);

        WebElement selectedLanguage = webDriver.findElement(By.xpath("//li[text()='Arabic']"));
        assertNotNull(selectedLanguage);

        scrollToY(1600);

        Select actualFriendsSetting = new Select(webDriver.findElement(By.id("user_follow_setting")));
        String actualFriendsValue = actualFriendsSetting.getFirstSelectedOption().getText();
        assertEquals(expectedFriendsSetting, actualFriendsValue);

        Select actualBuddyReads = new Select(webDriver.findElement(By.id("user_buddy_reads_setting")));
        String actualBuddyReadsValue = actualBuddyReads.getFirstSelectedOption().getText();
        assertEquals(expectedBuddyReads, actualBuddyReadsValue);

        scrollToY(2200);

        Select actualOwnedBooksVisibility = new Select(webDriver.findElement(By.id("user_owned_books_visibility_setting")));
        String actualOwnedBooksValue = actualOwnedBooksVisibility.getFirstSelectedOption().getText();
        assertEquals(expectedOwnedBooksVisibility, actualOwnedBooksValue);
    }

    @Test
    public void testModeChange() throws InterruptedException{

        login("multipurpose.beca@gmail.com", PASSWORDS[0]);
        Thread.sleep(2000);

        webDriver.get("https://app.thestorygraph.com/preferences/edit/"+USERNAMES[0]);
        Select mode = new Select(webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/form/div/div[1]/div[4]/select")));
        mode.selectByVisibleText(MODES[1]);

        webDriver.manage().window().maximize();

        Thread.sleep(1000);

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 1800)");

        Thread.sleep(2000);

        WebElement update = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/form/div/div[2]/button"));
        update.click();

        Thread.sleep(1000);

        webDriver.get("https://app.thestorygraph.com/preferences/edit/"+USERNAMES[0]);

        Thread.sleep(1000);

        Select newMode = new Select(webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/form/div/div[1]/div[4]/select")));
        assertEquals(MODES[1], newMode.getFirstSelectedOption().getText());
    }

    @Test
    public void testReadingGoal() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);

        Thread.sleep(2000);

        webDriver.get("https://app.thestorygraph.com/reading_challenges/dashboard/multipurpose1");

        Thread.sleep(4000);

        WebElement editReadingGoalBooksButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div[1]/div/div[1]/div/div[1]/a/button"));
        editReadingGoalBooksButton.click();

        Thread.sleep(2000);

        WebElement booksToRead = webDriver.findElement(By.id("reading_goal_goal"));
        booksToRead.clear();

        WebElement booksToReadInputValue = webDriver.findElement(By.id("reading_goal_goal"));
        booksToReadInputValue.sendKeys("30");

        WebElement upadateReadingGoalButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/form/button"));
        upadateReadingGoalButton.click();

        webDriver.get("https://app.thestorygraph.com/reading_challenges/dashboard/multipurpose1");
        Thread.sleep(2000);

        WebElement element = webDriver.findElement(By.cssSelector("p > span.font-semibold.mb-2"));

        String elementText = element.getText();

        String numberOnly = elementText.replaceAll("\\D+", "");

        int number = Integer.parseInt(numberOnly);

        int expectedNumber = 30;
        assertEquals(expectedNumber, number, "The number of books does not match the expected value.");

    }

    @Test
    public void testReadingChallengesJoinGeneral() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);

        Thread.sleep(2000);

        webDriver.get("https://app.thestorygraph.com/reading_challenges/");

        Thread.sleep(4000);

        WebElement readsTheWorld2025Click = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/h6/a"));
        readsTheWorld2025Click.click();

        Thread.sleep(2000);

        WebElement joinChallengeButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/form/button"));
        joinChallengeButton.click();

        webDriver.get("https://app.thestorygraph.com/reading_challenges/dashboard/multipurpose1");

        Thread.sleep(2000);

        scrollToY(1000);

        WebElement joinedChallenge = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div[2]/div/div/turbo-frame/div/div/p[1]/a"));

        assertEquals("The StoryGraph Reads the World 2025", joinedChallenge.getText());
    }


    @Test
    public void testReadingChallengesLeaveGeneral() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);

        Thread.sleep(2000);

        webDriver.get("https://app.thestorygraph.com/reading_challenges/");

        Thread.sleep(4000);

        WebElement readsTheWorld2025Click = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/h6/a"));
        readsTheWorld2025Click.click();

        Thread.sleep(2000);

        WebElement joinChallengeButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div[1]/div[1]/div/button"));
        joinChallengeButton.click();

        Thread.sleep(1000);

        WebElement svgElement = webDriver.findElement(By.cssSelector("svg.w-6"));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].style.display='none';", svgElement);

        WebElement leaveButton = webDriver.findElement(By.linkText("Leave"));
        leaveButton.click();

        Alert alert = webDriver.switchTo().alert();
        alert.accept();

        Thread.sleep(1500);

        webDriver.get("https://app.thestorygraph.com/reading_challenges/dashboard/multipurpose1");

        Thread.sleep(2000);

        scrollToY(1000);

        WebElement joinedChallenge = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div[2]/div/div/turbo-frame/div/div/p[1]/a"));

        assertNotEquals("The StoryGraph Reads the World 2025", joinedChallenge.getText());
    }

    @ParameterizedTest
    @CsvSource({
            "/browse?sort_order=Last+updated, //*[@id=\"navbar\"]/div[1]/div/div[1]/div[2]/a[1]",
            "/stats/multipurpose1?year=2025, //*[@id=\"navbar\"]/div[1]/div/div[1]/div[2]/a[2]",
            "/reading_challenges/dashboard/multipurpose1, //*[@id=\"navbar\"]/div[1]/div/div[1]/div[2]/a[3]",
            "/community, //*[@id=\"navbar\"]/div[1]/div/div[1]/div[2]/a[4]",
            "/giveaways, //*[@id=\"navbar\"]/div[1]/div/div[1]/div[2]/a[5]"
    })
    public void testNavBar(String route, String xpath) throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);

        Thread.sleep(2000);

        webDriver.manage().window().maximize();

        WebElement button = webDriver.findElement(By.xpath(xpath));
        button.click();

        Thread.sleep(2000);

        assertEquals(baseUrl + route, webDriver.getCurrentUrl());

        webDriver.manage().window().minimize();

        logout();

        Thread.sleep(2000);
    }

    @Test
    public void testRating() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get("https://app.thestorygraph.com/books/94aaa555-1ff2-46c0-8de6-77e86d3a7120");

        WebElement addReview = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/div/div[1]/div[1]/div[1]/div/a"));
        addReview.click();
        Thread.sleep(1000);

        WebElement mood = webDriver.findElement(By.xpath("//*[@id=\"review_mood_ids_1\"]"));
        mood.click();

        WebElement medium = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/form/input[3]"));
        medium.click();

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 500)");
        Thread.sleep(500);

        Select driven = new Select(webDriver.findElement(By.id("review_character_or_plot_driven")));
        driven.selectByValue("A mix");

        Select dev = new Select(webDriver.findElement(By.id("review_strong_character_development")));
        dev.selectByValue("Yes");

        Select lovable = new Select(webDriver.findElement(By.id("review_loveable_characters")));
        lovable.selectByValue("Yes");

        Select diverse = new Select(webDriver.findElement(By.id("review_diverse_characters")));
        diverse.selectByValue("No");

        Select flaws = new Select(webDriver.findElement(By.id("review_flawed_characters")));
        flaws.selectByValue("It's complicated");

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 500)");
        Thread.sleep(500);

        Select stars = new Select(webDriver.findElement(By.id("stars_integer")));
        stars.selectByValue("4");

        Select partStars = new Select(webDriver.findElement(By.id("stars_decimal")));
        partStars.selectByValue("75");

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 500)");
        Thread.sleep(500);

        WebElement submit = webDriver.findElement(By.xpath("//*[@id=\"new_review\"]/button"));
        submit.click();

        Thread.sleep(1500);

        WebElement seeReview = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[2]/div[3]/div/a"));
        seeReview.click();
        Thread.sleep(500);

        assertEquals("adventurous", webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/span[1]")).getText());
        assertNotEquals("emotional", webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/span[1]")).getText());
        assertTrue(webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/p")).getText().contains("4.75"));
        assertTrue(webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/ul/li[1]")).getText().contains("A mix"));
        assertTrue(webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/ul/li[2]")).getText().contains("Yes"));
        assertTrue(webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/ul/li[3]")).getText().contains("Yes"));
        assertTrue(webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/ul/li[4]")).getText().contains("No"));
        assertFalse(webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/ul/li[5]")).getText().contains("Yes"));

        WebElement editButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/h4/a[1]"));
        editButton.click();

        Thread.sleep(500);
        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 1300)");
        Thread.sleep(500);

        WebElement deleteReview = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/a"));
        deleteReview.click();
        Alert alert = webDriver.switchTo().alert();
        alert.accept();
        Thread.sleep(500);
        assertEquals("0 reviews", webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/h1/span[2]")).getText());
    }

    @Test
    public void testMakeMyChallenge() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);
        webDriver.get("https://app.thestorygraph.com/reading_challenges/dashboard/"+USERNAMES[0]);
        Thread.sleep(500);
        WebElement browse = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div/div/div/a[1]")));
        browse.click();

        WebElement createNew = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div/div[2]/a")));
        createNew.click();

        Thread.sleep(500);

        WebElement numbers = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/a[3]")));
        numbers.click();

        Thread.sleep(500);

        WebElement title =  webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reading_challenge_title")));
        title.sendKeys("Read 10 classics");

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 500)");
        Thread.sleep(500);

        Select year = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reading_challenge_end_date_1i"))));
        year.selectByValue("2026");

        WebElement numberOfBooks = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reading_challenge_number_of_books_required")));
        numberOfBooks.sendKeys("10");

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 800)");
        Thread.sleep(500);

        WebElement makeChallengeLive = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reading_challenge_live")));
        makeChallengeLive.click();

        WebElement saveButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"new_reading_challenge\"]/button")));
        saveButton.click();

        WebElement joinChallenge = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button.mt-3.inline-flex.items-center.px-4.py-2.border.border-transparent.text-sm.rounded-md.shadow-sm")));
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("arguments[0].click();", joinChallenge);

        Thread.sleep(500);

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 200)");
        Thread.sleep(1000);

        assertTrue(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[5]/turbo-frame/p[2]/span"))).getText().contains("0%"));

        webDriver.navigate().to("https://app.thestorygraph.com/reading_challenges/dashboard/"+USERNAMES[0]);

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 800)");
        Thread.sleep(500);

        WebElement challengesContainer = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".reading-challenge-panes")));
        WebElement specificChallenge = challengesContainer.findElement(By.xpath(".//div[contains(@id, 'urc_') and .//a[text()='Read 10 classics']]"));
        WebElement booksReadElement = specificChallenge.findElement(By.cssSelector("p.mb-3"));
        String booksReadText = booksReadElement.getText();
        assertTrue(booksReadText.contains("0 books read out of 10"));
    }

    @Test
    public void addBookToChallenge() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);

        webDriver.get("https://app.thestorygraph.com/books/d4bee89f-3fdd-4dd0-8d77-4316bed132e7");

        WebElement addChallenge = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/div/div[2]/a")));
        addChallenge.click();

        WebElement challengeCheckbox = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[.//label[contains(text(), 'Read 10 classics')]]//input[@type='checkbox']")));
        challengeCheckbox.click();

        // Verify the confirmation message for the "Read 10 classics" challenge
        assertTrue(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated
                        (By.xpath("//div[.//label[contains(text(), 'Read 10 classics')]]/following-sibling::div[@id[contains(., 'confirmation_message')]]/span")))
                .getText().contains("Saved!"));

        addBookToReadPile("https://app.thestorygraph.com/books/d4bee89f-3fdd-4dd0-8d77-4316bed132e7", "10", "1", "2025");

        webDriver.get("https://app.thestorygraph.com/reading_challenges/dashboard/"+USERNAMES[0]);

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 600)");
        Thread.sleep(500);

        WebElement challengesContainer = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".reading-challenge-panes")));
        WebElement specificChallenge = challengesContainer.findElement(By.xpath(".//div[contains(@id, 'urc_') and .//a[text()='Read 10 classics']]"));
        WebElement booksReadElement = specificChallenge.findElement(By.cssSelector("p.mb-3"));
        String booksReadText = booksReadElement.getText();
        assertFalse(booksReadText.contains("0 books read out of 10"));
    }

    @Test
    public void testArchiveChallenge() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(500);
        webDriver.get("https://app.thestorygraph.com/reading_challenges/dashboard/"+USERNAMES[0]);

        WebElement archive = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[a[contains(text(), 'Read 10 classics')]]/following-sibling::form/button")));
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        ((JavascriptExecutor) js).executeScript("arguments[0].scrollIntoView(true);", archive);
        ((JavascriptExecutor) js).executeScript("arguments[0].click();", archive);

        Alert alert = webDriver.switchTo().alert();
        alert.accept();

        Thread.sleep(2000);
        webDriver.get("https://app.thestorygraph.com/reading_challenges/archive/"+USERNAMES[0]);

        Thread.sleep(500);

        String challengeText = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Read 10 classics')]"))).getText();
        System.out.println(challengeText);
        assertNotNull(challengeText, "Challenge 'Read 10 classics' not found!");
    }

    @Test
    public void deleteChallenge() throws InterruptedException {
        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(1000);

        webDriver.get("https://app.thestorygraph.com/reading_challenges/archive/"+USERNAMES[0]);

        Thread.sleep(2000);
        WebElement clickChallenge = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Read 10 classics')]")));
        clickChallenge.click();
        Thread.sleep(1000);

        WebElement editChallenge = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("edit-reading-challenge-link")));
        editChallenge.click();
        Thread.sleep(1000);

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 800)");
        Thread.sleep(2000);

        WebElement deleteChallenge = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[4]/a")));
        deleteChallenge.click();

        Alert alert1 = webDriver.switchTo().alert();
        alert1.accept();

        webDriver.get("https://app.thestorygraph.com/reading_challenges/archive/"+USERNAMES[0]);
        webDriver.navigate().refresh();

        Thread.sleep(2000);

        try {
            WebElement noChallengesMessage = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), \"There's nothing here yet!\")]")));
            assertEquals("There's nothing here yet!", noChallengesMessage.getText());
        } catch (NoSuchElementException e) {
            System.out.println("No challenge message found.");
        }
    }

    @Test
    public void testForHttps() {
        webDriver.get(baseUrl);
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.startsWith("https"), "Website should be using HTTPS");
    }

    @ParameterizedTest
    @CsvSource({"/reading_challenges/dashboard/user", "/profile/user", "/notifications", "/profile/edit/user", "/preferences/edit/user"})
    public void testProtectedRoutes(String protectedRoute) {
        webDriver.get(baseUrl + protectedRoute);
        String currentUrl = webDriver.getCurrentUrl();
        assertEquals("https://app.thestorygraph.com/users/sign_in", currentUrl, "Guest should be redirected to the login page");
    }
}

