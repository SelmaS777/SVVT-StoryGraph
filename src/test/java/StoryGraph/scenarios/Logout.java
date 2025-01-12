package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Logout extends SetupUtils {


    @Test
    public void testLogout() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        logout();
        WebElement loginButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(),'Log in')]")));
        assertTrue(loginButton.isDisplayed(), "User should be redirected to the login page after logout.");
    }

}
