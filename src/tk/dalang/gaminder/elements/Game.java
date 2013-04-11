package tk.dalang.gaminder.elements;

import java.util.Date;

/**
 * GameReminder
 * 
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr, 11 2013
 */

public class Game {
	public int _id;
	public String guest;
	public String host;
	public Date dt;
	public int type;
	public String chanls;
	public Boolean isEvent;
	public long eventId;
	
	public Game() {
	}
	
	public Game(String guest, String host, Date dt, int type, String chanls) {
		this.guest = guest;
		this.host = host;
		this.dt = dt;
		this.type = type;
		this.chanls = chanls;
		this.isEvent = false;
		this.eventId = 0;
	}
}
