package ustc.sse.assistant.calendar.data;

import java.util.HashMap;
import java.util.Map;
import ustc.sse.assistant.calendar.utils.GregorianDate;
import ustc.sse.assistant.calendar.utils.LunarDate;

public class DateMapping {
	private Map<GregorianDate, String> gregorianFestivalDateMap = new HashMap<GregorianDate, String>();
	private Map<LunarDate, String> lunarFestivalDateMap = new HashMap<LunarDate, String>();;
	
	private static DateMapping dateMapping;
	public static Integer FAKE_YEAR = -1;
	public static Integer FAKE_MONTH = -1;
	public static Integer FAKE_DAY = -1;
		
	private DateMapping() {
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 1, 1, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "元旦");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 2, 14, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "情人节");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 3, 8, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "妇女节");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 3, 12, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "植树节");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 4, 1, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "愚人节");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 5, 1, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "劳动节");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 5, 4, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "青年节");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 6, 1, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "儿童节");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 7, 1, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "建党日");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 8, 1, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "建军节");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 9, 10, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "教师节");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 10, 1, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "国庆节");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 10, 31, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "万圣夜");
		gregorianFestivalDateMap.put(new GregorianDate(FAKE_YEAR, 12, 25, FAKE_YEAR, FAKE_MONTH, FAKE_DAY), "圣诞节");
		
		lunarFestivalDateMap.put(new LunarDate(FAKE_YEAR, FAKE_MONTH, FAKE_DAY, FAKE_YEAR, 1, 1), "春节");
		lunarFestivalDateMap.put(new LunarDate(FAKE_YEAR, FAKE_MONTH, FAKE_DAY, FAKE_YEAR, 1, 15), "元宵节");
		lunarFestivalDateMap.put(new LunarDate(FAKE_YEAR, FAKE_MONTH, FAKE_DAY, FAKE_YEAR, 5, 5), "端午节");
		lunarFestivalDateMap.put(new LunarDate(FAKE_YEAR, FAKE_MONTH, FAKE_DAY, FAKE_YEAR, 7, 7), "七夕节");
		lunarFestivalDateMap.put(new LunarDate(FAKE_YEAR, FAKE_MONTH, FAKE_DAY, FAKE_YEAR, 8, 15), "中秋节");
		lunarFestivalDateMap.put(new LunarDate(FAKE_YEAR, FAKE_MONTH, FAKE_DAY, FAKE_YEAR, 9, 9), "重阳节");
		lunarFestivalDateMap.put(new LunarDate(FAKE_YEAR, FAKE_MONTH, FAKE_DAY, FAKE_YEAR, 12, 8), "腊八节");
		lunarFestivalDateMap.put(new LunarDate(FAKE_YEAR, FAKE_MONTH, FAKE_DAY, FAKE_YEAR, 12, 23), "除夕");
		lunarFestivalDateMap.put(new LunarDate(FAKE_YEAR, FAKE_MONTH, FAKE_DAY, FAKE_YEAR, 12, 30), "除夕");		
		
	}
	
	public Map<GregorianDate, String> getGregorianFestivalDateMap() {
		return gregorianFestivalDateMap;
	}

	public Map<LunarDate, String> getLunarFestivalDateMap() {
		return lunarFestivalDateMap;
	}

	public static DateMapping getInstance() {
		if (dateMapping == null) {
			dateMapping = new DateMapping();
		} 
		return dateMapping;
	}
	
}
