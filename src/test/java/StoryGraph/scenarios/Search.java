package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Search extends SetupUtils {

    // search

    @ParameterizedTest
    @CsvSource({"Romeo and Juliet, romeo", "Pride and Prejudice, pride", "The Fellowship of the Ring, ring", "Emma, emma", "Tale of Two Cities, cities"})
    public void testSearchForExistingBooks(String searchTermInput, String expectedPartialResult) {

        webDriver.get(BASEURL);

        WebElement searchInput = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/nav/div[1]/div/div[2]/div/div/form/input")));
        searchInput.sendKeys(searchTermInput);
        searchInput.sendKeys(Keys.ENTER);

        WebElement searchResult = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'results')]//h1/a")));
        String productTitle = searchResult.getText();

        assertTrue(productTitle.toLowerCase().contains(expectedPartialResult), "Search results should contain the search term anywhere in the title.");
    }


    @ParameterizedTest
    @CsvSource({"lajkrnblarjbn", "aerijbargunpq", "pairrugbqirug", "airurgbqiuvr", "arpeiguapivuna"})
    public void testSearchForNonExistingBooks(String searchTermInput) throws InterruptedException {
        webDriver.get(BASEURL);
        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();
        } catch (TimeoutException e) {/* Cookie banner was not visible, proceed without clicking*/}
        WebElement searchInput = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search")));
        searchInput.sendKeys(searchTermInput);
        searchInput.sendKeys(Keys.ENTER);
        Thread.sleep(1000);
        WebElement noResultsParagraph;
        String noResultsText = "";
        try {    // First attempt with the primary XPath
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
        assertEquals("There's nothing on The StoryGraph matching \"" + searchTermInput + "\".", noResultsText, "No results message should match expected text.");
    }

}
