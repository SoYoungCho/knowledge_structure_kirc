package f1.com.kirc.core.config;

public class Constants {

	public final static String DOC_ID_FIELD_NAME  = "doc_id";
	public final static String DOC_CONTENTS_FIELD_NAME = "contents";
	
	public final static String DB_TABLE_NAME_NLP_DIC = "nlp_dic";
	
	public final static String DB_TABLE_NAME_TFIDF = "tf_idf_vector";
	
	public final static String DB_TABLE_NAME_KS = "knowledge_structure";
	
	public final static String DB_COLUMN_NAME_ID = "DOC_ID";
	public final static String DB_COLUMN_NAME_KS = "KNOWLEDGE_STRUCTURE";
	public final static String DB_COLUMN_NAME_KS_SS = "KS_SS";
	public final static String DB_COLUMN_NAME_KS_SCS = "KS_SCS";
	public final static String DB_COLUMN_NAME_COTABLE = "COTABLE_GRAPH";
	public final static String DB_COLUMN_NAME_TFIDF = "TF_IDF_VECTOR";
	
	public final static String DB_COLUMN_NAME_ALL_HASH = "ALL_HASHMAP";
	public final static String DB_COLUMN_NAME_SEN_HASH = "SENTENCE_HASHMAP";
	public final static String DB_COLUMN_NAME_PARA_HASH = "PARAGRAPH_HASHMAP";
	public final static String DB_COLUMN_NAME_COTMODE = "COT_MODE";
	public final static String DB_COLUMN_NAME_SEN_NUM = "SENTENCE_NUM";
	public final static String DB_COLUMN_NAME_PAR_NUM = "PARAGRAPH_NUM";
	public final static String DB_COLUMN_NAME_TEXT_LENGTH = "TEXT_LENGTH";
	public final static String DB_COLUMN_NAME_NN_WORD_NUM = "NUM_NN_WORD";
	
	public final static String INDICATOR_SYMBOL_PERIOD = "¸";//" ItIsPeriod ";//"¸"; //UTF-8 Character: U+00B8	¸	c2 b8	CEDILLA
	public final static String INDICATOR_SYMBOL_NEW_LINE = "¶";//" ItIsNewLine ";//"¶";//UTF-8 Character: U+00B6	¶	c2 b6	PILCROW SIGN
	
	public final static String INDICATOR_CHARACTOR_PERIOD = "cedilla";//" ItIsPeriod ";//"¸"; //UTF-8 Character: U+00B8	¸	c2 b8	CEDILLA
	public final static String INDICATOR_CHARACTOR_NEW_LINE = "pilcrow";//" ItIsNewLine ";//"¶";//UTF-8 Character: U+00B6	¶	c2 b6	PILCROW SIGN
}
