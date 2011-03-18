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
		MappingDate mappingDate = new MappingDate(year, month, day);
		String displayText = dateMapping.getFestivalDateMap().get(mappingDate);
		//first check festival in ordinary calendar
		if (displayText != null) {
			mappingDate.setYearText(year.toString() + "年");
			mappingDate.setMonthText(month.toString() + "月");
			mappingDate.setDayText(day.toString() + "日");
			mappingDate.setDisplayText(displayText);
			
			return mappingDate;
		} 
		//then check lunar calendar change the year,month and day into lunar year, month and day
		Integer lunarYear = ; 
		Integer lunarMonth = ;
		Integer lunarDay = ;
		mappingDate = new MappingDate(lunarYear, lunarMonth, lunarDay);
		displayText = dateMapping.getLunarDateMap().get(mappingDate);
		if (displayText != null) {
			mappingDate.setDisplayText(displayText);
			//TODO use third party  lunar API to set yearText, monthText, and dayText
			
			return mappingDate;
		}
		return null;
	}
	
	public static SmartDateFactory getInstance(Context context) {
		if (orderedSmartDateFactory == null) {
			orderedSmartDateFactory = new OrderedSmartDateFactory(context);
		}
		return orderedSmartDateFactory;
	}

}
