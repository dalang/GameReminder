package tk.dalang.gaminder.elements;

import java.util.Date;

import android.text.Spanned;

public interface IGame {
	public String getGuest();
	public String getHost();
	public Date getDate();
	public int getType();
	public boolean getIsEvent();
	public long getEventId();
	
	public String getChanls();
	public Spanned getChanlsHTML();
	public void setIsEvent(boolean isEvent);
	public void setEventId(long eventid);
}
