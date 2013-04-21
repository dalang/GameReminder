package tk.dalang.gaminder.elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

public final class AzhiboGame extends Game {
	private List<Chanl> chanls;
	public static final String url = "http://www.azhibo.com/";
	
	class Chanl {
		String name;
		String link;
		
		public Chanl(String name, String link) {
			this.name = name;
			this.link = link;
		}
	}
	
	public AzhiboGame(String guest, String host, Date dt, int type) {
		this.guest = guest;
		this.host = host;
		this.dt = dt;
		this.type = type;
		this.isEvent = false;
		this.eventId = 0;
		this.chanls = new ArrayList<Chanl>();
	}
	
	public AzhiboGame(int _id, String guest, String host, Date dt, int type, String chanls, boolean isEvent, long eventId) {
		this._id = _id;
		this.guest = guest;
		this.host = host;
		this.dt = dt;
		this.type = type;
		this.isEvent = isEvent;
		this.eventId = eventId;
		
		this.chanls = new ArrayList<Chanl>();
		String []chanlstrs = chanls.split(";");
		for (String chanlstr : chanlstrs) {
			String []chanldetails = chanlstr.split(":");
			if (chanldetails.length >= 2)
				this.chanls.add(new Chanl(chanldetails[0], chanldetails[1]));
		}
	}
	
	public void addChanl(String name, String link) {
		link = link.replaceAll("http[s]?://", "");
		Chanl chanl = new Chanl(name, link);
		this.chanls.add(chanl);
	}
	
	public void clearChanl() {
		this.chanls.clear();
	}

	@Override
	public String getChanls() {
		String ret = "";
		for (Chanl chanl : this.chanls) {
			ret += chanl.name + ":" + chanl.link +";";
		}
		return ret;
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
		String source = "";
		for (int i = 0; i < this.chanls.size(); i++)
		{
			if (source.length() > 0)
				source += "<br/>";
			source += "<a target=\"_blank\" href=\"http://" + this.chanls.get(i).link + "\">" + 
						this.chanls.get(i).name + "</a>";
		}
		
		return Html.fromHtml(source);
	}
}
