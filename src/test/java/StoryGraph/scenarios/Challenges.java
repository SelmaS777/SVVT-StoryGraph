package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Challenges extends SetupUtils {

    // someone else's challenge

    @Test
    public void testReadingChallengesJoinGeneral() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);

        webDriver.get(BASEURL+"/reading_challenges/");

        WebElement readsTheWorld2025Click = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/h6/a")));
        readsTheWorld2025Click.click();
        Thread.sleep(500);
        scroll(300);

        WebElement joinChallengeButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/form/button")));
        joinChallengeButton.click();
        Thread.sleep(500);

        webDriver.get(BASEURL+"/reading_challenges/dashboard/"+USERNAMES[0]);
        Thread.sleep(1000);
        scroll(1000);

        WebElement joinedChallenge = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div[2]/div/div/turbo-frame/div/div/p[1]/a")));
        assertEquals("The StoryGraph Reads the World 2025", joinedChallenge.getText());
    }

    @Test
    public void testReadingChallengesLeaveGeneral() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/reading_challenges/");

        WebElement readsTheWorld2025Click = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div[3]/h6/a")));
        readsTheWorld2025Click.click();

        WebElement clickMoreButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("svg.w-7")));
        clickMoreButton.click();
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].style.display='none';", clickMoreButton);

        WebElement leaveButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Leave")));
        leaveButton.click();

        Alert alert = webDriver.switchTo().alert();
        alert.accept();

        webDriver.get(BASEURL+"/reading_challenges/dashboard/"+USERNAMES[0]);
        Thread.sleep(1000);
        scroll(1000);

        List<WebElement> elements = webDriver.findElements(By.xpath("//*[contains(text(), 'The StoryGraph Reads the World 2025')]"));
        assertTrue(elements.isEmpty(), "'The StoryGraph Reads the World 2025' is still present on the page, but it should not be.");

    }


}
