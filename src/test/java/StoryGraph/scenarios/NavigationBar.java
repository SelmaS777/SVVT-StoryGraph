package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NavigationBar extends SetupUtils {

    @ParameterizedTest
    @CsvSource({
            "/browse?sort_order=Last+updated, //*[@id=\"navbar\"]/div[1]/div/div[1]/div[2]/a[1]",
            "/stats/multipurpose?year=2025, //*[@id=\"navbar\"]/div[1]/div/div[1]/div[2]/a[2]",
            "/reading_challenges/dashboard/multipurpose, //*[@id=\"navbar\"]/div[1]/div/div[1]/div[2]/a[3]",
            "/community, //*[@id=\"navbar\"]/div[1]/div/div[1]/div[2]/a[4]",
            "/giveaways, //*[@id=\"navbar\"]/div[1]/div/div[1]/div[2]/a[5]"
    })

    public void testNavBar(String route, String xpath) throws InterruptedException {

        login(EMAIL, PASSWORDS[0]);
        WebElement button = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        button.click();
        Thread.sleep(2000);
        assertEquals(BASEURL + route, webDriver.getCurrentUrl());
        logout();

    }
}
