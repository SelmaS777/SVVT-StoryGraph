package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Rating extends SetupUtils {

    @Test
    public void testRating() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        addBookToReadPile(BASEURL+"/books/e0f01a40-b8fb-472c-998d-853fadf00a67", "7", "1", "2025");
        Thread.sleep(1000);

        webDriver.get(BASEURL+"/reviews/new?book_id=e0f01a40-b8fb-472c-998d-853fadf00a67&return_to=%2Fbooks%2Fe0f01a40-b8fb-472c-998d-853fadf00a67");
        WebElement mood = webDriver.findElement(By.xpath("//*[@id=\"review_mood_ids_1\"]"));
        mood.click();
        WebElement medium = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[4]/form/input[3]"));
        medium.click();
        scroll(500);

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
        scroll(500);

        Select stars = new Select(webDriver.findElement(By.id("stars_integer")));
        stars.selectByValue("4");

        Select partStars = new Select(webDriver.findElement(By.id("stars_decimal")));
        partStars.selectByValue("75");
        scroll(500);

        WebElement submit = webDriver.findElement(By.xpath("//*[@id=\"new_review\"]/button"));
        submit.click();
        Thread.sleep(1500);

        WebElement seeReview = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[2]/div[3]/div/a"));
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
        scroll(1300);

        WebElement deleteReview = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/a")));
        deleteReview.click();
        Alert alert = webDriver.switchTo().alert();
        alert.accept();
        Thread.sleep(1000);
        assertEquals("0 reviews", webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/h1/span[2]"))).getText());

        removeBookFromRead(BASEURL+"/books/e0f01a40-b8fb-472c-998d-853fadf00a67");
    }

}
