package ustc.sse.assistant.calendar.data;

import java.util.HashMap;
import java.util.Map;

import ustc.sse.assistant.calendar.utils.AbstractDate;

public class DateMapping {
	private Map<AbstractDate, String> festivalDateMap;
	private Map<AbstractDate, String> lunarDateMap;
	
	private static DateMapping dateMapping;
	public static Integer FAKE_YEAR = -1;
	public static Integer FAKE_MONTH = -1;
	public static Integer FAKE_DAY = -1;
	
	{
		festivalDateMap = new HashMap<AbstractDate, String>();
		lunarDateMap = new HashMap<AbstractDate, String>();
	}
	
	private DateMapping() {
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 1, 1), "元旦");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 2, 14), "情人节");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 3, 8), "妇女节");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 3, 12), "植树节");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 4, 1), "愚人节");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 5, 1), "劳动节");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 5, 4), "青年节");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 6, 1), "儿童节");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 7, 1), "建党日");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 8, 1), "建军节");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 9, 10), "教师节");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 10, 1), "国庆节");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 10, 31), "万圣夜");
		festivalDateMap.put(new AbstractDate(FAKE_YEAR, 12, 25), "圣诞节");
		
		lunarDateMap.put(new AbstractDate(FAKE_YEAR, 1, 1), "春节");
		lunarDateMap.put(new AbstractDate(FAKE_YEAR, 1, 15), "元宵节");
		lunarDateMap.put(new AbstractDate(FAKE_YEAR, 5, 5), "端午节");
		lunarDateMap.put(new AbstractDate(FAKE_YEAR, 7, 7), "七夕节");
		lunarDateMap.put(new AbstractDate(FAKE_YEAR, 8, 15), "中秋节");
		lunarDateMap.put(new AbstractDate(FAKE_YEAR, 9, 9), "重阳节");
		lunarDateMap.put(new AbstractDate(FAKE_YEAR, 12, 8), "腊八节");
		lunarDateMap.put(new AbstractDate(FAKE_YEAR, 12, 23), "除夕");
		lunarDateMap.put(new AbstractDate(FAKE_YEAR, 12, 30), "除夕");		
		
	}
	
	public Map<AbstractDate, String> getFestivalDateMap() {
		return festivalDateMap;
	}



	public Map<AbstractDate, String> getLunarDateMap() {
		return lunarDateMap;
	}



	public static DateMapping getInstance() {
		if (dateMapping == null) {
			dateMapping = new DateMapping();
		} 
		return dateMapping;
	}
	
}
