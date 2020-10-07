package com.kirc.core.utils;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import f1.com.kirc.core.config.Config;
import f1.com.kirc.core.config.Constants;
import f4.com.kirc.core.model.NlpResultModel;

public class DBUtils {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static String DB_URL;
	static String USERNAME;
	static String PASSWORD; 
	static Gson gson = new Gson();

	public static void main(String[] args) throws SQLException{
		//DB위치 수정할 것
		String DB_URL = "jdbc:mysql://localhost:3306/test?characterEncoding=utf8";
		String USERNAME = "root";
		String PASSWORD = "1234";
			
		Connection conn = DBUtils.connect(new Config());
		DBUtils.getDocIdfromDB(conn);
		//MySQLConn.uploadKS2DB(new Config(), conn, "1","1","item", "test");
		conn.close();
	}

	public static Connection connect(Config config){
		
		DBUtils.DB_URL = config.db_host;
		DBUtils.USERNAME = config.db_id;
		DBUtils.PASSWORD = config.db_pw;

		Connection conn = null;

		try{
			Class.forName(JDBC_DRIVER);

			System.out.println("DB_URL :"+DB_URL);
			System.out.println("Username :"+USERNAME);
			System.out.println("Password :"+PASSWORD);

			conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
			System.out.println("\tMySQL Connection");

			return conn;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	public static ArrayList<String> getDocIdfromDB(Connection conn){
		ArrayList<String> idList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		String sql = null;
		try{
			sql = "SELECT " + Constants.DB_COLUMN_NAME_ID 
					+" FROM "+ Constants.DB_TABLE_NAME_NLP_DIC;

			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while(rs.next()){
				idList.add(rs.getString(Constants.DB_COLUMN_NAME_ID));
			}rs.close();
			return idList;
		}catch(SQLException se1){
			se1.printStackTrace();
			return null;
		}catch(Exception e){          
			//LOG error
			e.printStackTrace();
			System.out.println("failed to get id list from database.");
			return null;
		}finally{                                                            
			if(pstmt != null) try{pstmt.close();}catch(SQLException sqle){sqle.printStackTrace();}      
		}
	}

	public static NlpResultModel getNlpResultFromDB(Connection conn, String doc_id){
		NlpResultModel nlpresult = new NlpResultModel();
		PreparedStatement pstmt = null;
		String sql = null;
		try{
			sql = "SELECT *" 
					+" FROM "+ Constants.DB_TABLE_NAME_NLP_DIC
					+" WHERE "+Constants.DB_COLUMN_NAME_ID+" = ?";

			pstmt = conn.prepareStatement(sql); 
			pstmt.setString(1, doc_id);
			ResultSet rs = pstmt.executeQuery();

			Type HashMapType = new TypeToken<HashMap<String, Integer>>() {}.getType();
			Type ArrayListOfHashMapType = new TypeToken<ArrayList<HashMap<String, Integer>>>() {}.getType();
			while(rs.next()){
				nlpresult.doc_id = rs.getString(Constants.DB_COLUMN_NAME_ID);
				nlpresult.allTermsHash = gson.fromJson(rs.getString(Constants.DB_COLUMN_NAME_ALL_HASH),HashMapType);
				nlpresult.sentenceArray = gson.fromJson(rs.getString(Constants.DB_COLUMN_NAME_SEN_HASH),ArrayListOfHashMapType);
				nlpresult.paragraphArray = gson.fromJson(rs.getString(Constants.DB_COLUMN_NAME_PARA_HASH),ArrayListOfHashMapType);
				nlpresult.totalNumberOfNNWords = rs.getInt(Constants.DB_COLUMN_NAME_NN_WORD_NUM);
				nlpresult.text_length = rs.getInt(Constants.DB_COLUMN_NAME_TEXT_LENGTH);
				nlpresult.sentence_num = rs.getInt(Constants.DB_COLUMN_NAME_SEN_NUM);
				nlpresult.paragraph_num = rs.getInt(Constants.DB_COLUMN_NAME_PAR_NUM);
			}rs.close();
			return nlpresult;
		}catch(SQLException se1){
			se1.printStackTrace();
			return null;
		}catch(Exception e){          
			//LOG error
			e.printStackTrace();
			System.out.println("failed to get knowledge structure from database.");
			return null;
		}finally{                                                            
			if(pstmt != null) try{pstmt.close();}catch(SQLException sqle){sqle.printStackTrace();}      
		}
	}

	public static HashMap<String, Float> getTFIDFFromDB(Connection conn, int doc_id){
		HashMap<String, Float> tfidfTermMap = null;
		PreparedStatement pstmt = null;
		String sql = null;
		try{
			sql = "SELECT "
					+ Constants.DB_COLUMN_NAME_TFIDF
					+" FROM "+ Constants.DB_TABLE_NAME_TFIDF
					+" WHERE "+Constants.DB_COLUMN_NAME_ID+" = ?";

			pstmt = conn.prepareStatement(sql); 
			pstmt.setInt(1, doc_id);
			ResultSet rs = pstmt.executeQuery();

			Type HashMapType = new TypeToken<HashMap<String, Float>>() {}.getType();
			while(rs.next()){
				tfidfTermMap = gson.fromJson(rs.getString(Constants.DB_COLUMN_NAME_TFIDF),HashMapType);
			}
			return tfidfTermMap;
		}catch(SQLException se1){
			se1.printStackTrace();
			return null;
		}catch(Exception e){          
			//LOG error
			e.printStackTrace();
			System.out.println("failed to get tfidf from database.");
			return null;
		}finally{                                                            
			if(pstmt != null) try{pstmt.close();}catch(SQLException sqle){sqle.printStackTrace();}      
		}
	}

	public static void uploadTfIdfVector2DB(Connection conn, String doc_id, String tfidfVectorJson){
		PreparedStatement pstmt = null;
		String sql = null;

		sql = "INSERT INTO "+ Constants.DB_TABLE_NAME_TFIDF
				+ " VALUES(?,?)"
				+ " ON DUPLICATE KEY UPDATE " + Constants.DB_COLUMN_NAME_TFIDF + " = ?";
		try{
			pstmt = conn.prepareStatement(sql); 
			pstmt.setString(1, doc_id);
			pstmt.setString(2, tfidfVectorJson);
			pstmt.setString(3, tfidfVectorJson);
			pstmt.executeUpdate(); 
			//System.out.println("\tKnowledge Structure is uploaded to DB!\n");//+pstmt
		}catch(SQLException se1){
			se1.printStackTrace();
		}catch(Exception e){                                                   
			e.printStackTrace();
			System.out.println("fail to insert tfidf.");
		}finally{                                                            
			if(pstmt != null) try{pstmt.close();}catch(SQLException sqle){}      
		}
	}

	public static void uploadNLPResults2DB(Connection conn, String doc_id, String allHash_json, String sen_hash_gson, String para_hash_gson, int sen_num, int par_num, int text_length, int totalNumberOfNNWord){
		PreparedStatement pstmt = null;
		String sql = null;

		sql = "INSERT INTO "+ Constants.DB_TABLE_NAME_NLP_DIC
				+ " VALUES(?,?,?,?,?,?,?,?)"
				//FIXME: 중복된 PK가 들어올 경우 내용 업데이트해야 함
				+ " ON DUPLICATE KEY UPDATE " + Constants.DB_COLUMN_NAME_ID + " = ?";
		try{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, doc_id);
			pstmt.setString(2, allHash_json);
			pstmt.setString(3, sen_hash_gson);
			pstmt.setString(4, para_hash_gson );
			pstmt.setInt(5, sen_num);
			pstmt.setInt(6, par_num);
			pstmt.setInt(7, text_length);
			pstmt.setInt(8, totalNumberOfNNWord);
			pstmt.setString(9, doc_id);

			System.out.println("pstmt : " + pstmt);
			pstmt.executeUpdate();
		}catch(SQLException se1){
			se1.printStackTrace();
		}catch(Exception e){                                                   
			e.printStackTrace();
			System.out.println("fail to insert the record.");
		}finally{                                                            
			if(pstmt != null) try{pstmt.close();}catch(SQLException sqle){}      
		}
	}

	public static void uploadKS2DB(Connection conn, String doc_id, String table_name, String cot_mode, String ks){
		PreparedStatement pstmt = null;
		String sql = null;

		sql = "INSERT INTO "+ table_name 
				+ " VALUES(?,?,?)"
				+ " ON DUPLICATE KEY UPDATE " + Constants.DB_COLUMN_NAME_COTMODE + " = ?,"
				+Constants.DB_COLUMN_NAME_KS+ "=?;";

		try{
			pstmt = conn.prepareStatement(sql); 

			pstmt.setString(1, doc_id);
			pstmt.setString(2, cot_mode);
			pstmt.setString(3, ks);
			pstmt.setString(4, cot_mode);
			pstmt.setString(5, ks);

			pstmt.executeUpdate(); 

		}catch(SQLException se1){
			se1.printStackTrace();
		}catch(Exception e){                                                   
			e.printStackTrace();
			System.out.println("fail to insert the record.");
		}finally{                                                            
			if(pstmt != null) try{pstmt.close();}catch(SQLException sqle){}      
		}
	}
}
