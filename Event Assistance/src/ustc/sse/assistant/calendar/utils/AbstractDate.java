package ustc.sse.assistant.calendar.utils;

import android.R;

public class AbstractDate implements SmartDate {
	protected Integer year;
	protected Integer month;
	protected Integer day;
	
	protected String yearText;
	protected String monthText;
	protected String dayText;
	
	private String displayText;
	private int colorResId;
	
	
	/**
	 * Caution:
	 * Create from OrderdSmartDateFactory, never use this constructor
	 * @param year
	 * @param month
	 * @param day
	 */
	public AbstractDate(Integer year, Integer month, Integer day) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		
		yearText = year.toString();
		monthText = month.toString();
		dayText = day.toString();
		displayText = yearText + "-" + monthText + "-" + dayText;
	}

	@Override
	public Integer getOriginalYear() {
		return year;
	}

	@Override
	public Integer getOriginalMonth() {	
		return month;
	}

	@Override
	public Integer getOriginalDay() {
		return day;
	}

	@Override
	public String getMonthText() {
		return monthText;
	}

	@Override
	public String getYearText() {
		return yearText;
	}

	@Override
	public String getDayText() {
		return dayText;
	}

	@Override
	public String getDisplayText() {
		return displayText;
	}

	public void setYearText(String yearText) {
		this.yearText = yearText;
	}

	public void setMonthText(String monthText) {
		this.monthText = monthText;
	}

	public void setDayText(String dayText) {
		this.dayText = dayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
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
		if (day == null) {
			if (other.day != null)
				return false;
		} else if (!day.equals(other.day))
			return false;
		if (month == null) {
			if (other.month != null)
				return false;
		} else if (!month.equals(other.month))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}

	@Override
	public int getColorResId() {
		return R.color.primary_text_light;
	}

	/**
	 * @param colorResId the colorResId to set
	 */
	public void setColorResId(int colorResId) {
		this.colorResId = colorResId;
	}

	
}
