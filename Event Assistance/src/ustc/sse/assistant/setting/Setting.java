package ustc.sse.assistant.setting;

import ustc.sse.assistant.R;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.AbsListView;

public class Setting extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}