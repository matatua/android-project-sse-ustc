/**
 * 
 */
package ustc.sse.assistant.backup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;

import ustc.sse.assistant.R;
import ustc.sse.assistant.backup.util.EventToXml;
import ustc.sse.assistant.backup.util.XmlToEvent;
import ustc.sse.assistant.util.EventAssistantUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * @author 李健
 *
 */
public class BackupRestore extends Activity {
	public static final String DATE_FORMAT = "yyyy年MM月dd日 EE";
	public static final String TIME_FORMAT = "h:mmaa";
	
	private static final int FROM_DATE_DIALOG = 1;
	private static final int FROM_TIME_DIALOG = 2;
	private static final int TO_DATE_DIALOG = 3;
	private static final int TO_TIME_DIALOG = 4;
	private static final int BACKUP_RESTORE = 5;

	private Button fromDateButton;
	private Button fromTimeButton;
	private Button toDateButton;
	private Button toTimeButton;
	
	private TextView fromTextView;
	private TextView toTextView;
	private TextView listViewLabel;
	
	private Button doOperationButton;
	private CheckBox backupTypeCheckBox; 
	private LinearLayout buttonBars;
	private RelativeLayout headerView;
	
	private ListView listView;
	private BackupRestoreFileAdapter adapter;
	
	private Calendar fromCalendar = Calendar.getInstance();
	private Calendar toCalendar = Calendar.getInstance();
	
	private File backupFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.backup_restore);
		//order can not be reversed
		getWidgets();
		initiateHeaderView();
		initiateListView();
		initiateButtonBar();
	}
	/**
	 * initiate button bar for quickly selecting and deleting
	 */
	private void initiateButtonBar() {
		//TODO 
	}

	/**
	 * get all backup files and show in this listview, if no file was found, show 
	 * a textview tell the user there are no backup files.
	 */
	private void initiateListView() {
		final BackupRestoreFileAdapter adapter = new BackupRestoreFileAdapter();
		//set empty view for listview
		this.adapter = adapter;
		listView.addHeaderView(headerView, null, false);
		listView.addHeaderView(listViewLabel, null, false);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//show a dialog and ask user whether to restore the 
				//selected backup file
				backupFile = adapter.backupFiles[(int) id];
				showDialog(BACKUP_RESTORE);
			}
		});
	}

	/**
	 * when user choose to backup, system call this method to do 
	 * the real backup operation, when system is processing, show a progressbar
	 * @param restoreContacts
	 */
	private void doRestore(final boolean restoreContacts) {
		final ProgressDialog dialog = ProgressDialog.show(this, "还原中", "请耐心等待..", true, false);
		Handler handler = new Handler();
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				XmlToEvent xte = new XmlToEvent(BackupRestore.this, backupFile, restoreContacts);
				if (xte.restore()) {
					Toast.makeText(BackupRestore.this, "还原成功！", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(BackupRestore.this, "还原失败！", Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
			}
		});
	}
	
	private AlertDialog makeAlertDialogForBackup() {
		android.content.DialogInterface.OnClickListener callBack = new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case Dialog.BUTTON_POSITIVE :
					doRestore(true);
					break;
				case Dialog.BUTTON_NEUTRAL :
					doRestore(false);
					break;
				case Dialog.BUTTON_NEGATIVE :
					dialog.dismiss();
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(BackupRestore.this);
		AlertDialog  dialog = builder.setIcon(android.R.drawable.ic_dialog_alert)
									.setMessage("你确定要还原事件以及相应联系人吗？")
									.setTitle("事件还原")
									.setPositiveButton("仅事件", callBack)
									.setNeutralButton("事件和联系人", callBack)
									.setNegativeButton("取消", callBack)
									.create();
		return dialog;
	}
	/**
	 * initiate date and time picker within headerview, also set proper OnClickListener
	 */
	private void initiateHeaderView() {
		
		fromDateButton.setText(DateFormat.format(DATE_FORMAT, fromCalendar));
		fromTimeButton.setText(DateFormat.format(TIME_FORMAT, fromCalendar));
		toDateButton.setText(DateFormat.format(DATE_FORMAT, toCalendar));
		toTimeButton.setText(DateFormat.format(TIME_FORMAT, toCalendar));
		
		fromDateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(FROM_DATE_DIALOG);
			}
		});
		fromTimeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(FROM_TIME_DIALOG);
			}
		});
		toDateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(TO_DATE_DIALOG);
				
			}
		});
		toTimeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(TO_TIME_DIALOG);
			}
		});
	
		doOperationButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EventToXml etx;
				if (backupTypeCheckBox.isChecked()) {
					//backup all data
					etx = new EventToXml(BackupRestore.this, null, null);
				} else {
					//backup date in the selected period
					etx = new EventToXml(BackupRestore.this, fromCalendar, toCalendar);
				}
				StringWriter sw = etx.generateXml();
				if (!writeXmlToExternalStorage(sw)) {
					Toast.makeText(BackupRestore.this, "备份失败", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(BackupRestore.this, "备份成功", Toast.LENGTH_SHORT).show();
					//notify data have changed
					adapter.notifyDataSetChanged();
				}
			}
		});
		
		backupTypeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					setHeaderComponentVisible(View.GONE);					
				} else {
					setHeaderComponentVisible(View.VISIBLE);
				}
			}
		});
	}
	
	private boolean writeXmlToExternalStorage(StringWriter sw)	{
		if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		
		File root = Environment.getExternalStorageDirectory();
		File backDir = new File(root, BACKUP_RESTORE_DIR);
		
		try {
			if (!backDir.exists()) {
				backDir.mkdirs();
			}
			Calendar now = Calendar.getInstance();
			String fileName = FILE_PREFIX + now.get(Calendar.YEAR) + now.get(Calendar.MONTH)
								+ now.get(Calendar.DAY_OF_MONTH) + now.get(Calendar.HOUR_OF_DAY)
								+ now.get(Calendar.MINUTE) + now.get(Calendar.SECOND) + ".xml";
			File backFile = new File(backDir, fileName);
			if (!backFile.exists()) {
				backFile.createNewFile();
			}
			
			BufferedOutputStream fs = new BufferedOutputStream(new FileOutputStream(backFile));
			byte[] byteArray = new byte[sw.getBuffer().length()];
			fs.write(sw.toString().getBytes());
			fs.flush();
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void setHeaderComponentVisible(int visibility) {
		fromDateButton.setVisibility(visibility);
		fromTimeButton.setVisibility(visibility);
		toDateButton.setVisibility(visibility);
		toTimeButton.setVisibility(visibility);
		fromTextView.setVisibility(visibility);
		toTextView.setVisibility(visibility);
	}
	
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case FROM_DATE_DIALOG :
			return createFromDateDialog();
		case FROM_TIME_DIALOG :
			return createFromTimeDialog();
		case TO_DATE_DIALOG :
			return createToDateDialog();
		case TO_TIME_DIALOG :
			return createToTimeDialog();
		case BACKUP_RESTORE :
			return makeAlertDialogForBackup();
		}
		
		return null;
	}

	private Dialog createFromDateDialog() {
		OnDateSetListener callBack = new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				fromCalendar.set(year, monthOfYear, dayOfMonth);
				fromDateButton.setText(DateFormat.format(DATE_FORMAT, fromCalendar));
			}
		};
		DatePickerDialog dpd = new DatePickerDialog(this, 
												callBack, 
												fromCalendar.get(Calendar.YEAR), 
												fromCalendar.get(Calendar.MONTH), 
												fromCalendar.get(Calendar.DAY_OF_MONTH));
		return dpd;
	}
	
	private Dialog createFromTimeDialog() {
		OnTimeSetListener callBack = new OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				fromCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				fromCalendar.set(Calendar.MINUTE, minute);
				fromTimeButton.setText(DateFormat.format(TIME_FORMAT, fromCalendar));
				
			}
		};
		TimePickerDialog tpd = new TimePickerDialog(this,
										callBack, 
										fromCalendar.get(Calendar.HOUR_OF_DAY), 
										fromCalendar.get(Calendar.MINUTE), 
										true);
		return tpd;
	}
	private Dialog createToDateDialog() {
		OnDateSetListener callBack = new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				toCalendar.set(year, monthOfYear, dayOfMonth);
				toDateButton.setText(DateFormat.format(DATE_FORMAT, toCalendar));
			}
		};
		DatePickerDialog dpd = new DatePickerDialog(this, 
												callBack, 
												toCalendar.get(Calendar.YEAR), 
												toCalendar.get(Calendar.MONTH), 
												toCalendar.get(Calendar.DAY_OF_MONTH));
		return dpd;
	}
	private Dialog createToTimeDialog() {
		OnTimeSetListener callBack = new OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				toCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				toCalendar.set(Calendar.MINUTE, minute);
				toTimeButton.setText(DateFormat.format(TIME_FORMAT, toCalendar));
				
			}
		};
		TimePickerDialog tpd = new TimePickerDialog(this,
										callBack, 
										toCalendar.get(Calendar.HOUR_OF_DAY), 
										toCalendar.get(Calendar.MINUTE), 
										true);
		return tpd;
	}

	/**
	 * inflate header view then add into listView as header.
	 * the intiation order can't be reordered.
	 */
	private void getWidgets() {
		listView = (ListView) findViewById(R.id.backup_restore_listView);
		listViewLabel = (TextView) getLayoutInflater().inflate(R.layout.backup_restore_existing_backup_file_label, listView, false);
		headerView = (RelativeLayout) getLayoutInflater().inflate(R.layout.backup_restore_header, listView, false);		

		fromDateButton = (Button) headerView.findViewById(R.id.backup_restore_from_date);
		fromTimeButton = (Button) headerView.findViewById(R.id.backup_restore_from_time);
		toDateButton = (Button) headerView.findViewById(R.id.backup_restore_to_date);
		toTimeButton = (Button) headerView.findViewById(R.id.backup_restore_to_time);
		doOperationButton = (Button) headerView.findViewById(R.id.backup_restore_do_operation);
		buttonBars = (LinearLayout) findViewById(R.id.backup_restore_button_bar);
		backupTypeCheckBox = (CheckBox) headerView.findViewById(R.id.backup_restore_type_checkBox);
		fromTextView = (TextView) headerView.findViewById(R.id.backup_restore_start_time_label);
		toTextView = (TextView) headerView.findViewById(R.id.backup_restore_end_time_label);
	}


	private static final String BACKUP_RESTORE_DIR = "/eventassistant/backup/";
	private static final String FILE_PREFIX = "EventAssistant";
	private static final FilenameFilter BACKUP_FILE_NAME_FILTER = new FilenameFilter() {
		
		@Override
		public boolean accept(File arg0, String name) {
			if (name.startsWith(FILE_PREFIX)) {
				return true;
			} else {
			
				return false;
			}
		}
	};
	private static final FileFilter BACKUP_FILE_FILTER = new FileFilter() {
		
		@Override
		public boolean accept(File file) {
			if (file.getName().startsWith(FILE_PREFIX) && file.isFile()) {
				return true;
			}				
			return false;
		}
	};
	
	private class BackupRestoreFileAdapter extends BaseAdapter {

		private static final String DATE_FORMAT = "yyyy年MM月dd日 h:mmaa";
		
		private SparseBooleanArray checkedItems = new SparseBooleanArray();
		private File[] backupFiles = null;
		
		public BackupRestoreFileAdapter() {
			super();
			
			initiateBackupFiles();
			registerDataSetObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
					initiateBackupFiles();
					
				}
			});
		}

		private void initiateBackupFiles() {
			String storageState = Environment.getExternalStorageState();
			if (storageState.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
				File root = Environment.getExternalStorageDirectory();
				File backupDir = new File(root, BACKUP_RESTORE_DIR);
				
				if (!backupDir.exists()) {
					backupFiles = new File[0];
				} else {
					backupFiles = backupDir.listFiles(BACKUP_FILE_FILTER);
				}
				
				for (int i = 0; i < backupFiles.length; i++) {
					checkedItems.put(i, false); 
				}
			}
		}
		
		@Override
		public int getCount() {
			return backupFiles.length;
		}


		@Override
		/**
		 * return the file instance in a given position
		 */
		public Object getItem(int position) {
			return backupFiles[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.backup_restore_file_item, parent, false);

			}
			TextView timeTv = (TextView) convertView.findViewById(R.id.backup_restore_item_backup_time);
			TextView sizeTv = (TextView) convertView.findViewById(R.id.backup_restore_item_backup_size);
			ImageView checkIv = (ImageView) convertView.findViewById(R.id.backup_restore_item_check);
			
			File curFile = backupFiles[position];
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(curFile.lastModified());
			timeTv.setText("备份于：" + DateFormat.format(DATE_FORMAT, calendar));
			sizeTv.setText("大小：" + String.valueOf(EventAssistantUtils.bytesToMB(curFile.length())) + "M");
			checkIv.setTag(position);
			checkIv.setOnClickListener(checkImageViewListener);
			
			if (checkedItems.get(position)) {
				checkIv.setSelected(true);
			} else {
				checkIv.setSelected(false);
			}
			
			return convertView;		
		}
		
		OnClickListener checkImageViewListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				boolean checkState = checkedItems.get(position) ? false : true;
				checkedItems.put(position, checkState);
				v.setSelected(checkState);
			}
		};
		
	}

}
