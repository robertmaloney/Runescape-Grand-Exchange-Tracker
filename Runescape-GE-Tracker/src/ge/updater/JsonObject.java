package ge.updater;

import java.util.HashMap;

public class JsonObject {
	
	HashMap<String,Object> properties;
	
	public JsonObject () {
		properties = new HashMap<String,Object>();
	}
	
	public JsonObject(String syntax) {
		properties = parseSyntax(syntax.substring(1,syntax.length()-1));
	}
	
	private HashMap<String,Object> parseSyntax(String syntax) {
		HashMap<String,Object> shell = new HashMap<String,Object>();
		boolean idmode = true;
		String idbuf = "";
		Object valbuf = null;
		for (int i = 0; i < syntax.length(); ++i) {
			switch (syntax.charAt(i)) {
			case ':':
				idmode = false;
				break;
			case ',':
				idbuf = idbuf.substring(idbuf.indexOf("\"")+1,idbuf.lastIndexOf("\""));
				//Need to strip quotes off of value strings
				shell.put(idbuf,valbuf);
				idbuf = "";
				valbuf = null;
				idmode = true;
				break;
			case '[':
				String newsyntax = syntax.substring(i+1);
				int newi = i+1, brackcount = 1;
				for (;newi < syntax.length(); ++newi) {
					if (syntax.charAt(newi) == '[')
						brackcount++;
					else if (syntax.charAt(newi) == ']')
						brackcount--;
					
					if (brackcount == 0)
						break;
				}
				i = newi + 1;
				valbuf = parseSyntax(newsyntax.substring(0,newi));
				break;
			case '{':
				String newsyntax = syntax.substring(i+1);
				int newi = i+1, brackcount = 1;
				for (;newi < syntax.length(); ++newi) {
					if (syntax.charAt(newi) == '{')
						brackcount++;
					else if (syntax.charAt(newi) == '}')
						brackcount--;
					
					if (brackcount == 0)
						break;
				}
				i = newi + 1;
				valbuf = parseSyntax(newsyntax.substring(0,newi));
				break;
			}
		}
	}
}
