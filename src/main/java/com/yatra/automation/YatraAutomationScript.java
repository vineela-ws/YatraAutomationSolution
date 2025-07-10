package com.yatra.automation;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class YatraAutomationScript {

	public static void main(String[] args) throws InterruptedException {
		ChromeOptions chromeOptions= new ChromeOptions();
		chromeOptions.addArguments("--disable-notifications");

		WebDriver wd = new ChromeDriver(chromeOptions);
		WebDriverWait wait = new WebDriverWait(wd,Duration.ofSeconds(20));  //Synchronizing the WebDriver -->ExplicitWait
		
		wd.get("https://www.yatra.com/");
		wd.manage().window().maximize();
		
		closePopup(wait);
		
		clickOnDepartureDate(wait);
		
		WebElement currentMonthCalenderWebElement = selectTheMonthFromCalender(wait,0); //Current Month
		WebElement nextMonthCalenderWebElement =selectTheMonthFromCalender(wait,1); //Current Month
	
		Thread.sleep(3000);
		String lowsetPriceForCurrentMonth = getMeLowestPrice(currentMonthCalenderWebElement);
		String lowsetPriceForNextMonth = getMeLowestPrice(nextMonthCalenderWebElement);
		
		System.out.println(lowsetPriceForCurrentMonth);
		System.out.println(lowsetPriceForNextMonth);
		
		compareTwoMonthsPrices(lowsetPriceForCurrentMonth,lowsetPriceForNextMonth);

	  }


	private static void clickOnDepartureDate(WebDriverWait wait) {
		By departureDateLocator = By.xpath("//div[@aria-label = 'Return Date inputbox'and@role='button']");
		
		WebElement departuredateButton = wait.until(ExpectedConditions.elementToBeClickable(departureDateLocator)); 
		
		departuredateButton.click();
	}


	private static void closePopup(WebDriverWait wait) {
		By popUpLocator = By.xpath("(//div[contains(@class,'style_popup')])[1]");
		try {
		WebElement popUpElement = wait.until(ExpectedConditions.visibilityOfElementLocated(popUpLocator));
		WebElement cross = popUpElement.findElement(By.xpath("(//img[@alt='cross'])[1]"));	
		cross.click();
		}catch(TimeoutException e) {
			System.out.println("Popup not shown on the screen!!!");
		}
	}


	private static String getMeLowestPrice(WebElement MonthWebElement) {
		By priceLocator = By.xpath(".//span[contains(@class, 'custom-day-content')]");
		List<WebElement> julyPriceList = MonthWebElement.findElements(priceLocator);
		
		int lowestPrice = Integer.MAX_VALUE;
		
		WebElement priceElement = null;
		
		for(WebElement price : julyPriceList) {		
			String priceString = price.getText();
			if(priceString.length()>0) {
			priceString = priceString.replace("₹","").replace(",","");
			int priceInt = Integer.parseInt(priceString);
			if(priceInt < lowestPrice) {
				lowestPrice = priceInt;
				priceElement= price;
			}
		  }
		}
		WebElement dateElement = priceElement.findElement(By.xpath(".//../.."));
		String result = dateElement.getAttribute("aria-label")+"....Price is Rs" + lowestPrice;
		return result;
	}
	
		
	public static WebElement selectTheMonthFromCalender(WebDriverWait wait, int index) {
		By calenderMonthsLocator = By.xpath("//div[@class = 'react-datepicker__month-container']");
		List<WebElement> calenderMonthsList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(calenderMonthsLocator));
		
		WebElement monthCalenderWebElement = calenderMonthsList.get(index);  //Current Month
			
		return monthCalenderWebElement;	
	  }
	
	
	public static void compareTwoMonthsPrices(String currentMonthPrice, String nextMonthPrice) {
		int currentMonthRSIndex = currentMonthPrice.indexOf("Rs");
		int nextMonthRSIndex = nextMonthPrice.indexOf("Rs");
		
		System.out.println(currentMonthRSIndex);
		System.out.println(nextMonthRSIndex);
		
		String currentPrice = currentMonthPrice.substring(currentMonthRSIndex + 3);
		String nextPrice = nextMonthPrice.substring(nextMonthRSIndex + 3);
			
		   try {
			   int current = Integer.parseInt(currentPrice);
				int next    = Integer.parseInt(nextPrice);
				
				if(current<next) {
					System.out.println("The lowest price for the two months is "+current);
				}
				else if(current == next) {
					System.out.println("Price is same for both Months! choose whatever you prefer!!");
				}
				else {
					System.out.println("The lowest price for the two months is "+next);
				}
		    }
		   catch (NumberFormatException e) {
		        System.err.println("Error: Invalid input string for number conversion:");
		    }
	   }

	}
