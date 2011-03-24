package ustc.sse.assistant.calendar.utils;

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
	
	protected String displayText;
	protected Integer lunarColorResId;
	protected Integer gregorianColorResId;
	
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
	public AbstractDate(Integer gregorianYear, Integer gregorianMonth, Integer gregorianDay, Integer lunarYear, Integer lunarMonth, Integer lunarDay) {
		super();
		this.lunarYear = lunarYear;
		this.lunarMonth = lunarMonth;
		this.lunarDay = lunarDay;		
		this.gregorianMonth = gregorianMonth;
		this.gregorianYear = gregorianYear;		
		this.gregorianDay = gregorianDay;
	}
	

	public String getLunarYearText() {
		return lunarYearText;
	}


	public void setLunarYearText(String lunarYearText) {
		this.lunarYearText = lunarYearText;
	}


	public String getLunarMonthText() {
		return lunarMonthText;
	}


	public void setLunarMonthText(String lunarMonthText) {
		this.lunarMonthText = lunarMonthText;
	}


	public String getLunarDayText() {
		return lunarDayText;
	}


	public void setLunarDayText(String lunarDayText) {
		this.lunarDayText = lunarDayText;
	}


	public String getDisplayText() {
		return displayText;
	}


	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}


	public Integer getLunarColorResId() {
		return lunarColorResId;
	}


	public void setLunarColorResId(Integer lunarColorResId) {
		this.lunarColorResId = lunarColorResId;
	}


	public Integer getGregorianColorResId() {
		return gregorianColorResId;
	}


	public void setGregorianColorResId(Integer gregorianColorResId) {
		this.gregorianColorResId = gregorianColorResId;
	}


	public Integer getLunarYear() {
		return lunarYear;
	}


	public Integer getLunarMonth() {
		return lunarMonth;
	}


	public Integer getLunarDay() {
		return lunarDay;
	}


	public Integer getGregorianYear() {
		return gregorianYear;
	}


	public Integer getGregorianMonth() {
		return gregorianMonth;
	}


	public Integer getGregorianDay() {
		return gregorianDay;
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
