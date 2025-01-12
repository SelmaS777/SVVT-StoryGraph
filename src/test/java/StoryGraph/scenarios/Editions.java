package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Editions extends SetupUtils {


    @Test
    public void testPaperToAudio() throws InterruptedException{

        login(EMAIL, PASSWORDS[0]);

        addBookToReadPile(BASEURL+"/books/e0f01a40-b8fb-472c-998d-853fadf00a67", "1", "1", "2025");
        WebElement moreInfo = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[2]/p/span/span[2]"));
        moreInfo.click();
        String oldFormat = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[2]/div[2]/p[2]"))).getText();

        WebElement editions = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div[2]/p/a")));
        editions.click();
        Thread.sleep(2000);

        scroll(400);
        WebElement switchEdition = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"book_94aaa555-1ff2-46c0-8de6-77e86d3a7120\"]/div[1]/div[1]/div[2]/div[2]/div/div[2]/form[2]/button")));
        switchEdition.click();
        Thread.sleep(500);

        webDriver.get(BASEURL+"/books-read/"+USERNAMES[0]);

        scroll(300);
        WebElement moreInfo1 = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"book_94aaa555-1ff2-46c0-8de6-77e86d3a7120\"]/div[1]/div[1]/div[2]/div[1]/p/span/span[2]")));
        moreInfo1.click();
        String newFormat = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"book_94aaa555-1ff2-46c0-8de6-77e86d3a7120\"]/div[1]/div[1]/div[2]/div[1]/div[2]/p[2]"))).getText();

        assertNotEquals(oldFormat,newFormat);

        webDriver.get(BASEURL+"/books/e0f01a40-b8fb-472c-998d-853fadf00a67/editions");
        WebElement switchBack = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"book_e0f01a40-b8fb-472c-998d-853fadf00a67\"]/div[1]/div[1]/div[2]/div[2]/div/div[2]/form[2]/button")));
        switchBack.click();

        removeBookFromRead(BASEURL+"/books/e0f01a40-b8fb-472c-998d-853fadf00a67");
    }

}
