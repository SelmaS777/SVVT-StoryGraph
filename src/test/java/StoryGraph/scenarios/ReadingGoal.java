package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReadingGoal extends SetupUtils {

    @Test
    public void testAddingGoal(){

        login(EMAIL, PASSWORDS[0]);

        webDriver.get(BASEURL+"/reading_challenges/dashboard/"+USERNAMES[0]);
        WebElement addReadingGoal = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div[1]/div/div[1]/a[1]/button")));
        addReadingGoal.click();

        WebElement booksToRead = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reading_goal_goal")));
        booksToRead.clear();

        WebElement booksToReadInputValue = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reading_goal_goal")));
        booksToReadInputValue.sendKeys("20");

        WebElement updateReadingGoalButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/form/button")));
        updateReadingGoalButton.click();

        webDriver.get(BASEURL+"/reading_challenges/dashboard/"+USERNAMES[0]);

        WebElement element = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p > span.font-semibold.mb-2")));
        String elementText = element.getText();
        String numberOnly = elementText.replaceAll("\\D+", "");
        int number = Integer.parseInt(numberOnly);

        int expectedNumber = 20;
        assertEquals(expectedNumber, number, "The number of books does not match the expected value.");

    }

    @Test
    public void testReadingGoal() {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/reading_challenges/dashboard/"+USERNAMES[0]);

        WebElement editReadingGoalBooksButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div[1]/div/div[1]/div/div[1]/a/button")));
        editReadingGoalBooksButton.click();

        WebElement booksToRead = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reading_goal_goal")));
        booksToRead.clear();
        WebElement booksToReadInputValue = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reading_goal_goal")));
        booksToReadInputValue.sendKeys("30");

        WebElement updateReadingGoalButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/form/button")));
        updateReadingGoalButton.click();

        webDriver.get(BASEURL+"/reading_challenges/dashboard/"+USERNAMES[0]);
        WebElement element = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p > span.font-semibold.mb-2")));
        String elementText = element.getText();
        String numberOnly = elementText.replaceAll("\\D+", "");
        int number = Integer.parseInt(numberOnly);

        int expectedNumber = 30;
        assertEquals(expectedNumber, number, "The number of books does not match the expected value.");
    }

    @Test
    public void deleteReadingGoal(){

        login(EMAIL, PASSWORDS[0]);

        webDriver.get(BASEURL+"/reading_challenges/dashboard/"+USERNAMES[0]);

        WebElement editReadingGoalBooksButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div[1]/div/div[1]/div/div[1]/a/button")));
        editReadingGoalBooksButton.click();

        WebElement deleteReadingGoal = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/a")));
        deleteReadingGoal.click();

        Alert alert = webDriver.switchTo().alert();
        alert.accept();

        assertTrue(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[@href='/reading_goals/new']/button[@type='button']"))).getText().contains("Set up reading goal for 2025"));

    }

}
