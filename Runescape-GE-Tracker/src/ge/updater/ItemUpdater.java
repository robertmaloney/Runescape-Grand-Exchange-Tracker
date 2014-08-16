package ge.updater;

import java.util.HashMap;
import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class ItemUpdater {
	
	private HashMap<String,Integer> items;
	
	public ItemUpdater() {
		items = new HashMap<String,Integer>();
	}
	
	public void update()  throws Exception {
		/*
		JsonObject page = new JsonObject("{\"total\":82,\"items\":[{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=4798\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=4798\",\"id\":4798,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Adamant brutal\",\"description\":\"Blunt adamantite arrow...ouch\",\"current\":{\"trend\":\"neutral\",\"price\":238},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"true\"},{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=810\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=810\",\"id\":810,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Adamant dart\",\"description\":\"A deadly throwing dart with an adamant tip.\",\"current\":{\"trend\":\"neutral\",\"price\":14},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"true\"},{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=829\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=829\",\"id\":829,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Adamant javelin\",\"description\":\"An adamant tipped javelin.\",\"current\":{\"trend\":\"neutral\",\"price\":59},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"false\"},{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=867\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=867\",\"id\":867,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Adamant knife\",\"description\":\"A finely balanced throwing knife.\",\"current\":{\"trend\":\"neutral\",\"price\":38},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"true\"},{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=804\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=804\",\"id\":804,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Adamant throwing axe\",\"description\":\"A finely balanced throwing axe.\",\"current\":{\"trend\":\"neutral\",\"price\":144},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"true\"},{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=31597\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=31597\",\"id\":31597,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Azure skillchompa\",\"description\":\"Can be used to enhance gathering of energy, fish, logs and ore, as a rune-equivalent tool, where applicable.\",\"current\":{\"trend\":\"neutral\",\"price\":377},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"true\"}]}");
		ArrayList<JsonObject> items = (ArrayList<JsonObject>) page.get("items");
		for (int i = 0; i < items.size(); ++i)
			System.out.println(items.get(i).get("id") + ": " + items.get(i).get("name"));
		
		System.out.println(((JsonObject) items.get(0).get("today")).get("price"));
		
		System.out.println("New Json");
		JsonObject page2 = new JsonObject();
		page2.put("total", 92);
		page2.put("ids", new int[]{0, 1, 2, 3, 4});
		page2.put("names", new String[]{"item1", "item2", "item3", "item4", "item5"});
		System.out.println(page2);
		*/
		for (int i = 0; i < 38; ++i) {
			int counts[] = grabAlphaCounts(i);
			for (int j = 0; j < counts.length; ++j) {
				int page = 1;
				while (counts[j] > 0) {
					String alpha = (j == 0) ? "%23" : "" + (char) ('a' + j - 1);
					URL alphas = new URL("http://services.runescape.com/m=itemdb_rs/api/catalogue/items.json?category=" + i + "&alpha=" + alpha + "&page=" + page);
					URLConnection conn = alphas.openConnection();
					BufferedReader in = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
					JsonObject json = new JsonObject(in.readLine());
					//System.out.println("\n" + json.toString());
					ArrayList<JsonObject> items = (ArrayList<JsonObject>) json.get("items");
					//System.out.println(items.size());
					for (int k = 0; k < items.size(); ++k) {
						//System.out.println(items.get(k).toString());
						String name = (String) items.get(k).get("name");
						int id = (int) items.get(k).get("id");
						this.items.put(name, id);
					}
					counts[j] -= items.size();
					++page;
					Thread.sleep(1000);
				}
			}
		}
	}
	
	public String toString() {
		return items.toString();
	}
	
	public int size() {
		return items.size();
	}
	
	private int[] grabAlphaCounts(int category) throws Exception {
		URL alphas = new URL("http://services.runescape.com/m=itemdb_rs/api/catalogue/category.json?category=" + category);
		URLConnection conn = alphas.openConnection();
		BufferedReader in = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
		String jstring = in.readLine();
		//System.out.println(jstring+"\n");
		JsonObject json = new JsonObject(jstring);
		//System.out.println(json.toString());
		int counts[] = new int[27];
		for (int i = 0; i < 26; ++i)
			counts[i] = (int) ((ArrayList<JsonObject>) json.get("alpha")).get(i).get("items");
		return counts;
	}
	
	public static void main (String [] args) throws Exception {
		ItemUpdater iu = new ItemUpdater();
		iu.update();
		System.out.println(iu.toString());
		System.out.println(iu.size());
	}
	
	
}
