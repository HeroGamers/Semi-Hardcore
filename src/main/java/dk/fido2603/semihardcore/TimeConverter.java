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
		String d = "days";
		String h = "hours";
		String m = "minutes";
		
		if (banDays > 0) {
			// For the grammar <3
			if (banDays == 1) {
				d = "day";
			}
			if ((banHours - TimeUnit.DAYS.toHours(banDays)) > 0) {
				// For the grammar <3
				if ((banHours - TimeUnit.DAYS.toHours(banDays)) == 1) {
					h = "hour";
				}
				if ((banMinutes - TimeUnit.HOURS.toMinutes(banHours)) > 0) {
					// For the grammar <3
					if ((banMinutes - TimeUnit.HOURS.toMinutes(banHours)) == 1) {
						m = "minute";
					}
					// Send the return string
					return String.format("%d %s, %d %s and %d %s", banDays, d, banHours - TimeUnit.DAYS.toHours(banDays), h, banMinutes - TimeUnit.HOURS.toMinutes(banHours), m);
				}
				// Send the return string
				return String.format("%d %s, %d %s", banDays, d, banHours - TimeUnit.DAYS.toHours(banDays), h);
			}
			// Send the return string
			return String.format("%d %s", banDays, d);
		}
		if (banHours > 0) {
			// For the grammar <3
			if ((banHours - TimeUnit.DAYS.toHours(banDays)) == 1) {
				h = "hour";
			}
			if ((banMinutes - TimeUnit.HOURS.toMinutes(banHours)) > 0) {
				// For the grammar <3
				if ((banMinutes - TimeUnit.HOURS.toMinutes(banHours)) == 1) {
					m = "minute";
				}
				// Send the return string
				return String.format("%d %s and %d %s", banHours, h, banMinutes - TimeUnit.HOURS.toMinutes(banHours), m);
			}
			// Send the return string
			return String.format("%d %s", banHours, h);
		}
		// For the grammar <3
		if ((banMinutes - TimeUnit.HOURS.toMinutes(banHours)) == 1) {
			m = "minute";
		}
		// Send the return string
		return String.format("%d %s", banMinutes, m);
	}
	
}