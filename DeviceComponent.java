import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class DeviceComponent extends JComponent {
	private String message;
	private int fontSize;
	public final int lineSize;
	private Color color;
	public DeviceComponent(String _message,int _fontSize,int _lineSize){
		this.message=_message;
		this.fontSize=_fontSize;
		this.lineSize=_lineSize;
		this.color=Color.BLACK;
	}
	
	
	
	public void select(Color _color, int delay){
		JComponent comp=this;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				color=_color;
				comp.repaint();
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				color=Color.BLACK;
				comp.repaint();
			}
		}).start();
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		Dimension size=this.getSize();
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color);
		g2.setStroke(new BasicStroke(lineSize));
		g2.drawOval(lineSize/2, lineSize/2, size.width-lineSize, size.height-lineSize);
		Font font =new Font("πŸ≈¡", 1, fontSize);
		g2.setFont(font);
		
	    FontMetrics metrics = g2.getFontMetrics(font);
	    int x = (size.width - metrics.stringWidth(message)) / 2;
	    int y =((size.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    
		g2.drawString(message, x, y);
	}
}
