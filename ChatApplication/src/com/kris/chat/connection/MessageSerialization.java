package com.kris.chat.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageSerialization {

	public synchronized static String createMessage(String... arguments) {
		StringBuffer stringBuffer = new StringBuffer();
		if (arguments.length > 0) {
			stringBuffer.append("\"");
			stringBuffer.append(arguments[0]);
			stringBuffer.append("\"");
			for (int i = 1; i < arguments.length; i++) {
				stringBuffer.append(" ");
				stringBuffer.append("\"");
				stringBuffer.append(arguments[i]);
				stringBuffer.append("\"");
			}
		}

		return stringBuffer.toString();
	}

	public synchronized static String unquote(String s) {

		 if (s != null
		   && ((s.startsWith("\"") && s.endsWith("\""))
		   || (s.startsWith("'") && s.endsWith("'")))) {

		  s = s.substring(1, s.length() - 1);
		 }
		 return s;
		}

	public synchronized static List<String> splitMessage(String message) {
		List<String> list = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(message);
		while (m.find()) {
			String token = m.group(1);
			list.add(unquote(token));
		}

		return list;
	}
}
