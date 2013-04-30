import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
public class TestGraphics extends JPanel {
  public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2d = (Graphics2D) g;
    Point2D start = new Point2D.Float(0, 0);
    Point2D end = new Point2D.Float(350, 350);
    float[] dist = { 0.0f, 0.5f, 1.0f };
    Color[] colors = { Color.RED, Color.WHITE, Color.BLUE };
    g2d.setPaint(new LinearGradientPaint(start,end,dist,colors));
//    g2d.setPaint(new GradientPaint(
//            64,0,Color.GREEN,
//            64,-64,Color.BLUE,true));
    g2d.fillRect(20, 140, 300, 40);
  }
  public static void main(String[] args) {
    JFrame frame = new JFrame("GradientsLine");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(new TestGraphics());
    frame.setSize(350, 350);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
   