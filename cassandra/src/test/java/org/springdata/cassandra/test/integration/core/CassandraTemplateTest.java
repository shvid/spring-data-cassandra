/*
 * Copyright 2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springdata.cassandra.test.integration.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.DataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.dataset.yaml.ClassPathYamlDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdata.cassandra.core.CassandraOperations;
import org.springdata.cassandra.core.EntryCallbackHandler;
import org.springdata.cassandra.test.integration.CassandraTestConstants;
import org.springdata.cassandra.test.integration.config.JavaConfig;
import org.springdata.cassandra.test.integration.table.Book;
import org.springdata.cql.core.RetryPolicyInstance;
import org.springdata.cql.core.StatementCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.collect.Lists;

/**
 * Unit Tests for CassandraTemplate
 * 
 * @author David Webb
 * @author Alex Shvid
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JavaConfig.class }, loader = AnnotationConfigContextLoader.class)
public class CassandraTemplateTest {

	@Autowired
	private CassandraOperations cassandraTemplate;

	private static Logger log = LoggerFactory.getLogger(CassandraTemplateTest.class);

	private final static String CASSANDRA_CONFIG = "cassandra.yaml";
	private final static String KEYSPACE_NAME = CassandraTestConstants.DEFAULT_KEYSPACE;
	private final static String CASSANDRA_HOST = CassandraTestConstants.DEFAULT_HOST;
	private final static int CASSANDRA_NATIVE_PORT = CassandraTestConstants.DEFAULT_PORT;
	private final static int CASSANDRA_THRIFT_PORT = CassandraTestConstants.DEFAULT_THRIFT_PORT;

	@Rule
	public CassandraCQLUnit cassandraCQLUnit = new CassandraCQLUnit(new ClassPathCQLDataSet("cql-dataload.cql",
			KEYSPACE_NAME), CASSANDRA_CONFIG, CASSANDRA_HOST, CASSANDRA_NATIVE_PORT);

	@BeforeClass
	public static void startCassandra() throws IOException, TTransportException, ConfigurationException,
			InterruptedException {

		EmbeddedCassandraServerHelper.startEmbeddedCassandra(CASSANDRA_CONFIG);

		/*
		 * Load data file to creat the test keyspace before we init the template
		 */
		DataLoader dataLoader = new DataLoader("Test Cluster", CASSANDRA_HOST + ":" + CASSANDRA_THRIFT_PORT);
		dataLoader.load(new ClassPathYamlDataSet("cassandra-keyspace.yaml"));
	}

	@Test
	public void insertTest() {

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Guide");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b1).execute();

		Book b2 = new Book();
		b2.setIsbn("123456-2");
		b2.setTitle("Spring Data Cassandra Guide");
		b2.setAuthor("Cassandra Guru");
		b2.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b2).toTable("book_alt").execute();

		/*
		 * Test Single Insert with entity
		 */
		Book b3 = new Book();
		b3.setIsbn("123456-3");
		b3.setTitle("Spring Data Cassandra Guide");
		b3.setAuthor("Cassandra Guru");
		b3.setPages(265);

		cassandraTemplate.buildSaveNewOperation(b3).toTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		/*
		 * Test Single Insert with entity
		 */
		Book b5 = new Book();
		b5.setIsbn("123456-5");
		b5.setTitle("Spring Data Cassandra Guide");
		b5.setAuthor("Cassandra Guru");
		b5.setPages(265);

		cassandraTemplate.buildSaveNewOperation(b5).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

	}

	@Test
	public void insertAsynchronouslyTest() {

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Guide");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b1).executeAsync();

		Book b2 = new Book();
		b2.setIsbn("123456-2");
		b2.setTitle("Spring Data Cassandra Guide");
		b2.setAuthor("Cassandra Guru");
		b2.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b2).toTable("book_alt").executeAsync();

		/*
		 * Test Single Insert with entity
		 */
		Book b3 = new Book();
		b3.setIsbn("123456-3");
		b3.setTitle("Spring Data Cassandra Guide");
		b3.setAuthor("Cassandra Guru");
		b3.setPages(265);

		cassandraTemplate.buildSaveNewOperation(b3).toTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();

		/*
		 * Test Single Insert with entity
		 */
		Book b5 = new Book();
		b5.setIsbn("123456-5");
		b5.setTitle("Spring Data Cassandra Guide");
		b5.setAuthor("Cassandra Guru");
		b5.setPages(265);

		cassandraTemplate.buildSaveNewOperation(b5).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();

	}

	@Test
	public void insertBatchTest() {

		List<Book> books = null;

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book_alt").execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

	}

	@Test
	public void insertBatchAsynchronouslyTest() {

		List<Book> books = null;

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).executeAsync();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book_alt").executeAsync();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();

	}

	/**
	 * @return
	 */
	private List<Book> getBookList(int numBooks) {

		List<Book> books = new ArrayList<Book>();

		Book b = null;
		for (int i = 0; i < numBooks; i++) {
			b = new Book();
			b.setIsbn(UUID.randomUUID().toString());
			b.setTitle("Spring Data Cassandra Guide");
			b.setAuthor("Cassandra Guru");
			b.setPages(i * 10 + 5);
			books.add(b);
		}

		return books;
	}

	@Test
	public void updateTest() {

		insertTest();

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Book");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveOperation(b1).execute();

		Book b2 = new Book();
		b2.setIsbn("123456-2");
		b2.setTitle("Spring Data Cassandra Book");
		b2.setAuthor("Cassandra Guru");
		b2.setPages(521);

		cassandraTemplate.buildSaveOperation(b2).toTable("book_alt").execute();

		/*
		 * Test Single Insert with entity
		 */
		Book b3 = new Book();
		b3.setIsbn("123456-3");
		b3.setTitle("Spring Data Cassandra Book");
		b3.setAuthor("Cassandra Guru");
		b3.setPages(265);

		cassandraTemplate.buildSaveOperation(b3).toTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		/*
		 * Test Single Insert with entity
		 */
		Book b5 = new Book();
		b5.setIsbn("123456-5");
		b5.setTitle("Spring Data Cassandra Book");
		b5.setAuthor("Cassandra Guru");
		b5.setPages(265);

		cassandraTemplate.buildSaveOperation(b5).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

	}

	@Test
	public void updateAsynchronouslyTest() {

		insertTest();

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Book");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveOperation(b1).executeAsync();

		Book b2 = new Book();
		b2.setIsbn("123456-2");
		b2.setTitle("Spring Data Cassandra Book");
		b2.setAuthor("Cassandra Guru");
		b2.setPages(521);

		cassandraTemplate.buildSaveOperation(b2).toTable("book_alt").executeAsync();

		/*
		 * Test Single Insert with entity
		 */
		Book b3 = new Book();
		b3.setIsbn("123456-3");
		b3.setTitle("Spring Data Cassandra Book");
		b3.setAuthor("Cassandra Guru");
		b3.setPages(265);

		cassandraTemplate.buildSaveOperation(b3).toTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();

		/*
		 * Test Single Insert with entity
		 */
		Book b5 = new Book();
		b5.setIsbn("123456-5");
		b5.setTitle("Spring Data Cassandra Book");
		b5.setAuthor("Cassandra Guru");
		b5.setPages(265);

		cassandraTemplate.buildSaveOperation(b5).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();
		;

	}

	@Test
	public void updateBatchTest() {

		List<Book> books = null;

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		alterBooks(books);

		cassandraTemplate.buildSaveInBatchOperation(books).execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book_alt").execute();

		alterBooks(books);

		cassandraTemplate.buildSaveInBatchOperation(books).inTable("book_alt").execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		alterBooks(books);

		cassandraTemplate.buildSaveInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		alterBooks(books);

		cassandraTemplate.buildSaveInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

	}

	@Test
	public void updateBatchAsynchronouslyTest() {

		List<Book> books = null;

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		alterBooks(books);

		cassandraTemplate.buildSaveInBatchOperation(books).executeAsync();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book_alt").execute();

		alterBooks(books);

		cassandraTemplate.buildSaveInBatchOperation(books).inTable("book_alt").executeAsync();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		alterBooks(books);

		cassandraTemplate.buildSaveInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		alterBooks(books);

		cassandraTemplate.buildSaveInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();

	}

	/**
	 * @param books
	 */
	private void alterBooks(List<Book> books) {

		for (Book b : books) {
			b.setAuthor("Ernest Hemmingway");
			b.setTitle("The Old Man and the Sea");
			b.setPages(115);
		}
	}

	@Test
	public void deleteTest() {

		insertTest();

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");

		cassandraTemplate.buildDeleteOperation(b1).execute();

		Book b2 = new Book();
		b2.setIsbn("123456-2");

		cassandraTemplate.buildDeleteOperation(b2).fromTable("book_alt").execute();

		/*
		 * Test Single Insert with entity
		 */
		Book b3 = new Book();
		b3.setIsbn("123456-3");

		cassandraTemplate.buildDeleteOperation(b3).fromTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		/*
		 * Test Single Insert with entity
		 */
		Book b5 = new Book();
		b5.setIsbn("123456-5");

		cassandraTemplate.buildDeleteOperation(b5).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

	}

	@Test
	public void deleteAsynchronouslyTest() {

		insertTest();

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");

		cassandraTemplate.buildDeleteOperation(b1).executeAsync();

		Book b2 = new Book();
		b2.setIsbn("123456-2");

		cassandraTemplate.buildDeleteOperation(b2).fromTable("book_alt").executeAsync();

		/*
		 * Test Single Insert with entity
		 */
		Book b3 = new Book();
		b3.setIsbn("123456-3");

		cassandraTemplate.buildDeleteOperation(b3).fromTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();

		/*
		 * Test Single Insert with entity
		 */
		Book b5 = new Book();
		b5.setIsbn("123456-5");

		cassandraTemplate.buildDeleteOperation(b5).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();
	}

	@Test
	public void deleteBatchTest() {

		List<Book> books = null;

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		cassandraTemplate.buildDeleteInBatchOperation(books).execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book_alt").execute();

		cassandraTemplate.buildDeleteInBatchOperation(books).inTable("book_alt").execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		cassandraTemplate.buildDeleteInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		cassandraTemplate.buildDeleteInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

	}

	@Test
	public void deleteBatchAsynchronouslyTest() {

		List<Book> books = null;

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		cassandraTemplate.buildDeleteInBatchOperation(books).executeAsync();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book_alt").execute();

		cassandraTemplate.buildDeleteInBatchOperation(books).inTable("book_alt").executeAsync();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		cassandraTemplate.buildDeleteInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		cassandraTemplate.buildDeleteInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();

	}

	@Test
	public void deleteByIdTest() {

		insertTest();

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");

		cassandraTemplate.buildDeleteByIdOperation(Book.class, b1.getIsbn()).execute();

		Book b2 = new Book();
		b2.setIsbn("123456-2");

		cassandraTemplate.buildDeleteByIdOperation(Book.class, b2.getIsbn()).fromTable("book_alt").execute();

		/*
		 * Test Single Insert with entity
		 */
		Book b3 = new Book();
		b3.setIsbn("123456-3");

		cassandraTemplate.buildDeleteByIdOperation(Book.class, b3.getIsbn()).fromTable("book")
				.withConsistencyLevel(ConsistencyLevel.ONE).withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY)
				.execute();

		/*
		 * Test Single Insert with entity
		 */
		Book b5 = new Book();
		b5.setIsbn("123456-5");

		cassandraTemplate.buildDeleteByIdOperation(Book.class, b5.getIsbn()).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

	}

	@Test
	public void deleteByIdAsynchronouslyTest() {

		insertTest();

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");

		cassandraTemplate.buildDeleteByIdOperation(Book.class, b1.getIsbn()).executeAsync();

		Book b2 = new Book();
		b2.setIsbn("123456-2");

		cassandraTemplate.buildDeleteByIdOperation(Book.class, b2.getIsbn()).fromTable("book_alt").executeAsync();

		/*
		 * Test Single Insert with entity
		 */
		Book b3 = new Book();
		b3.setIsbn("123456-3");

		cassandraTemplate.buildDeleteByIdOperation(Book.class, b3.getIsbn()).fromTable("book")
				.withConsistencyLevel(ConsistencyLevel.ONE).withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY)
				.executeAsync();

		/*
		 * Test Single Insert with entity
		 */
		Book b5 = new Book();
		b5.setIsbn("123456-5");

		cassandraTemplate.buildDeleteByIdOperation(Book.class, b5.getIsbn()).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).executeAsync();

	}

	@Test
	public void deleteByIdBatchTest() {

		List<Book> books = null;

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		cassandraTemplate.buildDeleteByIdInBatchOperation(Book.class, ids(books)).execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book_alt").execute();

		cassandraTemplate.buildDeleteByIdInBatchOperation(Book.class, ids(books)).inTable("book_alt").execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		cassandraTemplate.buildDeleteByIdInBatchOperation(Book.class, ids(books)).inTable("book")
				.withConsistencyLevel(ConsistencyLevel.ONE).withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY)
				.execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		cassandraTemplate.buildDeleteByIdInBatchOperation(Book.class, ids(books))
				.withConsistencyLevel(ConsistencyLevel.ONE).withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY)
				.execute();

	}

	private List<String> ids(List<Book> books) {
		List<String> ids = new ArrayList<String>(books.size());
		for (Book book : books) {
			ids.add(book.getIsbn());
		}
		return ids;
	}

	@Test
	public void deleteByIdBatchAsynchronouslyTest() {

		List<Book> books = null;

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		cassandraTemplate.buildDeleteByIdInBatchOperation(Book.class, ids(books)).execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book_alt").execute();

		cassandraTemplate.buildDeleteByIdInBatchOperation(Book.class, ids(books)).inTable("book_alt").execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).inTable("book").withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		cassandraTemplate.buildDeleteByIdInBatchOperation(Book.class, ids(books)).inTable("book")
				.withConsistencyLevel(ConsistencyLevel.ONE).withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY)
				.execute();

		books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).withConsistencyLevel(ConsistencyLevel.ONE)
				.withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY).execute();

		cassandraTemplate.buildDeleteByIdInBatchOperation(Book.class, ids(books))
				.withConsistencyLevel(ConsistencyLevel.ONE).withRetryPolicy(RetryPolicyInstance.DOWNGRADING_CONSISTENCY)
				.execute();

	}

	@Test
	public void deleteAllTest() {

		List<Book> books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		cassandraTemplate.buildDeleteAllOperation(Book.class).execute();

		Long count = cassandraTemplate.buildCountAllOperation(Book.class).execute();

		assertEquals(new Long(0), count);
	}

	@Test
	public void findOneTest() {

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Guide");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b1).execute();

		String cql = "SELECT * FROM book WHERE isbn='123456-1'";
		Book b = cassandraTemplate.buildFindOneOperation(Book.class, cql).execute();

		log.info("SingleSelect Book Title -> " + b.getTitle());
		log.info("SingleSelect Book Author -> " + b.getAuthor());

		assertEquals(b.getTitle(), "Spring Data Cassandra Guide");
		assertEquals(b.getAuthor(), "Cassandra Guru");

	}

	@Test
	public void findByIdTest() {

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Guide");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b1).execute();

		Book b = cassandraTemplate.buildFindByIdOperation(Book.class, "123456-1").execute();

		log.info("SingleSelect Book Title -> " + b.getTitle());
		log.info("SingleSelect Book Author -> " + b.getAuthor());

		assertEquals(b.getTitle(), "Spring Data Cassandra Guide");
		assertEquals(b.getAuthor(), "Cassandra Guru");
	}

	@Test
	public void findByPartitionKeyTest() {

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Guide");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b1).execute();

		List<Book> found = cassandraTemplate.buildFindByPartitionKeyOperation(Book.class, "123456-1").execute();

		assertEquals(1, found.size());
		Book b = found.get(0);

		log.info("SingleSelect Book Title -> " + b.getTitle());
		log.info("SingleSelect Book Author -> " + b.getAuthor());

		assertEquals(b.getTitle(), "Spring Data Cassandra Guide");
		assertEquals(b.getAuthor(), "Cassandra Guru");
	}

	@Test
	public void findAllTest() {

		List<Book> books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		List<Book> found = cassandraTemplate.buildFindAllOperation(Book.class).execute();

		assertEquals(20, found.size());

	}

	@Test
	public void findAllByIdsTest() {

		List<Book> books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		List<Book> found = cassandraTemplate.buildFindAllOperation(Book.class, ids(books)).execute();

		assertEquals(20, found.size());

	}

	@Test
	public void findTest() {

		List<Book> books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		Select select = QueryBuilder.select().all().from("book");

		List<Book> b = Lists.newArrayList(cassandraTemplate.buildFindOperation(Book.class, select.getQueryString())
				.execute());

		log.info("Book Count -> " + b.size());

		assertEquals(b.size(), 20);

	}

	@Test
	public void countTest() {

		List<Book> books = getBookList(20);

		cassandraTemplate.buildSaveNewInBatchOperation(books).execute();

		Long count = cassandraTemplate.buildCountAllOperation(Book.class).execute();

		log.info("Book Count -> " + count);

		assertEquals(count, new Long(20));

	}

	@Test
	public void existsTest() {

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Guide");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b1).execute();

		Boolean exists = cassandraTemplate.buildExistsOperation(b1).execute();

		log.info("Book Exists -> " + exists);

		assertEquals(Boolean.TRUE, exists);

		Boolean notExists = cassandraTemplate.buildExistsOperation(Book.class, "xxx").execute();

		log.info("Book Not Exists -> " + notExists);

		assertEquals(Boolean.FALSE, notExists);

	}

	@Test
	public void rowMapperTest() {

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Guide");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b1).execute();

		List<Book> books = cassandraTemplate.getCqlOperations().buildQueryOperation(new StatementCreator() {

			@Override
			public Statement createStatement() {
				return QueryBuilder.select().all().from("book").where(QueryBuilder.eq("isbn", "123456-1"));
			}

		}).map(cassandraTemplate.getRowMapperFor(Book.class)).execute();

		assertEquals(1, books.size());

		assertEquals(b1.getIsbn(), books.get(0).getIsbn());
		assertEquals(b1.getTitle(), books.get(0).getTitle());
		assertEquals(b1.getAuthor(), books.get(0).getAuthor());

	}

	@Test
	public void resultSetExtractorTest() {

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Guide");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b1).execute();

		List<Book> books = cassandraTemplate.getCqlOperations().buildQueryOperation(new StatementCreator() {

			@Override
			public Statement createStatement() {
				return QueryBuilder.select().all().from("book").where(QueryBuilder.eq("isbn", "123456-1"));
			}

		}).transform(cassandraTemplate.getResultSetExtractorFor(Book.class)).execute();

		assertEquals(1, books.size());

		assertEquals(b1.getIsbn(), books.get(0).getIsbn());
		assertEquals(b1.getTitle(), books.get(0).getTitle());
		assertEquals(b1.getAuthor(), books.get(0).getAuthor());

	}

	@Test
	public void processTest() {

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Guide");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b1).execute();

		ResultSet resultSet = cassandraTemplate.getCqlOperations().buildQueryOperation(new StatementCreator() {

			@Override
			public Statement createStatement() {
				return QueryBuilder.select().all().from("book").where(QueryBuilder.eq("isbn", "123456-1"));
			}

		}).execute();

		List<Book> books = cassandraTemplate.process(resultSet, Book.class);

		assertEquals(1, books.size());

		assertEquals(b1.getIsbn(), books.get(0).getIsbn());
		assertEquals(b1.getTitle(), books.get(0).getTitle());
		assertEquals(b1.getAuthor(), books.get(0).getAuthor());

	}

	@Test
	public void processHandlerTest() {

		/*
		 * Test Single Insert with entity
		 */
		final Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Guide");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b1).execute();

		ResultSet resultSet = cassandraTemplate.getCqlOperations().buildQueryOperation(new StatementCreator() {

			@Override
			public Statement createStatement() {
				return QueryBuilder.select().all().from("book").where(QueryBuilder.eq("isbn", "123456-1"));
			}

		}).execute();

		cassandraTemplate.process(resultSet, Book.class, new EntryCallbackHandler<Book>() {

			boolean singleValueExpected = false;

			@Override
			public void processEntry(Book entry) {
				if (singleValueExpected) {
					throw new IllegalStateException("single value expected");
				}
				singleValueExpected = true;

				assertEquals(b1.getIsbn(), entry.getIsbn());
				assertEquals(b1.getTitle(), entry.getTitle());
				assertEquals(b1.getAuthor(), entry.getAuthor());
			}

		});

	}

	@Test
	public void processOneTest() {

		/*
		 * Test Single Insert with entity
		 */
		Book b1 = new Book();
		b1.setIsbn("123456-1");
		b1.setTitle("Spring Data Cassandra Guide");
		b1.setAuthor("Cassandra Guru");
		b1.setPages(521);

		cassandraTemplate.buildSaveNewOperation(b1).execute();

		ResultSet resultSet = cassandraTemplate.getCqlOperations().buildQueryOperation(new StatementCreator() {

			@Override
			public Statement createStatement() {
				return QueryBuilder.select().all().from("book").where(QueryBuilder.eq("isbn", "123456-1"));
			}

		}).execute();

		Book entry = cassandraTemplate.processOne(resultSet, Book.class, true);

		assertEquals(b1.getIsbn(), entry.getIsbn());
		assertEquals(b1.getTitle(), entry.getTitle());
		assertEquals(b1.getAuthor(), entry.getAuthor());

	}

	@After
	public void clearCassandra() {
		EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
	}

	@SuppressWarnings("deprecation")
	@AfterClass
	public static void stopCassandra() {
		EmbeddedCassandraServerHelper.stopEmbeddedCassandra();
	}
}
