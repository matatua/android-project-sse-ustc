/**
 * 
 */
package ustc.sse.assistant.util;

import android.util.SparseBooleanArray;

/**
 * add two function, get false count and get true count
 * @author 李健
 *
 */
public class MySparseBooleanArray extends SparseBooleanArray {

	private int trueCount = 0;
	private int falseCount = 0;
	/* (non-Javadoc)
	 * @see android.util.SparseBooleanArray#delete(int)
	 */
	@Override
	public void delete(int key) {
		int index = indexOfKey(key);
		if (index >= 0) {
			if (valueAt(index)) {
				trueCount--;
			} else {
				falseCount--;
			}
		}
		super.delete(key);
		
	}

	/* (non-Javadoc)
	 * @see android.util.SparseBooleanArray#put(int, boolean)
	 */
	@Override
	public void put(int key, boolean value) {
		operateTrueAndFalseCount(key, value);
		super.put(key, value);
	}

	/* (non-Javadoc)
	 * @see android.util.SparseBooleanArray#clear()
	 */
	@Override
	public void clear() {
		super.clear();
		falseCount = 0;
		trueCount = 0;
	}

	/* (non-Javadoc)
	 * @see android.util.SparseBooleanArray#append(int, boolean)
	 */
	@Override
	public void append(int key, boolean value) {
		operateTrueAndFalseCount(key, value);
		super.append(key, value);
		
	}

	/**
	 * @param key
	 * @param value
	 */
	private void operateTrueAndFalseCount(int key, boolean value) {
		int index = indexOfKey(key);
		if (index < 0) {
			if (value)
				trueCount++;
			else
				falseCount++;
		} else {
			boolean valueAtIndex = valueAt(index);
			if (valueAtIndex != value) { 
				if (value) {
					trueCount++;
					falseCount--;
				} else {
					falseCount++;
					trueCount--;
				}
			}
		}
	}
	
	public int getFalseCount() {
		return falseCount;
	}
	
	public int getTrueCount() {
		return trueCount;
	}
}
