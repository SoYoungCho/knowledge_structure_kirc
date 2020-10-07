package com.kirc.core.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.TermStats;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import f1.com.kirc.core.config.Config;
import f1.com.kirc.core.config.Constants;
import f2.com.kirc.core.nlp.English_postagger;

/*
 * This class is calculating the tf*idf score for every term in every document using the latest techniques of the Lucene API
 */


public class TF_IDF 
{
	static float tf = 1;
	static float idf = 0;
	private static float tfidf_score;

	static HashMap<String, Float> tfidfVector = new HashMap<String, Float>();
	static EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_4_9);
	static TFIDFSimilarity tfidfSIM = new DefaultSimilarity();
	static IndexSearcher searcher = null;
	static QueryParser qp = new QueryParser(Version.LUCENE_4_9,Constants.DOC_CONTENTS_FIELD_NAME, analyzer);

	public static void main(String args[]) {
	}

	public static HashMap<String, Float> getNN_TFIDF(HashMap<String, Integer> allHash, IndexReader reader, String field){
		tfidfVector = new HashMap<String, Float>();

		if(searcher == null)
			searcher = new IndexSearcher(reader);

		Iterator<String> keys = allHash.keySet().iterator();

		while( keys.hasNext() ){
			String key = keys.next();
			//System.out.println(key);
			//following TF IDF formular of lucene
			float tf =  tfidfSIM.tf(allHash.get(key));
			float idf = 0.0f;
			int num_of_docs =  reader.numDocs();

			if(!key.contains(" ")) 
				idf = getIDF_term(reader, num_of_docs, field, key);
			else {
				/*NOTE 키워드가 구(phrase)일 경우 
				 * 루씬에 쿼리를 날려서 idf 구해야 함 -> 시간 소요
				 * tf가 2개 이상인 구만 처리
				 * 구를 사용하지 않을 것이면 tf 제한을 올리거나 주석처리**/
				if(tf <= 1) continue;
				idf = getIDF_phrase(searcher, num_of_docs, field, key);
			}
				//System.out.println("term: "+ key + " freq: "+allHash.get(key)+" tf: "+tf+" idf:" +idf);   			

			tfidfVector.put(key, tf * idf); 
		}
		return tfidfVector;
	}

	public static float getIDF_term(IndexReader reader, int num_of_docs, String field, String term)
			 {
		float idf =0.0f;

		/*
		 * 
		 * /** GET TERM FREQUENCY & IDF 
		TFIDFSimilarity tfidfSIM = new DefaultSimilarity();
		Term termInstance = new Term(Constants.DOC_CONTENT_FIELD_NAME, term);     
		idf = tfidfSIM.idf(reader.docFreq(termInstance), reader.numDocs());
		 
		Query q;
		TopDocs topDocs = null;
		try {
			//q = new QueryParser(Version.LUCENE_4_9, Constants.DOC_CONTENTS_FIELD_NAME, analyzer).parse(term);
			q = new TermQuery(new Term( Constants.DOC_CONTENTS_FIELD_NAME, term));
			topDocs = searcher.search(q, 1);
			
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		int docFreq = topDocs.totalHits;
		idf = tfidfSIM.idf( docFreq, num_of_docs);*/

		//Term termInstance = new Term(field, term);   
		int docFreq = 0;
		try {docFreq = reader.docFreq(new Term(field, term));} catch (IOException e) {}
		idf = tfidfSIM.idf(docFreq, num_of_docs);
		//System.out.println(docFreq);
		if(docFreq == 0) {
			idf = 0;
			//System.out.println(term + " idf: "+idf);
		}
		return idf;
	}

	public static float getIDF_phrase(IndexSearcher searcher, int num_of_docs, String field, String term)
			 {
		float idf =0.0f;
		
		PhraseQuery phrasequery = new PhraseQuery();
		String[] words = term.split(" ");

		for(int i = 0 ; i < words.length ; i ++){
			phrasequery.add(new Term(field, words[i]), i);
		}
		
		//Query query =qp.createPhraseQuery(field, term, 0);
		
		TopDocs topDocs = null;
		try {topDocs = searcher.search(phrasequery, 1);} catch (IOException e) {e.printStackTrace();}
		int docFreq = topDocs.totalHits;

		idf = tfidfSIM.idf( docFreq, num_of_docs);
		if(docFreq == 0) {
			idf = 0;
			//System.out.println(term + " idf: "+idf);
		}
		//System.out.println(phrasequery.toString());
		//System.out.println(term + " idf: "+idf);
		return idf;
	}

	public static void scoreCalculator(IndexReader reader, String field, String term)
			throws IOException {
		/** GET TERM FREQUENCY & IDF **/
		TFIDFSimilarity tfidfSIM = new DefaultSimilarity();
		Bits liveDocs = MultiFields.getLiveDocs(reader);
		TermsEnum termEnum = MultiFields.getTerms(reader, field).iterator(null);
		BytesRef bytesRef;
		while ((bytesRef = termEnum.next()) != null) {
			if (bytesRef.utf8ToString().trim().equals(term.trim())) {
				if (termEnum.seekExact(bytesRef)) {

					int docfreq = termEnum.docFreq();
					int numdocs = reader.numDocs();

					idf = tfidfSIM.idf(docfreq, numdocs);

					DocsEnum docsEnum = termEnum.docs(liveDocs, null);
					if (docsEnum != null) {
						int doc;
						while ((doc = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
							tf = tfidfSIM.tf(docsEnum.freq());
							tfidf_score = tf * idf;
							System.out.println("doc number: " + doc+ "  term:" + term+ " -tfidf_score- " + tfidf_score);
							System.out.println("doc number: " + doc+ "  term:" + term+ " -tf_score- " + tf + " docsEnum.freq:"+ docsEnum.freq());
							System.out.println("doc number: " + doc+ "  term:" + term+ " -idf_score- " + idf+ " docfreq:"+ docfreq+ " numDocs:"+ numdocs);
						}
					}
				}
			}
		}
	}


	public static float getTF(IndexReader reader, int docID,String field, String NN_terms)
			throws IOException {
		String term_stemmed = "";
		try {term_stemmed = qp.parse(NN_terms).toString();} catch (ParseException e) {;}
		/*
		Terms vector = reader.getTermVector(docID, field);
		TermsEnum termsEnum = null;
		termsEnum = vector.iterator(termsEnum);

		Map<String, Long> frequencies = new HashMap<>();
		BytesRef text = null;
		while ((text = termsEnum.next()) != null) {
			String term = text.utf8ToString();
			if(NN_terms.contains(term)){
				long freq =  termsEnum.totalTermFreq();
				frequencies.put(term, freq);
				System.out.println(term + " " + freq);
				//terms.add(term);
			}
		}
		*/
		return tf;
	}
}