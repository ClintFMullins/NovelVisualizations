import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

import cc.mallet.grmm.types.Tree;
import cc.mallet.types.FeatureConjunction.List;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.TreebankLanguagePack;

public class Parse {
	private LexicalizedParser lp;
	
	public Parse(){
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	}
	
	/**
	 * 
	 * @param name
	 */
	public static int nameGender(String name){
		try{
			Scanner scan = new Scanner(new File("../referenceFiles/male_First.txt"));
			while (scan.hasNext()) {
			    if (scan.next().equals(name)){  //if the name is in the male database
			    	scan.close();
			    	return 1;
			    }
			}
			scan = new Scanner(new File("../referenceFiles/female_First.txt"));
			while (scan.hasNext()) {
			    if (scan.next().equals(name)){  //if the name is in the female database
			    	scan.close();
			    	return 0;
			    }
			}
			scan.close();
			return -1;
		}
		catch (Exception e){
			System.out.println("File Read Failed\n\n\n\n");
			return -1;
		}
	}
	
	/**
	 * Given ONE SENTENCE, this will return the parts of speech of all words within that sentence. 
	 * @param sentence (String) one sentence, no more no less.
	 * @return (String) that same sentence with POS tagged. Output looks like this:
	 * 
	 * (ROOT (S (VP (VB Hey) (S (NP (EX there)) (NP (NNP John)))) (. .)))
	 */
	public String getPOS (String sentence){
		edu.stanford.nlp.trees.Tree parse = lp.apply(sentence);
		//System.out.println(parse.toString());
		return parse.toString();
	}
	
	/**
	 * Given ONE SENTENCE, this will return the relationships (dependencies between words) of all words within that sentence. 
	 * @param sentence
	 * @return (String) given sentence with relationships tagged. Output looks like this:
	 * 
	 * [root(ROOT-0, Hey-1), expl(John-3, there-2), nsubj(John-3, there-2), xcomp(Hey-1, John-3)]
	 */
	public String getDep(String sentence){
	    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    edu.stanford.nlp.trees.Tree parse = lp.apply(sentence);
		//System.out.println();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		@SuppressWarnings("rawtypes")
		Collection tdl = gs.typedDependenciesCCprocessed(true);
		//System.out.println(tdl.toString());
		return tdl.toString();
	}
	
    /**
     * Searches for a 
     * @param fileName
     * @return
     */
    public static String cleanFile(String fileName) {
    	System.out.println("File Cleaned");
    	File novelFile = new File("books/"+fileName+".txt");
        StringBuilder contents = new StringBuilder();
        String line;
        try {
        	BufferedReader input =  new BufferedReader(new FileReader(novelFile));
        	try {
        		boolean read = false;
        		while (( line = input.readLine()) != null){
        			if (!read && line.contains("*** START OF THIS PROJECT GUTENBERG EBOOK")){
        				contents = new StringBuilder();
        				read=true;
        			}
        			else if (read && line.contains("*** END OF THIS PROJECT GUTENBERG EBOOK")){
        				break;
        			}
        			contents.append(line);
        			contents.append(System.getProperty("line.separator"));
        		}
          	}
          	finally {
          		input.close();
          	}
        }
        catch (IOException ex){
        		ex.printStackTrace();
        }
        try{
        	  // Create file 
        	  FileWriter fstream = new FileWriter("books/"+fileName);
        	  BufferedWriter out = new BufferedWriter(fstream);
        	  out.write(contents.toString());
        	  //Close the output stream
        	  out.close();
        }catch (Exception e){//Catch exception if any
        	  System.err.println("Error: " + e.getMessage());
        }
        return contents.toString();
    }
    
     public static void main(String[] args){
    	 Parse test = new Parse();
    	 String sentence = "Monica is so disgustingly pretty";
    	 System.out.println(test.getDep(sentence));
    	 System.out.println(test.getPOS(sentence));
     }
}