/**
 * 
 */
package ustc.sse.assistant.calendar;

/**
 * Caution:
 * This represent lunar date, so it's year, month, day variable is lunar 
 * date not Gregorian date.
 * @author 李健
 *
 */
public class LunarDate extends MappingDate {

	public LunarDate(Integer year, Integer month, Integer day) {
		super(year, month, day);
	}

}
