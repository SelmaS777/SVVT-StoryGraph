package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Mode extends SetupUtils {


    @Test
    public void testModeChange() throws InterruptedException{

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/preferences/edit/"+USERNAMES[0]);

        Select mode = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/form/div/div[1]/div[4]/select"))));
        mode.selectByVisibleText(MODES[1]);
        scroll(1800);

        WebElement update = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/form/div/div[2]/button")));
        update.click();
        Thread.sleep(1000);

        webDriver.get(BASEURL+"/preferences/edit/"+USERNAMES[0]);
        Select newMode = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/form/div/div[1]/div[4]/select"))));
        assertEquals(MODES[1], newMode.getFirstSelectedOption().getText());
    }

}
