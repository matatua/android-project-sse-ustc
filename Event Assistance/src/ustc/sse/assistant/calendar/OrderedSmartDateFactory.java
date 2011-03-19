package ustc.sse.assistant.calendar;

import ustc.sse.assistant.calendar.data.DateMapping;
import android.content.Context;

public class OrderedSmartDateFactory implements SmartDateFactory {

	private static OrderedSmartDateFactory orderedSmartDateFactory;
	
	private DateMapping dateMapping;
	
	private OrderedSmartDateFactory(Context context) {
		dateMapping = DateMapping.getInstance(context);
	}
	
	@Override
	public SmartDate createSmartDate(Integer year, Integer month, Integer day) {
		MappingDate gregorianDate = new GregorianDate(year, month, day);
		String displayText = dateMapping.getFestivalDateMap().get(mappingDate);
		//first check festival in ordinary calendar
		if (displayText != null) {
			gregorianDate.setYearText(year.toString() + "年");
			gregorianDate.setMonthText(month.toString() + "月");
			gregorianDate.setDayText(day.toString() + "日");
			gregorianDate.setDisplayText(displayText);
			
			return gregorianDate;
		} 
		//then check lunar calendar change the year,month and day into lunar year, month and day
		Integer lunarYear = ; 
		Integer lunarMonth = ;
		Integer lunarDay = ;
		MappingDate lunarDate = new LunarDate(lunarYear, lunarMonth, lunarDay);
		displayText = dateMapping.getLunarDateMap().get(lunarDate);
		lunarDate.setYearText(yearText);
		lunarDate.setMonthText(monthText);
		lunarDate.setDayText(dayText);

		if (displayText != null) {
			lunarDate.setDisplayText(displayText);
			//TODO use third party  lunar API to set yearText, monthText, and dayText
			return lunarDate;
		}
		
		lunarDate.setDisplayText(dayText);
		return lunarDate;
	}
	
	public static SmartDateFactory getInstance(Context context) {
		if (orderedSmartDateFactory == null) {
			orderedSmartDateFactory = new OrderedSmartDateFactory(context);
		}
		return orderedSmartDateFactory;
	}

}
