package svn;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import org.dom4j.*;
import org.dom4j.io.*;

public class SVN {

	public HashMap readXMLfile(File file) {

		try {

			SAXReader reader = new SAXReader();
			Document doc = reader.read(file);
			Element root = doc.getRootElement();
			Element foo;
			HashMap records = new HashMap();
			for (Iterator i = root.elementIterator("logentry"); i.hasNext();) {

				ArrayList fileList = new ArrayList();
				foo = (Element) i.next();

				String author = foo.elementText("author");
				String dateUTC = foo.elementText("date");
				String comment = foo.elementText("msg");

				// 2014-07-16T02:09:55.595436Z
				String dateLocal = utc2Local(dateUTC,
						"yyyy-MM-dd'T'HH:mm:ss'.'SSSS'Z'",
						"yyyy-MM-dd HH:mm:ss");

				for (Iterator j = foo.elementIterator("paths"); j.hasNext();) {
					Element elementInner = (Element) j.next();
					List<Element> files = elementInner.elements();
					for (Element e : files) {

						List<Attribute> listAttr = e.attributes();
						for (Attribute attr : listAttr) {
							String name = attr.getName();
							String value = attr.getValue();
							if (name.equals("action")) {
								fileList.add("<br/>" + value + ": "
										+ e.getTextTrim());
							}
						}
					}

				}
				records.put(author + "@@" + dateLocal + "@@" + comment,
						fileList);

			}

			return records;
		} catch (DocumentException e) {
			// e.printStackTrace();
			System.out.println("parse xml file faild!");
			return null;
		}
	}

	public void writeHTML(List infoIds, String path) {
		
		StringBuilder sb = new StringBuilder();

		sb.append("<table style=\"border-collapse: collapse;\">");
		sb.append("<tr>");
		sb.append("<th style=\"border: 1px solid black;\">Author</th><th style=\"border: 1px solid black;\">Date</th>");
		sb.append("<th style=\"border: 1px solid black;\">Comment</th><th style=\"border: 1px solid black;\">FileList</th>");
		sb.append("</tr>");
		
		Iterator itx = ((ArrayList) infoIds).iterator();
		while (itx.hasNext()) {

			Map.Entry map = (Entry) itx.next();

			String key = (String) map.getKey();
			Object val = map.getValue();

			String[] info = key.split("@@");
			sb.append("<tr>");
			
			sb.append("<td style=\"border: 1px solid black;width:100px;height:100px;\">");
			sb.append(info[0]);
			sb.append("</td>");
			
			sb.append("<td style=\"border: 1px solid black;width:160px;height:100px;\">");
			sb.append(info[1]);
			sb.append("</td>");
			
			sb.append("<td style=\"border: 1px solid black;width:360px;height:100px;\">");
			sb.append(info[2]);
			sb.append("</td>");

			Iterator it1 = ((ArrayList) val).iterator();
			sb.append("<td style=\"border: 1px solid black;width:1200px;height:100px;\">");
			while (it1.hasNext()) {
				sb.append(it1.next());
			}
			sb.append("</td>");

			sb.append("</tr>");

		}
		sb.append("</table>");
		FileWriter fw;
		try {
			String filePath = path + ".html";
			fw = new FileWriter(filePath);
			fw.write(sb.toString());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String utc2Local(String utcTime, String utcTimePatten,
			String localTimePatten) {
		SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
		utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));// 时区定义并进行时间获取
		Date gpsUTCDate = null;
		try {
			gpsUTCDate = utcFormater.parse(utcTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
		localFormater.setTimeZone(TimeZone.getDefault());
		String localTime = localFormater.format(gpsUTCDate.getTime());
		return localTime;
	}

	public List recordSortByDate(HashMap records) {

		List<Map.Entry<String, List>> infoIds = new ArrayList<Map.Entry<String, List>>(
				records.entrySet());

		// 排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, List>>() {
			public int compare(Map.Entry<String, List> o1,
					Map.Entry<String, List> o2) {
				// return (o2.getValue() - o1.getValue());
				String[] tmp1 = o1.getKey().split("@@");
				String[] tmp2 = o2.getKey().split("@@");
				// System.out.println(o1.getKey());
				return (tmp1[1]).toString().compareTo(tmp2[1]);
			}
		});

		return infoIds;
	}

	public static void main(String[] args) {
		SVN svn = new SVN();
		// args[0] = "D:\\logz.xml";
		//String filePath = "P:\\xx oo\\123456.xml";
		String filePath = args[0];
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("file not exit!");
			return;
		}

		String folder = file.getParent();
		String fileNameTmp = file.getName();
		String[] tmp = fileNameTmp.split(".xml");
		String fileName = folder+"\\"+ tmp[0];
		
		HashMap records = svn.readXMLfile(file);
		if (records != null) {

			List infolist = svn.recordSortByDate(records);
			svn.writeHTML(infolist, fileName);
		}
		else{
			System.out.println("no commits!");
		}
	}

}
