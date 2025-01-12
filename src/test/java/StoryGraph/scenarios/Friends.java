package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Friends extends SetupUtils {

    //friends

    @Test
    public void testAcceptFriends() throws InterruptedException{

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/profile/downbadforjean");

        WebElement addFriend = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"profile-heading-pane\"]/div/div/div[2]/div[1]/form/button")));
        addFriend.click();

        logout();
        login("ajlabeca@gmail.com", "svvtprojectpurposes");
        webDriver.get(BASEURL+"/notifications");

        WebElement accept = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(text(), 'Accept')]\n")));
        accept.click();

        logout();
        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/notifications");

        assertTrue(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/div[2]/div/ul/li/div/div/div/div[2]/div")))
                .getText().contains("has accepted your friend request!"));

        webDriver.get(BASEURL+"/following");

        boolean isFriendPresent = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'standard-pane')]//a[contains(text(), 'downbadforjean')]"))).isDisplayed();
        assertTrue(isFriendPresent, "'downbadforjean' is not present in the Friends section.");

        webDriver.get(BASEURL+"/profile/downbadforjean");
        WebElement unfriend = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"unfriend-btn\"]")));
        unfriend.click();

        Alert alert = webDriver.switchTo().alert();
        alert.accept();

    }

    @Test
    public void testRejectFriends() throws InterruptedException{

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/profile/downbadforjean");

        WebElement addFriend = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"profile-heading-pane\"]/div/div/div[2]/div[1]/form/button")));
        addFriend.click();
        logout();
        login("ajlabeca@gmail.com", "svvtprojectpurposes");

        webDriver.get(BASEURL+"/notifications");

        WebElement reject = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(text(), 'Reject')]\n")));
        reject.click();
        logout();
        login(EMAIL, PASSWORDS[0]);

        webDriver.get(BASEURL+"/following");

        List<WebElement> elements = webDriver.findElements(
                By.xpath("//div[contains(@class, 'standard-pane')]//a[contains(text(), 'downbadforjean')]"));
        assertTrue(elements.isEmpty(), "'downbadforjean' is still present on the page, but it should not be.");

    }

}
