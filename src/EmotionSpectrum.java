import de.linguatools.disco.DISCO;
import de.linguatools.disco.ReturnDataBN;
import de.linguatools.disco.ReturnDataCol;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
	
public class EmotionSpectrum {
	/**
	 * Initializes the DISCO word association functions
	 */
	private static DISCO disco;
	private static PrimaryEmotion[] spectrum;
	private static int imageHeight;
	private static boolean influence; //variable that reflects whether secondary emotion relatedness effect primary
	private static String[] primary = {"joy","love","anger","sadness","fear"};
	private static String[][] secondary = 
		{{"cheerful", "bright", "content", "beauty", "optimism","relief"}, //joy
		 {"affection","lust","longing","attraction","compassion","infatuation","passion"}, //love
		 {"irritation", "kill", "rage", "disgust", "envy" ,"torment"}, //anger
		 {"cry", "tear", "shame", "neglect", "empty", "numb"}, //sadness
		 {"horror", "nervous","scare","dead","creep","sick"}}; //fear
	
//	private static String[] primary = {"sadness","love","anger","joy","fear"};
//	private static String[][] secondary = 
//		{{"cry", "tear", "shame", "neglect", "empty", "numb"}, //joy
//		 {"affection","lust","longing","attraction","compassion","infatuation","passion"}, //love
//		 {"irritation", "kill", "rage", "disgust", "envy" ,"torment"}, //anger
//		 {"cheerful", "bright", "content", "beauty", "optimism","relief"}, //sadness
//		 {"horror", "nervous","scare","dead","creep","sick"}}; //fear
	
//	private static String[] primary = {"female","male"};
//	private static String[][] secondary = 
//		{ //joy
//		 {"compassion", "love", "soft", "beauty","giving"},
//		 {"strong", "tough", "hard", "cold","determined"}}; //fear

	/**
	 * 1. Sets up the emotional spectrum to be constantly referenced. 
	 * This is where you change the primary, secondary emotions, weights, rgbs, height. 
	 * Pretty much every important visual and technical definition. This is all accessible statically.
	 * 2. This must be initialized basically first in the program. It will error otherwise.
	 */
    public EmotionSpectrum(int imageHeight, int marginTop){
    	String emotionalSpectrumWO= "";
    	this.imageHeight = imageHeight;
    	emotionalSpectrumWO+=imageHeight+"\n";
    	influence = true;
        String discoDir = System.getProperty("user.dir")+"/PackageJars/DISCO/en-BNC-20080721";
        try {
			disco = new DISCO(discoDir, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
        spectrum = new PrimaryEmotion[primary.length];
    	double[][] weights = {{1,1,1,1,1,1,1},
    					   	  {1,1,1,1,1,1,1},
    					   	  {1,1,1,1,1,1,1},
    					   	  {1,1,1,1,1,1,1},
    					   	  {1,1,1,1,1,1,1}};
    	int[] hsvs ={294, //love -- purple
					 64, //Joy -- yellow
					 0, //anger -- red
					 242, //sadness -- blue
					 86};  //fear -- green 
    	//here we split up the given height and give each secondary emotion a slot within our slot.
    	int imageEmotionSlot = imageHeight/primary.length;
    	int imageBottom = imageEmotionSlot+marginTop;
    	int imageTop = 0+marginTop;
    	for (int i=0;i<primary.length;i++){
    		spectrum[i] = new PrimaryEmotion(primary[i], secondary[i], weights[i], hsvs[i],imageTop, imageBottom);
    		imageBottom=(i==primary.length-1?imageHeight:imageBottom+imageEmotionSlot);
    		imageTop+=imageEmotionSlot;
    		
    		//writeout file appending
    		emotionalSpectrumWO+=primary[i]+"\n"+  //add primary name
    		                     hsvs[i]+"|"+100+"|"+100+"\n"+ //primary color
    							 imageTop+"|"+imageBottom+"\n"; //primary spacing
    		for (SecondaryEmotion curSec: getPrimary(primary[i]).getSecondary()){
    			int[] curHsv = curSec.getHsv();
    			emotionalSpectrumWO+=curSec.getSecondary()+"\n"+ //secondary name
    			                     curHsv[0]+"|"+curHsv[1]+"|"+curHsv[2]+"|"+"\n"+ //secondary spacing
    			                     curSec.getyTop()+"|"+curSec.getyBottom()+"\n";  //secondary color					 
    		}
    	}
    	writeOut(emotionalSpectrumWO);
    }  
    
    /**
     * Writes out text
     * @param text
     */
	public void writeOut(String text){
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("VisualData/emotionalSpectrum", false)));
		    out.println(text);
		    out.close();
		} catch (Exception e) {
		    System.out.println();
		}
	}
    
    /**
     * 1. Given a string word, this will return an ArrayList of MomentEmotion
     * representation of that word's relation to that specific emotion.\
     * 2. boolean influence comes in here. If true, secondary emotions 
     * add emotion value to their primary emotion. Otherwise, they don't. Should be true.
     * @param word 
     * @throws IOException 
     */
    public static MomentEmotion[] momentValue(String word){
    	double primaryVal;
    	double[] secondaryVal;
    	MomentEmotion[] tempSpectrum = new MomentEmotion[primary.length];  //to be returned full of emotions
    	for (int i=0;i<primary.length;i++){
    		try {
    			PrimaryEmotion curEmote = getPrimary(primary[i]);
    			primaryVal = disco.secondOrderSimilarity(word, curEmote.getPrimary());
    			primaryVal = (primaryVal<0?0:primaryVal); //make sure the value isn't negative, if it is, it is now 0
    			secondaryVal = new double[curEmote.getSecondary().length];
    			SecondaryEmotion[] tempSecEmotes = curEmote.getSecondary();
    			for (int j=0;j<secondaryVal.length;j++){  //for each secondary emotion
    				secondaryVal[j] = disco.secondOrderSimilarity(word, tempSecEmotes[j].getSecondary())*tempSecEmotes[j].getWeight(); //get relatedness value * weight of sec. word
    				secondaryVal[j] = (secondaryVal[j]<0?0:secondaryVal[j]); //make sure the value isn't negative, if it is, it is now 0
    				if (influence){
    					primaryVal+= (secondaryVal[j]/secondaryVal.length);  //secondary value gets added top primary/number of secondary vals
    				}
    			}
    			primaryVal/=2; //this changes our value from 0-2 range to 0-1
    			System.out.println("PrimaryVal: "+primaryVal);
    			//moment emotion is created and added to our ArrayList to be returned
    			tempSpectrum[i] = new MomentEmotion(curEmote.getPrimary(),secondary[i],primaryVal,secondaryVal);
    		}
    		catch (Exception e){
    			System.out.println("Word Relation Failure");
    		}
    	}
    	return tempSpectrum;
    }   
    
    public static PrimaryEmotion getPrimary(String emote){
    	for (PrimaryEmotion primEmote: spectrum){
    		if (primEmote.getPrimary().equals(emote)){
    			return primEmote;
    		}
    	}
    	System.out.println("No such thang.");
    	return null;
    }
    public static PrimaryEmotion getPrimaryEmotion(String prime){
    	for (PrimaryEmotion primTemp: spectrum){
    		if (primTemp.getPrimary().equals(prime)){
    			return primTemp;
    		}
    	}
    	return null;
    }
    
    public static PrimaryEmotion[] getSpectrum(){
    	return spectrum;
    }
    
    public static String[] getPrimaryStrings(){
    	return primary;
    }
    
    public static String[][] getSecondaryStrings(){
    	return secondary;
    }
    
}