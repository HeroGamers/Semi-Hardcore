package dk.fido2603.semihardcore;

import java.util.concurrent.TimeUnit;

public class TimeConverter
{
	// parseStringToMillis() and convert() is from https://stackoverflow.com/a/4015476
	public static long parseStringToMillis(String input) {
	   long result = 0;
	   String number = "";
	   for (int i = 0; i < input.length(); i++) {
	     char c = input.charAt(i);
	     if (Character.isDigit(c)) { 
	       number += c; 
	     } else if (Character.isLetter(c) && !number.isEmpty()) {
	       result += convert(Integer.parseInt(number), c);
	       number = "";
	     }
	   }
	   return result;
	}

	private static long convert(int value, char unit) {
	  switch(unit) {
	    case 'd' : return value * 1000*60*60*24;
	    case 'h' : return value * 1000*60*60;         
	    case 'm' : return value * 1000*60;
	    case 's' : return value * 1000;
	    default: return value * 0;
	  }
	}
	
	// Self-made from here
	public static String parseMillisToUFString(long millis) {
		long banDays = TimeUnit.MILLISECONDS.toDays(millis);
		long banHours = TimeUnit.MILLISECONDS.toHours(millis);
		long banMinutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		
		if (banDays > 0) {
			if ((banHours - TimeUnit.DAYS.toHours(banDays)) > 0) {
				if ((banMinutes - TimeUnit.HOURS.toMinutes(banHours)) > 0) {
					return String.format("%d days, %d hours and %d minutes", banDays, banHours - TimeUnit.DAYS.toHours(banDays), banMinutes - TimeUnit.HOURS.toMinutes(banHours));
				}
				return String.format("%d days, %d hours", banDays, banHours - TimeUnit.DAYS.toHours(banDays));
			}
			return String.format("%d days", banDays);
		}
		if (banHours > 0) {
			if ((banMinutes - TimeUnit.HOURS.toMinutes(banHours)) > 0) {
				return String.format("%d hours and %d minutes", banHours, banMinutes - TimeUnit.HOURS.toMinutes(banHours));
			}
			return String.format("%d hours", banHours);
		}
		return String.format("%d minutes", banMinutes);
	}
	
}