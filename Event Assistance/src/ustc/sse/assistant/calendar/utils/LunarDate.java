/**
 * 
 */
package ustc.sse.assistant.calendar.utils;

/**
 * Caution:
 * This represent lunar date, so it's year, month, day variable is lunar 
 * date not Gregorian date.
 * @author 李健
 *
 */
public class LunarDate extends AbstractDate {

	public LunarDate(Integer gregorianYear, Integer gregorianMonth, Integer gregorianDay, Integer lunarDay, Integer lunarMonth, Integer lunarYear) {
		super(gregorianYear, gregorianMonth, gregorianDay, lunarDay, lunarMonth, lunarYear);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lunarDay == null) ? 0 : lunarDay.hashCode());
		result = prime * result
				+ ((lunarMonth == null) ? 0 : lunarMonth.hashCode());
		result = prime * result
				+ ((lunarYear == null) ? 0 : lunarYear.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		AbstractDate other = (AbstractDate) obj;
		if (lunarDay == null) {
			if (other.lunarDay != null)
				return false;
		} else if (!lunarDay.equals(other.lunarDay))
			return false;
		if (lunarMonth == null) {
			if (other.lunarMonth != null)
				return false;
		} else if (!lunarMonth.equals(other.lunarMonth))
			return false;
		if (lunarYear == null) {
			if (other.lunarYear != null)
				return false;
		} else if (!lunarYear.equals(other.lunarYear))
			return false;
		return true;
	}


}
