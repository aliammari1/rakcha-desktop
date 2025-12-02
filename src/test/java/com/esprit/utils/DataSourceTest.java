package com.esprit.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for DataSource utility class.
 * Tests singleton pattern, database connection, and error handling.
 * <p>
 * Test Categories:
 * - Singleton Pattern
 * - Database Connection
 * - Connection Management
 * - Error Handling
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataSourceTest {

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Singleton Pattern Tests")
    class SingletonPatternTests {

        @Test
        @Order(1)
        @DisplayName("Should return same instance on multiple calls")
        void testSingletonInstance() {
            DataSource instance1 = DataSource.getInstance();
            DataSource instance2 = DataSource.getInstance();

            assertThat(instance1).isNotNull();
            assertThat(instance2).isNotNull();
            assertThat(instance1).isSameAs(instance2);
        }


        @Test
        @Order(2)
        @DisplayName("Should maintain singleton across threads")
        void testSingletonThreadSafety() throws InterruptedException {
            DataSource[] instances = new DataSource[2];

            Thread thread1 = new Thread(() -> {
                instances[0] = DataSource.getInstance();
            });

            Thread thread2 = new Thread(() -> {
                instances[1] = DataSource.getInstance();
            });

            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();

            assertThat(instances[0]).isSameAs(instances[1]);
        }


        @Test
        @Order(3)
        @DisplayName("Should initialize only once")
        void testSingleInitialization() {
            DataSource first = DataSource.getInstance();
            DataSource second = DataSource.getInstance();
            DataSource third = DataSource.getInstance();

            assertThat(first).isSameAs(second);
            assertThat(second).isSameAs(third);
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Database Connection Tests")
    class DatabaseConnectionTests {

        @Test
        @Order(4)
        @DisplayName("Should provide valid database connection")
        void testValidConnection() {
            DataSource dataSource = DataSource.getInstance();
            Connection connection = dataSource.getConnection();

            assertThat(connection).isNotNull();
        }


        @Test
        @Order(5)
        @DisplayName("Should return same connection instance")
        void testSameConnectionInstance() {
            DataSource dataSource = DataSource.getInstance();
            Connection conn1 = dataSource.getConnection();
            Connection conn2 = dataSource.getConnection();

            assertThat(conn1).isNotNull();
            assertThat(conn2).isNotNull();
            assertThat(conn1).isSameAs(conn2);
        }


        @Test
        @Order(6)
        @DisplayName("Should maintain connection across calls")
        void testConnectionPersistence() {
            DataSource dataSource = DataSource.getInstance();
            Connection connection = dataSource.getConnection();

            assertThat(connection).isNotNull();

            // Get connection again
            Connection sameConnection = dataSource.getConnection();
            assertThat(sameConnection).isSameAs(connection);
        }


        @Test
        @Order(7)
        @DisplayName("Connection should be valid and open")
        void testConnectionValid() throws SQLException {
            DataSource dataSource = DataSource.getInstance();
            Connection connection = dataSource.getConnection();

            assertThat(connection).isNotNull();
            assertThat(connection.isClosed()).isFalse();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Connection Properties Tests")
    class ConnectionPropertiesTests {

        @Test
        @Order(8)
        @DisplayName("Connection should have valid metadata")
        void testConnectionMetadata() throws SQLException {
            DataSource dataSource = DataSource.getInstance();
            Connection connection = dataSource.getConnection();

            assertThat(connection.getMetaData()).isNotNull();
        }


        @Test
        @Order(9)
        @DisplayName("Should support transactions")
        void testTransactionSupport() throws SQLException {
            DataSource dataSource = DataSource.getInstance();
            Connection connection = dataSource.getConnection();

            assertThat(connection.getMetaData().supportsTransactions()).isTrue();
        }


        @Test
        @Order(10)
        @DisplayName("Should be able to create statements")
        void testStatementCreation() throws SQLException {
            DataSource dataSource = DataSource.getInstance();
            Connection connection = dataSource.getConnection();

            assertThat(connection.createStatement()).isNotNull();
        }


        @Test
        @Order(11)
        @DisplayName("Should be able to create prepared statements")
        void testPreparedStatementCreation() throws SQLException {
            DataSource dataSource = DataSource.getInstance();
            Connection connection = dataSource.getConnection();

            assertThat(connection.prepareStatement("SELECT 1")).isNotNull();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Connection Lifecycle Tests")
    class ConnectionLifecycleTests {

        @Test
        @Order(12)
        @DisplayName("Should maintain connection across multiple operations")
        void testMultipleOperations() throws SQLException {
            DataSource dataSource = DataSource.getInstance();
            Connection connection = dataSource.getConnection();

            // Perform multiple operations
            connection.createStatement();
            connection.prepareStatement("SELECT 1");

            assertThat(connection.isClosed()).isFalse();
        }


        @Test
        @Order(13)
        @DisplayName("Should reuse same connection")
        void testConnectionReuse() {
            DataSource dataSource = DataSource.getInstance();

            Connection conn1 = dataSource.getConnection();
            Connection conn2 = dataSource.getConnection();
            Connection conn3 = dataSource.getConnection();

            assertThat(conn1).isSameAs(conn2);
            assertThat(conn2).isSameAs(conn3);
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @Order(14)
        @DisplayName("Should handle concurrent access")
        void testConcurrentAccess() throws InterruptedException {
            DataSource dataSource = DataSource.getInstance();
            Connection[] connections = new Connection[5];

            Thread[] threads = new Thread[5];
            for (int i = 0; i < 5; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    connections[index] = dataSource.getConnection();
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // All connections should be the same instance
            for (int i = 1; i < 5; i++) {
                assertThat(connections[i]).isSameAs(connections[0]);
            }
        }


        @Test
        @Order(15)
        @DisplayName("Should be thread-safe for singleton access")
        void testThreadSafeSingletonAccess() throws InterruptedException {
            final int threadCount = 10;
            DataSource[] instances = new DataSource[threadCount];
            Thread[] threads = new Thread[threadCount];

            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    instances[index] = DataSource.getInstance();
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // All instances should be the same
            for (int i = 1; i < threadCount; i++) {
                assertThat(instances[i]).isSameAs(instances[0]);
            }
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Database Configuration Tests")
    class DatabaseConfigurationTests {

        @Test
        @Order(16)
        @DisplayName("Should connect to MySQL database")
        void testMySQLConnection() throws SQLException {
            DataSource dataSource = DataSource.getInstance();
            Connection connection = dataSource.getConnection();

            String dbProduct = connection.getMetaData().getDatabaseProductName();
            assertThat(dbProduct).containsIgnoringCase("mysql");
        }


        @Test
        @Order(17)
        @DisplayName("Should use correct database name")
        void testDatabaseName() throws SQLException {
            DataSource dataSource = DataSource.getInstance();
            Connection connection = dataSource.getConnection();

            String catalog = connection.getCatalog();
            assertThat(catalog).isNotEmpty();
        }


        @Test
        @Order(18)
        @DisplayName("Should have valid connection URL")
        void testConnectionURL() throws SQLException {
            DataSource dataSource = DataSource.getInstance();
            Connection connection = dataSource.getConnection();

            String url = connection.getMetaData().getURL();
            assertThat(url).isNotEmpty();
            assertThat(url).startsWith("jdbc:mysql://");
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @Order(19)
        @DisplayName("Should handle multiple getInstance calls safely")
        void testMultipleGetInstance() {
            for (int i = 0; i < 100; i++) {
                DataSource instance = DataSource.getInstance();
                assertThat(instance).isNotNull();
            }
        }


        @Test
        @Order(20)
        @DisplayName("Should handle rapid connection requests")
        void testRapidConnectionRequests() {
            DataSource dataSource = DataSource.getInstance();

            for (int i = 0; i < 50; i++) {
                Connection connection = dataSource.getConnection();
                assertThat(connection).isNotNull();
            }
        }

    }

}
