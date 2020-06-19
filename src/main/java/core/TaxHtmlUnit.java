package core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;

public class TaxHtmlUnit {
	static WebClient driver;

	public static final String ANSI_RESET = "\u001B[0m";

	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

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

		final long start = System.currentTimeMillis();
		String resultColor;

		driver = new WebClient();
		driver.setCssErrorHandler(new SilentCssErrorHandler());
		driver.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
		String[] order = { "", "2", "3", "4", "E" };

		for (String n : order) {
			HtmlPage page = driver.getPage("http://alex.academy/exe/payment_tax/index" + n + ".html");

			String input = getValue(page, "id_monthly_payment_and_tax");
			System.out.println("String:                  " + input);

			Pattern p = Pattern.compile("[0-9]?\\,?[0-9]+\\.[0-9]{2}");
			Matcher m = p.matcher(input);
			m.find();
			double monthly_payment = Double.parseDouble(m.group());

			m.find();
			double tax = new BigDecimal(monthly_payment / 100 * Double.parseDouble(m.group()))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();

			double annual_payment = new BigDecimal((monthly_payment + tax) * 12).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();
			System.out.println("Annual Payment with Tax: " + annual_payment);

			DecimalFormat df = new DecimalFormat("0.00");
			setValue(page, "id_annual_payment_with_tax", df.format(annual_payment));
			HtmlPage confirmation_page = page.getElementById("id_validate_button").click();
			
			String result = getValue(confirmation_page, "id_result");
			if (result.contains("CORRECT")) resultColor = ANSI_GREEN;
			else resultColor = ANSI_RED;

			System.out.println("Result:                  " + resultColor + result + ANSI_RESET);
		}

		final long finish = System.currentTimeMillis();
		System.out.println("Response time:           " + ANSI_CYAN + (finish - start) / 1000.0 + ANSI_RESET);

	}

}