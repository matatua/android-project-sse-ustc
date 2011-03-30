package ustc.sse.assistant.event.data;

import java.util.ArrayList;

/**
 * 
 * @author 李健
 *
 */
public class EventEntity {
	public Long id;
	public String content;
	public String alarmTime;
	public Integer alarmType;
	public String beginTime;
	public String endTime;
	public String createTime;
	public String lastModifyTime;
	public String location;
	public String note;
	public Integer priorAlarmDay;
	public Integer priorRepeat;
	
	public ArrayList<EventContactEntity> contacts = new ArrayList<EventContactEntity>();
}
