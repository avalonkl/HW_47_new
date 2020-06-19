package core;

import java.math.*;
import java.text.DecimalFormat;
import java.util.regex.*;

import org.openqa.selenium.By;

public class Payment {

	public static void main(String[] args) {
		String[] order = { "", "2", "3", "4", "E" };
		for (String n : order) {

			Common.open("Edge", "http://alex.academy/exe/payment/index" + n + ".html");

			Pattern p = Pattern.compile("[0-9]?\\,?[0-9]+\\.[0-9]{2}");
			Matcher m = p.matcher(Common.getValue(By.id("id_monthly_payment")));
			m.find();

			double monthly_payment = Double.parseDouble(m.group(0).replaceAll(",", ""));
			double annual_payment = new BigDecimal(monthly_payment * 12).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();
			DecimalFormat df = new DecimalFormat("0.00");

			Common.setValue(By.id("id_annual_payment"), df.format(annual_payment));
			Common.submit(By.id("id_validate_button"));
			System.out.println(Common.getValue(By.id("id_result")));

			Common.quit();
		}

	}

}
