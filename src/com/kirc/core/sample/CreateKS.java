package com.kirc.core.sample;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kirc.core.utils.DBUtils;

import f1.com.kirc.core.config.Config;
import f1.com.kirc.core.config.Constants;
import f3.com.kirc.core.coTable.coTableGenerator;
import f4.com.kirc.core.model.GraphUtil;
import f4.com.kirc.core.model.NlpResultModel;
import f4.com.kirc.core.model.PRXKSGenerator;
import f5.com.kirc.core.output.outputInterface;

/**
요약:  지식구조 생성하여 DB에 업로드
인풋: 2에서 생성된 문장or문단 단위의 명사들, 명사들의 TF-IDF
결과물: 지식구조 DB에 업로드
소요시간: 50G에 2일 정도
*/
public class CreateKS {

	static Config config = null;
	static IndexReader reader = null;

	static String indexPath; 

	public static void main(String[] args)  {

		CreateKS cks = new CreateKS();
		cks.excute();

	}

	public CreateKS()  {
		config = new Config();
		indexPath = config.LuceneIndexLocation;
		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void excute() {

		/* Configuration setting, read from config.cfg. (file handler) */
		Connection conn = DBUtils.connect(config);

		Gson gson = new Gson();
		Type arrayTpe = new TypeToken< ArrayList<String>>() {}.getType();

		/* NOTE: 갯수가 많은 경우 여기서 에러 날 수 있음.
		 * 이 경우 id list를 json 포맷으로 txt로 저장해놨다가 읽으면 됨.
		 * 예) String idListStr = FileUtils.readStringFromFile(config.resourceLocation+"\\idListJson.txt");
		 * ArrayList<String> idList =gson.fromJson(idListStr, arrayTpe);
		 */
		ArrayList<String> idList = DBUtils.getDocIdfromDB(conn);
		int maxDoc = idList.size();
		System.out.println("find "+maxDoc+"documents");
		NlpResultModel nlpresult = null;

		Date start = new Date();
		int i = 0;
		String doc_id = "";
		String ks = "";


		for (i=0; i<maxDoc; i++) {

			ks = "";
			doc_id = idList.get(i);
			config.targetFileName = doc_id;
			/* FLOW2) get NLP result from DB */
			nlpresult = DBUtils.getNlpResultFromDB(conn, doc_id);

			//NOTE 너무 내용이 없을 경우 skip
			if(nlpresult.paragraph_num==0||nlpresult.totalNumberOfNNWords<4)
				continue;

			coTableGenerator coTable = new coTableGenerator(config, reader, nlpresult);

			/*NOTE 명사구의 IDF 구하는 게 오래걸림
			 * (명사구는 따로 루씬에서 인덱싱을 안 해 놓아서 일일이 쿼리를 날려서 계산해야 함)
			 * tf-idf 결과를 처음 돌렸을 때 저장해놓았다가 다음에 재활용 
			 * 예) HashMap<String, Float> tfidfTermMap = DBUtils.getTFIDFFromDB(conn, doc_id);*/
			/*NOTE: 한 단어의 IDF를 루씬에서 구하는 건 오래 안 걸림
			 * 명사구 IDF는 루씬에서 구할 수 없어서 쿼리 날려서 계산하고 DB에 넣어놓는 것임
			 * 단어 IDF는 루씬에서 그때그때 구하고 DB에 명사구 IDF만 넣으면 DB 크기 줄일 수 있음*/
			HashMap<String, Float> tfIdfVector = coTable.tfidfTermMap;
			DBUtils.uploadTfIdfVector2DB(conn, doc_id, gson.toJson(tfIdfVector));
			PRXKSGenerator prxks = new PRXKSGenerator(config, coTable);

			ks = GraphUtil.printKsEdges(prxks);

			DBUtils.uploadKS2DB(conn, doc_id, Constants.DB_TABLE_NAME_KS, config.cot_mode, ks);

			/* outputInterface. (json handler, print into file, putting into DB) */ 
			outputInterface outputInterface = new outputInterface(config, prxks);
			
			if(i%100 == 0){
				System.out.println(i);
			}
		}
		
		System.out.println("Total " + (new Date().getTime() - start.getTime()) + " milliseconds have taken.");
		System.out.println(i+"th document is uploaded. id: "+ doc_id);
		try {if( conn != null) conn.close();} catch (SQLException e) {e.printStackTrace();}
	}
}
