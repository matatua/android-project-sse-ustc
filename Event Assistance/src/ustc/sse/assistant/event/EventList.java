/**
 * 
 */
package ustc.sse.assistant.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ustc.sse.assistant.R;
import ustc.sse.assistant.event.provider.EventAssistant;
import ustc.sse.assistant.event.provider.EventAssistant.Event;
import ustc.sse.assistant.event.provider.EventAssistant.EventContact;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * a event list 
 * @author 李健
 *
 */
public class EventList extends Activity {
	
	public static final String FROM_CALENDAR = "begin_calendar";
	public static final String TO_CALENDAR = "end_calendar";
	
	public static final String DATE_FORMAT = "yyyy年MM月dd日";
	
	private static final int HEADER_FOOTER_NUMBER = 1;
	private static final int DELETE_DIALOG = 100;
	
	private ListView listView;
	private Button selectButton;
	private Button deleteButton;
	private LinearLayout buttonBar;
	private Calendar todayCalendar = Calendar.getInstance();
	private Calendar fromCalendar;
	private Calendar toCalendar;
	
	private Set<Long> selectedItemIds = new HashSet<Long>();
	private List<ImageView> imageViewList = new ArrayList<ImageView>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);
		
		getWidgets();
		initiateEvents();
		initiateBirthdayInfo();
		initiateButtons();
	}


	private void initiateButtons() {
		selectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int count = listView.getAdapter().getCount() - HEADER_FOOTER_NUMBER;
				int size = selectedItemIds.size();
				//all items selected
				if ( size < count) {
					selectButton.setText(getString(R.string.event_list_deselect_button));
					for (ImageView iv : imageViewList) {
						iv.setSelected(true);
						selectedItemIds.add((Long) iv.getTag());
					}
					
				} else if (size >= count){
					//when all items was selected, then deselect all items
					selectButton.setText(getString(R.string.event_list_select_button));
					for (ImageView iv : imageViewList) {
						iv.setSelected(false);
						selectedItemIds.remove((Long) iv.getTag());
						
					}
					buttonBar.setVisibility(View.INVISIBLE);
				}
				
				deleteButton.setText(getString(R.string.event_list_delete_button) + "(" + selectedItemIds.size() + ")");
			}
		});
		
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DELETE_DIALOG);
			}
		});
		
	}


	private void getWidgets() {
		listView = (ListView) findViewById(R.id.event_list_listView);
		selectButton = (Button) findViewById(R.id.event_list_select_button);
		deleteButton = (Button) findViewById(R.id.event_list_delete_button);
		buttonBar = (LinearLayout) findViewById(R.id.event_list_button_bar);
	}
	
	private OnItemClickListener listViewItemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent i = new Intent(EventList.this, EventDetail.class);
			i.putExtra(Event._ID, id);
			startActivity(i);
			//Toast.makeText(EventList.this, String.valueOf(id), Toast.LENGTH_SHORT).show();
			//TODO start event detail
		}
	};
	
	private void initiateEvents() {
		fromCalendar = (Calendar) getIntent().getSerializableExtra(FROM_CALENDAR);
		toCalendar = (Calendar) getIntent().getSerializableExtra(TO_CALENDAR);
		//
		//if from equal null, then set the time to today 0:00
		if (fromCalendar == null) {
			fromCalendar = new GregorianCalendar
							(todayCalendar.get(Calendar.YEAR), 
							todayCalendar.get(Calendar.MONTH), 
							todayCalendar.get(Calendar.DAY_OF_MONTH), 
							0, 
							0, 
							0);
		}
		//if to equal null, then set the time to tomorrow 0:00
		if (toCalendar == null) {
			toCalendar = new GregorianCalendar(todayCalendar.get(Calendar.YEAR), 
												todayCalendar.get(Calendar.MONTH), 
												todayCalendar.get(Calendar.DAY_OF_MONTH) + 1, 
												0, 
												0, 
												0);
		}
		ContentResolver cr = getContentResolver();
		String[] projection = {Event._ID, Event.CONTENT, Event.LOCATION, Event.BEGIN_TIME, Event.END_TIME};
		String selection = Event.BEGIN_TIME + " >=  ? AND " + Event.BEGIN_TIME + " <= ? ";
		String[] selectionArgs = {String.valueOf(fromCalendar.getTimeInMillis()),
									String.valueOf(toCalendar.getTimeInMillis())};
		Cursor cursor = cr.query(Event.CONTENT_URI, projection, selection, selectionArgs, null);
		startManagingCursor(cursor);
		EventListCursorAdapter adapter = new EventListCursorAdapter(this, 
														R.layout.event_list_item, 
														cursor,
														imageViewListener,
														imageViewList);
		
		TextView tv = new TextView(this);
		tv.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, 50));
		tv.setText("查看更多事件");
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Do nothing
				
			}
		});
		listView.addFooterView(tv);	
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listViewItemListener);
		
		
	}

	private void initiateBirthdayInfo() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * delete the event and eventcontact whose id in selectedItemIds
	 * and cancel alarm service associated with these events
	 */
	private void deleteSelectedEvents() {
		//delete events
		ContentResolver cr = getContentResolver();
		ArrayList<ContentProviderOperation> eventOps = new ArrayList<ContentProviderOperation>();
		ArrayList<ContentProviderOperation> eventContactOps = new ArrayList<ContentProviderOperation>();
		Iterator<Long> iter = selectedItemIds.iterator();
		while (iter.hasNext()) {
			Long id = iter.next();
			eventOps.add(ContentProviderOperation.newDelete(Event.CONTENT_URI)
											.withSelection(Event._ID + " = ?", new String[]{id.toString()})
											.build());
			
			Uri eventContactUri = ContentUris.withAppendedId(EventContact.CONTENT_URI, id);
			eventContactOps.add(ContentProviderOperation.newDelete(eventContactUri)
														.build());
		}
		
		try {
			ProgressDialog dialog = ProgressDialog.show(this, null, "删除中...", true, false);
			cr.applyBatch(EventAssistant.EVENT_AUTHORITY, eventOps);
			cr.applyBatch(EventAssistant.EVENT_CONTACT_AUTHORITY, eventContactOps);
			
			dialog.cancel();

		} catch (RemoteException e) {
			e.printStackTrace();
			Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
			Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
		}
		buttonBar.setVisibility(View.INVISIBLE);
		
		//cancel alarm service
		//TODO cancel alarm service
		
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DELETE_DIALOG :
			
			 DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE :
							deleteSelectedEvents();
							dialog.cancel();
							break;
						case DialogInterface.BUTTON_NEGATIVE :
							dialog.cancel();
							break;
						}
						
					}
				
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.event_list_delete_dialog_title);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setMessage(R.string.event_list_delete_dialog_message);
			builder.setPositiveButton(R.string.event_list_delete_dialog_affirm, dialogListener);
			
			builder.setNegativeButton(R.string.event_list_delete_dialog_cancel, dialogListener);
			
			return builder.create();
		}
		
		return null;
		
	}
	
	//this listener is used by the check image view only
	private OnClickListener imageViewListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int count = listView.getAdapter().getCount() - HEADER_FOOTER_NUMBER;
			int size = selectedItemIds.size();

			//Toast.makeText(EventList.this, v.toString() + " " + v.getTag() + " " + v.isSelected(), Toast.LENGTH_SHORT).show();
			if (v.isSelected()) {
				if (size == count) {
					selectButton.setText(getString(R.string.event_list_select_button));
				}
				//if size equal 1, here we hide the button bar
				if (size == 1) {
					buttonBar.setVisibility(View.INVISIBLE);
				}
				v.setSelected(false);
				selectedItemIds.remove(v.getTag());
			} else {
				
				if (size + 1 == count) {
					selectButton.setText(getString(R.string.event_list_deselect_button));
				}
				
				v.setSelected(true);
				selectedItemIds.add((Long) v.getTag());
								
				//show the button bar
				if (size == 0) {
					buttonBar.setVisibility(View.VISIBLE);
				}
			}
			
			deleteButton.setText(getString(R.string.event_list_delete_button) + "(" + selectedItemIds.size() + ")");
			
		}
	
	};

	private static class EventListCursorAdapter extends ResourceCursorAdapter {
		private Context context;
		private OnClickListener imageViewListener;
		private List<ImageView> imageViews;

		public EventListCursorAdapter(Context context, int layout, Cursor c, OnClickListener l, List<ImageView> imageViews) {
			super(context, layout, c);
			this.context = context;
			this.imageViewListener = l;
			this.imageViews = imageViews;
			
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return super.getView(position, null, parent);
		     
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView dateTv = (TextView) view.findViewById(R.id.event_list_item_date);
			TextView contentTv = (TextView) view.findViewById(R.id.event_list_item_content);
			TextView locationTv = (TextView) view.findViewById(R.id.event_list_item_location);
			ImageView checkImageView = (ImageView) view.findViewById(R.id.event_list_item_check);
			
			long id = cursor.getLong(cursor.getColumnIndex(Event._ID));
			String content = cursor.getString(cursor.getColumnIndex(Event.CONTENT));
			String location = cursor.getString(cursor.getColumnIndex(Event.LOCATION));
			String beginTimeStr = cursor.getString(cursor.getColumnIndex(Event.BEGIN_TIME));
			String endTimeStr = cursor.getString(cursor.getColumnIndex(Event.END_TIME));
			
			checkImageView.setOnClickListener(imageViewListener);
			Drawable drawable = context.getApplicationContext().getResources().getDrawable(R.drawable.indicator_check_mark_light);
			checkImageView.setImageDrawable(drawable.mutate());
			checkImageView.setTag(id); 
			imageViews.add(checkImageView);
			
			Calendar begin = Calendar.getInstance();
			begin.setTimeInMillis(Long.valueOf(beginTimeStr));
			Calendar end = Calendar.getInstance();
			end.setTimeInMillis(Long.valueOf(endTimeStr));		
			
			dateTv.setText("From " + DateFormat.format(DATE_FORMAT, begin) 
							+ " to " + DateFormat.format(DATE_FORMAT, end));
			contentTv.setText(content);
			locationTv.setText(location);
		}

	}
}
