package core.wordutils;

import java.util.List;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;


public class WS4JCalculateWordSimilarity {
	private static ILexicalDatabase db = new NictWordNet();
    private static RelatednessCalculator lin = new Lin(db);
    private static RelatednessCalculator wup = new WuPalmer(db);
    private static RelatednessCalculator path = new Path(db);
    private static RelatednessCalculator lcn = new LeacockChodorow(db);
    private static RelatednessCalculator jcn = new JiangConrath(db);
    private static RelatednessCalculator resnik = new Resnik(db);
    private static RelatednessCalculator lesk = new Lesk(db);
    
    
    public static double calcMaxScore(String word1, String word2, RelatednessCalculator rc){
        	WS4JConfiguration.getInstance().setMFS(true);
			List<POS[]> posPairs = rc.getPOSPairs();
			double maxScore = -1D;

			for(POS[] posPair: posPairs) {
			    List<Concept> synsets1 = (List<Concept>)db.getAllConcepts(word1, posPair[0].toString());
			    List<Concept> synsets2 = (List<Concept>)db.getAllConcepts(word2, posPair[1].toString());

			    for(Concept synset1: synsets1) {
			        for (Concept synset2: synsets2) {
			            Relatedness relatedness = rc.calcRelatednessOfSynset(synset1, synset2);
			            double score = relatedness.getScore();
			            if (score > maxScore) { 
			                maxScore = score;
			            }
			        }
			    }
			}

			if (maxScore == -1D) {
			    maxScore = 0.0;
			}
			
			return maxScore;
    }
    public static double getLin(String word1, String word2){
    	return calcMaxScore(word1, word2, lin);
    }
    public static double getWup(String word1, String word2){
    	return calcMaxScore(word1, word2, wup);
    }
    public static double getPath(String word1, String word2){
    	return calcMaxScore(word1, word2, path);
    }
    
    public static double getLcn(String word1, String word2){
    	return calcMaxScore(word1, word2, lcn);
    }
    
    public static double getJcn(String word1, String word2){
    	return calcMaxScore(word1, word2, jcn);
    }
    
    public static double getResnik(String word1, String word2){
    	return calcMaxScore(word1, word2, resnik);
    }
    
    public static double getLesk(String word1, String word2){
    	return calcMaxScore(word1, word2, lesk);
    }
}
