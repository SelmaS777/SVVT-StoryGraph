package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Filter extends SetupUtils {

    //filter

    @Test
    public void testFilterByMood() throws InterruptedException {

        webDriver.get(BASEURL+"/browse?sort_order=Last+updated");
        WebElement filterButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[2]/span")));
        filterButton.click();
        Thread.sleep(1000);

        WebElement moodSelect = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mood_adventurous")));
        moodSelect.click();
        /*Select moodSelect = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mood_adventurous"))));
        moodSelect.selectByValue("adventurous");*/
        WebElement moodSelect1 = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mood_dark")));
        moodSelect1.click();
        /*moodSelect = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mood_dark"))));
        moodSelect.selectByValue("dark");*/

        scroll(300);
        WebElement filterButtonApply = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/div[5]/div[2]/input")));
        filterButtonApply.click();
        Thread.sleep(5000);
        scroll(1200);

        List<WebElement> filteredResults = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/div[3]/div[1]")));
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
    public void testFilterByGenres() throws InterruptedException {

        webDriver.get(BASEURL+"/browse?sort_order=Last+updated");
        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();
        } catch (TimeoutException e) { /* Cookie banner was not visible, proceed without clicking*/}

        WebElement filterButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[2]/span")));
        filterButton.click();
        Thread.sleep(1000);

        scroll(500);
        WebElement genreDropdown = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/span[1]/span[1]/span/ul")));
        genreDropdown.click();

        WebElement sciFiOption = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[text()='Science Fiction']")));
        sciFiOption.click();
        WebElement fantasyOption = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Fantasy']")));
        fantasyOption.click();
        WebElement randomClick = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]")));
        randomClick.click();
        Thread.sleep(500);
        scroll(500);
        Thread.sleep(2000);

        WebElement filterButtonApply = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/div[5]/div[2]/input")));
        filterButtonApply.click();
        Thread.sleep(3000);
        scroll(1200);

        List<WebElement> filteredResults = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/div[3]/div[1]")));
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

}
