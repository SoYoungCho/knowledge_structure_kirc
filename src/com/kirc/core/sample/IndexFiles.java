package com.kirc.core.sample;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;

import com.kirc.core.utils.FileUtils;
import com.kirc.core.utils.IndexUtils;

import f1.com.kirc.core.config.Config;
/**
요약: 디렉토리에 있는 txt파일을 Lucene에 인덱싱함 
WHY? 후에 IDF 점수를 제대로 구하려면 문서 전체 셋을 우선 인덱싱 해놓은 후 계산해야 함. -> 인덱싱 후 IDF 구하기 
소요시간: 50G 인덱싱하는데 4시간 정도 
**/
public class IndexFiles {
	FieldType type = new FieldType();
	Config config = null;
	String directoryPath = "";


	public static void main(String[] args){
		// NOTE: txt는 인코딩이 utf-8일 것.
		IndexFiles idxf = new IndexFiles();
		idxf.excute();
	}
	
	public IndexFiles(){
		config = new Config();
		// 루씬 설정
		type.setTokenized(true);
		type.setIndexed(true);
		type.setStoreTermVectors(true);
		type.setStored(true);
		type.setStoreTermVectors(true);
		
		this.directoryPath = config.resourceLocation;
	}
	
	public void excute(){

		int i = 0;
		Date start = new Date();
		String lucenePath = config.LuceneIndexLocation;
		
		try {
			System.out.println("Indexing to directory '" + lucenePath + "'...");
			//TODO: lucene 최신 버전으로 업그레이드
			Directory dir = FSDirectory.open(new File(lucenePath));

			//한글 분석기
//			KoreanAnalyzer analyzer_kor = new com.tistory.devyongsik.analyzer.KoreanAnalyzer();
//			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer_kor);

			//영어 분석기
			EnglishAnalyzer analyzer_eng = new EnglishAnalyzer(Version.LUCENE_4_9);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer_eng);

			boolean create = false; 
			if (create) {
				// 기존의 인덱스파일은 삭제하고 새로운 인덱스파일을 생성한다. 
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// 기존 인덱스파일에 추가한다. +=
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			IndexWriter writer = new IndexWriter(dir, iwc);
			
			ArrayList<File> txt_files = FileUtils.read_folder(directoryPath);
			int limit = txt_files.size();
			System.out.println("총 "+limit+" 개의 파일을 인덱싱 합니다.");
			for(i = 0; i<limit;i++){	
				File targetFile = txt_files.get(i);
				//NOTE: 파일 제목을 txt ID로 함.
				String docID = targetFile.getName().split("\\.")[0];
				String docContents = FileUtils.readStringFromFile(targetFile);
				// 루씬에 인덱싱
				IndexUtils.indexDoc(type, writer, docID, docContents);
				if(i%100 == 0 && i!=0) System.out.println(i+"th document is indexed!");
			}		
			writer.close();
		}catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		} 
		System.out.println("인덱싱작업을 하는데 총 " + (new Date().getTime() - start.getTime()) + " milliseconds 가 걸렸습니다.");
	}
}