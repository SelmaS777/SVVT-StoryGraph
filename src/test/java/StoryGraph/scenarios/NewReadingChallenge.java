package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.By.className;

public class NewReadingChallenge extends SetupUtils {

    //reading challenges

    @Test
    public void testMakeMyChallenge() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/reading_challenges/dashboard/"+USERNAMES[0]);
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
        scroll(500);

        Select year = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reading_challenge_end_date_1i"))));
        year.selectByValue("2026");

        WebElement numberOfBooks = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reading_challenge_number_of_books_required")));
        numberOfBooks.sendKeys("10");
        scroll(500);

        WebElement makeChallengeLive = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reading_challenge_live")));
        makeChallengeLive.click();

        WebElement saveButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"new_reading_challenge\"]/button")));
        saveButton.click();

        WebElement joinChallenge = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("button.mt-3.inline-flex.items-center.px-4.py-2.border.border-transparent.text-sm.rounded-md.shadow-sm")));
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("arguments[0].click();", joinChallenge);
        Thread.sleep(500);
        scroll(200);

        assertTrue(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[5]/turbo-frame/p[2]/span")))
                .getText().contains("0%"));

        webDriver.navigate().to(BASEURL+"/reading_challenges/dashboard/"+USERNAMES[0]);

        scroll(800);

        WebElement challengesContainer = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".reading-challenge-panes")));
        WebElement specificChallenge = challengesContainer.findElement(By.xpath(".//div[contains(@id, 'urc_') and .//a[text()='Read 10 classics']]"));
        WebElement booksReadElement = specificChallenge.findElement(By.cssSelector("p.mb-3"));
        String booksReadText = booksReadElement.getText();
        assertTrue(booksReadText.contains("0 books read out of 10"));
    }

    @Test
    public void addBookToChallenge() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/books/d4bee89f-3fdd-4dd0-8d77-4316bed132e7");

        WebElement addChallenge = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[3]/div/div[2]/a")));
        addChallenge.click();
        WebElement challengeCheckbox = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//form[.//label[contains(text(), 'Read 10 classics')]]//input[@type='checkbox']")));
        challengeCheckbox.click();

        assertTrue(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated
                        (By.xpath("//div[.//label[contains(text(), 'Read 10 classics')]]/following-sibling::div[@id[contains(., 'confirmation_message')]]/span")))
                .getText().contains("Saved!"));

        addBookToReadPile(BASEURL+"/books/d4bee89f-3fdd-4dd0-8d77-4316bed132e7", "11", "1", "2025");
        webDriver.get(BASEURL+"/reading_challenges/dashboard/"+USERNAMES[0]);
        scroll(600);

        WebElement challengesContainer = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".reading-challenge-panes")));
        WebElement specificChallenge = challengesContainer.findElement(By.xpath(".//div[contains(@id, 'urc_') and .//a[text()='Read 10 classics']]"));
        WebElement booksReadElement = specificChallenge.findElement(By.cssSelector("p.mb-3"));
        String booksReadText = booksReadElement.getText();
        assertFalse(booksReadText.contains("0 books read out of 10"));

        removeBookFromRead(BASEURL+"/books/d4bee89f-3fdd-4dd0-8d77-4316bed132e7");

    }

    @Test
    public void testArchiveChallenge() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);

        webDriver.get(BASEURL+"/reading_challenges/dashboard/"+USERNAMES[0]);

        WebElement archive = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[a[contains(text(), 'Read 10 classics')]]/following-sibling::form/button")));
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("arguments[0].scrollIntoView(true);", archive);
        js.executeScript("arguments[0].click();", archive);

        Alert alert = webDriver.switchTo().alert();
        alert.accept();
        Thread.sleep(1000);

        webDriver.get(BASEURL+"/reading_challenges/archive/"+USERNAMES[0]);
        Thread.sleep(500);

        String challengeText = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Read 10 classics')]"))).getText();
        System.out.println(challengeText);
        assertNotNull(challengeText, "Challenge 'Read 10 classics' not found!");
    }

    @Test
    public void testDeleteChallenge() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/reading_challenges/archive/"+USERNAMES[0]);

        WebElement clickChallenge = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Read 10 classics')]")));
        clickChallenge.click();
        WebElement editChallenge = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(className("edit-reading-challenge-link")));
        editChallenge.click();
        Thread.sleep(1000);
        scroll(800);

        WebElement deleteChallenge = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div[4]/a")));
        deleteChallenge.click();
        Alert alert1 = webDriver.switchTo().alert();
        alert1.accept();

        webDriver.get(BASEURL+"/reading_challenges/archive/"+USERNAMES[0]);
        webDriver.navigate().refresh();
        Thread.sleep(1000);
        try {
            WebElement noChallengesMessage = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(), \"There's nothing here yet!\")]")));
            assertEquals("There's nothing here yet!", noChallengesMessage.getText());
        } catch (NoSuchElementException e) {
            System.out.println("No challenge message found.");
        }
    }


}
