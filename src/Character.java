import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class Character {
	//private String[] fullName; //all names, for example: {"Mr.", "Franklin", "Jones"}
	private ConcurrentHashMap<String,Word> diction;
	private LinkedList occurences;  //holds all character mentions by index
	private String name;
	private int maleCount, femaleCount, gender;  //gender: 0 - female / 1 - male 
	private ArrayList<Moment> emotionalRollercoaster; //keeps arrays of each emotion
	
	/**
	 * 1. Retrieves the character's name 
	 * 2. Sets up the dictionary to save all character diction
	 * 3. Tries to define gender of character first by honorifics, then by name, if this fails
	 * we use a constant gender pronoun count to guess gender.
	 * 4. Sets up occurences which keeps track of all mentions of this character
	 * @param name
	 */
	public Character(String name, String fullName){
		System.out.print("New Character: "+name+" ||Fullname: "+fullName);
		this.name = name;
		emotionalRollercoaster = new ArrayList<Moment>();
		boolean assigned = false;
		if (!name.equals(fullName)){ //if there is a Mister, or Ms or what have you!
			boolean dot = fullName.contains(".")?true:false;
			System.out.print(" //Gender: ");
			if ("mr".contains(fullName.substring(0,(fullName.indexOf(" ")+(dot?-1:0))))){ //checks for Mr
				System.out.print("male");
				gender = 1;
				assigned = true;
			}
			else if ("msmrsmisses".contains(fullName.substring(0,fullName.indexOf(" ")+(dot?-1:0)))){ //checks for ms, mrs, or misses
				System.out.print("female");
				gender = 0;
				assigned = true;
			}
			else{
				System.out.print("unassigned1");  
			}
		}
		if (!assigned){
			gender = Parse.nameGender(name);
			if (gender==-1){
				System.out.print("Unassigned2");
			}
			else if (gender==1){
				System.out.print("male");
				assigned = true;
			}
			else{
				System.out.print("female");
				assigned=true;
			}
		}
		maleCount = femaleCount = 0;
		occurences = new LinkedList();
		diction = new ConcurrentHashMap<String,Word>();
	}
	
	/**
	 * Writes out character moments to a specific character file [name].txt
	 */
	public void writeOutMoments(boolean append){
		String momentString = "";
		for (Moment tempMoment: emotionalRollercoaster){
			momentString+="!"+tempMoment.getWord()+"\n"+tempMoment.getX()+"\n";
			for (MomentEmotion tempEmote: tempMoment.getMomentSpectrum()){
				momentString+="+"+tempEmote.getPrimaryString()+"\n"+tempEmote.getPrimaryVal()+"\n"; //add primary name and val (these are sorted by val so we need the name) 
				for (MomentEmoteSec tempSecVal: tempEmote.getSecEmote()){
					momentString+=tempSecVal.getSecondaryVal()+"\n";
				}
			}
		}
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/VisualData/Characters/"+name, false)));
		    out.println(momentString);
		    out.close();
		} catch (Exception e) {
		    System.out.println("FAILED TO WRITE CHARACTER: "+name);
		}
	}
	
	/**
	 * Adds a new word or adds occurrence of an old word that relates this character
	 * @param newWord
	 */
	public void addWord(String newWord, int wordInd, String pOS){
		System.out.println("Name|Word|Index  "+name+"|"+newWord+"|"+wordInd);
		if (!diction.containsKey(newWord)){ //Word is not in list
			diction.put(newWord, new Word(newWord, wordInd, pOS)); //add word to list
		}
		else{
			diction.get(newWord).addOccurence(wordInd);
		}
		//here we add the moment to the emotional rollercoaster which holds all moments
		Moment newMoment = new Moment(diction.get(newWord));
		emotionalRollercoaster.add(newMoment);
		//if (name.equals("luke")){
			Visualize.addToAllMoments(newMoment);
		//}
	}
	
	
	/**
	 * adds occurrence of this character
	 * @param index
	 */
	public void addOccurence(int index){
		occurences.add(index);
	}
	
	/**
	 * If there have been any gender assignment, returns true
	 * @return
	 */
	public boolean isGender(){
		if (gender!=-1 || maleCount+femaleCount>0){
			return true;
		}
		return false;
	}
	
	/**
	 * Get name of character 
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * adds weight to this as a male character
	 */
	public void maleMention(){
		maleCount++;
	}
	
	/**
	 * adds weight to this as a female character
	 */
	public void femaleMention(){
		femaleCount++;
	}
	
	/**
	 * Returns the gender assignment. If no assignment has been made then:
	 * Returns true (male) if the instances of male pronouns said directly after outweighs the female pronouns
	 * @return
	 */
	public boolean isMale(){
		return (gender<0?(maleCount-femaleCount>0?true:false):(gender==0?false:true));
	}
	
	/**
	 * Returns a collection of words
	 * @return
	 */
	public Collection<Word> getDiction(){
		return diction.values();
	}
	
	public ArrayList<Moment> getEmotionalRollercoaster(){
		return emotionalRollercoaster;
	}
}
