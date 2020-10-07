package com.kirc.core.sample;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import com.google.gson.Gson;
import com.kirc.core.utils.DBUtils;

import f1.com.kirc.core.config.Constants;
import f1.com.kirc.core.config.Config;
import f2.com.kirc.core.nlp.English_postagger;
import f2.com.kirc.core.nlp.Korean_postagger;
import f2.com.kirc.core.nlp.NLP;

public class PreprocNLP {
	static Config config = null;
	static NLP nlp = null;

	static English_postagger engtagger = null;
	static Korean_postagger kortagger = null;

	static IndexReader reader;
	static String indexPath; 

	public static void main(String[] args){

		PreprocNLP unlp = new PreprocNLP();
		unlp.execute();

	}

	public PreprocNLP(){
		config = new Config();
		nlp = new NLP();
		//모델 로드하는 게 느려서 미리 로드해 놓음
		engtagger = new English_postagger(config.engTaggerLocation);
		kortagger = new Korean_postagger(config.korTaggerLocation);
		indexPath = config.LuceneIndexLocation;
	}

	public boolean execute(){

		/* FLOW1) Configuration setting, read from config.cfg. (file handler) */
		Connection conn = DBUtils.connect(config);
		Date start = new Date();
		int Luc_id = 0;
		Gson gson = new Gson();
		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
			int maxDoc = reader.maxDoc();
			//NOTE 루씬에 저장된 모든 파일을 순서대로 불러와서 전처리함
			for (int i=0; i<maxDoc; i++) {
				Document doc = reader.document(i);
				Luc_id = i;
				String doc_id = doc.get(Constants.DOC_ID_FIELD_NAME);
				String contents = doc.get(Constants.DOC_CONTENTS_FIELD_NAME);

				//System.out.println("doc_id: "+doc_id+" lucene id: "+Luc_id);

				// 루씬에서 파일이 삭제된 경우 null로 남음
				if(doc_id == null || contents == null )
					continue;

				/* FLOW2) NLP - 한/영 판단, 형태소 분석, TF 계산, 명사구 처리 */
				NLP nlp = new NLP();
				nlp.preprocessing(config, contents);	

				//NOTE: json으로 변환하여 DB에 올림
				//NOTE: 스트링으로 된 중간 결과물을 다 넣다보니 DB가 커짐.  
				DBUtils.uploadNLPResults2DB(conn, doc_id, gson.toJson(nlp.allHash),
						gson.toJson(nlp.sentenceHash), gson.toJson(nlp.paragraphHash),
						nlp.sentenceHash.size(), nlp.paragraphHash.size(), 
						contents.length(), nlp.totalNumberOfNNWord);

				if(i%100 == 0){
					System.out.println(i);
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				if( reader != null) reader.close();
				if( conn != null) conn.close();
			} 
			catch (IOException e) {e.printStackTrace();} 
			catch (SQLException e) {e.printStackTrace();}
			System.out.println("lucene id: "+Luc_id);
			System.out.println("Total " + (new Date().getTime() - start.getTime()) + " milliseconds have taken.");
		}
	}
}
