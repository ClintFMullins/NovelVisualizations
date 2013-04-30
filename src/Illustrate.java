import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Illustrate extends JPanel {

    static double[][] points = { 
        { 0, 85 }, { 75, 75 }, { 100, 10 }, { 125, 75 }, 
        { 200, 85 }, { 150, 125 }, { 160, 20 }, { 100, 150 }, 
        { 40, 190 }, { 50, 125 }, { 0, 85 } 
    };

    public Illustrate(){
    	double[] newPoint = {0,20};
    	points[0] = newPoint;
    }
    
    public void paint(Graphics g) {
        super.paint(g);

        int ht = getHeight();
        int wd = getWidth();


        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                             RenderingHints.VALUE_RENDER_QUALITY);

        g2d.translate(25, 5);

        GeneralPath star = new GeneralPath();

        star.moveTo(points[0][0], points[0][1]);

        for (int k = 1; k < points.length; k++)
            star.lineTo(points[k][0], points[k][1]);

        star.closePath();
        float h = (float) .5;
        float s = (float) .2;
        float b = (float) .1;
        Color c = Color.getHSBColor(4, 4, 4);
        System.out.println("What: "+Color.HSBtoRGB(h, s, b));
        g2d.setPaint(new GradientPaint(700, 700, Color.red, 100, 100, Color.green,true));
        g2d.fill(star);
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Star");
        frame.add(new Illustrate());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 250);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}