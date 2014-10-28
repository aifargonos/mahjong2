

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import mahjong.dict_tile_content.DictTileContent;
import mahjong.gui.TileContentComponent;

/**
 * This is really fun ;-)
 * And it actually works :-P
 * 
 * @author aifargonos
 *
 */
public class DictTileContentSkuska extends JPanel implements ComponentListener, KeyListener {
	
	private static final long serialVersionUID = -2497266671431567422L;
	
	
	
	public JTextField text;
	
	private int dir = DictTileContent.DIR_DIAGONAL;
//	private int dir = KanjiTileContent.DIR_VERTIKAL;
	private TileContentComponent tcc;
	
	
	
	public DictTileContentSkuska() {
		super(null);
		
//		text = new JTextField("\u3042\u307B\u3058\u5973\u672C");
		text = new JTextField("sun");
//		text = new JTextField("bbbbbbbbbbbbbbbbbbbb");
		text.addKeyListener(this);
		
		Dimension size = new Dimension(300, 500);
		
		setSize(size);
		setPreferredSize(size);		
		
		addComponentListener(this);
		
		tcc = new TileContentComponent(new DictTileContent(text.getText(), dir), new Dimension(size.width - 100, size.height - 100));
		
		tcc.setLocation(50, 50);
		add(tcc);
		
		
		JFrame frame = new JFrame("title");
		frame.add(text, BorderLayout.NORTH);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
	}
	
	
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new DictTileContentSkuska();
			}
		});

	}
	
	
	
	public void componentHidden(ComponentEvent ce) {}
	public void componentMoved(ComponentEvent ce) {}
	public void componentResized(ComponentEvent ce) {
		tcc.setSize(getSize().width - 100, getSize().height - 100);
	}
	public void componentShown(ComponentEvent ce) {}
	
	public void keyPressed(KeyEvent ke) {}
	public void keyReleased(KeyEvent ke) {}
	public void keyTyped(KeyEvent ke) {
		System.out.println(text.getText());
		tcc.setTileContent(new DictTileContent(text.getText(), dir));
		repaint();
	}
	
	
	
}
