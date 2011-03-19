package ustc.sse.assistant.calendar.data;

import java.util.HashMap;
import java.util.Map;

import ustc.sse.assistant.calendar.AbstractDate;
import ustc.sse.assistant.calendar.MappingDate;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

public class DateMapping {
	private Map<MappingDate, String> festivalDateMap;
	private Map<MappingDate, String> lunarDateMap;
	
	private static DateMapping dateMapping;
	public static Integer FAKE_YEAR = -1;
	
	{
		festivalDateMap = new HashMap<MappingDate, String>();
		lunarDateMap = new HashMap<MappingDate, String>();
	}
	
	private DateMapping(Context context) {
		Resources resources = context.getApplicationContext().getResources();
		
		//TODO get corresponding data from resources and then put into maps
		
		
		
	}
	
	public Map<MappingDate, String> getFestivalDateMap() {
		return festivalDateMap;
	}



	public Map<MappingDate, String> getLunarDateMap() {
		return lunarDateMap;
	}



	public static DateMapping getInstance(Context context) {
		if (dateMapping == null) {
			dateMapping = new DateMapping(context);
		} 
		return dateMapping;
	}
	
}
