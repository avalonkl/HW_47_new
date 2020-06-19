package core;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;

public class HtmlUnit {
	static WebClient driver;

	public static boolean isElementPresent(HtmlPage page, String by) {
		return page.getElementsById(by).size() == 1;
	}

	public static void setValue(HtmlPage page, String by, String value) {
		if (isElementPresent(page, by) && page.getElementById(by).isDisplayed()) {
			HtmlInput intputBox = (HtmlInput) page.getHtmlElementById(by);
			intputBox.setValueAttribute(value);
		}
	}

	public static String getValue(HtmlPage page, String by) {
		return isElementPresent(page, by) && page.getElementById(by).isDisplayed()
				? page.getElementById(by).getTextContent()
				: "null";
	}

	public static void main(String[] args) throws Exception {

		Logger.getLogger("").setLevel(Level.OFF);

		driver = new WebClient();
		driver.setCssErrorHandler(new SilentCssErrorHandler());
		driver.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
		String[] order = { "", "2", "3", "4", "E" };
		for (String n : order) {
			HtmlPage page = driver.getPage("http://alex.academy/exe/payment/index" + n + ".html");
			Pattern p = Pattern.compile("[0-9]?\\,?[0-9]+\\.[0-9]{2}");
			Matcher m = p.matcher(getValue(page, "id_monthly_payment"));
			m.find();

			double monthly_payment = Double.parseDouble(m.group(0).replaceAll(",", ""));
			double annual_payment = new BigDecimal(monthly_payment * 12).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();
			DecimalFormat df = new DecimalFormat("0.00");
			setValue(page, "id_annual_payment", df.format(annual_payment));
			HtmlPage confirmation_page = page.getElementById("id_validate_button").click();

			System.out.println(getValue(confirmation_page, "id_result"));
		}

	}

}
