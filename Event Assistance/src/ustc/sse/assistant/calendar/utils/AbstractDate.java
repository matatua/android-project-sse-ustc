package ustc.sse.assistant.calendar.utils;

import android.R;

public class AbstractDate implements SmartDate {
	protected Integer lunarYear;
	protected Integer lunarMonth;
	protected Integer lunarDay;
	
	protected Integer gregorianYear;
	protected Integer gregorianMonth;
	protected Integer gregorianDay;
	
	protected String lunarYearText;
	protected String lunarMonthText;
	protected String lunarDayText;
	
	private String displayText;
	private int lunarColorResId;
	private int gregorianColorResId;
	
	/**
	 *  Caution:
	 *  Create from OrderdSmartDateFactory, never use this constructor
	 * @param gregorianYear
	 * @param gregorianMonth
	 * @param gregorianDay
	 * @param lunarDay
	 * @param lunarMonth
	 * @param lunarYear
	 */
	public AbstractDate(Integer gregorianYear, Integer gregorianMonth, Integer gregorianDay, Integer lunarDay, Integer lunarMonth, Integer lunarYear) {
		super();
		this.lunarYear = lunarYear;
		this.lunarMonth = lunarMonth;
		this.lunarDay = lunarDay;
		this.gregorianDay = gregorianDay;
		this.gregorianMonth = gregorianMonth;
		this.gregorianYear = gregorianYear;
		
		lunarYearText = lunarYear.toString();
		lunarMonthText = lunarMonth.toString();
		lunarDayText = lunarDay.toString();
		displayText = lunarYearText + "-" + lunarMonthText + "-" + lunarDayText;
	}


	@Override
	public String getDisplayText() {
		return displayText;
	}

	public void setLunarYearText(String lunarYearText) {
		this.lunarYearText = lunarYearText;
	}

	public void setLunarMonthText(String lunarMonthText) {
		this.lunarMonthText = lunarMonthText;
	}

	public void setDayText(String lunarDayText) {
		this.lunarDayText = lunarDayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	/**
	 * @param colorResId the colorResId to set
	 */
	public void setLunarColorResId(int colorResId) {
		this.lunarColorResId = colorResId;
	}

	@Override
	public Integer getGregorianYear() {
		return gregorianYear;
	}

	@Override
	public Integer getGregorianMonth() {
		return gregorianMonth;
	}

	@Override
	public Integer getGregorianDay() {
		return gregorianDay;
	}

	@Override
	public Integer getLunarYear() {
		return lunarYear;
	}

	@Override
	public Integer getLunarMonth() {
		return lunarYear;
	}

	@Override
	public Integer getLunarDay() {
		return lunarDay;
	}

	@Override
	public String getLunarMonthText() {
		return lunarMonthText;
	}

	@Override
	public String getLunarYearText() {
		return lunarYearText;
	}

	@Override
	public String getLunarDayText() {
		return lunarDayText;
	}

	@Override
	public int getLunarColorResId() {
		return R.color.black;
	}

	@Override
	public int getGregorianColorResId() {
		return R.color.black;
	}


	/**
	 * @param gregorianColorResId the gregorianColorResId to set
	 */
	public void setGregorianColorResId(int gregorianColorResId) {
		this.gregorianColorResId = gregorianColorResId;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((gregorianDay == null) ? 0 : gregorianDay.hashCode());
		result = prime * result
				+ ((gregorianMonth == null) ? 0 : gregorianMonth.hashCode());
		result = prime * result
				+ ((gregorianYear == null) ? 0 : gregorianYear.hashCode());
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
		if (gregorianDay == null) {
			if (other.gregorianDay != null)
				return false;
		} else if (!gregorianDay.equals(other.gregorianDay))
			return false;
		if (gregorianMonth == null) {
			if (other.gregorianMonth != null)
				return false;
		} else if (!gregorianMonth.equals(other.gregorianMonth))
			return false;
		if (gregorianYear == null) {
			if (other.gregorianYear != null)
				return false;
		} else if (!gregorianYear.equals(other.gregorianYear))
			return false;
		return true;
	}

	
}
