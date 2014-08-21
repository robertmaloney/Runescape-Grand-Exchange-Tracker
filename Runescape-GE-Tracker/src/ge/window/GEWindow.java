package ge.window;

import ge.updater.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

public class GEWindow extends JFrame {

	JsonObject allitems;
	JComboBox<String> search_item;
	GraphPanel yellowLabel;
	JList<String> itemlist;
	DefaultListModel<String> items;
	ArrayList<GEGraphData> graphdata;
	
	private void createAndShowGUI() {
		
		JFrame prepFrame = new JFrame();
        prepFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel preptext = new JLabel("Grabbing Grand Exchange Data...");
		preptext.setOpaque(true);
		preptext.setPreferredSize(new Dimension(250, 100));
		prepFrame.getContentPane().add(preptext, BorderLayout.CENTER);
		preptext.setBackground(new Color(0,0,0));

		prepFrame.pack();
		prepFrame.setLocation(1920/2 - prepFrame.getSize().width/2, 1080/2 - prepFrame.getSize().height/2);
		//prepFrame.setVisible(true);
		
		graphdata = new ArrayList<GEGraphData>();
        //Create and set up the window.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
 
        // Menu Bar
        JMenuBar greenMenuBar = new JMenuBar();
        greenMenuBar.setOpaque(true);
        greenMenuBar.setBackground(new Color(154, 165, 127));
        greenMenuBar.setPreferredSize(new Dimension(500, 20));
 
        //Create a yellow label to put in the content pane.
        yellowLabel = new GraphPanel();
        yellowLabel.setOpaque(true);
        yellowLabel.setBackground(new Color(248, 213, 131));
        yellowLabel.setPreferredSize(yellowLabel.getPreferredSize());
        yellowLabel.addMouseMotionListener(yellowLabel);
 
        //Set the menu bar and add the label to the content pane.
        this.setJMenuBar(greenMenuBar);
        //frame.getContentPane().add(yellowLabel, BorderLayout.CENTER);
 
        JPanel itemfinder = new JPanel();
        JLabel searchlabel = new JLabel("Find item:");
        search_item = new JComboBox<String>();
        search_item.setPreferredSize(new Dimension(300, 26));
        search_item.addItem("Select Item...");
        
        // Insert items
        String jstring = "";
        try {
        	BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream( "AllItems.json" )));
        	String line;
        	while ((line = br.readLine()) != null) jstring += line;
        	br.close();
        } catch (Exception e) {
        	
        }
        allitems = new JsonObject(jstring);
        Iterator<String> keys = allitems.keySet().iterator();
        while (keys.hasNext()) search_item.addItem(keys.next());
        
        JButton addbutton = new JButton("Add");
        addbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (search_item.getSelectedIndex() != 0) {
					String newitem = (String) search_item.getSelectedItem();
					if (!items.contains(newitem)) {
						items.addElement(newitem);
						grabGraph((int)allitems.get(newitem));
					}
				}
			}
        	
        });
        itemfinder.add(searchlabel, BorderLayout.LINE_END);
        itemfinder.add(search_item, BorderLayout.LINE_END);
        itemfinder.add(addbutton, BorderLayout.LINE_END);
        this.getContentPane().add(itemfinder, BorderLayout.NORTH);
        
        items = new DefaultListModel<String>();
        items.addElement("Armadyl chaps");
        for (int i = 0; i < 10; ++i)
        	items.addElement(search_item.getItemAt(i+1));
        
        itemlist = new JList<String>( items );
        JScrollPane itemscroll = new JScrollPane(itemlist);
        itemlist.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				//String output = "Data:";
				if (itemlist.getSelectedIndex() > -1) {
					GEGraphData d = graphdata.get(itemlist.getSelectedIndex());
					/*
					for (int i = 0; i < 180; ++i) {
						String sdf = new SimpleDateFormat("MMM dd").format(new Date(d.getDateAt(i)));
						output += "Date: " + sdf + ", Price: " + d.getPriceAt(i) + "\n";
					}
					*/
					yellowLabel.displayGraph(d, itemlist.getSelectedValue());
				}
			}
        	
        });
        JPanel itemoptions = new JPanel();
        JButton removeitem = new JButton("Remove Item");
        removeitem.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int index = itemlist.getSelectedIndex();
				if (index > -1) {
					items.removeElementAt(index);
					graphdata.remove(graphdata.get(index));
					//itemlist.updateUI();
					System.out.println("ItemList Size: " + graphdata.size());
				}
			}
        	
        });
        itemoptions.add(removeitem);
        JSplitPane items_n_options = new JSplitPane(JSplitPane.VERTICAL_SPLIT, itemscroll, itemoptions);
        items_n_options.setDividerSize(0);
        items_n_options.setDividerLocation(340);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, items_n_options, yellowLabel);
        split.setDividerSize(0);
        split.setDividerLocation(225);
        this.getContentPane().add(split);
        
        // Grab graph data for current items
        for (int i = 0; i < items.size(); ++i)
        	grabGraph((int) allitems.get(items.getElementAt(i)));
        
        //Display the window
        //prepFrame.setVisible(false);
        //prepFrame.dispose();
        
        this.pack();
		this.setLocation(1920/2 - this.getSize().width/2, 1080/2 - this.getSize().height/2);
        this.setVisible(true);
    }
	
	private void grabGraph(int itemid) {
		//System.out.println(itemid);
		String jstring = null;
		while (jstring == null) {
			try {
				URL alphas = new URL("http://services.runescape.com/m=itemdb_rs/api/graph/" + itemid + ".json");
				URLConnection conn = alphas.openConnection();
				BufferedReader in = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
				jstring = in.readLine();
				in.close();
				//Thread.sleep(500);
			} catch (Exception e) {
				
			}
		}
		GEGraphData thedata = new GEGraphData();
		thedata.populate((JsonObject) (new JsonObject(jstring)).get("daily"));
		graphdata.add(thedata);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				GEWindow ge = new GEWindow();
				ge.setTitle("GE Window");
				ge.createAndShowGUI();
			}
		});
	}

}

class GraphPanel extends JPanel implements MouseMotionListener {
	private int max, min;
	private ArrayList<Point> points;
	private ArrayList<Integer> prices;
	private GEGraphData data;
	private String name;
	private int totalPoints = 0;
	
	public GraphPanel() {}
	
	public Dimension getPreferredSize() {
        return new Dimension(500, 380);
    }
    
    public void displayString(String out) {
    	super.paint(this.getGraphics());
    	this.getGraphics().drawString(out, 10, 20);
    }
    
    public void displayGraph(GEGraphData gd, String itemname) {
    	data = gd; name = itemname;
    	repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	if (data != null) {
	    	
	    	points = new ArrayList<Point>();
	    	prices = new ArrayList<Integer>();
	    	
	    	Font font = new Font("SansSerif", Font.BOLD, 14);
	    	g.setFont(font);
	    	FontMetrics fm = g.getFontMetrics();
	    	
	    	this.setScale(data.getMax(), data.getMin());
	    	
	    	final int LEFT = 63, RIGHT = 363, TOP = 30, BOTTOM = 250;
	    	// Title
	    	g.drawString(name, LEFT + (RIGHT - LEFT)/2 - fm.stringWidth(name)/2, TOP - 10);
	
	    	font = new Font("SansSerif", Font.BOLD, 12);
	    	g.setFont(font);
	    	fm = g.getFontMetrics();
	    	
	    	// Border Lines
	    	g.drawLine(LEFT, TOP, LEFT, BOTTOM);
	    	g.drawLine(LEFT, BOTTOM, RIGHT, BOTTOM);
	    	
	    	// Intervals
	    	g.drawLine(LEFT, BOTTOM, LEFT, BOTTOM + 10);
	    	g.drawLine(LEFT + (RIGHT - LEFT)/4, BOTTOM, LEFT + (RIGHT - LEFT)/4, BOTTOM + 10);
	    	g.drawLine(LEFT + (RIGHT - LEFT)/2, BOTTOM, LEFT + (RIGHT - LEFT)/2, BOTTOM + 10);
	    	g.drawLine(LEFT + 3*(RIGHT - LEFT)/4, BOTTOM, LEFT + 3*(RIGHT - LEFT)/4, BOTTOM + 10);
	    	g.drawLine(RIGHT, BOTTOM, RIGHT, BOTTOM + 10);
	
	    	g.drawLine(LEFT - 10, TOP, LEFT, TOP);
	    	g.drawLine(LEFT - 10, TOP + (BOTTOM - TOP)/4, LEFT, TOP + (BOTTOM - TOP)/4);
	    	g.drawLine(LEFT - 10, TOP + (BOTTOM - TOP)/2, LEFT, TOP + (BOTTOM - TOP)/2);
	    	g.drawLine(LEFT - 10, TOP + 3*(BOTTOM - TOP)/4, LEFT, TOP + 3*(BOTTOM - TOP)/4);
	    	g.drawLine(LEFT - 10, BOTTOM, LEFT, BOTTOM);
	    	
	    	// Min and Max values
	    	String maxstr = formatPrice(max), minstr = formatPrice(min), midstr = formatPrice((max+min) / 2);
	    	g.drawString(maxstr, LEFT - 12 - fm.stringWidth(maxstr), TOP + 5);
	    	g.drawString(midstr, LEFT - 12 - fm.stringWidth(midstr), (TOP + BOTTOM + 10) / 2);
	    	g.drawString(minstr, LEFT - 12 - fm.stringWidth(minstr), BOTTOM + 5);
	    	
	    	// Grid Lines
	    	g.setColor(new Color(125,125,125,125));
	    	g.drawLine(LEFT + (RIGHT - LEFT)/4, TOP, LEFT + (RIGHT - LEFT)/4, BOTTOM);
	    	g.drawLine(LEFT + (RIGHT - LEFT)/2, TOP, LEFT + (RIGHT - LEFT)/2, BOTTOM);
	    	g.drawLine(LEFT + 3*(RIGHT - LEFT)/4, TOP, LEFT + 3*(RIGHT - LEFT)/4, BOTTOM);
	    	
	    	g.drawLine(LEFT, TOP + (BOTTOM - TOP)/4, RIGHT, TOP + (BOTTOM - TOP)/4);
	    	g.drawLine(LEFT, TOP + (BOTTOM - TOP)/2, RIGHT, TOP + (BOTTOM - TOP)/2);
	    	g.drawLine(LEFT, TOP + 3*(BOTTOM - TOP)/4, RIGHT, TOP + 3*(BOTTOM - TOP)/4);
	    	
	    	// Set plot dimensions
	    	int samples = 30;
	    	final int OFFSET = 169 - samples - 1;
	    	
	    	// Date interval labels
	    	g.setColor(new Color(0, 0, 0));
	    	int j = 0;
	    	double i;
	    	for (i = OFFSET; i < OFFSET + samples + 1; i += samples / 4.0) {
				String sdf = new SimpleDateFormat("MMM dd").format(new Date(data.getDateAt((int)i)));
				g.drawString(sdf, LEFT - fm.stringWidth(sdf)/2 + j*(RIGHT-LEFT)/4, BOTTOM + 22);
				++j;
	    	}
	    	
	    	// Plot the curve
	    	g.setColor(new Color(54, 99, 8));
	    	for (i = OFFSET; i < OFFSET + samples; ++i) {
	    		int x0 = (int) ((i-OFFSET)/samples * (RIGHT - LEFT) + LEFT), x1 = (int) ((i - OFFSET + 1)/samples * (RIGHT - LEFT) + LEFT);
	    		int y0 = this.calcPoint(data.getPriceAt((int)i), TOP, BOTTOM), y1 = this.calcPoint(data.getPriceAt((int)i+1), TOP, BOTTOM);
	    		g.fillRect(x0-2, y0-2, 4, 4);
	    		points.add(new Point(x0,y0));
	    		prices.add(data.getPriceAt((int)i));
	    		g.drawLine(x0, y0, x1, y1);
	    	}
	    	g.fillRect((int) ((i-OFFSET)/samples * (RIGHT - LEFT) + LEFT)-2, this.calcPoint(data.getPriceAt((int)i), TOP, BOTTOM)-2, 4, 4);
    		points.add(new Point((int) ((i-OFFSET)/samples * (RIGHT - LEFT) + LEFT), this.calcPoint(data.getPriceAt((int)i), TOP, BOTTOM)));
    		prices.add(data.getPriceAt((int)i));
	    }
    }
    
    private String formatPrice(int price) {
    	if (price < 10000) return ""+price;
    	if (price < 1000000) return (price / 1000) + "K";
    	if (price < 100000000) return (double) (price - (price % 100000)) / 1000000 + "M";
    	
    	return price / 1000000 + "M";
    }
    
    private int calcPoint(int price, int top, int bottom) {
    	int range = bottom - top;
    	return (int) (bottom - ((double) (price - min) / (max - min)) * range);
    }
    
    private void setScale(int tmax, int tmin) {
    	max = tmax+(int)Math.pow(10,(""+tmax).length()-1)/2;
    	min = tmin-(int)Math.pow(10,(""+tmin).length()-1)/2;
    }

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stubs
		DecimalFormat fm = new DecimalFormat("#,###");
		if (points != null) {
			boolean found = false;
			for (int i = 0; i < points.size(); ++i) {
				int x = (e.getX() - points.get(i).x)*(e.getX() - points.get(i).x);
				int y = (e.getY() - points.get(i).y)*(e.getY() - points.get(i).y);
				if (x + y < 4) {
					this.setToolTipText(fm.format(prices.get(i)));
					found = true;
				}
			}
			if (!found) this.setToolTipText("");
		}
	}
}