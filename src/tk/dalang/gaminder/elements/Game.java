package tk.dalang.gaminder.elements;

import java.util.Date;

/**
 * GameReminder
 * 
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr, 11 2013
 */

abstract public class Game implements IGame {
	public int _id;
	public String guest;
	public String host;
	public Date dt;
	public int type;
	public Boolean isEvent;
	public long eventId;
	
	@Override
	public String getGuest() {
		return this.guest;
	}

	@Override
	public String getHost() {
		return this.host;
	}
	

	@Override
	public Date getDate() {
		return this.dt;
	}

	@Override
	public int getType() {
		return this.type;
	}

	@Override
	public boolean getIsEvent() {
		return this.isEvent;
	}

	@Override
	public long getEventId() {
		return this.eventId;
	}


}
