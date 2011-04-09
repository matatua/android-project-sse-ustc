/**
 * 
 */
package ustc.sse.assistant.event;

import ustc.sse.assistant.R;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventAssistant.EventSearch;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
 
public class EventSearchActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		handleIntent();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.setIntent(intent);
		handleIntent();
	}
	
	private void handleIntent() {
		
		Intent i = getIntent();
		
		if (Intent.ACTION_VIEW.equals(i.getAction())) {
			//handle a click on suggestion
			Intent eventDetailIntent = new Intent(this, EventDetail.class);
			String eventId = i.getData().getPathSegments().get(1);
			eventDetailIntent.putExtra(Event._ID, Long.valueOf(eventId));
			
			startActivity(eventDetailIntent);
			finish();
		} else if (Intent.ACTION_SEARCH.equals(i.getAction())) {
			//handle a click on query
			showResult(i);
		}
	}

	private void showResult(Intent i) {
		String[] projection = {BaseColumns._ID, EventSearch.CONTENT,
								EventSearch.LOCATION, EventSearch.EVENT_ID};
		
		String query = i.getStringExtra(SearchManager.QUERY);
		String[] selectionArgs = {query};
		Cursor cursor = managedQuery(EventSearch.CONTENT_URI, projection, null, selectionArgs, null);
		
		if (cursor != null && cursor.getCount() > 0) {
			EventShareCursorAdapter adapter = new EventShareCursorAdapter(this,
					android.R.layout.two_line_list_item, cursor);
			setListAdapter(adapter);
			onContentChanged();
		} else {
			View emptyView = getLayoutInflater().inflate(R.layout.empty_view, getListView(), false);
			getListView().setEmptyView(emptyView);
		}
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		long eventId = (Long) v.getTag();
		Intent intent = new Intent(this, EventDetail.class);
		intent.putExtra(Event._ID, eventId);
		startActivity(intent);
		finish();
	}
	 
	private static class EventShareCursorAdapter extends ResourceCursorAdapter {

		public EventShareCursorAdapter(Context context, int layout, Cursor c) {
			super(context, layout, c);
			
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tv1 = (TextView) view.findViewById(android.R.id.text1);
			TextView tv2 = (TextView) view.findViewById(android.R.id.text2);
			
			tv1.setText(context.getString(ustc.sse.assistant.R.string.event_content) + cursor.getString(cursor.getColumnIndex(EventSearch.CONTENT)));
			tv2.setText(context.getString(R.string.event_location) + cursor.getString(cursor.getColumnIndex(EventSearch.LOCATION)));
			
			view.setTag(cursor.getLong(cursor.getColumnIndex(EventSearch.EVENT_ID)));
			
		}
		
	}
}
