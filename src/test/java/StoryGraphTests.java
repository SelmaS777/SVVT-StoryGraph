import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class StoryGraphTests {

    private static WebDriver webDriver;
    private static String baseUrl;
    private static WebDriverWait webDriverWait;
    private static JavascriptExecutor javascriptExecutor;

    private void scrollToY(int val) {
        javascriptExecutor.executeScript("window.scrollTo(0, arguments[0]);", val);
    }

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

    // TEST SCENARIO: LOGIN
    public void login(String email, String password) {
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
        emailField.sendKeys(email);
        passwordField.sendKeys(password);
        loginButton.click();

        /*// Verify successful login by checking redirection or dashboard presence
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.contains(""), "User should be redirected to the dashboard after login.");*/
    }

    public void logout() {
        // Navigate to the logout button
        webDriver.get("https://app.thestorygraph.com/");
        WebElement logoutMenuButton = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/nav/div[1]/div/div[3]/button/div")));
        logoutMenuButton.click();

        WebElement logoutButton = new WebDriverWait(webDriver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/nav/div[2]/div[2]/div/form/button")));
        logoutButton.click();
    }

    @Test
    public void testSignInValidUser() {
        login("multipurpose.beca@gmail.com", "blabla123");
    }

    @Test
    public void testSignInInvalidUser() {
        login("invaliduser@gmail.com", "wrongpassword");

        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.contains("users/sign_in"), "User should stay on the login page after failed login attempt.");
    }

    @Test
    public void testLogout() {
        // Ensure we are logged in before attempting logout
        login("multipurpose.beca@gmail.com", "1111112");
        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();  // Click the close button if the banner exists
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }
        logout();
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Log in')]")));
        assertTrue(loginButton.isDisplayed(), "User should be redirected to the login page after logout.");
    }

    // TEST SCENARIO: REGISTRATION
    public void signUp(String email, String emailConfirmation, String username, String password) {
        webDriver.get("https://app.thestorygraph.com/users/sign_in");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        // Click "Don't have an account"
        WebElement signUpLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[1]/div/a")));
        signUpLink.click();

        // Wait for the sign-up page to load
        wait.until(ExpectedConditions.urlToBe("https://app.thestorygraph.com/users/sign_up"));

        // Fill in the sign-up form
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

        // Submit the sign-up form
        signUpButton.click();
    }

    @Test
    public void testSignUpIncorrectUsername() {
        signUp("multipurpose.beca@gmail.com", "multipurpose.beca@gmail.com", "multipurpose.beca", "blabla123");
    }

    @Test
    public void testSignUp() throws InterruptedException {
        signUp("multipurpose.beca@gmail.com", "multipurpose.beca@gmail.com", "multipurpose_beca", "blabla123");

        // Wait for redirection to Goodreads import page
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe("https://app.thestorygraph.com/import-goodreads-0"));

        // Click "No, thanks" button
        WebElement noThanksButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[5]/span/a[2]/button")));
        noThanksButton.click();

        // Wait for redirection to community preferences page
        wait.until(ExpectedConditions.urlToBe("https://app.thestorygraph.com/onboarding-community-preferences"));

        scrollToY(200);
        Thread.sleep(1000);

        // Click "Save" button
        WebElement saveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/div/div[2]/button/span[1]")));
        saveButton.click();

        // Wait for redirection to audio preferences page
        wait.until(ExpectedConditions.urlToBe("https://app.thestorygraph.com/onboarding-audio-preferences-0"));

        // Click "No" button
        WebElement noButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[4]/span/a[2]/button")));
        noButton.click();

        // Wait for redirection to launch pad page
        wait.until(ExpectedConditions.urlToBe("https://app.thestorygraph.com/onboarding-launch-pad"));

        // Verify the final URL to ensure the process is complete
        String finalUrl = webDriver.getCurrentUrl();
        assertEquals("https://app.thestorygraph.com/onboarding-launch-pad", finalUrl, "User should be redirected to the launch pad after completing onboarding.");
    }

    // TEST SCENARIO: SEARCH FOR EXISTING BOOKS
// TEST SCENARIO: SEARCH FOR EXISTING BOOKS
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

    // TEST SCENARIO: SEARCH FOR NON-EXISTING BOOKS
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

        // Check and click the "X" button to close the cookie banner if visible
        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();  // Click the close button if the banner exists
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        WebElement searchInput = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search")));
        searchInput.sendKeys(searchTermInput);
        searchInput.sendKeys(Keys.ENTER);

        Thread.sleep(1000);

        WebElement noResultsParagraph = null;
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

        // Check and click the "X" button to close the cookie banner if visible
        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();  // Click the close button if the banner exists
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        // Expand Filter All Books Section
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

        // Verify filtering (example: check if the results contain the selected moods)
        List<WebElement> filteredResults = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/div[3]/div[1]")));

        boolean foundMood = false;

        for (WebElement moodElement : filteredResults) {
            String moodText = moodElement.getText().toLowerCase(); // Get the text and convert to lowercase
            if (moodText.contains("adventurous") || moodText.contains("dark")) {
                foundMood = true;
                break; // Exit the loop early if any matching mood is found
            }
        }
        assertTrue(foundMood, "Expected mood 'adventurous' or 'dark' not found.");
    }

    @Test
    public void testFilterByPace() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(2));

        // Check and click the "X" button to close the cookie banner if visible
        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();  // Click the close button if the banner exists
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        // Expand Filter All Books Section
        WebElement filterButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[2]/span")));
        filterButton.click();
        Thread.sleep(1000);

        // Select pace 'medium'
        WebElement paceSelectMedium = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pace_medium")));
        paceSelectMedium.click();

        // Select pace 'fast'
        WebElement paceSelectFast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pace_fast")));
        paceSelectFast.click();

        scrollToY(300);
        Thread.sleep(1000);

        // Click the apply filter button
        WebElement filterButtonApply = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/div[5]/div[2]/input")));
        filterButtonApply.click();

        Thread.sleep(5000);

        scrollToY(1200);
        Thread.sleep(1000);

        // Verify filtering (example: check if the results contain the selected paces)
        List<WebElement> filteredResults = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/div[3]/div[1]")));

        boolean foundPace = false;

        for (WebElement paceElement : filteredResults) {
            String paceText = paceElement.getText().toLowerCase(); // Get the text and convert to lowercase
            if (paceText.contains("medium-paced") || paceText.contains("fast-paced")) {
                foundPace = true;
                break; // Exit the loop early if any matching pace is found
            }
        }

        assertTrue(foundPace, "Expected pace 'medium-paced' or 'fast-paced' not found.");
    }

    @Test
    public void testFilterByGenres() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        // Check and click the "X" button to close the cookie banner if visible
        try {
            WebElement cookieCloseButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();  // Click the close button if the banner exists
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        // Expand Filter All Books Section
        WebElement filterButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[2]/span")));
        filterButton.click();
        Thread.sleep(1000);

        scrollToY(500);
        Thread.sleep(1000);

        // Locate the dropdown list (ul) and select "Science Fiction"
        WebElement genreDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/span[1]/span[1]/span/ul")));
        genreDropdown.click(); // Open the dropdown

        // Select the "Science Fiction" option
        WebElement sciFiOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Science Fiction']")));
        sciFiOption.click();
        Thread.sleep(500);

        // Select the "Fantasy" option
        WebElement fantasyOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Fantasy']")));
        fantasyOption.click();
        Thread.sleep(500);

        WebElement randomClick = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]")));
        randomClick.click();

        scrollToY(800);

        Thread.sleep(1000);

        // Apply the filter
        WebElement filterButtonApply = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/div[5]/div[2]/input")));
        filterButtonApply.click();


        Thread.sleep(3000);

        scrollToY(1200);

        // Verify filtering results
        List<WebElement> filteredResults = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/div[3]/div[1]")));

        boolean foundGenre = false;

        for (WebElement book : filteredResults) {
            String genresText = book.getText().toLowerCase(); // Get the text and convert to lowercase
            if (genresText.contains("science fiction") || genresText.contains("fantasy")) {
                foundGenre = true;
                break; // Exit the loop early if any matching genre is found
            }
        }
        assertTrue(foundGenre, "Expected genre 'Science Fiction' or 'Fantasy' not found.");
    }

    @Test
    public void testOrderByPagesHighToLow() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        // Check and click the "X" button to close the cookie banner if visible
        try {
            WebElement cookieCloseButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();  // Click the close button if the banner exists
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        // Click the 'Last Updated' button
        WebElement lastUpdatedButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/button")));
        lastUpdatedButton.click();

        // Select the third option from the dropdown
        WebElement dropdownOption = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/div/input[3]")));
        dropdownOption.click();

        // Wait for the sorting to take effect
        Thread.sleep(3000);

        // Get the values from the specified XPath locations
        WebElement firstResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/p")));
        WebElement secondResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[2]/div[1]/div[2]/div[2]/div[1]/p")));

        // Extract and parse the integer values from the text
        int firstValue = Integer.parseInt(firstResultElement.getText().split(" ")[0]);
        int secondValue = Integer.parseInt(secondResultElement.getText().split(" ")[0]);

        System.out.println("First value: " + firstValue);
        System.out.println("Second value: " + secondValue);

        // Assert that the first value is greater than or equal to the second value
        assertTrue(firstValue >= secondValue, "Results should be sorted by 'Last Updated' in descending order");
    }

    @Test
    public void testOrderByPagesLowToHighFantasy() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        // Check and click the "X" button to close the cookie banner if visible
        try {
            WebElement cookieCloseButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();  // Click the close button if the banner exists
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        // Click the 'Last Updated' button
        WebElement lastUpdatedButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/button")));
        lastUpdatedButton.click();

        // Select the third option from the dropdown
        WebElement dropdownOption = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/div/input[3]")));
        dropdownOption.click();

        // Wait for the sorting to take effect
        Thread.sleep(3000);

        // Get the values from the specified XPath locations
        WebElement firstResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/p")));
        WebElement secondResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[2]/div[1]/div[2]/div[2]/div[1]/p")));

        // Extract and parse the integer values from the text
        int firstValue = Integer.parseInt(firstResultElement.getText().split(" ")[0]);
        int secondValue = Integer.parseInt(secondResultElement.getText().split(" ")[0]);

        System.out.println("First value: " + firstValue);
        System.out.println("Second value: " + secondValue);

        // Assert that the first value is greater than or equal to the second value
        assertTrue(firstValue >= secondValue, "Results should be sorted by 'Last Updated' in descending order");
    }

    @Test
    public void testOrderByLastUpdated() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        // Check and click the "X" button to close the cookie banner if visible
        try {
            WebElement cookieCloseButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();  // Click the close button if the banner exists
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        // Click the 'Last Updated' button
        WebElement lastUpdatedButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/button")));
        lastUpdatedButton.click();

        // Select the third option from the dropdown
        WebElement dropdownOption = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/div/input[3]")));
        dropdownOption.click();

        // Wait for the sorting to take effect
        Thread.sleep(3000);

        // Get the values from the specified XPath locations
        WebElement firstResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/p")));
        WebElement secondResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[2]/div[1]/div[2]/div[2]/div[1]/p")));

        // Extract and parse the integer values from the text
        int firstValue = Integer.parseInt(firstResultElement.getText().split(" ")[0]);
        int secondValue = Integer.parseInt(secondResultElement.getText().split(" ")[0]);

        System.out.println("First value: " + firstValue);
        System.out.println("Second value: " + secondValue);

        // Assert that the first value is greater than or equal to the second value
        assertTrue(firstValue >= secondValue, "Results should be sorted by 'Last Updated' in descending order");
    }


    /*@Test
    public void testCompareDatesFromLatestFirst() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        // Check and click the "X" button to close the cookie banner if visible
        try {
            WebElement cookieCloseButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();  // Click the close button if the banner exists
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        // Expand Filter All Books Section
        WebElement filterButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[2]/span")));
        filterButton.click();
        Thread.sleep(1000);

        scrollToY(500);
        Thread.sleep(1000);

        // Locate the dropdown list (ul)
        WebElement genreDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/span[1]/span[1]/span/ul")));
        genreDropdown.click(); // Open the dropdown

        // Select the "Fantasy" option
        WebElement fantasyOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Fantasy']")));
        fantasyOption.click();
        Thread.sleep(500);

        WebElement randomClick = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]")));
        randomClick.click();

        scrollToY(800);

        Thread.sleep(1000);

        // Apply the filter
        WebElement filterButtonApply = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/div[5]/div[2]/input")));
        filterButtonApply.click();

        Thread.sleep(3000);

        scrollToY(1100);

        // Click the 'Last Updated' button
        WebElement lastUpdatedButton = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/button")));
        lastUpdatedButton.click();

        // Select the fifth option from the dropdown
        WebElement dropdownOption = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/div/input[5]")));
        dropdownOption.click();

        // Wait for 3 seconds
        Thread.sleep(3000);

        scrollToY(1300);

        Thread.sleep(3000);

        // Click on the first publication Date reveal
        WebElement firstPublicationDateReveal = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/p/span")));
        firstPublicationDateReveal.click();

        // Wait for 1 second
        Thread.sleep(5000);

        // Find the div with the class "edition-info" and wait for it to be visible
        WebElement infoDiv = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("edition-info")));
        // Log out success for debugging
        System.out.println("infoDiv: " + infoDiv);
        // The infoDiv contains <p> elements, the last of which is the publication date
        // Find how many <p> elements are in the infoDiv and use the count for the index number
        int pCount = infoDiv.findElements(By.tagName("p")).size();
        // Get the last <p> element in the infoDiv
        WebElement publicationDateElement = infoDiv.findElements(By.tagName("p")).get(pCount - 1);
        String dateTextFirst = publicationDateElement.getText().trim();
        String[] datePartsFirst = dateTextFirst.split(" ");
        int dayFirst = Integer.parseInt(datePartsFirst[2]);
        int monthFirst = getMonthAsNumber(datePartsFirst[3]);
        int yearFirst = Integer.parseInt(datePartsFirst[4]);
        // Log out the entire text, then the individual day/month/year parts
        System.out.println("Date text: " + dateTextFirst);
        System.out.println("Day: " + dayFirst);
        System.out.println("Month: " + monthFirst);
        System.out.println("Year: " + yearFirst);

        // Wait for 3 seconds
        Thread.sleep(3000);

        // Click on the second publication
        WebElement secondPublicationDateReveal = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[2]/div[1]/div[2]/div[2]/div[1]/p/span")));
        secondPublicationDateReveal.click();
        Thread.sleep(5000); // Allow time for the edition-info div to load

        //*********************************************************************************************************************************************************

        // Find the div with the class "edition-info" and wait for it to be visible
        WebElement infoDiv1 = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("edition-info")));
        System.out.println("infoDiv1: " + infoDiv1);

        // The infoDiv contains <p> elements, the last of which is the publication date
        int pCount1 = infoDiv1.findElements(By.tagName("p")).size();
        WebElement publicationDateElement1 = infoDiv1.findElements(By.tagName("p")).get(pCount1 - 1);
        String dateTextSecond = publicationDateElement1.getText().trim();
        System.out.println("Date text (second): " + dateTextSecond);

        // Parse the date
        String[] datePartsSecond = dateTextSecond.split(" ");
        int daySecond = Integer.parseInt(datePartsSecond[2]);
        int monthSecond = getMonthAsNumber(datePartsSecond[3]);
        int yearSecond = Integer.parseInt(datePartsSecond[4]);
        System.out.println("Day (second): " + daySecond);
        System.out.println("Month (second): " + monthSecond);
        System.out.println("Year (second): " + yearSecond);

        Thread.sleep(3000);*/


        /*// Click on the second publication
        WebElement secondPublicationDateReveal = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[2]/div[1]/div[2]/div[2]/div[1]/p/span")));
        secondPublicationDateReveal.click();

        // Find the div with the class "edition-info" and wait for it to be visible
        WebElement infoDiv1 = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("edition-info")));
        // Log out success for debugging
        System.out.println("infoDiv: " + infoDiv1);
        // The infoDiv contains <p> elements, the last of which is the publication date
        // Find how many <p> elements are in the infoDiv and use the count for the index number
        int pCount1 = infoDiv1.findElements(By.tagName("p")).size();
        // Get the last <p> element in the infoDiv
        WebElement publicationDateElement1 = infoDiv1.findElements(By.tagName("p")).get(pCount1 - 1);
        String dateTextSecond = publicationDateElement1.getText().trim();
        String[] datePartsSecond = dateTextSecond.split(" ");
        int daySecond = Integer.parseInt(datePartsSecond[2]);
        int monthSecond = getMonthAsNumber(datePartsSecond[3]);
        int yearSecond = Integer.parseInt(datePartsSecond[4]);
        // Log out the entire text, then the individual day/month/year parts
        System.out.println("Date text: " + dateTextSecond);
        System.out.println("Day: " + daySecond);
        System.out.println("Month: " + monthSecond);
        System.out.println("Year: " + yearSecond);*/

        /*// Compare the two dates
        boolean isDateFirstGreater = compareDates(dayFirst, monthFirst, yearFirst, daySecond, monthSecond, yearSecond);

        // Assert that the first date is greater than the second date
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
    }*/

    @Test
    public void testCompareDatesFromLatestFirst() throws InterruptedException {
        // Open the StoryGraph browse page
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        // Check and click the "X" button to close the cookie banner if visible
        try {
            WebElement cookieCloseButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();  // Click the close button if the banner exists
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        // Expand Filter All Books Section
        WebElement filterButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[2]/span")));
        filterButton.click();
        Thread.sleep(1000);

        scrollToY(500);
        Thread.sleep(1000);

        // Locate the dropdown list (ul)
        WebElement genreDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/span[1]/span[1]/span/ul")));
        genreDropdown.click(); // Open the dropdown

        // Select the "Fantasy" option
        WebElement fantasyOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Fantasy']")));
        fantasyOption.click();
        Thread.sleep(500);

        WebElement randomClick = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]")));
        randomClick.click();

        scrollToY(800);

        Thread.sleep(1000);

        // Apply the filter
        WebElement filterButtonApply = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/div[5]/div[2]/input")));
        filterButtonApply.click();

        Thread.sleep(3000);

        scrollToY(1100);

        // Click the 'Last Updated' button
        WebElement lastUpdatedButton = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/button")));
        lastUpdatedButton.click();

        // Select the fifth option from the dropdown
        WebElement dropdownOption = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/div/input[5]")));
        dropdownOption.click();

        // Wait for 3 seconds
        Thread.sleep(3000);

        scrollToY(1300);

        Thread.sleep(3000);

        // Handle cookies, filters, or any initial interactions if required
        // scrollToY is a helper method assumed to scroll to a specific Y position
        scrollToY(1150);

        // Wait for publication date elements to be loaded
        Thread.sleep(3000);

        // Fetch all elements with publication dates
        List<WebElement> publicationDateReveals = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//div[contains(@class, 'edition-info')]//p")
        ));

        // Extract publication dates into a list
        List<String> dateTexts = new ArrayList<>();
        for (WebElement dateElement : publicationDateReveals) {
            dateTexts.add(dateElement.getText().trim());
        }

        // Log the publication dates for debugging
        System.out.println("Collected dates: " + dateTexts);

        // Ensure at least two publication dates exist
        assertTrue(dateTexts.size() >= 2, "There should be at least two publication dates to compare");

        // Parse the first and second publication dates
        String[] datePartsFirst = dateTexts.get(0).split(" ");
        int dayFirst = Integer.parseInt(datePartsFirst[2]);
        int monthFirst = getMonthAsNumber(datePartsFirst[3]);
        int yearFirst = Integer.parseInt(datePartsFirst[4]);

        String[] datePartsSecond = dateTexts.get(1).split(" ");
        int daySecond = Integer.parseInt(datePartsSecond[2]);
        int monthSecond = getMonthAsNumber(datePartsSecond[3]);
        int yearSecond = Integer.parseInt(datePartsSecond[4]);

        // Compare the two dates
        boolean isDateFirstGreater = compareDates(dayFirst, monthFirst, yearFirst, daySecond, monthSecond, yearSecond);

        // Assert that the first date is more recent than the second date
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
        if (year1 != year2) {
            return year1 > year2;
        } else if (month1 != month2) {
            return month1 > month2;
        } else {
            return day1 > day2;
        }
    }

    @Test
    public void testVerifyBookAddedToRead() throws InterruptedException {
        // Perform login
        login("multipurpose.beca@gmail.com", "1111112");
        Thread.sleep(2000);

        webDriver.get("https://app.thestorygraph.com/books/96e8fd00-733d-4686-9e41-d388149c438b");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        // Check and click the "X" button to close the cookie banner if visible
        try {
            WebElement cookieCloseButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }
        scrollToY(200);
        Thread.sleep(111000);
        // Click the book to add it to the "To Read" list
        WebElement dropdownOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[5]/div[1]/div[2]/div[4]/div[1]/div[1]/div/form/button")));
        dropdownOption.click();

        // Navigate to the "To Read" section
        webDriver.get("https://app.thestorygraph.com/to-read/multipurpose");

        // Wait for the "To Read" page to load
        List<WebElement> booksList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div[1]/div[4]/div/a/button")
        ));

        // Verify the book is in the list
        String expectedBookTitle = "The Title of the Book You Clicked"; // Replace with the actual title
        boolean bookFound = booksList.stream()
                .anyMatch(book -> book.getText().equalsIgnoreCase(expectedBookTitle));

        assertTrue(bookFound, "The book should be listed in the 'To Read' section.");
    }

    @Test
    public void testChangeUsername() throws InterruptedException {
        // Perform login
        login("multipurpose.beca@gmail.com", "111111");
        Thread.sleep(2000);
        // Navigate to the Edit Profile page
        webDriver.get("https://app.thestorygraph.com/profile/edit/multipurpose1");

        Thread.sleep(1000);
        // Locate the username input field
        // Check and click the "X" button to close the cookie banner if visible
        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();  // Click the close button if the banner exists
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }
        WebElement usernameInput = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//html/body/div[1]/div/main/div/div[3]/div/form/div[2]/input")));

        // Clear the field and enter a new username
        String newUsername = "multipurpose";
        usernameInput.clear();
        usernameInput.sendKeys(newUsername);

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 500)"); // Scroll down by 500 pixels


        // Locate and click the Save Changes button
        WebElement saveButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/button"));
        saveButton.click();
        Thread.sleep(2000);

        // Verify the URL ends with the new username
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.contains(newUsername), "The URL did not end with the new username.");
    }


    @Test
    public void testToggleVisibility() throws InterruptedException {
        // Perform login
        login("multipurpose.beca@gmail.com", "111111");
        Thread.sleep(3000);
        // Navigate to the Edit Profile page
        webDriver.get("https://app.thestorygraph.com/profile/edit/multipurpose");

        // Locate the visibility dropdown
        WebElement visibilityDropdown = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/div[6]/select"));
        Select dropdown = new Select(visibilityDropdown);

        // Select 'Private' option
        dropdown.selectByVisibleText("Community");

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 300)"); // Scroll down by 500 pixels
        // Submit the form
        WebElement saveButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/button"));
        saveButton.click();

        // Wait for the changes to apply
        Thread.sleep(1000);

        // Reload the page to check the new visibility
        webDriver.get("https://app.thestorygraph.com/profile/edit/multipurpose");
        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 300)");
        visibilityDropdown = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/div[6]/select"));
        dropdown = new Select(visibilityDropdown);

        // Verify the visibility has been set to 'Private'
        String selectedOption = dropdown.getFirstSelectedOption().getText();
        assertEquals("Community", selectedOption, "The visibility option was not updated correctly.");
    }

    @Test
    public void testChangePassword() throws InterruptedException {

        login("multipurpose.beca@gmail.com", "111111");
        Thread.sleep(3000);
        webDriver.get("https://app.thestorygraph.com/profile/edit/multipurpose");

        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();
        } catch (TimeoutException e) {
            // Cookie banner was not visible, proceed without clicking
        }

        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 600)");
        WebElement changePasswordButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/a[1]/button")));
        changePasswordButton.click();

        Thread.sleep(2000);
        // Wait for the Change Password page to load
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        WebElement currentPasswordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_current_password")));
        WebElement newPasswordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_password")));
        WebElement confirmPasswordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_password_confirmation")));
        WebElement saveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div/form/div[4]/button")));

        // Enter current and new passwords
        currentPasswordField.sendKeys("111111");
        newPasswordField.sendKeys("1111112");
        confirmPasswordField.sendKeys("1111112");
        saveButton.click();

        Thread.sleep(3000);

        logout();

        Thread.sleep(3000);

        login("multipurpose.beca@gmail.com", "1111112");
        Thread.sleep(3000);
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.equals("https://app.thestorygraph.com/"));

        logout();
        Thread.sleep(3000);
        login("multipurpose.beca@gmail.com", "111111");
        Thread.sleep(3000);
        String currentUrl1 = webDriver.getCurrentUrl();
        assertFalse(currentUrl1.equals("https://app.thestorygraph.com/"));
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
        // Protected route
        webDriver.get(baseUrl + protectedRoute);
        String currentUrl = webDriver.getCurrentUrl();
        assertEquals("https://app.thestorygraph.com/users/sign_in", currentUrl, "Guest should be redirected to the login page");
    }
}

