package tk.dalang.gaminder.utils;

import java.util.Comparator;

/**
 * GameReminder
 * <p>
 * </p>
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr 9, 2013
 */
public class EventInfo implements Comparable<EventInfo>{
	public Long eventId;
	public String title;
	public String description;
	public String location;
	public String startDT;
	public String reminderTime;
	
	public EventInfo(Long eventId, String title, String description, String location, String startDT, String reminderTime)
	{
		this.eventId = eventId;
		this.title = title;
		this.description = description;
		this.location = location;
		this.startDT = startDT;
		this.reminderTime = reminderTime;
	}

	@Override
	public int compareTo(EventInfo arg0) {
		return arg0.startDT.compareTo(this.startDT);
	}
}
