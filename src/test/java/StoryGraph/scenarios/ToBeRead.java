package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToBeRead extends SetupUtils {

    //tbr

    @Test
    public void testVerifyBookAddedToRead() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);

        webDriver.get(BASEURL+"/books/96e8fd00-733d-4686-9e41-d388149c438b");
        Thread.sleep(1000);

        List<WebElement> buttons = webDriver.findElements(By.xpath("//button[contains(text(), 'to read')]"));
        WebElement toReadButton = buttons.get(1);
        toReadButton.click();
        Thread.sleep(500);

        webDriver.get(BASEURL+"/to-read/" + USERNAMES[0]);
        Thread.sleep(300);
        scroll(500);

        String title = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"book_96e8fd00-733d-4686-9e41-d388149c438b\"]/div[1]/div[1]/div[2]/div[1]/div[1]/h3/a"))).getText();
        assertEquals("The Seer King", title);

        removeBookFromRead(BASEURL+"/books/96e8fd00-733d-4686-9e41-d388149c438b");
    }

}
