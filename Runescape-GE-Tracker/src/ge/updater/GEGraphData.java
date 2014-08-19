package ge.updater;

import java.util.*;

public class GEGraphData {

	private long datecodes[];
	private int prices[];
	private int max, min;
	
	public GEGraphData() {
		datecodes = new long[180];
		prices = new int[180];
	}
	
	public GEGraphData(int size) {
		datecodes = new long[size];
		prices = new int[size];
	}
	
	public void populate(JsonObject vals) {
		Iterator<Map.Entry<String,Object>> entries = vals.entrySet().iterator();
		max = 0;
		min = 2147000000;
		int i = 0;
		while(entries.hasNext()) {
			Map.Entry<String,Object> entry = entries.next();
			datecodes[i] = Long.parseLong(entry.getKey());
			prices[i] = (int) entry.getValue();
			if (prices[i] > max) max = prices[i];
			if (prices[i] < min) min = prices[i];
			++i;
		}
		//System.out.println("Filled with "+i+" prices.");
	}
	
	public long getDateAt(int index) {
		return datecodes[index];
	}
	
	public int getPriceAt(int index) {
		return prices[index];
	}
	
	public int getMax() {
		return max;
	}
	
	public int getMin() {
		return min;
	}
}
