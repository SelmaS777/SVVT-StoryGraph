package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSignIn extends SetupUtils {

    @Test
    public void testSignInNonExistentUser() {

        webDriver.get(BASEURL+"/users/sign_in");

        WebElement emailField = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_email")));
        WebElement passwordField = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_password")));
        WebElement loginButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in-btn")));

        assertTrue(emailField.isDisplayed(), "Email field should be visible on the login page.");
        assertTrue(passwordField.isDisplayed(), "Password field should be visible on the login page.");
        assertTrue(loginButton.isDisplayed(), "Login button should be visible on the login page.");

        emailField.sendKeys("svvttest@test.com");
        passwordField.sendKeys("SVVTTest123");
        loginButton.click();

        String currentUrl = webDriver.getCurrentUrl();
        assertEquals(BASEURL + "/users/sign_in", currentUrl, "User shouldn't be redirected to the app after login.");
    }

    @Test
    public void testSignInWrongPassword() {

        login(EMAIL, "wrongpassword");
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.contains("users/sign_in"), "User should stay on the login page after failed login attempt.");

    }

    @Test
    public void testSignInValidUser() {

        login(EMAIL, PASSWORDS[0]);

        String currentUrl = webDriver.getCurrentUrl();
        assertNotEquals(BASEURL + "/users/sign_in", currentUrl, "User shouldn't be redirected to the app after login.");

    }
}
