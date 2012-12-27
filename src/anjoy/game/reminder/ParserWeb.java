package anjoy.game.reminder;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

public class ParserWeb {
	/**
	 * 
	 * @param inputHtml
	 * @return
	 */
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

	public static String parserSinaHtml(String resource) throws Exception {
		System.out.println(resource);
		Parser myParser = new Parser(resource);
		myParser.setEncoding("GBK");
		String filterStr = "table";
		NodeFilter filter = new TagNameFilter(filterStr);
		NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
		Node node = nodeList.elementAt(3);

//		System.out.println(node.toPlainTextString());
//		System.out.println("==============");
		return node.toPlainTextString();
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

	public static void main(String[] args) throws Exception {
		testHtml();
	}
}
