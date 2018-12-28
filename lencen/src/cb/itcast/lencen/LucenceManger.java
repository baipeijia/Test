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
	 * 创建索引库
	 * @throws IOException
	 */
	@Test
	public void createDump() throws IOException{
		/*
		//创建索引库存放的位置
		String dumpPath="D:\\temp\\1101";
		Directory directory=FSDirectory.open(new File(dumpPath));
		//指定分析器
		StandardAnalyzer sAnalyzer=new StandardAnalyzer();
		IndexWriterConfig indexWriterConfig=new IndexWriterConfig(Version.LATEST, sAnalyzer);
		//创建indexWrite对象
		 * 
		 */
		IndexWriter indexWriter=getIndexWrite();	
		//读取歌曲并创建ducument对象
		int i=0;
		File musiceFile=new File("F:\\教材\\day67-Lucene\\day71-Lucene\\00.参考资料\\歌词");
		for(File f:musiceFile.listFiles()){
			if(f.isFile()){
				//创建ducument对象
				Document document=new Document();
				//创建Filed对象
				Field fieldId=new StringField("id", ""+i++, Store.YES);
				//文件名
				Field fieldName=new TextField("fieldName", f.getName(), Store.YES);
				//文件内容
				String contentString=FileUtils.readFileToString(f);
				Field fieldContent=new TextField("fieldContent", contentString, Store.YES);
				//文件路径
				Field fieldPath=new StoredField("path", f.getPath());
				//文件大小
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
	 * 查询
	 * @throws IOException
	 */
	@Test
	public void queryIndex() throws IOException{
		//创建索引库存放的位置
		String dumpPath="D:\\temp\\1101";
		Directory directory=FSDirectory.open(new File(dumpPath));
		//创建indexReader
		IndexReader indexReader=DirectoryReader.open(directory);
		//使用indexSeracher查询
		IndexSearcher indexSearcher=new IndexSearcher(indexReader);
		//创建一个query
		Query query=new TermQuery(new Term("id", "0"));
		//执行查询
		TopDocs topDocs=indexSearcher.search(query, 100);
		//取查询数量
		System.out.println(topDocs.totalHits);
		//
		for(ScoreDoc scoreDoc:topDocs.scoreDocs){
			Document document=indexSearcher.doc(scoreDoc.doc);
			//从document中获取内容
			System.out.println(document.get("id"));
			System.out.println(document.get("fieldName"));
			System.out.println(document.get("fieldContent"));
			System.out.println(document.get("path"));
			System.out.println(document.get("size"));
		}
				
	}
	
	/**
	 * 标准分析器:StandardAnalyzer
	 * @throws IOException 
	 */
	@Test
	public void StandardAnalyzer() throws IOException{
		//创建标准分析器
		StandardAnalyzer standardAnalyzer=new StandardAnalyzer();
		//获取tokenStream
		TokenStream tokenStream=standardAnalyzer.tokenStream("text", "I LOVE YOU,DO YOU LOVE ME");
		//查看关键词属性
		CharTermAttribute charTermAttribute=tokenStream.addAttribute(CharTermAttribute.class);
		//偏移量属性
		OffsetAttribute offsetAttribute=tokenStream.addAttribute(OffsetAttribute.class);
		//重置tokenStream
		tokenStream.reset();
		while(tokenStream.incrementToken()){
			System.out.println("start-->"+offsetAttribute.startOffset());
			System.out.println(charTermAttribute);
			System.out.println("end-->"+offsetAttribute.endOffset());
		}
		tokenStream.close();
	} 
	/**
	 * 中文分析器:CJKAnalyzer
	 * @throws IOException 
	 */
	@Test
	public void testCJKAnalyzer() throws IOException{
		CJKAnalyzer cjkAnalyzer=new CJKAnalyzer();
		TokenStream tokenStream=cjkAnalyzer.tokenStream("text1", "我喜欢你，你喜欢我吗");
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
	 * 中文分析器:SmartChineseAnalyzer
	 * @throws IOException 
	 */
	@Test
	public void testSmartChineseAnalyzer() throws IOException {
		SmartChineseAnalyzer chineseAnalyzer=new SmartChineseAnalyzer();
		TokenStream tokenStream=chineseAnalyzer.tokenStream("text2", "我喜欢你，你喜欢我吗");
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
	 * 中文分析器:IKAnalyzer
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
	 * 增加索引
	 * @throws IOException 
	 */
	@Test
	public void  addIndex() throws IOException {
		/*
		//创建索引库
		String dumpPath="D:\\temp\\1101";
		//创建directory
		Directory directory =FSDirectory.open(new File(dumpPath));
		//创建中文分词器
		IKAnalyzer analyzer=new IKAnalyzer();
		//创建一个indexWriteConfig
		IndexWriterConfig config=new IndexWriterConfig(Version.LATEST, analyzer);
		*
		*/
		//创建IndexWrite
		IndexWriter indexWriter=getIndexWrite();
		File musicDir=new File("F:\\教材\\day67-Lucene\\day71-Lucene\\00.参考资料\\歌词");
		for (File f:musicDir.listFiles()) {
			//创建ducument
			Document document=new Document();
			//创建field
			Field fieldName=new TextField("fieldName", f.getName(), Store.YES);
			String stringContent=FileUtils.readFileToString(f);
			Field fieldContent=new TextField("fieldContent", stringContent, Store.YES);
			Field fieldPath=new StoredField("path", f.getPath());
			Field fieldSize=new LongField("size", FileUtils.sizeOf(f), Store.YES);
			//添加域到document中
			document.add(fieldName);
			document.add(fieldContent);
			document.add(fieldPath);
			document.add(fieldSize);
			//添加document
			indexWriter.addDocument(document);
		}
		indexWriter.close();
	}
	/**
	 * 获取indexWriter对象
	 * @return
	 * @throws IOException
	 */
	private IndexWriter getIndexWrite() throws IOException{
		String dumpPath="D:\\temp\\1101";
		//创建directory
		Directory directory =FSDirectory.open(new File(dumpPath));
		//创建中文分词器
		IKAnalyzer analyzer=new IKAnalyzer();
		//创建一个indexWriteConfig
        IndexWriterConfig config=new IndexWriterConfig(Version.LATEST, analyzer);
		//创建IndexWrite
		IndexWriter indexWriter=new IndexWriter(directory, config);
		return indexWriter;
	}
	
	
	/**
	 * 添加一个document
	 * @throws IOException 
	 */
	@Test
	public void  addOneDocument() throws IOException {
		IndexWriter indexWriter=getIndexWrite();
		Document document=new Document();
		Field fieldName=new TextField("filedName", "这是新增加的document标题",Store.YES);
		Field fieldContent1=new TextField("fieldContent", "这是新添加的docment的内容，内容就是不让你知道",Store.YES);
		Field fieldContent2=new TextField("fieldContent", "我喜欢你，你喜欢我吗,传智播客",Store.YES);
		document.add(fieldName);
		document.add(fieldContent1);
		document.add(fieldContent2);
		indexWriter.addDocument(document);
		indexWriter.close();
	}
	
	/**
	 * 删除指定文档
	 * @throws IOException 
	 */
	@Test
	public void testDeleteDocument() throws IOException{
		IndexWriter indexWriter=getIndexWrite();
		indexWriter.deleteDocuments(new TermQuery(new Term("filedName", "张信哲")));
		indexWriter.commit();
		indexWriter.close();
	}
	
	/**
	 * 删除所有的document
	 * @throws IOException 
	 */
	@Test
	public void testDeleteAll() throws IOException{
		IndexWriter indexWriter=getIndexWrite();
		indexWriter.deleteAll();
		indexWriter.close();

	}
	
	/**
	 * 获取IndexSearcher对象
	 * @return
	 * @throws IOException
	 */
	private IndexSearcher getIndexSearch() throws IOException {
		//创建索引库存放的位置
		String dumpPath="D:\\temp\\1101";
		Directory directory=FSDirectory.open(new File(dumpPath));
		//创建indexReader
		IndexReader indexReader=DirectoryReader.open(directory);
		//使用indexSeracher查询
		IndexSearcher indexSearcher=new IndexSearcher(indexReader);
		return indexSearcher;
	}
	
	/**
	 * 打印查询结构
	 * @throws IOException 
	 */
	private void printResult(IndexSearcher indexSearcher,Query query) throws IOException {
		TopDocs topDocs=indexSearcher.search(query, 10);
		System.out.println(topDocs.totalHits);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document document=indexSearcher.doc(scoreDoc.doc);
			//从document中获取内容
			System.out.println(document.get("id"));
			System.out.println(document.get("fieldName"));
			System.out.println(document.get("fieldContent"));
			System.out.println(document.get("path"));
			System.out.println(document.get("size"));
		}
	}
	
	/**
	 * 数值范围查询:NumericRangeQuery
	 * @throws IOException 
	 */
	@Test
	public void testNumericRangeQuery() throws IOException {
		IndexSearcher indexSearcher=getIndexSearch();
		Query query=NumericRangeQuery.newLongRange("size", 01l, 1001l, true, false);
		printResult(indexSearcher, query);
	}
	
	/**
	 * 条件查询:BooleanQuery
	 * @throws IOException 
	 */
	@Test
	public void testBooleanQuery() throws IOException {
		IndexSearcher indexSearcher=getIndexSearch();
		BooleanQuery query=new BooleanQuery();
		Query query2=new TermQuery(new Term("id", "1"));
		Query query3=new TermQuery(new Term("fieldName", "雨下一整晚"));
		query.add(query2,Occur.MUST);
		query.add(query3, Occur.MUST_NOT);
		printResult(indexSearcher, query);
	}
	
	/**
	 * 查询所有文档：MatchAllDocsQuery
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
		//创建索引时使用IKAnalyzer分析器与查询索引时匹配
		IKAnalyzer analyzer=new IKAnalyzer();
		/*
		 * 创建queryParser对象
		 * 第一个参数是默认域
		 * 第二个参数是分析器
		 */
		QueryParser queryParser=new QueryParser("content", analyzer);
		//Query query=queryParser.parse("Do it right");
		//查询指定域
		//Query query=queryParser.parse("fieldName:鸳鸯 ");
		//组合查询
		//Query query=queryParser.parse("fieldName:鸳鸯 AND fieldName:黄安");
		//Query query=queryParser.parse("fieldName:鸳鸯 NOT fieldName:黄安");
		Query query=queryParser.parse("fieldName:鸳鸯 OR fieldName:黄安");
		//Query query=queryParser.parse("+fieldName:鸳鸯 + fieldName:黄安");
		//Query query=queryParser.parse("+fieldName:鸳鸯 - fieldName:黄安");
		//Query query=queryParser.parse("fieldName:鸳鸯  fieldName:黄安");
		//范围查询
		//Query query=queryParser.parse("id:[2 TO 6]");
		//Query query=queryParser.parse("size:[1 TO 100]");
		printResult(indexSearcher, query);
	}
	
	/**
	 * 混合域查询
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
		Query query=queryParser.parse("fieldName:周杰伦");
		printResult(indexSearcher, query);
	}
	
	/**
	 * 相关度查询添加索引时添加boost
	 * @throws IOException 
	 */
	@Test
	public void testBoost() throws IOException{
		IndexWriter indexWriter=getIndexWrite();
		Document document=new Document();
		//Document document=new Document();
		Field fieldName=new TextField("filedName", "这是新增加鸳鸯的document标题",Store.YES);
		fieldName.setBoost(100.0f);
		Field fieldContent1=new TextField("fieldContent", "这是新添加的docment的内容，内容就是不让你知道",Store.YES);
		Field fieldContent2=new TextField("fieldContent", "我喜欢你，你喜欢我吗,传智播客",Store.YES);
		document.add(fieldName);
		document.add(fieldContent1);
		document.add(fieldContent2);
		indexWriter.addDocument(document);
		indexWriter.close();
	}
	
	/**
	 * 查询时添加相关度 MultiFieldQueryParser
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@Test
	public void  testBoostQuery() throws IOException, ParseException {
		IndexSearcher indexSearcher=getIndexSearch();
		//域列表
		String[] fields = {"filename","content"};
		Analyzer analyzer = new IKAnalyzer();
		Map<String, Float> boostMap = new HashMap<String, Float>();
		boostMap.put("filename", 1.0f);
		boostMap.put("content", 100.0f);
		MultiFieldQueryParser queryParse = new MultiFieldQueryParser(fields, analyzer, boostMap);
		Query query = queryParse.parse("鸳鸯");
		//打印结果
		printResult(indexSearcher, query);
	}
}
