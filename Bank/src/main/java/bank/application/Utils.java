package bank.application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	public static Date parseDate(String date) throws ParseException {
		return new SimpleDateFormat(WebApplication.dateFormat).parse(date);
	}

	public static String formatAccountNumber(String accountNumber) {
		StringBuilder str = new StringBuilder();
		if (accountNumber.length() > 16) {
			accountNumber = accountNumber.substring(0, 16);
		} else {
			for (int i = 0; i < 16 - accountNumber.length(); i++) {
				str.append("0");
			}
		}
		str.append(accountNumber);
		int pos = 0;
		for (int i = 1; i < str.length(); i++) {
			if (str.length() == str.length() + 3) {
				break;
			} else if (i % 4 == 0) {
				str.insert(i + pos, " ");
				pos++;
			}
		}
		return str.toString();
	}

	public static String formatShortAccountNumber(String accountNumber) {
		StringBuilder str = new StringBuilder();
		str.append("**");
		if (accountNumber.length() < 4) {
			for (int i = 0; i < 4 - accountNumber.length(); i++) {
				str.append("0");
			}
		}
		str.append(accountNumber);
		return str.toString();
	}
}
