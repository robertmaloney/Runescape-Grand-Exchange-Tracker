package ge.updater;

import java.util.ArrayList;
import java.util.Iterator;

public class JsonObject extends java.util.HashMap<String, Object> {
	
	public JsonObject () {}
	
	public JsonObject(String syntax) {
		parseSyntax(syntax.substring(1,syntax.length()-1));
	}
	
	/*
	public Object put(String key, Object value) {
		if (!(value instanceof String) && !(value instanceof Number)
			&& !(value instanceof JsonObject) && !(value instanceof ArrayList))
			return null;
		else
			return super.put(key, value);
	}
	*/
	
	/**
	 * parseSyntax takes as input a string of syntactically correct Json and fills
	 * the HashMap with its key-value pairs.
	 * <p>
	 * This is achieved by a combination of iteration and recursion. The method
	 * iterates through the string, copying unimportant characters into the idbuf
	 * and valbuf, which are buffers for a String and Object, respectively. If a
	 * special character is found, the following is performed:
	 * -'}', ',': Write the buffers into the HashMap and clear them
	 * -':': Start writing to valbuf, and default to MODE_INT
	 * -'[': Begin filling Json array, set to MODE_ARR
	 * -']': Write Json array to HashMap and skip next comma
	 * -'{': Recurse into a new JsonObject
	 * -'"': Switch to MODE_STRING and turn off/on ignoring commas
	 * 
	 * @return void
	 * @author RobertMaloney
	 * @param syntax the Json string to be parsed
	 */
	private void parseSyntax(String syntax) {
		// Prints strings if debugging
		final boolean debug = false;
		if (debug) System.out.println(syntax);
		
		// These modes dictate what is put into the HashMap
		final int MODE_STRING = 0, MODE_INT = 1, MODE_ARR = 2, MODE_SKIP = -1;
		
		// Helper variables
		ArrayList<Object> list = null;
		boolean idmode = true, ignorecommas = false;
		int mode = MODE_INT;
		String idbuf = "";
		Object valbuf = null;
		
		// Iterate over the string
		for (int i = 0; i < syntax.length(); ++i) {
			if (debug) System.out.println(""+i+": "+syntax.charAt(i));
			switch (syntax.charAt(i)) {
			// Start writing to valbuf, and default to MODE_INT
			case ':':
				idmode = false;
				mode = MODE_INT;
				break;
				
			// Write the buffers into the HashMap and clear them
			case '}':
			case ',':
				if (debug) System.out.println("Adding " + idbuf + ": " + valbuf + ", mode " + mode);
				
				// Add commas to valbuf if they are unimportant
				if (ignorecommas) {
					if (valbuf == null)
						valbuf = "" + syntax.charAt(i);
					else
						valbuf = valbuf.toString() + syntax.charAt(i);
				} else {
					// Strings are saved directly, numbers are parsed
					if (mode == MODE_STRING) {
						this.put(idbuf,valbuf);
						idbuf = "";
					} else if (mode == MODE_INT) {
						this.put(idbuf,Integer.parseInt((String)valbuf));
						idbuf = "";
					// We save the array separately and use this to fill it
					// idbuf isn't reset because we need it at the end of the array
					} else if (mode == MODE_ARR) {
						if (debug) System.out.println("Adding element to array.");
						list.add(valbuf);
					}
					// Reset valbuf
					valbuf = null;
					idmode = true;
				}
				break;
				
			// Begin filling Json array, set to MODE_ARR
			case '[':
				mode = MODE_ARR;
				list = new ArrayList<Object>();
				if (debug) System.out.println("Starting array " + idbuf);
				break;
				
			// Write Json array to HashMap and skip next comma
			case ']':
				mode = MODE_SKIP;
				if (debug) System.out.println("Adding array " + idbuf);
				list.add(valbuf);
				this.put(idbuf, list);
				idbuf = "";
				break;
				
			// Recurse into a new JsonObject
			case '{':
				// Change to MODE_STRING if valbuf isn't an array
				if (mode != MODE_ARR) mode = MODE_STRING;
				
				// Cut out the Json child object from the mother string
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
				
				// Place i after the child object
				i = Math.min(i + newi, syntax.length());
				
				if (debug) System.out.println("Recursing...");
				
				// Parse child
				valbuf = new JsonObject(newsyntax.substring(0,newi+2));
				
				if (debug) System.out.println("Out of recursion.");
				break;
				
			// Switch to MODE_STRING and turn off/on ignoring commas
			case '\"':
				if (!idmode) {
					ignorecommas = !ignorecommas;
					mode = MODE_STRING;
				}
				break;
			case '\n':
				break;
				
			// We only care about spaces when they are in a String
			case ' ':
				if (ignorecommas) {
					if (valbuf == null)
						valbuf = "" + syntax.charAt(i);
					else
						valbuf = valbuf.toString() + syntax.charAt(i);
				}
				break;
				
			// Write any content characters
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
	
	/**
	 * Outputs syntactically correct Json Object code by iterating through
	 * all keys and ArrayLists and concatenating them to a result String.
	 * @author RobertMaloney
	 * @return String
	 */
	public String toString() {
		String res = "{";
		Iterator<String> keys = this.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			//System.out.println(key);
			res += "\"" + key + "\":";
			Object value = this.get(key);
			if (value instanceof String)
				res += "\"" + value + "\"";
			else if (value instanceof Integer)
				res += value;
			else if (value instanceof Object[]) {
				Object[] list = (Object[]) value;
				res += "[";
				for (int i = 0; i < list.length; ++i) {
					res += list[i].toString();
					if (i != list.length - 1)
						res += ",";
				}
				res += "]";
			} else if (value instanceof ArrayList<?>) {
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
