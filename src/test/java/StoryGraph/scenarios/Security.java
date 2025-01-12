package StoryGraph.scenarios;

import StoryGraph.setup.SetupUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Security extends SetupUtils {

    @Test
    public void testForHttps() {
        webDriver.get(BASEURL);
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.startsWith("https"), "Website should be using HTTPS");
    }

    @ParameterizedTest
    @CsvSource({"/reading_challenges/dashboard/user", "/profile/user", "/notifications", "/profile/edit/user", "/preferences/edit/user"})
    public void testProtectedRoutes(String protectedRoute) {
        webDriver.get(BASEURL + protectedRoute);
        String currentUrl = webDriver.getCurrentUrl();
        assertEquals(BASEURL+"/users/sign_in", currentUrl, "Guest should be redirected to the login page");
    }

}
