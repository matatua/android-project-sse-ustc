/**
 * @author 李健
 */

package ustc.sse.event.contact.data;

public class Group {
	
	private String title;
	private long groupId;
	private int summaryCount;
	private int summaryCountWithPhone;
	private String note;
	
	public int getSummaryCount() {
		return summaryCount;
	}
	public void setSummaryCount(int summaryCount) {
		this.summaryCount = summaryCount;
	}
	public int getSummaryCountWithPhone() {
		return summaryCountWithPhone;
	}
	public void setSummaryCountWithPhone(int summaryCountWithPhone) {
		this.summaryCountWithPhone = summaryCountWithPhone;
	}

	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	
}
