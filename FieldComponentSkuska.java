import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import mahjong.engine.Field;
import mahjong.gui.FieldComponent;


public class FieldComponentSkuska extends JFrame {
	
	private static final long serialVersionUID = -6516797004614205200L;
	
	
	
	public FieldComponentSkuska() {
		super("title ...");
		
		
		
		add(new FieldComponent(new Field()));
		
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationByPlatform(true);
		setVisible(true);
		
	}
	
	
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new FieldComponentSkuska();
			}
		});
		
	}
	
}
