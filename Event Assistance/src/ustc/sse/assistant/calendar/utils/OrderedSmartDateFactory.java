package ustc.sse.assistant.calendar.utils;

import java.util.Map;

import ustc.sse.assistant.calendar.data.DateMapping;
import ustc.sse.assistant.calendar.data.Lunar;

public class OrderedSmartDateFactory implements SmartDateFactory {

	private static OrderedSmartDateFactory orderedSmartDateFactory;
	
	private DateMapping dateMapping;
	
	private OrderedSmartDateFactory() {
		dateMapping = DateMapping.getInstance();
	}
	
	@Override
	public SmartDate createSmartDate(Integer year, Integer month, Integer day) {
		MappingDate gregorianDate = new GregorianDate(year, month, day);
		String displayText = dateMapping.getFestivalDateMap().get(gregorianDate);
		//first check festival in ordinary calendar
		if (displayText != null) {
			gregorianDate.setYearText(year.toString() + "年");
			gregorianDate.setMonthText(month.toString() + "月");
			gregorianDate.setDayText(day.toString() + "日");
			gregorianDate.setDisplayText(displayText);
			
			return gregorianDate;
		} 
		
		//then check lunar calendar change the year,month and day into lunar year, month and day
		Lunar.getLunar(year, month, day);
		Integer lunarYear = Lunar.getYear(); 
		Integer lunarMonth = Lunar.getMonth();
		Integer lunarDay = Lunar.getDay();
		String yearText;
		String monthText;
		String dayText;
		MappingDate lunarDate = new LunarDate(lunarYear, lunarMonth, lunarDay);

		yearText = Lunar.getLunarYear() + "年";
		monthText = Lunar.getLunarMonth() + "月";
		dayText = Lunar.getLunarDay();

		if(Lunar.getIsLeap()){
			monthText = "闰" + Lunar.getLunarMonth() + "月";
		}
		
		else {
			Map<MappingDate, String> map = dateMapping.getLunarDateMap();
			displayText = map.get(lunarDate);
		}
		
		lunarDate.setYearText(yearText);
		lunarDate.setMonthText(monthText);
		lunarDate.setDayText(dayText);
		lunarDate.setDisplayText(dayText);

		if (displayText != null) {
			lunarDate.setDisplayText(displayText);
			//TODO use third party  lunar API to set yearText, monthText, and dayText
			return lunarDate;
		}
	
		if(lunarDay == 1){
			lunarDate.setDisplayText(monthText);	
		}
				
		return lunarDate;
	}
	
	public static SmartDateFactory getInstance() {
		if (orderedSmartDateFactory == null) {
			orderedSmartDateFactory = new OrderedSmartDateFactory();
		}
		return orderedSmartDateFactory;
	}

}
