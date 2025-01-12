package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteAccount extends SetupUtils {


    @Test
    public void testDeleteAccount() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/profile/edit/"+USERNAMES[0]);
        Thread.sleep(1000);
        scroll(1500);

        WebElement deleteAccount = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/div/a[4]")));
        deleteAccount.click();

        Alert alert = webDriver.switchTo().alert();
        alert.accept();
        Thread.sleep(2500);

        assertEquals(BASEURL+"/", webDriver.getCurrentUrl());
        login(EMAIL, PASSWORDS[0]);
        assertEquals(BASEURL+"/users/sign_in", webDriver.getCurrentUrl());
    }

}
