package work.hang;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * [概 要]
 * [环 境] JAVA 1.8
 *
 * @author 六哥
 * @date 2018/7/23
 */
public class SearchTest {

	private IndexReader reader;
	private IndexSearcher is;

	@Before
	public void setUp() throws Exception {
		Directory dir = FSDirectory.open(Paths.get("/Users/maxiaohu/Desktop/lucene/lucene04"));
		reader = DirectoryReader.open(dir);
		is = new IndexSearcher(reader);
	}

	@After
	public void tearDown() throws Exception {
		reader.close();
	}

	/**
	 * 对特定项搜索
	 *
	 * @throws Exception Exception
	 */
	@Test
	public void testTermQuery() throws Exception {
		String searchField = "contents";
		String q = "particular";
		Term term = new Term(searchField, q);
		Query query = new TermQuery(term);
		TopDocs hits = is.search(query, 10);
		System.out.println("匹配 '" + q + "'，总共查询到" + hits.totalHits + "个文档");
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}

	/**
	 * 解析查询表达式
	 *
	 * @throws Exception Exception
	 */
	@Test
	public void testQueryParser() throws Exception {
		// 标准分词器
		Analyzer analyzer = new StandardAnalyzer();
		String searchField = "contents";
		String q = "abc~";
		QueryParser parser = new QueryParser(searchField, analyzer);
		Query query = parser.parse(q);
		TopDocs hits = is.search(query, 100);
		System.out.println("匹配 " + q + "查询到" + hits.totalHits + "个记录");
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}
}
