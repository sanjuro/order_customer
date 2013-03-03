package com.vosto.customer.utils;

import org.joda.money.Money;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

public class MoneyUtils {
	
	public static String getRandString(Money money){
		MoneyFormatterBuilder builder = new MoneyFormatterBuilder();
		builder.appendLiteral("R ");
		builder.appendAmount();
		MoneyFormatter formatter = builder.toFormatter();
		return formatter.print(money);
	}
	
	public static String getAmountStringWithoutSymbol(Money money){
		MoneyFormatterBuilder builder = new MoneyFormatterBuilder();
		builder.appendAmount();
		MoneyFormatter formatter = builder.toFormatter();
		return formatter.print(money);
	}
	
	
}