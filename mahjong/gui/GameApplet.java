package mahjong.gui;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class GameApplet extends JApplet {
	
	private static final long serialVersionUID = -7482348020342365305L;
	
	
	
	private Game game;
	
	
	
	@Override
	public void init() {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				game = new Game(getContentPane());
				
				texts();
				
			}
		});
		
	}
	
	public void texts() {
		game.texts();
	}
	
	
	
	@Override
	public void start() {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				game.newGame();
				
			}
		});
		
	}
	
	
	
}
