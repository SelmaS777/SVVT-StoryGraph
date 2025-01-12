package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChangeSettings extends SetupUtils {

    @Test
    public void testToggleVisibility() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        webDriver.get(BASEURL+"/profile/edit/"+USERNAMES[0]);

        WebElement visibilityDropdown = webDriver.findElement(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/div[6]/select"));
        Select dropdown = new Select(visibilityDropdown);
        dropdown.selectByVisibleText("Private");

        scroll(300);
        WebElement saveButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/button"));
        saveButton.click();
        Thread.sleep(1000);

        webDriver.get(BASEURL+"/profile/edit/"+USERNAMES[0]);
        scroll(300);

        visibilityDropdown = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[3]/div/form/div[6]/select"));
        dropdown = new Select(visibilityDropdown);
        String selectedOption = dropdown.getFirstSelectedOption().getText();

        assertEquals("Private", selectedOption, "The visibility option was not updated correctly.");
    }

    @Test
    public void testMultiPreferences() throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);

        webDriver.get(BASEURL+"/preferences/edit/" + USERNAMES[0]);

        Select timeZone = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_time_zone"))));
        timeZone.selectByValue("Hawaii");
        String expectedTimeZone = "(GMT-10:00) Hawaii";
        scroll(500);

        WebElement languagesClick = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[3]/form/div/div[1]/div[5]/span/span[1]/span/ul")));
        languagesClick.click();
        WebElement arabicOption = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Arabic']")));
        arabicOption.click();

        Select following = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_friends_setting"))));
        following.selectByValue("anybody");
        String expectedFriendsSetting = "Anybody";

        Select buddyReads = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_buddy_reads_setting"))));
        buddyReads.selectByValue("anybody");
        String expectedBuddyReads = "Anybody";

        Select ownedBooks = new Select(webDriver.findElement(By.id("user_owned_books_visibility_setting")));
        ownedBooks.selectByValue("friends_and_following");
        String expectedOwnedBooksVisibility = "Friends & People I follow";
        Thread.sleep(2000);

        WebElement updateButton =webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"edit_user_c9b1edd8-284d-4d28-adaf-f8a1bddbad17\"]/div/div[2]/button")));
        updateButton.click();
        Thread.sleep(1000);

        webDriver.get(BASEURL+"/preferences/edit/" + USERNAMES[0]);
        Thread.sleep(2000);

        Select actualTimeZone = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_time_zone"))));
        String actualTimeZoneValue = actualTimeZone.getFirstSelectedOption().getText();
        assertEquals(expectedTimeZone, actualTimeZoneValue);

        WebElement selectedLanguage = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[text()='Arabic']")));
        assertNotNull(selectedLanguage);
        scroll(1800);

        Select actualFriendsSetting = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_friends_setting"))));
        String actualFriendsValue = actualFriendsSetting.getFirstSelectedOption().getText();
        assertEquals(expectedFriendsSetting, actualFriendsValue);

        Select actualBuddyReads = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_buddy_reads_setting"))));
        String actualBuddyReadsValue = actualBuddyReads.getFirstSelectedOption().getText();
        assertEquals(expectedBuddyReads, actualBuddyReadsValue);

        Select actualOwnedBooksVisibility = new Select(webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("user_owned_books_visibility_setting"))));
        String actualOwnedBooksValue = actualOwnedBooksVisibility.getFirstSelectedOption().getText();
        assertEquals(expectedOwnedBooksVisibility, actualOwnedBooksValue);
    }


}
