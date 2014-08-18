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
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

public class GEWindow extends JFrame {

	JsonObject allitems;
	JComboBox<String> search_item;
	JLabel yellowLabel;
	JList<String> itemlist;
	DefaultListModel<String> items;
	ArrayList<GEGraphData> graphdata;
	
	private void createAndShowGUI() {
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
        yellowLabel = new JLabel();
        yellowLabel.setOpaque(true);
        yellowLabel.setBackground(new Color(248, 213, 131));
        yellowLabel.setPreferredSize(new Dimension(500, 380));
 
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
					if (!items.contains(newitem))
						items.addElement(newitem);
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
				String output = "<html>Data:<br>";
				GEGraphData d = graphdata.get(itemlist.getSelectedIndex());
				for (int i = 0; i < 180; ++i) {
					String sdf = new SimpleDateFormat("MMM dd").format(new Date(d.getDateAt(i)));
					output += "Date: " + sdf + ", Price: " + d.getPriceAt(i) + "<br>";
				}
				yellowLabel.setText(output+"</html>");
			}
        	
        });
        JPanel itemoptions = new JPanel();
        JButton removeitem = new JButton("Remove Item");
        removeitem.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int index = itemlist.getSelectedIndex();
				if (index > -1)
					items.removeElementAt(index);
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
        this.pack();
        this.setVisible(true);
    }
	
	private void grabGraph(int itemid) {
		String jstring = null;
		while (jstring == null) {
			try {
				URL alphas = new URL("http://services.runescape.com/m=itemdb_rs/api/graph/" + itemid + ".json");
				URLConnection conn = alphas.openConnection();
				BufferedReader in = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
				jstring = in.readLine();
				in.close();
				Thread.sleep(500);
			} catch (Exception e) {
				
			}
		}
		GEGraphData thedata = new GEGraphData();
		thedata.populate((JsonObject) (new JsonObject(jstring)).get("daily"));;
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
