package tk.dalang.gaminder.utils;

/**
 * GameReminder
 * <p>
 * </p>
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr 9, 2013
 */
public class MyCalendar {
	public String name;
	public String id;
	public MyCalendar(String _name, String _id) {
		name = _name;
		id = _id;
	}
	@Override
	public String toString() {
		return name;
	}
}
