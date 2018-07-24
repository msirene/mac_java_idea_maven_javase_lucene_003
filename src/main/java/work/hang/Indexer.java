package work.hang;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

/**
 * [概 要]
 * [环 境] JAVA 1.8
 *
 * @author 六哥
 * @date 2018/7/17
 */
public class Indexer {

	/**
	 * 写索引实例
	 */
	private IndexWriter writer;

	/**
	 * 构造方法 实例化IndexWriter
	 *
	 * @param indexDir 数据目录
	 * @throws Exception Exception
	 */
	private Indexer(String indexDir) throws Exception {
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		// 标准分词器
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(dir, iwc);
	}

	/**
	 * 关闭写索引
	 *
	 * @throws Exception Exception
	 */
	private void close() throws Exception {
		writer.close();
	}

	/**
	 * 索引指定目录的所有文件
	 *
	 * @param dataDir 数据目录
	 * @throws Exception Exception
	 */
	private int index(String dataDir) throws Exception {
		File[] files = new File(dataDir).listFiles();
		if (files != null) {
			for (File f : files) {
				indexFile(f);
			}
		}
		return writer.numDocs();
	}

	/**
	 * 索引指定文件
	 *
	 * @param f File对象
	 */
	private void indexFile(File f) throws Exception {
		System.out.println("索引文件：" + f.getCanonicalPath());
		Document doc = getDocument(f);
		writer.addDocument(doc);
	}

	/**
	 * 获取文档，文档里再设置每个字段
	 *
	 * @param f File对象
	 */
	private Document getDocument(File f) throws Exception {
		Document doc = new Document();
		doc.add(new TextField("contents", new FileReader(f)));
		doc.add(new TextField("fileName", f.getName(), Field.Store.YES));
		doc.add(new TextField("fullPath", f.getCanonicalPath(), Field.Store.YES));
		return doc;
	}

	public static void main(String[] args) {
		String indexDir = "/Users/maxiaohu/Desktop/lucene/lucene04";
		String dataDir = "/Users/maxiaohu/Desktop/lucene/lucene04/data";
		Indexer indexer = null;
		int numIndexed = 0;
		long start = System.currentTimeMillis();
		try {
			indexer = new Indexer(indexDir);
			numIndexed = indexer.index(dataDir);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (indexer != null) {
					indexer.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("索引：" + numIndexed + " 个文件 花费了" + (end - start) + " 毫秒");
	}
}
