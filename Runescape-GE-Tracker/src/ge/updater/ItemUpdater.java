package ge.updater;

import java.util.HashMap;
import java.util.ArrayList;

public class ItemUpdater {
	
	HashMap<String,Integer> items;
	
	public static void update() {
		JsonObject page = new JsonObject("{\"total\":82,\"items\":[{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=4798\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=4798\",\"id\":4798,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Adamant brutal\",\"description\":\"Blunt adamantite arrow...ouch\",\"current\":{\"trend\":\"neutral\",\"price\":238},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"true\"},{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=810\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=810\",\"id\":810,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Adamant dart\",\"description\":\"A deadly throwing dart with an adamant tip.\",\"current\":{\"trend\":\"neutral\",\"price\":14},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"true\"},{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=829\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=829\",\"id\":829,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Adamant javelin\",\"description\":\"An adamant tipped javelin.\",\"current\":{\"trend\":\"neutral\",\"price\":59},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"false\"},{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=867\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=867\",\"id\":867,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Adamant knife\",\"description\":\"A finely balanced throwing knife.\",\"current\":{\"trend\":\"neutral\",\"price\":38},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"true\"},{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=804\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=804\",\"id\":804,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Adamant throwing axe\",\"description\":\"A finely balanced throwing axe.\",\"current\":{\"trend\":\"neutral\",\"price\":144},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"true\"},{\"icon\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_sprite.gif?id=31597\",\"icon_large\":\"http://services.runescape.com/m=itemdb_rs/4551_obj_big.gif?id=31597\",\"id\":31597,\"type\":\"Ammo\",\"typeIcon\":\"http://www.runescape.com/img/categories/Ammo\",\"name\":\"Azure skillchompa\",\"description\":\"Can be used to enhance gathering of energy, fish, logs and ore, as a rune-equivalent tool, where applicable.\",\"current\":{\"trend\":\"neutral\",\"price\":377},\"today\":{\"trend\":\"neutral\",\"price\":0},\"members\":\"true\"}]}");
		System.out.println(page.toString());
		System.out.println(page.properties.get("total"));
		ArrayList<HashMap<String,Object>> items = (ArrayList<HashMap<String,Object>>) page.properties.get("items");
		for (int i = 0; i < items.size(); ++i)
			System.out.println(items.get(i).get("id") + ": " + items.get(i).get("name"));
	}
	
	public static void main (String [] args) {
		update();
	}
}
