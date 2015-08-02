//package core.wordutils;
//
//import java.util.List;
//
//import edu.cmu.lti.jawjaw.pobj.POS;
//import edu.cmu.lti.lexical_db.ILexicalDatabase;
//import edu.cmu.lti.lexical_db.NictWordNet;
//import edu.cmu.lti.lexical_db.data.Concept;
//import edu.cmu.lti.ws4j.Relatedness;
//import edu.cmu.lti.ws4j.RelatednessCalculator;
//import edu.cmu.lti.ws4j.impl.HirstStOnge;
//import edu.cmu.lti.ws4j.impl.JiangConrath;
//import edu.cmu.lti.ws4j.impl.LeacockChodorow;
//import edu.cmu.lti.ws4j.impl.Lesk;
//import edu.cmu.lti.ws4j.impl.Lin;
//import edu.cmu.lti.ws4j.impl.Path;
//import edu.cmu.lti.ws4j.impl.Resnik;
//import edu.cmu.lti.ws4j.impl.WuPalmer;
//
//public class SimilarityCalculationDemo {
//	
//	private static ILexicalDatabase db = new NictWordNet();
//	private static RelatednessCalculator[] rcs = {
//			new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db), 
//			new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
//			};
//	
//	private static void run( String word1, String word2 ) {
////		WS4JConfiguration.getInstance().setMFS(true);
////		PorterStemmer porterStemmer = new PorterStemmer();
////		word1 = porterStemmer.stemWord(word1);
////		word2 = porterStemmer.stemWord(word2);
//		for ( RelatednessCalculator rc : rcs ) {
//			List<POS[]> posPairs = rc.getPOSPairs();
//			double maxScore = -1D;
//
//			for(POS[] posPair: posPairs) {
//			    List<Concept> synsets1 = (List<Concept>)db.getAllConcepts(word1, posPair[0].toString());
//			    List<Concept> synsets2 = (List<Concept>)db.getAllConcepts(word2, posPair[1].toString());
//
//			    for(Concept synset1: synsets1) {
//			        for (Concept synset2: synsets2) {
//			            Relatedness relatedness = rc.calcRelatednessOfSynset(synset1, synset2);
//			            double score = relatedness.getScore();
//			            if (score > maxScore) { 
//			                maxScore = score;
//			            }
//			        }
//			    }
//			}
//
//			if (maxScore == -1D) {
//			    maxScore = 0.0;
//			}
//
//			System.out.println(rc.getClass().getName() + " sim('" + word1 + "', '" + word2 + "') =  " + maxScore);
//		}
//	}
//	public static void main(String[] args) {
//		long t0 = System.currentTimeMillis();
//		run( "beauty","sunrise" );
//		long t1 = System.currentTimeMillis();
//		System.out.println( "Done in "+(t1-t0)+" msec." );
//	}
//}