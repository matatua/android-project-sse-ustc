/**
 * 
 */
package ustc.sse.assistant.test;

import ustc.sse.assistant.MainScreen;
import ustc.sse.assistant.R;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

/**
 * @author AlexanderLee
 *
 */
public class MainScreenTest extends ActivityInstrumentationTestCase2<MainScreen> {

	private TextView titleTextView;
	private CharSequence title;
	private String resourceTitle;
	
	public MainScreenTest() {
		super("ustc.sse.assistant", MainScreen.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		MainScreen ms = getActivity();
		titleTextView = (TextView) ms.findViewById(R.id.main_screen_textview);
		title = titleTextView.getText();
		resourceTitle = ms.getResources().getString(R.string.main_screen_title);
	}
	
	public void testPreconditions() {
		assertNotNull(titleTextView);
	}
	
	public void testText() {
		assertEquals("Hello", title.toString());
	}
}
