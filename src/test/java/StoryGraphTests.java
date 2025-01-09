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
import java.util.List;

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
        // Navigate to the logout button
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

            // Submit form
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
        WebElement secondButton = buttons.get(1); // Assume the second button is always required
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
        // Ensure we are logged in before attempting logout
        login(EMAIL, PASSWORDS[0]);

        logout();
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Log in')]")));
        assertTrue(loginButton.isDisplayed(), "User should be redirected to the login page after logout.");
    }

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
        signUp(EMAIL, EMAIL, "multipurpose.beca", PASSWORDS[0]);
    }

    @Test
    public void testSignUp() throws InterruptedException {
        signUp(EMAIL, EMAIL, USERNAMES[0], PASSWORDS[0]);

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
    public void testCompareDatesFromLatestFirst() throws InterruptedException {
        webDriver.get("https://app.thestorygraph.com/browse?sort_order=Last+updated");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

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
        WebElement infoDiv = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(className("edition-info")));
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

        List<WebElement> infoDivs = webDriver.findElements(By.className("edition-info"));
        WebElement infoDiv1 = infoDivs.get(2); // Assume the second button is always required

        /*// Wait until all elements with the class "edition-info" are visible
        List<WebElement> infoDivs = webDriverWait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("edition-info"))
        );*/

       /* // Find the div with the class "edition-info" and wait for it to be visible
        WebElement infoDiv1 = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("edition-info")));
        // Log out success for debugging
        System.out.println("infoDiv: " + infoDiv1);*/
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
        System.out.println("Year: " + yearSecond);

        // Compare the two dates
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
    }

    @Test
    public void testVerifyBookAddedToRead() throws InterruptedException {
        // Perform login
        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(2000);

        webDriver.get("https://app.thestorygraph.com/books/96e8fd00-733d-4686-9e41-d388149c438b");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        scrollToY(200);
        Thread.sleep(111000);
        // Click the book to add it to the "To Read" list
        WebElement dropdownOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[5]/div[1]/div[2]/div[4]/div[1]/div[1]/div/form/button")));
        dropdownOption.click();

        // Navigate to the "To Read" section
        webDriver.get("https://app.thestorygraph.com/to-read/" + USERNAMES[0]);

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
        // Log in with the current username and password
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
        // Perform login
        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(3000);
        // Navigate to the Edit Profile page
        webDriver.get("https://app.thestorygraph.com/profile/edit/" + USERNAMES[0]);

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
        webDriver.get("https://app.thestorygraph.com/profile/edit/" + USERNAMES[0]);
        ((JavascriptExecutor) webDriver).executeScript("window.scrollBy(0, 300)");
        visibilityDropdown = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/div[6]/select"));
        dropdown = new Select(visibilityDropdown);

        // Verify the visibility has been set to 'Private'
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

        // Set Time Zone
        Select timeZone = new Select(webDriver.findElement(By.id("user_time_zone")));
        timeZone.selectByValue("Hawaii");
        String expectedTimeZone = "(GMT-10:00) Hawaii";
        Thread.sleep(1000);

        // Set Language
        scrollToY(500);
        Thread.sleep(1000);
        WebElement languagesClick = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/form/div/div[1]/div[5]/span/span[1]/span/ul"));
        languagesClick.click();
        WebElement arabicOption = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Arabic']")));
        arabicOption.click();
        String expectedLanguage = "Arabic";
        Thread.sleep(1000);

        // Set Friends Dropdown
        scrollToY(1600);
        Thread.sleep(1000);
        WebElement friendsDropdown = webDriver.findElement(By.id("user_follow_setting"));
        friendsDropdown.click();
        WebElement friendsSelect = webDriver.findElement(By.xpath("//*[@id=\"user_friends_setting\"]/option[3]"));
        friendsSelect.click();
        String expectedFriendsSetting = "Friends";

        // Set Buddy Reads
        Select buddyReads = new Select(webDriver.findElement(By.id("user_buddy_reads_setting")));
        buddyReads.selectByValue("anybody");
        String expectedBuddyReads = "Anybody";

        // Set Owned Books Visibility
        scrollToY(2200);
        Select ownedBooks = new Select(webDriver.findElement(By.id("user_owned_books_visibility_setting")));
        ownedBooks.selectByValue("friends_and_following");
        String expectedOwnedBooksVisibility = "Friends & People I follow";

        // Save Preferences
        Thread.sleep(1000);
        WebElement updateButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/form/div/div[2]/button"));
        updateButton.click();

        // Reload the page and verify the values
        webDriver.get("https://app.thestorygraph.com/preferences/edit/" + USERNAMES[0]);
        Thread.sleep(2000);

        // Verify Time Zone
        Select actualTimeZone = new Select(webDriver.findElement(By.id("user_time_zone")));
        String actualTimeZoneValue = actualTimeZone.getFirstSelectedOption().getText();
        assertEquals(expectedTimeZone, actualTimeZoneValue);

        // Verify Language
        scrollToY(500);
        WebElement selectedLanguage = webDriver.findElement(By.xpath("//li[text()='Arabic']"));
        assertNotNull(selectedLanguage);

        // Verify Friends Dropdown
        scrollToY(1600);
        Select actualFriendsSetting = new Select(webDriver.findElement(By.id("user_follow_setting")));
        String actualFriendsValue = actualFriendsSetting.getFirstSelectedOption().getText();
        assertEquals(expectedFriendsSetting, actualFriendsValue);

        // Verify Buddy Reads
        Select actualBuddyReads = new Select(webDriver.findElement(By.id("user_buddy_reads_setting")));
        String actualBuddyReadsValue = actualBuddyReads.getFirstSelectedOption().getText();
        assertEquals(expectedBuddyReads, actualBuddyReadsValue);

        // Verify Owned Books Visibility
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

