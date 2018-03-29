/*
TODO: Disallow multi-word defiitions
 */

package com.company;

import javafx.scene.web.WebErrorEvent;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", ".\\WebDrivers\\chromedriver.exe");

        final String START_WORD = "happy"; //Word to start at.
        final String DESTINATION_WORD = "rich"; //Word to search for.
        final int SYN_TOL = 5; //Decide how detached selected synonyms can be when recursively searching.

        int totalSearches = 0;
        List<String> searchedWords = new ArrayList<>();

        //Initialize Headless Chrome
        String[] chromeArgs = new String[1];
        chromeArgs[0] = "--headless";
        ChromeDriver driver = InitializeChromeDriverWithArgs(chromeArgs);

        driver.navigate().to("Http://www.thesaurus.com");
        WebElement searchBox = GetSearchBox(driver, true);
        searchBox.sendKeys(START_WORD);
        searchBox.sendKeys(Keys.RETURN);

        boolean matchNotFound = true;
        do{

            totalSearches += 1;
            List<String> Synonyms = GetSynonyms(driver);

            String currentWord = "";
            if(totalSearches != 1){
                currentWord = GetNewWord(Synonyms, SYN_TOL);
            }
            else{
                currentWord = START_WORD;
            }

            for (int i = 0; i < Synonyms.size(); i++){
                if(DESTINATION_WORD.compareTo(Synonyms.get(i)) == 0){
                    System.out.println(String.format("Completed %s searches, found '%s'", totalSearches, DESTINATION_WORD));
                    matchNotFound = !matchNotFound;
                    break;
                }
            }
            System.out.println(String.format("{Iteration %s} Finished search on word %s, found %s synonyms, no match.", totalSearches, currentWord, Synonyms.size()));
            searchBox = GetSearchBox(driver, false);


            searchBox.sendKeys(currentWord);
            searchBox.sendKeys(Keys.RETURN);
        }while(matchNotFound);

        if(driver != null){
            driver.close();
        }
    }

    private static String GetNewWord(List<String> Synonyms, int Tolerance) {
        Random r = new Random();
        if(Tolerance > Synonyms.size()){
            return Synonyms.get(r.nextInt(Synonyms.size()));
        }
        else {
            return Synonyms.get(r.nextInt(Tolerance));
        }
    }

    private static ChromeDriver InitializeChromeDriverWithArgs(String[] args) {
        ChromeOptions options = new ChromeOptions();
        for (int i = 0; i < args.length; i++){
            options.addArguments(args[i]);
        }
        return new ChromeDriver(options);
    }

    private static WebElement GetSearchBox(ChromeDriver driver, Boolean firstPass){
        WebElement searchBox = null;
        if(firstPass) {
            searchBox = driver.findElement(By.id("searchAreaInputText"));
        }
        else {
            if (searchBox == null) {
                searchBox = driver.findElement(By.className("search-input"));
            }
        }
        return searchBox;
    }

    private static List<String> GetSynonyms(ChromeDriver driver){
        List<String> Synonyms = new ArrayList<>();
        List<WebElement> SynonymElements = driver.findElement(By.className("relevancy-list")).findElements(By.tagName("li"));
        for (int i = 0; i < SynonymElements.size(); i++){
            Synonyms.add(SynonymElements.get(i).getText().replace("\nstar", ""));
        }
        return Synonyms;
    }
}
