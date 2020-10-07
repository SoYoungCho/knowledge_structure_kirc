package com.kirc.core.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

import f1.com.kirc.core.config.Config;
import f1.com.kirc.core.config.Constants;

public class IndexUtils {
	
	/**주어진 환경설정에 따라(field type, indexWriter) 인풋으로 들어온 텍스트와, 텍스트의 id를 루신에 입력**/
	public static void indexDoc( FieldType type, IndexWriter writer, String txtID, String text){
		
		try {
			Document doc = new Document();

			// NOTE: txt 길이가 100 이하면 인덱싱하지 않음.
			if( text.length() > 100){
				doc.add(new StringField(Constants.DOC_ID_FIELD_NAME, txtID, Field.Store.YES));
				doc.add(new Field(Constants.DOC_CONTENTS_FIELD_NAME,text, type ));
			}

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				writer.addDocument(doc);
			} else {
				// txtID가 이미 루씬에 있으면, 중복으로 문서를 넣지 않고 문서 내용만 인덱싱 함.
				writer.updateDocument(new Term(Constants.DOC_ID_FIELD_NAME, txtID), doc);
			}
		}catch(Exception e){
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	
	}
	
	public static String getContentsFromLucene(String LuceneIndexLocation, String doc_id){
		String contents = null;
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(LuceneIndexLocation)));
			IndexSearcher isearcher = new IndexSearcher(reader);

			TermQuery tq = new TermQuery(new Term(Constants.DOC_ID_FIELD_NAME, doc_id));

			TopScoreDocCollector collector = TopScoreDocCollector.create(1, true);
			isearcher.search(tq, collector);
			ScoreDoc hits[] = collector.topDocs().scoreDocs;
			//System.out.println("Records found: "+hits.length);

			if(hits.length == 0)
				return null;
			for( int i = 0 ; i < hits.length ; i ++){
				Document HitDoc = isearcher.doc(hits[0].doc);
				contents = HitDoc.get(Constants.DOC_CONTENTS_FIELD_NAME);				
			}
			reader.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		return contents;
	}
}
