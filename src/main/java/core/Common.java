package core;

import java.io.Writer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

public class Common {
	
	static WebDriver driver;
	static Writer report;
	static String url;
	static void getWebDriver(String browser) {
		Logger.getLogger("").setLevel(Level.OFF);
		String driverPath = "";

		switch (browser.toLowerCase()) {

		case "chrome": {
			if (getOS().toUpperCase().contains("MAC") || getOS().toUpperCase().contains("LINUX"))
				driverPath = "/usr/local/bin/chromedriver";
			else if (getOS().toUpperCase().contains("WINDOWS"))
				driverPath = "c:\\windows\\chromedriver.exe";
			else
				throw new IllegalArgumentException("Browser dosn't exist for this OS");
			System.setProperty("webdriver.chrome.driver", driverPath);
			System.setProperty("webdriver.chrome.silentOutput", "true"); // Chrome
			ChromeOptions option = new ChromeOptions(); // Chrome
			option.addArguments("disable-infobars"); // Chrome
			option.addArguments("--disable-notifications"); // Chrome

			driver = new ChromeDriver();
			break;
		}

		case "edge": {
			if (getOS().toUpperCase().contains("MAC"))
				driverPath = "/usr/local/bin/msedgedriver.sh";
			else if (getOS().toUpperCase().contains("WINDOWS"))
				driverPath = "c:\\windows\\msedgedriver.exe";
			else
				throw new IllegalArgumentException("Browser dosn't exist for this OS");
			System.setProperty("webdriver.edge.driver", driverPath);

			driver = new EdgeDriver();
			break;
		}

		case "firefox": {
			if (getOS().toUpperCase().contains("MAC") || getOS().toUpperCase().contains("LINUX"))
				driverPath = "/usr/local/bin/geckodriver.sh";
			else if (getOS().toUpperCase().contains("WINDOWS"))
				driverPath = "c:\\windows\\geckodriver.exe";
			else
				throw new IllegalArgumentException("Browser dosn't exist for this OS");
			System.setProperty("webdriver.gecko.driver", driverPath);

			driver = new FirefoxDriver();
			break;
		}

		case "safari": {
			if (!getOS().toUpperCase().contains("MAC"))
				throw new IllegalArgumentException("Browser dosn't exist for this OS");

			driver = new SafariDriver();
			break;
		}

		default:
			throw new WebDriverException("Unknown WebDriver");

		}
	}

	public static String getOS() {
		return System.getProperty("os.name").toUpperCase();
	}

	public static void open(String browser, String url) {
		getWebDriver(browser);
		driver.manage().window().maximize();
		driver.get(url);
	}

	public static boolean isElementPresent(By by) {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		if (driver.findElements(by).size() == 1) {
			return true;
		} else
			return false;
	}

	public static void setValue(By by, String value) {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		if (isElementPresent(by))
			driver.findElement(by).sendKeys(value);
	}

	public static String getValue(By by) {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		if (isElementPresent(by) && driver.findElement(by).getTagName().equalsIgnoreCase("input"))
			return driver.findElement(by).getAttribute("value").toString().trim();

		else if (isElementPresent(by) && driver.findElement(by).getTagName().equalsIgnoreCase("span"))
			return driver.findElement(by).getText().trim();
		else
			return "null";
	}

	public static void submit(By by) {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		if (isElementPresent(by))
			driver.findElement(by).submit();
	}
	
	public static void quit() {
		driver.quit();
	}

}
