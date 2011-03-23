package ustc.sse.assistant.calendar.utils;

/**
 * 
 * @author 李健
 *
 */
public interface SmartDate {
	/**
	 * the number of year
	 * @return
	 */
	Integer getOriginalYear();
	
	/**
	 * 
	 * @return the number of month
	 */
	Integer getOriginalMonth();
	
	/**
	 * 
	 * @return the number of day
	 */
	Integer getOriginalDay();
	
	/**
	 * 
	 * @return typical text represent the month
	 */
	String getMonthText();
	
	/**
	 * 
	 * @return typical text represent the year
	 */
	String getYearText();
	
	/**
	 * 
	 * @return typical text represent the day
	 */
	String getDayText();
	
	/**
	 * 
	 * @return text to be shown in a calendar
	 */
	String getDisplayText();
}
