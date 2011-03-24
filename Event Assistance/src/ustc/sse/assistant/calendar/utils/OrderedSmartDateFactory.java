package ustc.sse.assistant.calendar.utils;

import android.app.Activity;
import ustc.sse.assistant.R;

import ustc.sse.assistant.calendar.data.DateMapping;
import ustc.sse.assistant.calendar.data.Lunar;

public class OrderedSmartDateFactory implements SmartDateFactory {

	private static OrderedSmartDateFactory orderedSmartDateFactory;
	
	private DateMapping dateMapping;
	
	private OrderedSmartDateFactory() {
		dateMapping = DateMapping.getInstance();
	}
	
	protected Activity activity;
	
	@Override
	public SmartDate createSmartDate(Integer year, Integer month, Integer day) {
		//check lunar calendar change the year,month and day into lunar year, month and day
		Lunar.setLunar(year, month, day);
		Integer lunarYear = Lunar.getYear(); 
		Integer lunarMonth = Lunar.getMonth();
		Integer lunarDay = Lunar.getDay();
		String lunarYearText = Lunar.getLunarYear();
		String lunarMonthText = Lunar.getLunarMonth();
		String lunarDayText = Lunar.getLunarDay();
		String displayText = null;
				
		AbstractDate gregorianDate = new GregorianDate(year, month, day, lunarYear, lunarMonth, lunarDay);
		displayText = dateMapping.getGregorianFestivalDateMap().get(gregorianDate);
		
		if (displayText != null) {
			gregorianDate.setLunarYearText(lunarYearText);
			gregorianDate.setLunarMonthText(lunarMonthText);
			gregorianDate.setLunarDayText(lunarDayText);
			gregorianDate.setDisplayText(displayText);
			gregorianDate.setGregorianColorResId(activity.getResources().getColor(R.color.day_color));
			gregorianDate.setLunarColorResId(activity.getResources().getColor(R.color.gregorian_festival_color));
			return gregorianDate;
		} 
		AbstractDate lunarDate = new LunarDate(year, month, day, lunarYear, lunarMonth, lunarDay);
       
		lunarDate.setLunarYearText(lunarYearText);
		lunarDate.setLunarMonthText(lunarMonthText);
		lunarDate.setLunarDayText(lunarDayText);
		lunarDate.setDisplayText(lunarDayText);
		lunarDate.setGregorianColorResId(activity.getResources().getColor(R.color.day_color));
		lunarDate.setLunarColorResId(activity.getResources().getColor(R.color.day_color));
		
		if(Lunar.getIsLeap()){
			lunarMonthText = "闰" + Lunar.getLunarMonth();
			lunarDate.setLunarMonthText(lunarMonthText);
		}
		
		else if(lunarMonth == 12 && lunarDay == 29 && !Lunar.getIsBig()){
			lunarDate.setDisplayText("除夕");
			lunarDate.setLunarColorResId(activity.getResources().getColor(R.color.lunar_festival_color));
		}
		
		else {
			displayText = dateMapping.getLunarFestivalDateMap().get(lunarDate);
			if(displayText != null){
				lunarDate.setDisplayText(displayText);
				lunarDate.setLunarColorResId(activity.getResources().getColor(R.color.lunar_festival_color));
				return lunarDate;
			}
		}

		if(lunarDay == 1){
			lunarDate.setDisplayText(lunarMonthText);	
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
