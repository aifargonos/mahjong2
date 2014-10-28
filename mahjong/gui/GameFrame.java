package mahjong.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GameFrame extends JFrame {
	
	private static final long serialVersionUID = -7341990863809458423L;
	
	
	
	private Game game;
	
	
	
	public GameFrame() {
		
		game = new Game(getContentPane());
		
		texts();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationByPlatform(true);
		setVisible(true);
		
		game.newGame();
	}
	
	public void texts() {
		setTitle(Config.texts.gameFrameTitle);
		game.texts();
	}
	
	
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GameFrame();
			}
		});
		
	}
	
	
	
}
