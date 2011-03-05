package ustc.sse.event.contact.data;

public class Contact {

	private int rawContactId;
	private String displayedName;
	private String phoneNumber;
	private String phoneType;
	private String birthday;
	private String note;
	public int getRawContactId() {
		return rawContactId;
	}
	public void setRawContactId(int rawContactId) {
		this.rawContactId = rawContactId;
	}
	public String getDisplayedName() {
		return displayedName;
	}
	public void setDisplayedName(String displayedName) {
		this.displayedName = displayedName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPhoneType() {
		return phoneType;
	}
	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	
}
