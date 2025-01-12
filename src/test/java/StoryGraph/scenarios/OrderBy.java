package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.By.className;

public class OrderBy extends SetupUtils {

    // order by

    @Test
    public void testOrderByPagesHighToLow() throws InterruptedException {

        webDriver.get(BASEURL+"/browse?sort_order=Last+updated");
        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();
        } catch (TimeoutException e) { /* Cookie banner was not visible, proceed without clicking*/}

        WebElement lastUpdatedButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/button")));
        lastUpdatedButton.click();

        WebElement dropdownOption = webDriverWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/div/input[3]")));
        dropdownOption.click();

        Thread.sleep(3000);

        WebElement firstResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/p")));
        WebElement secondResultElement = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[2]/div[1]/div[2]/div[2]/div[1]/p")));

        int firstValue = Integer.parseInt(firstResultElement.getText().split(" ")[0]);
        int secondValue = Integer.parseInt(secondResultElement.getText().split(" ")[0]);

        System.out.println("First value: " + firstValue);
        System.out.println("Second value: " + secondValue);

        assertTrue(firstValue >= secondValue, "Results should be sorted by 'Last Updated' in descending order");
    }

    @Test
    public void testCompareDatesFromLatestFirstFantasyOption() throws InterruptedException {

        webDriver.get(BASEURL+"/browse?sort_order=Last+updated");
        webDriver.manage().window().maximize();
        try {
            WebElement cookieCloseButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("close-cookies-popup")));
            cookieCloseButton.click();
            System.out.println("Cookie banner closed.");
        } catch (TimeoutException e) {
            System.out.println("Cookie banner not visible, proceeding.");
        }

        Thread.sleep(1000);
        WebElement filterButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[2]/span")));
        filterButton.click();
        Thread.sleep(2000);
        scroll(500);

        WebElement genreDropdown = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/span[1]/span[1]/span/ul")));
        genreDropdown.click();

        WebElement fantasyOption = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[text()='Fantasy']")));
        fantasyOption.click();
        Thread.sleep(1000);

        WebElement randomClickButton = webDriver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/p[5]"));
        randomClickButton.click();
        scroll(600);
        Thread.sleep(1000);

        WebElement filterButtonApply = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[1]/div[3]/div[5]/div[2]/input")));
        filterButtonApply.click();
        Thread.sleep(4000);

        WebElement lastUpdatedButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/button")));
        lastUpdatedButton.click();

        WebElement dropdownOption = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div/form/div[2]/div/div/input[5]")));
        dropdownOption.click();
        Thread.sleep(1000);

        scroll(700);

        WebElement firstPublicationDateReveal = webDriverWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[1]/div[1]/div[2]/div[2]/div[1]/p/span/span[2]")));
        firstPublicationDateReveal.click();
        Thread.sleep(1000);

        WebElement infoDiv = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(className("edition-info")));
        //System.out.println("infoDiv: " + infoDiv);

        int pCount = infoDiv.findElements(By.tagName("p")).size();
        WebElement publicationDateElement = infoDiv.findElements(By.tagName("p")).get(pCount - 1);
        String dateTextFirst = publicationDateElement.getText().trim();
        String[] datePartsFirst = dateTextFirst.split(" ");

        int dayFirst = Integer.parseInt(datePartsFirst[2]);
        int monthFirst = getMonthAsNumber(datePartsFirst[3]);
        int yearFirst = Integer.parseInt(datePartsFirst[4]);

        /*System.out.println("Date text: " + dateTextFirst);
        System.out.println("Day: " + dayFirst);
        System.out.println("Month: " + monthFirst);
        System.out.println("Year: " + yearFirst);*/

        Thread.sleep(1000);
        WebElement secondPublicationDateReveal = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/span/div[2]/div[1]/div[2]/div[2]/div[1]/p/span/span[2]")));
        secondPublicationDateReveal.click();
        Thread.sleep(1000);

        List<WebElement> infoDivs = webDriver.findElements(className("edition-info"));
        WebElement infoDiv1 = infoDivs.get(2);
        int pCount1 = infoDiv1.findElements(By.tagName("p")).size();

        WebElement publicationDateElement1 = infoDiv1.findElements(By.tagName("p")).get(pCount1 - 1);
        String dateTextSecond = publicationDateElement1.getText().trim();
        String[] datePartsSecond = dateTextSecond.split(" ");

        int daySecond = Integer.parseInt(datePartsSecond[2]);
        int monthSecond = getMonthAsNumber(datePartsSecond[3]);
        int yearSecond = Integer.parseInt(datePartsSecond[4]);

        /*System.out.println("Date text: " + dateTextSecond);
        System.out.println("Day: " + daySecond);
        System.out.println("Month: " + monthSecond);
        System.out.println("Year: " + yearSecond);*/

        boolean isDateFirstGreater = compareDates(dayFirst, monthFirst, yearFirst, daySecond, monthSecond, yearSecond);
        assertTrue(isDateFirstGreater, "The first publication's date should be more recent than the second publication's date");
    }


}
