package tk.dalang.gaminder.elements;

import java.util.Date;

import android.text.Spanned;

public final class SinaGame extends Game {

	public String chanls;
	
	public SinaGame() {
	}
	
	public SinaGame(String guest, String host, Date dt, int type, String chanls) {
		this.guest = guest;
		this.host = host;
		this.dt = dt;
		this.type = type;
		this.chanls = chanls;
		this.isEvent = false;
		this.eventId = 0;
	}

	public SinaGame(int _id, String guest, String host, Date dt, int type, String chanls, boolean isEvent, long eventId) {
		this._id = _id;
		this.guest = guest;
		this.host = host;
		this.dt = dt;
		this.type = type;
		this.chanls = chanls;
		this.isEvent = isEvent;
		this.eventId = eventId;
	}
	
	@Override
	public String getChanls() {
		return this.chanls;
	}

	@Override
	public void setIsEvent(boolean isEvent) {
		this.isEvent = isEvent;
	}

	@Override
	public void setEventId(long eventid) {
		this.eventId = eventid;
	}

	@Override
	public Spanned getChanlsHTML() {
		// TODO Auto-generated method stub
		return null;
	}

}
