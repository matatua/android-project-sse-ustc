package ustc.sse.assistant.calendar.data;

import java.util.HashMap;
import java.util.Map;

import ustc.sse.assistant.calendar.MappingDate;

public class DateMapping {
	private Map<MappingDate, String> festivalDateMap;
	private Map<MappingDate, String> lunarDateMap;
	
	private static DateMapping dateMapping;
	public static Integer FAKE_YEAR = -1;
	
	{
		festivalDateMap = new HashMap<MappingDate, String>();
		lunarDateMap = new HashMap<MappingDate, String>();
	}
	
	private DateMapping() {
		festivalDateMap.put(new MappingDate(FAKE_YEAR, 1, 1), "元旦");
		festivalDateMap.put(new MappingDate(FAKE_YEAR, 2, 14), "情人节");
		festivalDateMap.put(new MappingDate(FAKE_YEAR, 3, 8), "妇女节");
		festivalDateMap.put(new MappingDate(FAKE_YEAR, 3, 12), "植树节");
		festivalDateMap.put(new MappingDate(FAKE_YEAR, 4, 1), "愚人节");
		festivalDateMap.put(new MappingDate(FAKE_YEAR, 5, 1), "劳动节");
		festivalDateMap.put(new MappingDate(FAKE_YEAR, 5, 4), "青年节");
		festivalDateMap.put(new MappingDate(FAKE_YEAR, 6, 1), "儿童节");
		festivalDateMap.put(new MappingDate(FAKE_YEAR, 9, 10), "教师节");
		festivalDateMap.put(new MappingDate(FAKE_YEAR, 10, 1), "国庆节");
		festivalDateMap.put(new MappingDate(FAKE_YEAR, 12, 25), "圣诞节");
		
		lunarDateMap.put(new MappingDate(FAKE_YEAR, 1, 1), "春节");
		lunarDateMap.put(new MappingDate(FAKE_YEAR, 1, 15), "元宵节");
		lunarDateMap.put(new MappingDate(FAKE_YEAR, 5, 5), "端午节");
		lunarDateMap.put(new MappingDate(FAKE_YEAR, 7, 7), "七夕节");
		lunarDateMap.put(new MappingDate(FAKE_YEAR, 8, 15), "中秋节");
		lunarDateMap.put(new MappingDate(FAKE_YEAR, 9, 9), "重阳节");
		lunarDateMap.put(new MappingDate(FAKE_YEAR, 12, 30), "除夕");
		
		
		
	}
	
	public Map<MappingDate, String> getFestivalDateMap() {
		return festivalDateMap;
	}



	public Map<MappingDate, String> getLunarDateMap() {
		return lunarDateMap;
	}



	public static DateMapping getInstance() {
		if (dateMapping == null) {
			dateMapping = new DateMapping();
		} 
		return dateMapping;
	}
	
}
