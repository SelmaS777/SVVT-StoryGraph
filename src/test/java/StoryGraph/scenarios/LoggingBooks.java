package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LoggingBooks extends SetupUtils {

    @Test
    public void testBookLog() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/stats/"+USERNAMES[0]+"?year=2025");
        Thread.sleep(2000);
        scroll(300);
        String number = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[2]/a"))).getText();
        String great = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[3]"))).getText();

        addBookToReadPile(BASEURL+"/books/e0f01a40-b8fb-472c-998d-853fadf00a67", "8", "1", "2025");
        webDriver.get(BASEURL+"/stats/"+USERNAMES[0]+"?year=2025");
        Thread.sleep(2000);
        scroll(300);
        WebElement numberNew = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[2]/a")));
        WebElement greatNew = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[3]")));

        assertNotEquals(numberNew.getText(), number);
        assertNotEquals(greatNew.getText(), great);

        removeBookFromRead(BASEURL+"/books/e0f01a40-b8fb-472c-998d-853fadf00a67");
    }

    @Test
    public void testBookLogLastYear() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/stats/"+USERNAMES[0]+"?year=2025");
        Thread.sleep(2000);

        scroll(300);
        String number = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[2]/a"))).getText();
        String great = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[3]"))).getText();

        addBookToReadPile(BASEURL+"/books/d93d6f72-8d62-4294-b087-91632fac143a", "7", "2", "2024");
        webDriver.get(BASEURL+"/stats/"+USERNAMES[0]+"?year=2025");
        Thread.sleep(2000);

        scroll(300);
        WebElement numberNew = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[2]/a")));
        WebElement greatNew = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/div/div/div[1]/p[3]")));

        assertEquals(number, numberNew.getText());
        assertEquals(great, greatNew.getText());

        removeBookFromRead(BASEURL+"/books/d93d6f72-8d62-4294-b087-91632fac143a");
    }

}
