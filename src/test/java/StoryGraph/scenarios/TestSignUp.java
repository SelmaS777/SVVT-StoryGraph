package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSignUp extends SetupUtils {

    @Test
    public void testSignUpIncorrectUsername() {
        signUp(EMAIL, "multi.beca", PASSWORDS[0]);
        assertTrue(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"new_user\"]/div[1]/div/div[2]/h3"))).getText().contains("error"));
        assertEquals(BASEURL+"/users", webDriver.getCurrentUrl());
    }

    @Test
    public void testSignUp() throws InterruptedException {

        signUp(EMAIL, USERNAMES[0], PASSWORDS[0]);
        webDriverWait.until(ExpectedConditions.urlToBe(BASEURL+"/import-goodreads-0"));
        WebElement noThanksButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[5]/span/a[2]/button")));
        noThanksButton.click();

        webDriverWait.until(ExpectedConditions.urlToBe(BASEURL+"/onboarding-community-preferences"));
        scroll(200);
        Thread.sleep(1000);

        WebElement saveButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/div/div[2]/button/span[1]")));
        saveButton.click();

        webDriverWait.until(ExpectedConditions.urlToBe(BASEURL+"/onboarding-audio-preferences-0"));
        WebElement noButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[4]/span/a[2]/button")));
        noButton.click();

        webDriverWait.until(ExpectedConditions.urlToBe(BASEURL+"/onboarding-launch-pad"));
        String finalUrl = webDriver.getCurrentUrl();
        assertEquals(BASEURL+"/onboarding-launch-pad", finalUrl, "User should be redirected to the launch pad after completing onboarding.");

    }


}
