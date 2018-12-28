package cb.itcast.lencen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;



public class LucenceManger {

	/**
	 * ����������
	 * @throws IOException
	 */
	@Test
	public void createDump() throws IOException{
		/*
		//�����������ŵ�λ��
		String dumpPath="D:\\temp\\1101";
		Directory directory=FSDirectory.open(new File(dumpPath));
		//ָ��������
		StandardAnalyzer sAnalyzer=new StandardAnalyzer();
		IndexWriterConfig indexWriterConfig=new IndexWriterConfig(Version.LATEST, sAnalyzer);
		//����indexWrite����
		 * 
		 */
		IndexWriter indexWriter=getIndexWrite();	
		//��ȡ����������ducument����
		int i=0;
		File musiceFile=new File("F:\\�̲�\\day67-Lucene\\day71-Lucene\\00.�ο�����\\���");
		for(File f:musiceFile.listFiles()){
			if(f.isFile()){
				//����ducument����
				Document document=new Document();
				//����Filed����
				Field fieldId=new StringField("id", ""+i++, Store.YES);
				//�ļ���
				Field fieldName=new TextField("fieldName", f.getName(), Store.YES);
				//�ļ�����
				String contentString=FileUtils.readFileToString(f);
				Field fieldContent=new TextField("fieldContent", contentString, Store.YES);
				//�ļ�·��
				Field fieldPath=new StoredField("path", f.getPath());
				//�ļ���С
				Field fieldSize=new LongField("size", FileUtils.sizeOf(f), Store.YES);
				document.add(fieldId);
				document.add(fieldName);
				document.add(fieldContent);
				document.add(fieldPath);
				document.add(fieldSize);
				indexWriter.addDocument(document);
			}
		}
		indexWriter.close();
	}
	
	/**
	 * ��ѯ
	 * @throws IOException
	 */
	@Test
	public void queryIndex() throws IOException{
		//�����������ŵ�λ��
		String dumpPath="D:\\temp\\1101";
		Directory directory=FSDirectory.open(new File(dumpPath));
		//����indexReader
		IndexReader indexReader=DirectoryReader.open(directory);
		//ʹ��indexSeracher��ѯ
		IndexSearcher indexSearcher=new IndexSearcher(indexReader);
		//����һ��query
		Query query=new TermQuery(new Term("id", "0"));
		//ִ�в�ѯ
		TopDocs topDocs=indexSearcher.search(query, 100);
		//ȡ��ѯ����
		System.out.println(topDocs.totalHits);
		//
		for(ScoreDoc scoreDoc:topDocs.scoreDocs){
			Document document=indexSearcher.doc(scoreDoc.doc);
			//��document�л�ȡ����
			System.out.println(document.get("id"));
			System.out.println(document.get("fieldName"));
			System.out.println(document.get("fieldContent"));
			System.out.println(document.get("path"));
			System.out.println(document.get("size"));
		}
				
	}
	
	/**
	 * ��׼������:StandardAnalyzer
	 * @throws IOException 
	 */
	@Test
	public void StandardAnalyzer() throws IOException{
		//������׼������
		StandardAnalyzer standardAnalyzer=new StandardAnalyzer();
		//��ȡtokenStream
		TokenStream tokenStream=standardAnalyzer.tokenStream("text", "I LOVE YOU,DO YOU LOVE ME");
		//�鿴�ؼ�������
		CharTermAttribute charTermAttribute=tokenStream.addAttribute(CharTermAttribute.class);
		//ƫ��������
		OffsetAttribute offsetAttribute=tokenStream.addAttribute(OffsetAttribute.class);
		//����tokenStream
		tokenStream.reset();
		while(tokenStream.incrementToken()){
			System.out.println("start-->"+offsetAttribute.startOffset());
			System.out.println(charTermAttribute);
			System.out.println("end-->"+offsetAttribute.endOffset());
		}
		tokenStream.close();
	} 
	/**
	 * ���ķ�����:CJKAnalyzer
	 * @throws IOException 
	 */
	@Test
	public void testCJKAnalyzer() throws IOException{
		CJKAnalyzer cjkAnalyzer=new CJKAnalyzer();
		TokenStream tokenStream=cjkAnalyzer.tokenStream("text1", "��ϲ���㣬��ϲ������");
		CharTermAttribute charTermAttribute=tokenStream.addAttribute(CharTermAttribute.class);
		OffsetAttribute offsetAttribute=tokenStream.addAttribute(OffsetAttribute.class);
		tokenStream.reset();
		while(tokenStream.incrementToken()){
			System.out.println("start-->"+offsetAttribute.startOffset());
			System.out.println(charTermAttribute);
			System.out.println("end-->"+offsetAttribute.endOffset());
			
		}
		tokenStream.close();
	}
	
	/**
	 * ���ķ�����:SmartChineseAnalyzer
	 * @throws IOException 
	 */
	@Test
	public void testSmartChineseAnalyzer() throws IOException {
		SmartChineseAnalyzer chineseAnalyzer=new SmartChineseAnalyzer();
		TokenStream tokenStream=chineseAnalyzer.tokenStream("text2", "��ϲ���㣬��ϲ������");
		CharTermAttribute charTermAttribute=tokenStream.addAttribute(CharTermAttribute.class);
		OffsetAttribute offsetAttribute=tokenStream.addAttribute(OffsetAttribute.class);
		tokenStream.reset();
		while(tokenStream.incrementToken()){
			System.out.println("start-->"+offsetAttribute.startOffset());
			System.out.println(charTermAttribute);
			System.out.println("end-->"+offsetAttribute.endOffset());
			
		}
		tokenStream.close();
	}
	/**
	 * ���ķ�����:IKAnalyzer
	 * @throws IOException 
	 */
	@Test
	public void testIKAnalyzer() throws IOException{
		IKAnalyzer ikAnalyzer=new IKAnalyzer();
		TokenStream tokenStream =ikAnalyzer.tokenStream("text", "Do it right");
		CharTermAttribute charTermAttribute=tokenStream.addAttribute(CharTermAttribute.class);
		OffsetAttribute offsetAttribute=tokenStream.addAttribute(OffsetAttribute.class);
		tokenStream.reset();
		while(tokenStream.incrementToken()){
			System.out.println("start-->"+offsetAttribute.startOffset());
			System.out.println(charTermAttribute);
			System.out.println("end-->"+offsetAttribute.endOffset());
			
		}
		tokenStream.close();
	}
	
	/**
	 * ��������
	 * @throws IOException 
	 */
	@Test
	public void  addIndex() throws IOException {
		/*
		//����������
		String dumpPath="D:\\temp\\1101";
		//����directory
		Directory directory =FSDirectory.open(new File(dumpPath));
		//�������ķִ���
		IKAnalyzer analyzer=new IKAnalyzer();
		//����һ��indexWriteConfig
		IndexWriterConfig config=new IndexWriterConfig(Version.LATEST, analyzer);
		*
		*/
		//����IndexWrite
		IndexWriter indexWriter=getIndexWrite();
		File musicDir=new File("F:\\�̲�\\day67-Lucene\\day71-Lucene\\00.�ο�����\\���");
		for (File f:musicDir.listFiles()) {
			//����ducument
			Document document=new Document();
			//����field
			Field fieldName=new TextField("fieldName", f.getName(), Store.YES);
			String stringContent=FileUtils.readFileToString(f);
			Field fieldContent=new TextField("fieldContent", stringContent, Store.YES);
			Field fieldPath=new StoredField("path", f.getPath());
			Field fieldSize=new LongField("size", FileUtils.sizeOf(f), Store.YES);
			//�����document��
			document.add(fieldName);
			document.add(fieldContent);
			document.add(fieldPath);
			document.add(fieldSize);
			//���document
			indexWriter.addDocument(document);
		}
		indexWriter.close();
	}
	/**
	 * ��ȡindexWriter����
	 * @return
	 * @throws IOException
	 */
	private IndexWriter getIndexWrite() throws IOException{
		String dumpPath="D:\\temp\\1101";
		//����directory
		Directory directory =FSDirectory.open(new File(dumpPath));
		//�������ķִ���
		IKAnalyzer analyzer=new IKAnalyzer();
		//����һ��indexWriteConfig
        IndexWriterConfig config=new IndexWriterConfig(Version.LATEST, analyzer);
		//����IndexWrite
		IndexWriter indexWriter=new IndexWriter(directory, config);
		return indexWriter;
	}
	
	
	/**
	 * ���һ��document
	 * @throws IOException 
	 */
	@Test
	public void  addOneDocument() throws IOException {
		IndexWriter indexWriter=getIndexWrite();
		Document document=new Document();
		Field fieldName=new TextField("filedName", "���������ӵ�document����",Store.YES);
		Field fieldContent1=new TextField("fieldContent", "��������ӵ�docment�����ݣ����ݾ��ǲ�����֪��",Store.YES);
		Field fieldContent2=new TextField("fieldContent", "��ϲ���㣬��ϲ������,���ǲ���",Store.YES);
		document.add(fieldName);
		document.add(fieldContent1);
		document.add(fieldContent2);
		indexWriter.addDocument(document);
		indexWriter.close();
	}
	
	/**
	 * ɾ��ָ���ĵ�
	 * @throws IOException 
	 */
	@Test
	public void testDeleteDocument() throws IOException{
		IndexWriter indexWriter=getIndexWrite();
		indexWriter.deleteDocuments(new TermQuery(new Term("filedName", "������")));
		indexWriter.commit();
		indexWriter.close();
	}
	
	/**
	 * ɾ�����е�document
	 * @throws IOException 
	 */
	@Test
	public void testDeleteAll() throws IOException{
		IndexWriter indexWriter=getIndexWrite();
		indexWriter.deleteAll();
		indexWriter.close();

	}
	
	/**
	 * ��ȡIndexSearcher����
	 * @return
	 * @throws IOException
	 */
	private IndexSearcher getIndexSearch() throws IOException {
		//�����������ŵ�λ��
		String dumpPath="D:\\temp\\1101";
		Directory directory=FSDirectory.open(new File(dumpPath));
		//����indexReader
		IndexReader indexReader=DirectoryReader.open(directory);
		//ʹ��indexSeracher��ѯ
		IndexSearcher indexSearcher=new IndexSearcher(indexReader);
		return indexSearcher;
	}
	
	/**
	 * ��ӡ��ѯ�ṹ
	 * @throws IOException 
	 */
	private void printResult(IndexSearcher indexSearcher,Query query) throws IOException {
		TopDocs topDocs=indexSearcher.search(query, 10);
		System.out.println(topDocs.totalHits);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document document=indexSearcher.doc(scoreDoc.doc);
			//��document�л�ȡ����
			System.out.println(document.get("id"));
			System.out.println(document.get("fieldName"));
			System.out.println(document.get("fieldContent"));
			System.out.println(document.get("path"));
			System.out.println(document.get("size"));
		}
	}
	
	/**
	 * ��ֵ��Χ��ѯ:NumericRangeQuery
	 * @throws IOException 
	 */
	@Test
	public void testNumericRangeQuery() throws IOException {
		IndexSearcher indexSearcher=getIndexSearch();
		Query query=NumericRangeQuery.newLongRange("size", 01l, 1001l, true, false);
		printResult(indexSearcher, query);
	}
	
	/**
	 * ������ѯ:BooleanQuery
	 * @throws IOException 
	 */
	@Test
	public void testBooleanQuery() throws IOException {
		IndexSearcher indexSearcher=getIndexSearch();
		BooleanQuery query=new BooleanQuery();
		Query query2=new TermQuery(new Term("id", "1"));
		Query query3=new TermQuery(new Term("fieldName", "����һ����"));
		query.add(query2,Occur.MUST);
		query.add(query3, Occur.MUST_NOT);
		printResult(indexSearcher, query);
	}
	
	/**
	 * ��ѯ�����ĵ���MatchAllDocsQuery
	 * @throws IOException 
	 */
	@Test
	public void testMatchAllDocsQuery() throws IOException {
		IndexSearcher indexSearcher=getIndexSearch();
		Query query=new MatchAllDocsQuery();
		printResult(indexSearcher, query);
		
	}
	
	/**
	 * QueryParser
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@Test
	public void testQuieryParser() throws IOException, ParseException {
		IndexSearcher indexSearcher=getIndexSearch();
		//��������ʱʹ��IKAnalyzer���������ѯ����ʱƥ��
		IKAnalyzer analyzer=new IKAnalyzer();
		/*
		 * ����queryParser����
		 * ��һ��������Ĭ����
		 * �ڶ��������Ƿ�����
		 */
		QueryParser queryParser=new QueryParser("content", analyzer);
		//Query query=queryParser.parse("Do it right");
		//��ѯָ����
		//Query query=queryParser.parse("fieldName:ԧ�� ");
		//��ϲ�ѯ
		//Query query=queryParser.parse("fieldName:ԧ�� AND fieldName:�ư�");
		//Query query=queryParser.parse("fieldName:ԧ�� NOT fieldName:�ư�");
		Query query=queryParser.parse("fieldName:ԧ�� OR fieldName:�ư�");
		//Query query=queryParser.parse("+fieldName:ԧ�� + fieldName:�ư�");
		//Query query=queryParser.parse("+fieldName:ԧ�� - fieldName:�ư�");
		//Query query=queryParser.parse("fieldName:ԧ��  fieldName:�ư�");
		//��Χ��ѯ
		//Query query=queryParser.parse("id:[2 TO 6]");
		//Query query=queryParser.parse("size:[1 TO 100]");
		printResult(indexSearcher, query);
	}
	
	/**
	 * ������ѯ
	 * MultiFieldQueryParser
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@Test
	public void testMultiFieldQueryParser() throws IOException, ParseException {
		IndexSearcher indexSearcher=getIndexSearch();
		String[] fields={"fieldName","fieldContent"};
		IKAnalyzer analyzer=new IKAnalyzer();
		MultiFieldQueryParser queryParser=new MultiFieldQueryParser(fields, analyzer);
		Query query=queryParser.parse("fieldName:�ܽ���");
		printResult(indexSearcher, query);
	}
	
	/**
	 * ��ضȲ�ѯ�������ʱ���boost
	 * @throws IOException 
	 */
	@Test
	public void testBoost() throws IOException{
		IndexWriter indexWriter=getIndexWrite();
		Document document=new Document();
		//Document document=new Document();
		Field fieldName=new TextField("filedName", "����������ԧ���document����",Store.YES);
		fieldName.setBoost(100.0f);
		Field fieldContent1=new TextField("fieldContent", "��������ӵ�docment�����ݣ����ݾ��ǲ�����֪��",Store.YES);
		Field fieldContent2=new TextField("fieldContent", "��ϲ���㣬��ϲ������,���ǲ���",Store.YES);
		document.add(fieldName);
		document.add(fieldContent1);
		document.add(fieldContent2);
		indexWriter.addDocument(document);
		indexWriter.close();
	}
	
	/**
	 * ��ѯʱ�����ض� MultiFieldQueryParser
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@Test
	public void  testBoostQuery() throws IOException, ParseException {
		IndexSearcher indexSearcher=getIndexSearch();
		//���б�
		String[] fields = {"filename","content"};
		Analyzer analyzer = new IKAnalyzer();
		Map<String, Float> boostMap = new HashMap<String, Float>();
		boostMap.put("filename", 1.0f);
		boostMap.put("content", 100.0f);
		MultiFieldQueryParser queryParse = new MultiFieldQueryParser(fields, analyzer, boostMap);
		Query query = queryParse.parse("ԧ��");
		//��ӡ���
		printResult(indexSearcher, query);
	}
}
