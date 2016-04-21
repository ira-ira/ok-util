package com.galkina.ok_util;

import com.google.common.collect.FluentIterable;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.util.stream.Collectors;
import java.util.stream.Stream;


public class OkTool {
    private WebDriver driver;
    private String baseUrl;
    private String profilesInfo = "/Users/infuntis/ok.txt";
    private List<String> alreadyAdded = new ArrayList<>();
    private static Logger logger = Logger.getLogger(OkTool.class.getName());

    public static void main(String... args) throws Exception {
        OkTool tool = new OkTool();
        tool.addPeople();

    }

    private void addPeople() throws Exception {
        driver.manage().window().maximize();
        driver.get(baseUrl);
        // type | id=field_email | 79612821110
        driver.findElement(By.id("field_email")).clear();
        driver.findElement(By.id("field_email")).sendKeys("79612821110");
        // type | id=field_password | SuperIgor456!
        driver.findElement(By.id("field_password")).clear();
        driver.findElement(By.id("field_password")).sendKeys("SuperIgor456!");
        // click | css=form.form > div.form-actions > input.button-pro.form-actions_yes |
        driver.findElement(By.cssSelector("form.form > div.form-actions > input.button-pro.form-actions_yes")).click();
        driver.get(baseUrl + "search");
        driver.get(baseUrl + "search?st.grmode=Groups&st.gender=f&st.fromAge=32&st.till" +
                "Age=52&st.location=%D0%A0%D0%BE%D1%81%D1%82%D0%BE%D0%B2-%D0%BD%D0%B0-%D0%94%D0%BE" +
                "%D0%BD%D1%83&st.country=10414533690&st.city=%D0%A0%D0%BE%D1%81%D1%82%D0%BE%D0%B2-%D0%" +
                "BD%D0%B0-%D0%94%D0%BE%D0%BD%D1%83&st.onSite=on&st.mode=Users&st.posted=set");
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,60250)", "");
        int i = 0;
        while (i < 3) {
            List<String> webElements = FluentIterable.from(driver.findElements(By.className("v1_gs_result_i_w")))
                    .filter((WebElement e) -> {
                        return e.getText().contains("Дружить") && !alreadyAdded.contains(e.findElement(By.className("ellip"))
                                .findElement(By.tagName("a"))
                                .getAttribute("href"));
                    })
                    .transform((WebElement e) -> {
                        return e.findElement(By.className("ellip"))
                                .findElement(By.tagName("a"))
                                .getAttribute("href");
                    })
                    .toList();

            for (String w : webElements) {
                logger.info("Новый потенциальный друг "+w);
            }

            String href = webElements.get(0);
//            WebElement nameDiv = person.findElement(By.className("ellip"));
//            WebElement profileLink = nameDiv.findElement(By.tagName("a"));
//            String href = profileLink.getAttribute("href");

            logger.info("Добавляю в друзья " + href);
            //logger.info(profileLink.getAttribute("href"));
            driver.get(href);
            //добавление в друзья
            WebElement addFriend = driver.findElement(By.linkText("Добавить в друзья"));

            Actions actions = new Actions(driver);

            actions.moveToElement(addFriend).click().perform();
            alreadyAdded.add(href);
            Files.write(Paths.get(profilesInfo), alreadyAdded);
            driver.navigate().back();
            i++;

        }
        // driver.close();

    }


    public OkTool() {
        driver = new FirefoxDriver();
        baseUrl = "http://www.ok.ru/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        try (Stream<String> stream = Files.lines(Paths.get(profilesInfo))) {

            alreadyAdded = stream
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Hi How r u?");
    }
}
