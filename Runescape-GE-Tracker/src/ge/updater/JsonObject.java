package ge.updater;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonObject {
	
	HashMap<String,Object> properties;
	
	public JsonObject () {
		properties = new HashMap<String,Object>();
	}
	
	public JsonObject(String syntax) {
		properties = parseSyntax(syntax.substring(1,syntax.length()-1));
	}
	
	private HashMap<String,Object> parseSyntax(String syntax) {
		final int MODE_STRING = 0, MODE_INT = 1, MODE_ARR = 2, MODE_SKIP = -1;
		HashMap<String,Object> shell = new HashMap<String,Object>();
		ArrayList<Object> list = null;
		boolean idmode = true;
		int mode = MODE_STRING;
		String idbuf = "";
		Object valbuf = null;
		for (int i = 0; i < syntax.length(); ++i) {
			System.out.println(""+i+": "+syntax.charAt(i));
			switch (syntax.charAt(i)) {
			case ':':
				idmode = false;
				break;
			case ',':
				//idbuf = idbuf.substring(idbuf.indexOf("\"")+1,idbuf.lastIndexOf("\""));
				//Need to strip quotes off of value strings
				if (mode == MODE_STRING)
					shell.put(idbuf,valbuf);
				else if (mode == MODE_INT) {
					shell.put(idbuf,valbuf);
				} else if (mode == MODE_ARR) {
					list.add(valbuf);
				}
				idbuf = "";
				valbuf = null;
				idmode = true;
				mode = MODE_STRING;
				break;
			case '[':
				mode = MODE_ARR;
				list = new ArrayList<Object>();
				break;
			case ']':
				mode = MODE_SKIP;
				shell.put(idbuf, list);
				break;
			case '{':
				String newsyntax = syntax.substring(i+1);
				int newi = 0, brackcount = 1;
				for (;newi < newsyntax.length(); ++newi) {
					if (newsyntax.charAt(newi) == '{')
						brackcount++;
					else if (newsyntax.charAt(newi) == '}')
						brackcount--;
					
					if (brackcount == 0)
						break;
				}
				i = Math.min(i + newi + 1, syntax.length());
				valbuf = parseSyntax(newsyntax.substring(0,newi));
				break;
			case ' ':
			case '\n':
				break;
			default:
				if (idmode)
					idbuf += syntax.charAt(i);
				else
					valbuf = (String) valbuf + syntax.charAt(i);
				break;
			}
		}
		return shell;
	}
	
	public String toString() {
		String res = "";
		Iterator<String> keys = properties.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			res += key + "\n";
		}
		return res;
	}
}
