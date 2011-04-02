/**
 * 
 */
package ustc.sse.assistant.util;

/**
 * @author 李健
 *
 */
public class EventAssistantUtils {
	
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
}
