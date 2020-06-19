package core;

import java.math.*;
import java.text.DecimalFormat;
import java.util.regex.*;

import org.openqa.selenium.By;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;

public class PaymentWithTax {
	static WebClient driverHU;

	public static final String ANSI_RESET = "\u001B[0m";

	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static void main(String[] args) throws Exception {
		String[] browsers = { "Firefox", "Chrome", "Edge", "Safari", "HtmlUnit" };
		String[] order = { "", "2", "3", "4", "E" };

		for (String browser : browsers) {
			final long start = System.currentTimeMillis();
			String resultColor;

			System.out.println("Browser: " + ANSI_PURPLE + browser + ANSI_RESET);

			if (browser == "HtmlUnit") {
				driverHU = new WebClient();
				driverHU.setCssErrorHandler(new SilentCssErrorHandler());
				driverHU.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());

				for (String n : order) {
					HtmlPage page = driverHU.getPage("http://alex.academy/exe/payment_tax/index" + n + ".html");

					String input = Common.getValue(page, "id_monthly_payment_and_tax");
					System.out.println("String:                  " + input);

					Pattern p = Pattern.compile("[0-9]?\\,?[0-9]+\\.[0-9]{2}");
					Matcher m = p.matcher(input);
					m.find();
					double monthly_payment = Double.parseDouble(m.group());

					m.find();
					double tax = new BigDecimal(monthly_payment / 100 * Double.parseDouble(m.group()))
							.setScale(2, RoundingMode.HALF_UP).doubleValue();

					double annual_payment = new BigDecimal((monthly_payment + tax) * 12)
							.setScale(2, RoundingMode.HALF_UP).doubleValue();
					System.out.println("Annual Payment with Tax: " + annual_payment);

					DecimalFormat df = new DecimalFormat("0.00");
					Common.setValue(page, "id_annual_payment_with_tax", df.format(annual_payment));
					HtmlPage confirmation_page = page.getElementById("id_validate_button").click();

					String result = Common.getValue(confirmation_page, "id_result");
					if (result.contains("CORRECT"))
						resultColor = ANSI_GREEN;
					else
						resultColor = ANSI_RED;

					System.out.println("Result:                  " + resultColor + result + ANSI_RESET);
				}
			} else {
				for (String n : order) {

					try {
						Common.open(browser, "http://alex.academy/exe/payment_tax/index" + n + ".html");
					} catch (IllegalArgumentException e) {
						System.out.println(ANSI_RED + "Browser dosn't exist for this OS" + ANSI_RESET);
						break;
					}

					String input = Common.getValue(By.id("id_monthly_payment_and_tax"));
					System.out.println("String:                  " + input);

					Pattern p = Pattern.compile("[0-9]+\\.[0-9]{2}");
					Matcher m = p.matcher(input);

					m.find();
					double monthly_payment = Double.parseDouble(m.group(0));

					m.find();
					double tax = new BigDecimal(monthly_payment / 100 * Double.parseDouble(m.group(0)))
							.setScale(2, RoundingMode.HALF_UP).doubleValue();

					double annual_payment = new BigDecimal((monthly_payment + tax) * 12)
							.setScale(2, RoundingMode.HALF_UP).doubleValue();
					System.out.println("Annual Payment with Tax: " + annual_payment);

					DecimalFormat df = new DecimalFormat("0.00");
					Common.setValue(By.id("id_annual_payment_with_tax"), df.format(annual_payment));

					Common.submit(By.id("id_validate_button"));

					String result = Common.getValue(By.id("id_result"));
					if (result.contains("CORRECT"))
						resultColor = ANSI_GREEN;
					else
						resultColor = ANSI_RED;

					System.out.println("Result:                  " + resultColor + result + ANSI_RESET);

					Common.quit();
				}
			}
			final long finish = System.currentTimeMillis();
			System.out.println("Response time:           " + ANSI_CYAN + (finish - start) / 1000.0 + ANSI_RESET);
		}

	}

}
