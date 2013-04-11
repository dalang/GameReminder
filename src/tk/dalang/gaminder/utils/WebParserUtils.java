package tk.dalang.gaminder.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import tk.dalang.gaminder.R;
import tk.dalang.gaminder.elements.Game;

import android.util.Log;

/**
 * GameReminder
 * <p>
 * Web Parser
 * </p>
 * @author <a href="http://weibo.com/iDalang">dalang</a>
 * @version Apr 9, 2013
 */
public class WebParserUtils {

    private WebParserUtils() {
        // Forbidden being instantiated.
    }
    
	public static String extractText(String inputHtml) throws Exception {
		StringBuffer text = new StringBuffer();

		Parser parser = Parser.createParser(new String(inputHtml.getBytes(),
				"8859_1"), "8859-1");
		// traverse the nodes
		NodeList nodes = parser.extractAllNodesThatMatch(new NodeFilter() {
			private static final long serialVersionUID = -8133368167194197206L;

			public boolean accept(Node node) {
				return true;
			}
		});
		Node node = nodes.elementAt(0);
		text.append(new String(node.toPlainTextString().getBytes("8859_1")));
		return text.toString();
	}

	public static void testHtml() {
		try {
			String sCurrentLine;
			String sTotalString;
			sCurrentLine = "";
			sTotalString = "";
			java.io.InputStream l_urlStream;
			java.net.URL l_url = new java.net.URL(
					"http://nba.sports.sina.com.cn/showtv.php");
			java.net.HttpURLConnection l_connection = (java.net.HttpURLConnection) l_url.openConnection();
			l_connection.connect();
			l_urlStream = l_connection.getInputStream();
			java.io.BufferedReader l_reader = new java.io.BufferedReader(
					new java.io.InputStreamReader(l_urlStream));
			while ((sCurrentLine = l_reader.readLine()) != null) {
				sTotalString += sCurrentLine;
			}
			
//			System.out.println(sTotalString);
//			System.out.println("====================");
			String testText = extractText(sTotalString);
			System.out.println(testText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Game> parserSinaHtml(String resource) throws Exception {
		System.out.println(resource);
		Parser myParser = new Parser(resource);
		myParser.setEncoding("GBK");
		String filterStr = "table";
		NodeFilter filter = new TagNameFilter(filterStr);
		NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
		Node node = nodeList.elementAt(3);

//		System.out.println(node.toPlainTextString());
//		System.out.println("==============");
		
		String rawdata[] = convert2Array(node.toPlainTextString());
		if (rawdata.length > 0)
			return convert2List(rawdata);
		else
			return null;
	}

	public static String parserTestHtml(String resource) throws Exception {
		Parser myParser = new Parser(resource);
		myParser.setEncoding("GBK");
		String filterStr = "table";
		NodeFilter filter = new TagNameFilter(filterStr);
		NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
		Node node = nodeList.elementAt(2);

		System.out.println(node.toPlainTextString());
		System.out.println("==============");
		return node.toPlainTextString();
	}

	public static String[] convert2Array(String str){
		String[] splitedstr = str.split("\\s+"); /*|[ \t\n\x0B\f\r]*/
		for(int i = 0; i< splitedstr.length; i++)
		{
			Log.v("Convert"+i, splitedstr[i]);
		}
		return splitedstr;
	}
	
	private static List<Game> convert2List(String [] nbadata) {
		List<Game> list = new ArrayList<Game>();
		Game game;

		Log.d("SOURCE string length: ", ""+nbadata.length);

		if(nbadata.length > 10)
		{
			int begin = 0;
			while(true)
			{
				Boolean isBegin = false;
				for (int i = nbadata[begin].length(); --i>0;)
				{
					if (Character.isDigit((nbadata[begin].charAt(i))))
					{
						isBegin = true;
						break;
					}
				}
				if (isBegin)
					break;
				begin++;
			}
			for (int i = begin; i < nbadata.length; i += 7)
			{
				String chanls = nbadata[i+6].replace('/', '\n');		
				int curYear = (Calendar.getInstance()).get(Calendar.YEAR);
				String dtstr = "" + curYear + "-" + nbadata[i] + " " + nbadata[i+1];
				Date date;
				int type;
				if (nbadata[i+3].equals("季前赛"))
					type = 1;
				else if (nbadata[i+3].equals("常规赛"))
					type = 2;
				else if (nbadata[i+3].equals("全明星"))
					type = 3;
				else if (nbadata[i+3].equals("季后赛"))
					type = 4;
				else if (nbadata[i+3].equals("总决赛"))
					type = 5;
				else
					type = 0;
				try {
					date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(dtstr);
					game = new Game(nbadata[i+4], nbadata[i+5], date, type, chanls);
					list.add(game);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("date parser error", dtstr);
				}				
			}
			Log.d("SOURCE nbaData size: ", ""+list.size());
		}

		return list;
	}
	
	public static final int getTeamLogoId(String name) {
		Log.d("TEAM NAME: ", name);
		if(name.equals("老鹰"))
			return R.drawable.teamlogo1;
		else if(name.equals("凯尔特人"))
			return R.drawable.teamlogo2;
		else if (name.equals("黄蜂"))
			return R.drawable.teamlogo3;
		else if (name.equals("公牛"))
			return R.drawable.teamlogo4;
		else if (name.equals("骑士"))
			return R.drawable.teamlogo5;
		else if (name.equals("小牛"))
			return R.drawable.teamlogo6;
		else if (name.equals("掘金"))
			return R.drawable.teamlogo7;
		else if (name.equals("活塞"))
			return R.drawable.teamlogo8;
		else if (name.equals("勇士"))
			return R.drawable.teamlogo9;
		else if (name.equals("火箭"))
			return R.drawable.teamlogo10;
		else if (name.equals("步行者"))
			return R.drawable.teamlogo11;
		else if (name.equals("快船"))
			return R.drawable.teamlogo12;
		else if (name.equals("湖人"))
			return R.drawable.teamlogo13;
		else if (name.equals("热火"))
			return R.drawable.teamlogo14;
		else if (name.equals("雄鹿"))
			return R.drawable.teamlogo15;
		else if (name.equals("森林狼"))
			return R.drawable.teamlogo16;
		else if (name.equals("篮网"))
			return R.drawable.teamlogo17;
		else if (name.equals("尼克斯"))
			return R.drawable.teamlogo18;
		else if (name.equals("魔术"))
			return R.drawable.teamlogo19;
		else if (name.equals("76人"))
			return R.drawable.teamlogo20;
		else if (name.equals("太阳"))
			return R.drawable.teamlogo21;
		else if (name.equals("开拓者"))
			return R.drawable.teamlogo22;
		else if (name.equals("国王"))
			return R.drawable.teamlogo23;
		else if (name.equals("马刺"))
			return R.drawable.teamlogo24;
		else if (name.equals("雷霆"))
			return R.drawable.teamlogo25;
		else if (name.equals("爵士"))
			return R.drawable.teamlogo26;
		else if (name.equals("奇才"))
			return R.drawable.teamlogo27;
		else if (name.equals("猛龙"))
			return R.drawable.teamlogo28;
		else if (name.equals("灰熊"))
			return R.drawable.teamlogo29;
		else if (name.equals("山猫"))
			return R.drawable.teamlogo30;
		else 
			return R.drawable.teamlogo0;
	}
	
	public static void main(String[] args) throws Exception {
		testHtml();
	}
}
