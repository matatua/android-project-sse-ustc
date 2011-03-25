package ustc.sse.assistant.calendar.utils;

import java.util.Calendar;

import ustc.sse.assistant.R;

public class MyCalendar {

	private SmartDateFactory factory = OrderedSmartDateFactory.getInstance();
	private SmartDate sd;
	private SmartDate[] date = new AbstractDate[42];
	
	// 当前年和月
	public int currentYear, currentMonth;
	// 上月或下月
	public int prevYear, prevMonth;
	public int nextYear, nextMonth;
	Calendar calendar = Calendar.getInstance();
		
	public MyCalendar(Calendar currentCalendar){
		currentYear = currentCalendar.get(Calendar.YEAR);
		currentMonth = currentCalendar.get(Calendar.MONTH);

		prevYear = currentYear;
		prevMonth = currentMonth - 1;
		nextYear = currentYear;
		nextMonth = currentMonth + 1;
		if(currentMonth == 0) 
		{
			prevYear = currentYear - 1;
			prevMonth = 11;
		}
		
		if(currentMonth == 11)
		{
			nextYear = currentYear + 1;
			nextMonth = 0;
		}
		
		calculateDays();
	}

	public SmartDate[] getCurrentMonthCalendar(){
		return date;
	}
	
	private void calculateDays()
	{
		calendar.set(currentYear, currentMonth, 1);

		int week = calendar.get(Calendar.DAY_OF_WEEK);
		int monthDays = 0;
		int prevMonthDays = 0;

		monthDays = getMonthDays(currentYear, currentMonth);
		prevMonthDays = getMonthDays(prevYear, prevMonth);

		for (int i = week, day = prevMonthDays; i > 1; i--, day--)
		{
			sd = factory.createSmartDate(prevYear, (prevMonth + 1), day);
			sd.setGregorianColorResId(R.color.prev_next_month_day_color);
			sd.setLunarColorResId(R.color.prev_next_month_day_color);
			date[i - 2] = sd;
		}
		for (int day = 1, i = week - 1; day <= monthDays; day++, i++)
		{		
			sd = factory.createSmartDate(currentYear, (currentMonth + 1), day);
			date[i] = sd;
		}
		for (int i = week + monthDays - 1, day = 1; i < date.length; i++, day++)
		{
			sd = factory.createSmartDate(nextYear, (nextMonth + 1), day);
			sd.setGregorianColorResId(R.color.prev_next_month_day_color);
			sd.setLunarColorResId(R.color.prev_next_month_day_color);
			date[i] = sd;
		}

	}
	
	private int getMonthDays(int year, int month)
	{
		month++;
		switch (month)
		{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
			{
				return 31;
			}
			case 4:
			case 6:
			case 9:
			case 11:
			{
				return 30;
			}
			case 2:
			{
				if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0))
					return 29;
				else
					return 28;
			}
		}
		return 0;
	}

}
