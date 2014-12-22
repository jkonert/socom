package de.tud.kom.socom.web.client.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.tud.kom.socom.web.client.sharedmodels.SimpleEntry;

/** provides some methods to convert absolute Date or Time objects to relative String representations
 * 
 * @author jkonert
 *
 */
public abstract class DateTimeUtils
{

	public static enum Units
	{		// in future change it to support months and years as well.. by using Date and Calendar objects..
		DAYS(null,1),
		HOURS(DAYS,24),
		MINUTES(HOURS,60),
		SECONDS(MINUTES,60),
		MILLISECONDS(SECONDS, 1000);			
		
		private Units nextUnit;
		private int factor;

		private Units(Units nextUnit, int factor)
		{
			this.nextUnit = nextUnit;
			this.factor = factor;
		}
		
		/** may return NULL if end of units is reached
		 * 
		 * @return
		 */
		public Units getNextUnit() {
			return nextUnit;
		}

		public int getFactor() {
			return factor;
		}
		
		/** returns the value converted to nextUnit
		 * 
		 * @param value
		 * @return
		 */
		public long toNextUnit(long value)
		{
			return (value/getFactor());
		}
		
		/** returns the rest of this current Unit by subtracting all values of nextUnit() from value
		 * 
		 * @param value
		 * @return
		 */
		public long toNextUnitRest(long value)
		{
			return value - (toNextUnit(value)*getFactor());
		}
		
		public static Units getMAXUnit()
		{
			return DAYS;
		}
	}
	private static final Map<Units, String[]> unitsDE = new HashMap<Units, String[]>();
	static
	{		
		unitsDE.put(Units.MILLISECONDS, new String[]{" Millisekunden", " Millisekunde", "ms","ms"});
		unitsDE.put(Units.SECONDS, new String[]{" Sekunden", " Sekunde", " sek", "s"});
		unitsDE.put(Units.MINUTES, new String[]{" Minuten", " Minute",  "min", "m"});
		unitsDE.put(Units.HOURS, new String[]{" Stunden", " Stunde", " Std","h"});
		unitsDE.put(Units.DAYS, new String[]{" Tage", " Tag", " Tage", "d"});
	}
	
	private static final String fewDE = "wenigen";
	private static final String LONGSEPERATOR = ", ";
	private static final String SHORTSEPERATOR = " ";
	
	/** only supports GERMAN output by now..
	 * returns a String encoding the given date with two units relatively to now(). e.g. ("12 Minuten, 1 Sekunde"). 
	 * 
	 * */
	public static final String toStringRelative(Date date)
	{
		//FIXME returns something like: "wenigen Minuten, 21 Sekunden." or "39 Minuten, wenigen Sekunden."
		return toStringRelative(date, new int[] {0,1}, LONGSEPERATOR, true);
	}
	
	/** only supports GERMAN output by now..
	 * returns a String encoding the given date with two units relatively to now(). e.g. ("12 Min, 1 Sek"). 
	 * 
	 * */
	public static String toShortStringRelative(Date date)
	{
		return toStringRelative(date, new int[] {2,2}, LONGSEPERATOR, false);
	}
	
	/** only supports GERMAN output by now..
	 * returns a String encoding the given date with two units relatively to now(). e.g. ("12m 1s"). 
	 * 
	 * */
	public static String toVeryShortStringRelative(Date date)
	{
		return toStringRelative(date, new int[] {3,3}, SHORTSEPERATOR, false);
	}
	
	/**
	 * 
	 * @param date
	 * @param indexesToUse  two numbers as array: first number indicates which index of Unit strings to use for plural forms, the second for single forms (e.g. Milliseconds and Millisecond)
	 * @return
	 */
	private static final String toStringRelative(Date date, int[] indexesToUse, String seperator, boolean allowTexts)
	{
		long diff = (new Date().getTime()-date.getTime()); 
		if (diff < 0) diff *= (-1);
		SimpleEntry<Units,Long> startEntry = new SimpleEntry<Units, Long>(Units.SECONDS, Units.MILLISECONDS.toNextUnit(diff));
		  @SuppressWarnings("unchecked")
		  SimpleEntry<Units, Long>[] result = new SimpleEntry[2];
		  result[0] = startEntry;
		  result = getTwoBiggestUnits(result);
		  
		  StringBuilder sb = new StringBuilder();
		  		  		  
		  if (result[0] != null)
		  {
			  formatResult(result[0], indexesToUse, allowTexts, sb);			 
		  }
		  if (result[1] != null)
		  {
			  sb.append(seperator);
			  formatResult(result[1], indexesToUse, allowTexts, sb);
		  }
		  return sb.toString();
	}

	private static void formatResult(SimpleEntry<Units, Long> simpleEntry, int[] indexesToUse, boolean allowTexts,
		StringBuilder sb) {
		long value = simpleEntry.getValue();
		if (value == 0)
		{
			sb.append(simpleEntry.getValue());
			sb.append(unitsDE.get(simpleEntry.getKey())[indexesToUse[0]]);
		}
		else if (simpleEntry.getValue() == 1)
		{
			sb.append(simpleEntry.getValue());
			sb.append(unitsDE.get(simpleEntry.getKey())[indexesToUse[1]]);
		}
		else if (allowTexts && value < 4)
		{
			sb.append(fewDE);
			sb.append(unitsDE.get(simpleEntry.getKey())[indexesToUse[0]]);
		}
		else
		{
			sb.append(simpleEntry.getValue());
			sb.append(unitsDE.get(simpleEntry.getKey())[indexesToUse[0]]);
		}
		
	}

	private static SimpleEntry<Units, Long>[] getTwoBiggestUnits(SimpleEntry<Units, Long>[] simpleEntries) {
		
		SimpleEntry<Units, Long> e1 = simpleEntries[0];	
		// there is nothing to convert to bigger Unit(s) 
		if (e1 == null || e1.getValue() == 0) return simpleEntries;		
		
		// we reached the max of units or the smaller unit is already zero
		if (simpleEntries[1] != null && simpleEntries[1].getKey().equals(Units.getMAXUnit())) return simpleEntries;

		Units current = e1.getKey();		
		Long nextValue = current.toNextUnit(e1.getValue());			
		
		// it makes no sense to calculate higher units..
		if (nextValue == 0 || current.getNextUnit() == null) return simpleEntries;
		
		SimpleEntry<Units, Long> e2 = new SimpleEntry<Units,Long>(current.getNextUnit(),nextValue);				
		long rest = current.toNextUnitRest(e1.getValue());
		e1.setValue(rest);		
			
		// shift and add
		simpleEntries[1] = e1;
		simpleEntries[0] = e2;
		return getTwoBiggestUnits(simpleEntries);
		
	}

	
}
