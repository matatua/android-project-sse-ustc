package ustc.sse.assistant.help;

import ustc.sse.assistant.R;
import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class HelpActivity extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_activity);
		TabHost vTabHost = getTabHost();

		vTabHost.addTab(vTabHost.newTabSpec("tab01")
				.setIndicator("Help Contents").setContent(R.id.textView01));
		vTabHost.addTab(vTabHost.newTabSpec("tab02")
				.setIndicator("About Event Assistance").setContent(R.id.textView02));
		vTabHost.setCurrentTab(0);

	}
}