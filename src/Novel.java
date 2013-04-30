import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


/**
 * If characters have the same name, that is implied connection
 * 
 * @author ClintFrank
 *
 */

public class Novel {
	//FOGVVV
	private static int letterCount, sentenceLength, wordCount, sentenceCount, speechCount, sylCount, totalSylCount, compWordCount, index;
	private static double averageLetWord, averageSylWord, averageWordSen, FOG;
	private static boolean speechFlag, senEndFlag, preVowelFlag;
	private static String punct, stringChar;
	//FOG^^^
	private static String[] words, depList, tokList;
	private static int parseReaderIndex;
	private String cleanFile, tokenized, depended, title, author, curSentence, curWord;
	private boolean parseExists;
	private ConcurrentHashMap<String,Character> cast;
	private LinkedList<String> listOfCharacters;
	private Parse parser;
	private String curCharMale, curCharFemale, curCharUni; //keeps track of the last Character mentioned, male, female, and before assignment
	
	public Novel (String title, String author, int imageHeight, int marginTop){
		this.title = title;
		this.author = author;
		listOfCharacters = new LinkedList<String>();
		@SuppressWarnings("unused")
		EmotionSpectrum emotionSpec = new EmotionSpectrum(imageHeight,marginTop); //we just need to initialize. All methods are static.
		parser = new Parse();
		punct = ".?!";
		tokenized=depended="";
		cast = new ConcurrentHashMap<String,Character>();
		cleanFile = Parse.cleanFile(title);  //here we get a clean copy of the book
//		cleanFile = "Mr. Hannifan was a good man. " +
//				    "He was always near Steph. " +
//				    "Until one day Dr. Hannifan slapped her. " +
//				    "Mike told him that she was upset." +
//				    "Mike cried the whole day." +
//				    "Mr. Hannifan then shouted, 'Go kill yourself!'";			    
		parseReaderIndex = 0; //keeps track of the sentence location in lists below
		String depPath = "VisualData/Parsed/depended"+title+cleanFile.charAt(0)+".txt";
		String tokPath = "VisualData/Parsed/tokenized"+title+cleanFile.charAt(1)+".txt";
		if (fileExists(depPath)&&fileExists(tokPath)){
			parseExists = true;
			depList = readFile(depPath).split("\n");
			tokList = readFile(tokPath).split("\n");
			characterExtraction();
		}
		else{
			parseExists = false;
			characterExtraction();
			writeOut(tokenized,tokPath);
			writeOut(depended,depPath);
		}
	}
	
	/**
	 * returns text length for visualization
	 * @return
	 */
	public int getTextLength(){
		return cleanFile.length();
	}
	
	public boolean fileExists(String path){
		if (new File(path).exists()){
			return true;
		}
		return false;
	}
	
	public String readFile(String path){
		try{
			String longString="";
			String curLine;
			BufferedReader br = new BufferedReader(new FileReader(path));
			while ((curLine = br.readLine()) != null) {
				longString+=curLine+"\n";
			}
			br.close();
			return longString;
		}
		catch(Exception e){
			System.out.println("FAILED TO READ");
			return null;
		}
			
	}
	
	public void characterExtraction(){
		curCharMale = curCharFemale = curCharUni = "0";
		curWord = curSentence = "";
		for (index=0;index<cleanFile.length();index++){
			stringChar = String.valueOf(cleanFile.charAt(index));
			//System.out.println(stringChar);
			//Counting the length of dialogs
			curWord+=stringChar;
			curSentence+=stringChar; //current sentence being created
			if (speechFlag){
				setSpeechCount(getSpeechCount() + 1);
			}
			//space
			if (" ".contains(stringChar)){
				//space after word (new word)
				if (senEndFlag==false){
					//Complex word
					if (sylCount>3){
						compWordCount++;
					}
					if (cast.contains(curCharUni)){  //if the last reference proper noun is a character
						curWord = curWord.trim().toLowerCase(); //no whitespace and lowercase
						if ("sheher".contains(curWord)){ //if feminine pronoun
							System.out.println("FEMASSIGN");
							cast.get(curCharUni).femaleMention(); //adds to female chance of last referenced char
						}
						else if ("hehim".contains(curWord)){
							System.out.println("MALEASSIGN");
							cast.get(curCharUni).maleMention(); //adds to the male chance of last referenced char
						}
					}
					setWordCount(getWordCount() + 1);
					sylCount=0;
					curWord="";
					preVowelFlag=false;
				}
				//space after end of sentence
				else{
					senEndFlag=false;
				}
			}
			//end of sentence (New Sentence)
			else if (punct.contains(stringChar)){
				try{
					String check = curSentence.substring(curSentence.length()-3,curSentence.length()); //created to check for Ms. Mr. Dr.
					if (check.contains("Mr.")||check.contains("Ms.")||check.contains("Dr.")){ //if Dr. Mr. Ms. then sentence isn't over
						continue;
					}
				}
				catch (Exception e){
					System.out.println("MR MS DR CHECK OUT OF RANGE");
				}
				//System.out.println("[word] \n[senClose]");
				curSentence = curSentence.trim();  //gets rid of whitespace in the beginning or end of a sentence
				System.out.println(curSentence); 
				characterIdentify(curSentence); //sends sentence for character identification
				characterPersonify(curSentence); //sends sentence for character personification
				setSentenceCount(getSentenceCount() + 1);
				setWordCount(getWordCount() + 1);
				curSentence="";
				senEndFlag=true;
			}
			//dialog
			else if ("\"".contains(stringChar)){
				//open dialog
				if (speechFlag==false){
					//System.out.print("[sOpen] ");
					speechFlag=true;
				}
				//closed dialog
				else {
					//System.out.print("[sClose] ");
					setSpeechCount(getSpeechCount() + 1);
					speechFlag=false;
				}
			}
			//letters
			else{
				//Vowel
				if ("aeiouy".contains(stringChar)){
					//Isolated Vowel
					if (preVowelFlag==false){
						//System.out.print("[s]");
						sylCount++;
						totalSylCount++;
						preVowelFlag=true;
					}
				}
				//Consonant 
				else{
					preVowelFlag=false;
				}
				setLetterCount(getLetterCount() + 1);
			}
			
		}
		averageWordSen = ((double) wordCount)/sentenceCount;
		setAverageLetWord(((double) letterCount)/wordCount);
		setAverageSylWord(((double) totalSylCount)/wordCount);
		setFOG((0.4)*(averageWordSen + 100*(compWordCount/wordCount)));
		characterWriteOut();
	}
	
	/**
	 * This class looks at all the characters and writes out their data to separate [name].txt files within VisualData/Characters
	 */
	public void characterWriteOut(){
		for (String tempActor: listOfCharacters){  //here we cycle through all added names
			File file = new File("VisualData/Character/", cast.get(tempActor).getName() + ".txt"); //file is created for character
			cast.get(tempActor).writeOutMoments(false);
		}
	}
	
	public LinkedList<String> getListOfCharacters(){
		return listOfCharacters;
	}	
	
	public Character getCharacter(String charName){
		return cast.get(charName);
	}

	public void writeOut(String text, String path){
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, false)));
		    out.println(text);
		    out.close();
		} catch (Exception e) {
		    System.out.println("FAILED TO WRITE: "+path);
		}
	}
	
	/**
	 * Identifies Proper nouns from texts and makes them characters
	 * @param sentence
	 */
	public boolean characterIdentify(String sentence){
		String senWithPOS;
		if (parseExists){
			 senWithPOS = tokList[parseReaderIndex];
		}
		else{
			senWithPOS = parser.getPOS(sentence);
			tokenized+=senWithPOS+"\n";
		}
		System.out.println("POS "+senWithPOS);
		int start, i;
		boolean charInSen = false;
		while (true){
			//System.out.println("loop1 STUCK?");
			if ((start = senWithPOS.indexOf("NNP")) == -1){
				return charInSen;
			}
			charInSen = true;
			start+=4;
			i = start;
			String name = "";
			while (true){
				while (!String.valueOf(senWithPOS.charAt(i)).equals(")")){ //grabs the name
					name+=String.valueOf(senWithPOS.charAt(i));
					i++;
				}
				if (senWithPOS.length()>=i+7 && senWithPOS.substring(i+3,i+6).equals("NNP")){ //is there more to this name? ex. John Smith
					i+=7;
					name+=" ";
				}
				else{
					break;
				}
			}
			addCharacter(name.trim().toLowerCase()); //here we send the name to be added
			senWithPOS = senWithPOS.substring(i,senWithPOS.length());
		}
	}

	/**
	 * 
	 * @param sentence
	 */
	public void characterPersonify(String sentence){
		String senWithDep;
		if (parseExists){
			senWithDep = depList[parseReaderIndex++];
		}
		else{
			senWithDep = parser.getDep(sentence);
			depended+=senWithDep+"\n";
		}
		boolean adv;
		String checkWithDep = "";
		System.out.println("Dep "+senWithDep);
		int start;
		String word, name, pOS, wordInd;
		pOS = "nsubj";
		while ((start = senWithDep.indexOf("nsubj"))!=-1){
			word = name = wordInd = "";
			if (senWithDep.contains("advmod")){
				checkWithDep = senWithDep;  //for advmod
				adv = true;
			}
			else{
				adv = false;
			}
			while (!String.valueOf(senWithDep.charAt(start)).equals("(")){ //here we get to the word
				start++;
			}
			start++; //skips the "("
			while (!String.valueOf(senWithDep.charAt(start)).equals("-")){ //we record the word
				word+=senWithDep.charAt(start);
				start++;
			}
			start++; //skips the "-"
			while (!String.valueOf(senWithDep.charAt(start)).equals(",")){  //we record the word index
				wordInd+=senWithDep.charAt(start);
				start++;
			}
			start+=2;  //skips the "-#, "
			while (!String.valueOf(senWithDep.charAt(start)).equals("-")){  //here we get the name
				name+=senWithDep.charAt(start);
				start++;
			}
			name = name.trim().toLowerCase();
			word = word.trim().toLowerCase();
			System.out.println("Name: "+name+" Verb: "+word);
			if (adv){
				String advWord = ""; 
				String checkWord = "";
				int curChar;
				checkWithDep = checkWithDep.substring(checkWithDep.indexOf("advmod"),checkWithDep.length());
				curChar = 0;
//				while (!String.valueOf(checkWithDep.charAt(curChar)).equals("(")){
//					
//				}
				curChar++; //skips the "("
			}
			if (name.equals("he")){
				System.out.println("adding word - curCharMale: "+curCharMale);
				if (!curCharUni.equals("0")){
					cast.get(curCharMale.equals("0")?curCharUni:curCharMale).addWord(word, getNum(wordInd)+index, pOS);
				}
			}
			else if (name.equals("she")){
				System.out.println("adding word - curCharFemale: "+curCharFemale);
				cast.get(curCharFemale.equals("0")?curCharUni:curCharFemale).addWord(word, getNum(wordInd)+index, pOS);
			}
			else if (cast.containsKey(name)){
				curCharUni = name;  //sets last mentioned male character as current
				System.out.println("adding word");
				cast.get(name).addWord(word, getNum(wordInd)+index, pOS);
			}
			senWithDep = senWithDep.substring(start+1, senWithDep.length());
			//System.out.println(senWithDep);
		}
	}
	
	
	/**
	 * kills all non numbers left of actual numbers in String
	 * @param numString
	 * @return
	 */
	public int getNum(String numString){
		if (numString.contains("'")){
			System.out.println("CONTAINS IT");
			numString = numString.substring(0,numString.length()-1);
		}
		while (true){
			try {
				return Integer.parseInt(numString);
			}
			catch(Exception e){
				return getNum(numString.substring(1,numString.length()));
			}
		}
	}
	
	public void addCharacter(String name){
		name = name.trim();
		String fullName = name;  //keeps full name in case we can assign gender here
		if (name.contains(" ")){  //if name has a Mr. Dr. etc we strip it
			name = name.substring(name.indexOf(" ")+1,name.length());
		}
		curCharUni = name;
		if (cast.containsKey(name)){
			System.out.print("Char Exists");
		}
		else{
			Character tempNewChar = new Character(name,fullName);
			cast.put(name, tempNewChar);  //character is added to hashmap
			listOfCharacters.add(name); //string name of character is added to this list for easy cycling later
		}
		cast.get(name).addOccurence(index);  //says index is sentence index
		if (cast.get(name).isGender()){ //if there has been any gender assignment
			if (cast.get(name).isMale()){  //if the character is male
				curCharMale = name;			//this is the last male referenced character
			}
			else{
				curCharFemale = name;       //otherwise it is the last female referenced character
			}
		}
	}
	
	public void setCleanFile(String cleanFile){
		this.cleanFile = cleanFile;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setAuthor(String author){
		this.author = author;
	}

	public static int getLetterCount() {
		return letterCount;
	}

	public static void setLetterCount(int letterCount) {
		Novel.letterCount = letterCount;
	}

	public static int getSentenceLength() {
		return sentenceLength;
	}

	public static void setSentenceLength(int sentenceLength) {
		Novel.sentenceLength = sentenceLength;
	}

	public static int getWordCount() {
		return wordCount;
	}

	public static void setWordCount(int wordCount) {
		Novel.wordCount = wordCount;
	}

	public static int getSentenceCount() {
		return sentenceCount;
	}

	public static void setSentenceCount(int sentenceCount) {
		Novel.sentenceCount = sentenceCount;
	}

	public static double getAverageSylWord() {
		return averageSylWord;
	}

	public static void setAverageSylWord(double averageSylWord) {
		Novel.averageSylWord = averageSylWord;
	}

	public static double getAverageLetWord() {
		return averageLetWord;
	}

	public static void setAverageLetWord(double averageLetWord) {
		Novel.averageLetWord = averageLetWord;
	}

	public static double getFOG() {
		return FOG;
	}

	public static void setFOG(double fOG) {
		FOG = fOG;
	}

	public static int getSpeechCount() {
		return speechCount;
	}

	public static void setSpeechCount(int speechCount) {
		Novel.speechCount = speechCount;
	}
}
