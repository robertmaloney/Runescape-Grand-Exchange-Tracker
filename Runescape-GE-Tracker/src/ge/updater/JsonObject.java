package ge.updater;

import java.util.ArrayList;
import java.util.Iterator;

public class JsonObject extends java.util.HashMap<String, Object> {
	
	public JsonObject () {}
	
	public JsonObject(String syntax) {
		parseSyntax(syntax.substring(1,syntax.length()-1));
	}
	
	private void parseSyntax(String syntax) {
		final boolean debug = false;
		final int MODE_STRING = 0, MODE_INT = 1, MODE_ARR = 2, MODE_SKIP = -1;
		
		ArrayList<Object> list = null;
		boolean idmode = true, ignorecommas = false;
		int mode = MODE_INT;
		String idbuf = "";
		Object valbuf = null;
		
		for (int i = 0; i < syntax.length(); ++i) {
			if (debug) System.out.println(""+i+": "+syntax.charAt(i));
			switch (syntax.charAt(i)) {
			case ':':
				idmode = false;
				mode = MODE_INT;
				break;
			case '}':
			case ',':
				if (debug) System.out.println("Adding " + idbuf + ": " + valbuf + ", mode " + mode);
				if (ignorecommas) {
					if (valbuf == null)
						valbuf = "" + syntax.charAt(i);
					else
						valbuf = valbuf.toString() + syntax.charAt(i);
				} else {
					//Need to strip quotes off of value strings
					if (mode == MODE_STRING) {
						this.put(idbuf,valbuf);
						idbuf = "";
					} else if (mode == MODE_INT) {
						this.put(idbuf,Integer.parseInt((String)valbuf));
						idbuf = "";
					} else if (mode == MODE_ARR) {
						if (debug) System.out.println("Adding element to array.");
						list.add(valbuf);
					} else {
						mode = MODE_INT;
					}
					valbuf = null;
					idmode = true;
				}
				break;
			case '[':
				mode = MODE_ARR;
				list = new ArrayList<Object>();
				if (debug) System.out.println("Starting array " + idbuf);
				break;
			case ']':
				mode = MODE_SKIP;
				if (debug) System.out.println("Adding array " + idbuf);
				list.add(valbuf);
				this.put(idbuf, list);
				break;
			case '{':
				if (mode != MODE_ARR) mode = MODE_STRING;
				String newsyntax = syntax.substring(i);
				int newi = 0, brackcount = 0;
				for (;newi < newsyntax.length(); ++newi) {
					if (newsyntax.charAt(newi) == '{')
						brackcount++;
					else if (newsyntax.charAt(newi) == '}')
						brackcount--;
					
					if (brackcount == 0)
						break;
				}
				i = Math.min(i + newi, syntax.length());
				if (debug) System.out.println("Recursing...");
				valbuf = new JsonObject(newsyntax.substring(0,newi+2));
				if (debug) System.out.println("Out of recursion.");
				break;
			case '\"':
				if (!idmode) {
					ignorecommas = !ignorecommas;
					mode = MODE_STRING;
				}
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
	}
	
	//change to output syntactically correct json
	public String toString() {
		String res = "{";
		Iterator<String> keys = this.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			res += "\"" + key + "\":";
			Object value = this.get(key);
			if (value instanceof String)
				res += "\"" + value + "\"";
			else if (value instanceof Integer)
				res += value;
			else if (value instanceof ArrayList<?>) {
				ArrayList<JsonObject> list = (ArrayList<JsonObject>) value;
				res += "[";
				for (int i = 0; i < list.size(); ++i) {
					res += list.get(i).toString();
					if (i != list.size() - 1)
						res += ",";
				}
				res += "]";
			} else if (value instanceof JsonObject)
				res += ((JsonObject) value).toString();
			
			if (keys.hasNext()) res += ",";
		}
		res += "}";
		return res;
	}
}
