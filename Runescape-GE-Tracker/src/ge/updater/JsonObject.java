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
		boolean idmode = true, ignorecommas = false;
		int mode = MODE_STRING;
		String idbuf = "";
		Object valbuf = null;
		for (int i = 0; i < syntax.length(); ++i) {
			System.out.println(""+i+": "+syntax.charAt(i));
			switch (syntax.charAt(i)) {
			case ':':
				idmode = false;
				break;
			case '}':
			case ',':
				//idbuf = idbuf.substring(idbuf.indexOf("\"")+1,idbuf.lastIndexOf("\""));
				if (ignorecommas) {
					if (valbuf == null)
						valbuf = "" + syntax.charAt(i);
					else
						valbuf = valbuf.toString() + syntax.charAt(i);
				} else {
					//Need to strip quotes off of value strings
					if (mode == MODE_STRING) {
						shell.put(idbuf,valbuf);
						idbuf = "";
					} else if (mode == MODE_INT) {
						shell.put(idbuf,valbuf);
						idbuf = "";
					} else if (mode == MODE_ARR) {
						System.out.println("Adding element to array.");
						list.add(valbuf);
					} else {
						mode = MODE_STRING;
					}
					valbuf = null;
					idmode = true;
				}
				break;
			case '[':
				mode = MODE_ARR;
				list = new ArrayList<Object>();
				System.out.println("Starting array " + idbuf);
				break;
			case ']':
				mode = MODE_SKIP;
				System.out.println("Adding array " + idbuf);
				list.add(valbuf);
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
				System.out.println("Recursing...");
				valbuf = parseSyntax(newsyntax.substring(0,newi+1));
				System.out.println("Out of recursion.");
				break;
			case '\"':
				if (!idmode)
					ignorecommas = !ignorecommas;
				break;
			case '\n':
				break;
			case ' ':
				if (ignorecommas) {
					if (valbuf == null)
						valbuf = "" + syntax.charAt(i);
					else
						valbuf = valbuf.toString() + syntax.charAt(i);
				}
				break;
			default:
				if (idmode)
					idbuf += syntax.charAt(i);
				else
					if (valbuf == null)
						valbuf = "" + syntax.charAt(i);
					else
						valbuf = valbuf.toString() + syntax.charAt(i);
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
