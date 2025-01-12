package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChangePasswordUsername extends SetupUtils {


    @Test
    public void testChangePassword() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/profile/edit/" + USERNAMES[0]);

        scroll(600);
        WebElement changePasswordButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/div/a[1]/button")));
        changePasswordButton.click();

        WebElement currentPasswordField = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_current_password")));
        WebElement newPasswordField = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_password")));
        WebElement confirmPasswordField = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_password_confirmation")));
        WebElement saveButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/div/div/form/div[4]/button")));

        currentPasswordField.sendKeys(PASSWORDS[0]);
        newPasswordField.sendKeys(PASSWORDS[1]);
        confirmPasswordField.sendKeys(PASSWORDS[1]);
        saveButton.click();
        Thread.sleep(1000);

        logout();
        Thread.sleep(2000);
        login(EMAIL, PASSWORDS[1]);
        Thread.sleep(2000);
        System.out.println(webDriver.getCurrentUrl());
        assertEquals(BASEURL+"/", webDriver.getCurrentUrl());

        logout();
        Thread.sleep(2000);
        login(EMAIL, PASSWORDS[0]);
        Thread.sleep(2000);
        System.out.println(webDriver.getCurrentUrl());
        assertNotEquals(BASEURL+"/", webDriver.getCurrentUrl());
    }

    @Test
    public void testChangeUsername() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/profile/edit/" + USERNAMES[0]);

        WebElement usernameInput = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//html/body/div[1]/div/main/div/div[3]/div/form/div[2]/input")));
        usernameInput.clear();
        usernameInput.sendKeys(USERNAMES[1]);
        scroll(500);

        WebElement saveButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/button")));
        saveButton.click();
        Thread.sleep(1000);

        webDriver.navigate().refresh();
        Thread.sleep(2000);
        assertTrue(webDriver.getCurrentUrl().contains(BASEURL+"/profile/"+USERNAMES[1]), "The URL did not reflect the updated username.");

    }


}
