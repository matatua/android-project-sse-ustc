package ustc.sse.assistant.calendar.utils;

/**
 * 
 * @author 李健
 *
 */
public interface SmartDate {
	/**
	 * the gregorian number of year
	 * @return
	 */
	Integer getGregorianYear();
	
	/**
	 * 
	 * @return the gregorian number of month
	 */
	Integer getGregorianMonth();
	
	/**
	 * 
	 * @return the gregorian number of day
	 */
	Integer getGregorianDay();
	
	Integer getLunarYear();
	
	Integer getLunarMonth();
	
	Integer getLunarDay();
	/**
	 * 
	 * @return typical text represent the month
	 */
	String getLunarMonthText();
	
	/**
	 * 
	 * @return typical text represent the year
	 */
	String getLunarYearText();
	
	/**
	 * 
	 * @return typical text represent the day
	 */
	String getLunarDayText();
	
	/**
	 * 
	 * @return text to be shown in a calendar
	 */
	String getDisplayText();
	
	int getLunarColorResId();
	
	int getGregorianColorResId();
}
