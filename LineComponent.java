import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

public class LineComponent extends JComponent {
	private DeviceComponent comp1;
	private DeviceComponent comp2;
	
	private int x1, y1;
	private int x2, y2;
	private int width, height;
	private BasicStroke stroke;
	
	private int startX1;
	private int startY1;
	private int startX2;
	private int startY2;
	
	private int radiusX1,radiusY1;
	private int radiusX2,radiusY2;
		
	public LineComponent(DeviceComponent _comp1,DeviceComponent _comp2){
		this.comp1=_comp1;
		this.comp2=_comp2;
		this.stroke=new BasicStroke((comp1.lineSize+comp2.lineSize)/2);
	}


	@Override
	public void repaint(){
		calcPosition();
		super.repaint();
	}
	
	
	private void calcPosition(){
		Point comp1Point=comp1.getLocation();
		Dimension comp1Size=comp1.getSize();
		Point comp2Point=comp2.getLocation();
		Dimension comp2Size=comp2.getSize();
		
		int x, y;
		if(comp1Point.x<comp2Point.x){
			x = comp1Point.x;
			width = comp2Point.x+comp2Size.width-x;
		}else{
			x = comp2Point.x;
			width = comp1Point.x+comp1Size.width-x;
		}
		if(comp1Point.y<comp2Point.y){
			y = comp1Point.y;
			height = comp2Point.y+comp2Size.height-y;
		}else{
			y = comp2Point.y;
			height = comp1Point.y+comp1Size.height-y;
		}
		setBounds(x, y, width, height);
		radiusX1 = comp1Size.width/2;
		radiusX2 = comp2Size.width/2;
		
		radiusY1 = comp1Size.height/2;
		radiusY2 = comp2Size.height/2;
		
		startX1=comp1Point.x-x;
		startY1=comp1Point.y-y;
		startX2=comp2Point.x-x;
		startY2=comp2Point.y-y;
		
		int node1_x=startX1+radiusX1;
		int node1_y=startY1+radiusY1;
		x1=node1_x;
		y1=node1_y;
		
		int node2_x=startX2+radiusX2;
		int node2_y=startY2+radiusY2;
		x2=node2_x;
		y2=node2_y;
		
		int width=node2_x-node1_x;
		int height=node2_y-node1_y;
		// �� ����� �߽��� �̿��Ͽ� ���� �ﰢ���� �׸�
		// ���� �ﰢ���� �׸��� ������ �� ��尣 ���� ������ �߽��� �Ǵ� ���� ���� �ﰢ���� ������
		// �� ��尣�� ������ ����Ͽ� redain���� ������.
		// �ڹ��� Math Ŭ������ cos, sin �Լ� ���� radian �� �̿���
		double degreeRadian=Math.atan2(Math.abs(height), Math.abs(width));
		
		int xDiff=node1_x-node2_x;
		int yDiff=node1_y-node2_y;
		
		// �� ����� ��ġ�� ����Ͽ� ���� �������� ������ �����
		// �� �� ���� ����� ������ �̿��Ͽ� �����ﰢ���� ������ ���� �ѷ��� �������� �� ��ǥ�� �����
		// �̸� ���� �����ﰢ�� ������ �׸��� �� �ۿ��� �׸����� ��.
		if(xDiff>0){
			if(yDiff>0){
				x1=(int) (node1_x-Math.round(Math.cos(degreeRadian)*radiusX1));
				y1=(int) (node1_y-Math.round(Math.sin(degreeRadian)*radiusY1));
				x2=(int) (node2_x+Math.round(Math.cos(degreeRadian)*radiusX2));
				y2=(int) (node2_y+Math.round(Math.sin(degreeRadian)*radiusY2));
			}else if(yDiff==0){
				x1=node1_x-radiusX1;
				x2=node2_x+radiusX2;
			}else{
				x1=(int) (node1_x-Math.round(Math.cos(degreeRadian)*radiusX1));
				y1=(int) (node1_y+Math.round(Math.sin(degreeRadian)*radiusY1));
				x2=(int) (node2_x+Math.round(Math.cos(degreeRadian)*radiusX2));
				y2=(int) (node2_y-Math.round(Math.sin(degreeRadian)*radiusY2));
			}
		}else if(xDiff==0){
			if(yDiff>0){
				y1=node1_y-radiusY1;
				y2=node2_y+radiusY2;
			}else if(yDiff==0){
				x2=x1;
				y2=y1;
			}else{
				y1=node1_y+radiusY1;
				y2=node2_y-radiusY2;
			}
		}else{
			if(yDiff>0){
				x1=(int) (node1_x+Math.round(Math.cos(degreeRadian)*radiusX1));
				y1=(int) (node1_y-Math.round(Math.sin(degreeRadian)*radiusY1));
				x2=(int) (node2_x-Math.round(Math.cos(degreeRadian)*radiusX2));
				y2=(int) (node2_y+Math.round(Math.sin(degreeRadian)*radiusY2));
			}else if(yDiff==0){
				x1=node1_x+radiusX1;
				x2=node2_x-radiusX2;
			}else{
				x1=(int) (node1_x+Math.round(Math.cos(degreeRadian)*radiusX1));
				y1=(int) (node1_y+Math.round(Math.sin(degreeRadian)*radiusY1));
				x2=(int) (node2_x-Math.round(Math.cos(degreeRadian)*radiusX2));
				y2=(int) (node2_y-Math.round(Math.sin(degreeRadian)*radiusY2));
			}
		}
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.RED);
		g2.setStroke(stroke);
		g2.drawLine(x1, y1, x2, y2);
	}
}
