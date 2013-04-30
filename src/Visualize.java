import processing.core.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Visualize extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ArrayList<int[]> plotPoints;
	private static ArrayList<Moment> allMoments;
	private static ArrayList<Segment> novelSegments;
	private ArrayList<ArrayList<double[]>> shapePoints;
	private ArrayList<ArrayList<double[]>> shapeLines;
	private static ArrayList<String[]> plotTexts;
	private static Segment curSegment;
	private static Novel novelObj;
	private static int height, width, textLength, segSize;
	private static boolean primaryOnly;
	
	public Visualize(){
		primaryOnly = false;
		height = 700;
		width = 1000;
		int marginTop = 100;
		segSize = 2;
		setSize(height,width);
		curSegment = new Segment(segSize);
		novelSegments = new ArrayList<Segment>();
		allMoments = new ArrayList<Moment>();
		plotPoints = new ArrayList<int[]>();
		plotTexts = new ArrayList<String[]>();
		shapePoints = new ArrayList<ArrayList<double[]>>();
		novelObj = new Novel("huckfin", "testAuthor", (int)(height),marginTop); //Novel class initialized 
		textLength = novelObj.getTextLength();
		populatePointLists();
		saveImage();
	}
	
	public void populatePointLists(){
		System.out.println("POPULATING");
		if (primaryOnly){ //only shows primary colors
			for (int i=0;i<novelSegments.size();i++){  //for each segment
				 MomentEmoteSpace[] bounderies=novelSegments.get(i).getBounderies();
				 for (int j=0;j<bounderies.length;j++){ //for each category 
					if (i==0){  //if this is our first time through, we create all the point array lists
						shapePoints.add(new ArrayList<double[]>()); //add an array list to be added to!
					}
					System.out.println(bounderies[j].getEmotion());
					double[] pointXYTemp = {((novelSegments.get(i).getX()*width)/(textLength)),bounderies[j].getTop()}; //create temporary xy point
					shapePoints.get(j).add(pointXYTemp); //add this point to this category's list
				}
			}
			for (int i=novelSegments.size()-1;i>=0;i--){  //for each segment
				MomentEmoteSpace[] bounderies=novelSegments.get(i).getBounderies();
				for (int j=0;j<bounderies.length;j++){ //for each category 
					double[] pointXYTemp = {((novelSegments.get(i).getX()*width)/(textLength)),bounderies[j].getBottom()}; //create temporary xy point
					shapePoints.get(j).add(pointXYTemp); //add this point to this category's list
				}
			}
		}
		else{ //shows secondary colors
			for (int i=0;i<novelSegments.size();i++){  //for each segment
				int curSec = 0;
				 MomentEmoteSpace[] bounderies=novelSegments.get(i).getBounderies();
				 for (int j=0;j<bounderies.length;j++){ //for each primary
					 MomentEmoteSpace[] secondaries =  bounderies[j].getSecondary();
					 for (int k=0;k<secondaries.length;k++){ //for each secondary 
						if (i==0){  //if this is our first time through, we create all the point array lists
							shapePoints.add(new ArrayList<double[]>()); //add an array list to be added to!
						}
						System.out.println(secondaries[k].getEmotion());
						double[] pointXYTemp = {((novelSegments.get(i).getX()*width)/(textLength)),secondaries[k].getTop()}; //create temporary xy point
						shapePoints.get(curSec).add(pointXYTemp); //add this point to this category's list
						curSec++;
					 }
					 System.out.println(i+" JSIZE: "+shapePoints.get(j).size());
				}
			}
			for (int i=novelSegments.size()-1;i>=0;i--){  //for each segment
				int curSec = 0;
				MomentEmoteSpace[] bounderies=novelSegments.get(i).getBounderies();
				 for (int j=0;j<bounderies.length;j++){ //for each primary
					 MomentEmoteSpace[] secondaries =  bounderies[j].getSecondary();
					 for (int k=0;k<secondaries.length;k++){ //for each secondary
						double[] pointXYTemp = {((novelSegments.get(i).getX()*width)/(textLength)),secondaries[k].getBottom()}; //create temporary xy point
						shapePoints.get(curSec).add(pointXYTemp); //add this point to this category's list
						curSec++;
					 }
				}
			}
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		System.out.println();
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                             RenderingHints.VALUE_RENDER_QUALITY);
        //create shapes from point lists
        System.out.println("ShapePointsSize: "+shapePoints.size());
        GeneralPath[] shapes = new GeneralPath[shapePoints.size()];

        g2d.setBackground(Color.white);
        if (primaryOnly){
			for (int i=0;i<shapePoints.size();i++){
				shapes[i] = new GeneralPath();
				ArrayList<double[]> shapePointsCat = shapePoints.get(i);
				shapes[i].moveTo(shapePointsCat.get(0)[0], shapePointsCat.get(0)[1]); //move to the first point
				for (int j=0;j<shapePoints.get(i).size();j++){
					shapes[i].lineTo(shapePointsCat.get(j)[0],shapePointsCat.get(j)[1]);
				}
				shapes[i].closePath();
				g2d.setPaint(new GradientPaint(0,0,EmotionSpectrum.getSpectrum()[i].getPrimeColor(),1000,1000,EmotionSpectrum.getSpectrum()[(i==(shapePoints.size()-1)?i:i+1)].getPrimeColor()));
				g2d.fill(shapes[i]);
				g2d.draw(shapes[i]);
			}
        }
        else{
        	ArrayList<Color> secColors = new ArrayList<Color>();
        	for (PrimaryEmotion prime: EmotionSpectrum.getSpectrum()){
        		for (SecondaryEmotion sec: prime.getSecondary()){
        			secColors.add(sec.getSecColor());
        		}
        	}
        	for (int i=0;i<shapePoints.size();i++){ //for each shape
        		System.out.println(i);
				shapes[i] = new GeneralPath();
				ArrayList<double[]> shapePointsCat = shapePoints.get(i);
				shapes[i].moveTo(shapePointsCat.get(0)[0], shapePointsCat.get(0)[1]); //move to the first point
				for (int j=0;j<shapePointsCat.size();j++){ //for each
					shapes[i].lineTo(shapePointsCat.get(j)[0],shapePointsCat.get(j)[1]);
				}
				shapes[i].closePath();
				g2d.setPaint(new GradientPaint(0,0,secColors.get(i),1000,1000,secColors.get(i==(shapePoints.size()-1)?i:i+1)));
				g2d.fill(shapes[i]);
				g2d.draw(shapes[i]);
			}
        }
    }
	
	
	public static void getPlotsToAdd(){
		LinkedList<String> castList = novelObj.getListOfCharacters();
		for (String tempActor: castList){
			ArrayList<Moment> tempMomentList = novelObj.getCharacter(tempActor).getEmotionalRollercoaster();
			for (Moment tempMoment: tempMomentList){
				System.out.println("\n\n--------"+tempMoment.getWord()+"---------");
				for (MomentEmotion tempMoEmote: tempMoment.getMomentSpectrum()){
					for (MomentEmoteSec tempMoSec: tempMoEmote.getSecEmote()){
						System.out.println(tempMoSec.getSecondaryString()+"--- X: "+(tempMoment.getX()*width)/textLength+"  Y: "+tempMoSec.getY()+ "  Size: "+tempMoSec.getSize());
						//addPlotPoint((int)(((tempMoment.getX()*width)/textLength)), (int)(tempMoSec.getY()), tempMoSec.getSize(), EmotionSpectrum.getPrimary(tempMoEmote.getPrimaryString()).getSecondaryEmotion(tempMoSec.getSecondaryString()).getHsv());
					}
				}
			}
		}
	}
	
	public static void drawSegments(){
		if (!curSegment.isEmpty()){
			novelSegments.add(curSegment);
		}
		int[] hsvTemp = {360,100,100};  //temporarily used to whatever I will remove it soon
		System.out.println("SIZEEEE:"+novelSegments.size());
		for (Segment tempSeg: novelSegments){
			if (!tempSeg.isEmpty()){
				for (MomentEmoteSpace tempSpace: tempSeg.getBounderies()){
					//addPlotPoint(((tempSeg.getX()*width)/(textLength*2)), tempSpace.getTop(), 2, hsvTemp);
					//addPlotPoint(((tempSeg.getX()*width)/(textLength*2)), tempSpace.getBottom(), 2, hsvTemp);
				}
			}
		}
	}
	
	public static void addToAllMoments(Moment newMoment){
		allMoments.add(newMoment);
		if (curSegment.isFull()){
			novelSegments.add(curSegment);
			curSegment = new Segment(segSize);
		}
		else{
			curSegment.addMoment(newMoment);
		}
	}
	
	/**
	 * Stumbled upon a previously defined getHeight(); shoulda known...processing!
	 * @return
	 */
	public static int getImageHeight(){
		return height;
	}
	
	public void saveImage(){
		BufferedImage bi = new BufferedImage(this.getSize().width+1000, this.getSize().height+1000, BufferedImage.TYPE_INT_ARGB); 
		Graphics g = bi.createGraphics();
		this.paint(g);  //this == JComponent
		g.dispose();
		try{ImageIO.write(bi,"png",new File("test.png"));}catch (Exception e) {}
	}
	
	public static void main(String args[]) {
		JFrame frame = new JFrame("Visualization");
        frame.add(new Visualize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}