/**
 * 
 */
package ustc.sse.assistant.util;

/**
 * @author 李健
 *
 */
public class EventAssistantUtils {
	
	public static final int UNIT_KB = 1;
	public static final int UNIT_MB = 2;
	public static final int UNIT_GB = 3;
	public static final int UNIT_SMART = 4;
	
	public static final int KB = 1024;
	public static final int MB = KB * 1024;
	public static final int GB = MB * 1024;
	
	private EventAssistantUtils(){}
	
	/**
	 * convert unit of byte to unit of MB
	 * @param length
	 * @return
	 */
	public static final double bytesToMB(long length) {
		if (length <= 0) {
			return 0.0;
		}
		return length / 1000000.0;
	}
	
	public static final String bytesToLargeUnit(long length, int unit) {
		switch (unit) {
		case UNIT_KB :
			return  String.valueOf(length / 1024.0) + "KB";
		case UNIT_MB :
			return String.valueOf(length / (1024.0 * 1024.0)) + "MB";
		case UNIT_GB :
			return String.valueOf(length / (1024.0 * 1024.0 * 1024.0)) + "GB";
		case UNIT_SMART :
			if (length > GB) {
				return String.valueOf(length / GB) + "GB";
			} else if (length > MB) {
				return String.valueOf(length / MB) + "MB";
			} else if (length > KB) {
				return String.valueOf(length / KB) + "KB";
			} else {
				return String.valueOf(length) + "B";
			}
		}
		
		return "";
	}
}
