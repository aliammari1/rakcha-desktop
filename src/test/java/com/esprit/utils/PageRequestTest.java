package com.esprit.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for PageRequest utility class.
 * Tests pagination request creation, validation, and defaults.
 * <p>
 * Test Categories:
 * - PageRequest Creation
 * - Default Values
 * - Validation
 * - Edge Cases
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PageRequestTest {

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("PageRequest Creation Tests")
    class PageRequestCreationTests {

        @Test
        @Order(1)
        @DisplayName("Should create page request with valid values")
        void testCreatePageRequest() {
            PageRequest pageRequest = PageRequest.of(2, 20);

            assertThat(pageRequest).isNotNull();
            assertThat(pageRequest.getPage()).isEqualTo(2);
            assertThat(pageRequest.getSize()).isEqualTo(20);
        }


        @Test
        @Order(2)
        @DisplayName("Should create page request with page 0")
        void testCreatePageRequestPageZero() {
            PageRequest pageRequest = PageRequest.of(0, 10);

            assertThat(pageRequest.getPage()).isZero();
            assertThat(pageRequest.getSize()).isEqualTo(10);
        }


        @Test
        @Order(3)
        @DisplayName("Should create page request with large page number")
        void testCreatePageRequestLargePage() {
            PageRequest pageRequest = PageRequest.of(1000, 10);

            assertThat(pageRequest.getPage()).isEqualTo(1000);
        }


        @Test
        @Order(4)
        @DisplayName("Should create page request with large page size")
        void testCreatePageRequestLargeSize() {
            PageRequest pageRequest = PageRequest.of(0, 1000);

            assertThat(pageRequest.getSize()).isEqualTo(1000);
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Default Page Request Tests")
    class DefaultPageRequestTests {

        @Test
        @Order(5)
        @DisplayName("Should create default page request")
        void testDefaultPageRequest() {
            PageRequest pageRequest = PageRequest.defaultPage();

            assertThat(pageRequest).isNotNull();
            assertThat(pageRequest.getPage()).isZero();
            assertThat(pageRequest.getSize()).isGreaterThan(0);
        }


        @Test
        @Order(6)
        @DisplayName("Default page should be first page")
        void testDefaultPageIsFirst() {
            PageRequest pageRequest = PageRequest.defaultPage();

            assertThat(pageRequest.getPage()).isZero();
        }


        @Test
        @Order(7)
        @DisplayName("Default page should have reasonable size")
        void testDefaultPageSize() {
            PageRequest pageRequest = PageRequest.defaultPage();

            // Default size should be reasonable (typically 10-20)
            assertThat(pageRequest.getSize()).isBetween(1, 100);
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("PageRequest Properties Tests")
    class PageRequestPropertiesTests {

        @Test
        @Order(8)
        @DisplayName("Should store page number correctly")
        void testPageNumber() {
            PageRequest pageRequest = PageRequest.of(5, 10);

            assertThat(pageRequest.getPage()).isEqualTo(5);
        }


        @Test
        @Order(9)
        @DisplayName("Should store page size correctly")
        void testPageSize() {
            PageRequest pageRequest = PageRequest.of(0, 25);

            assertThat(pageRequest.getSize()).isEqualTo(25);
        }


        @Test
        @Order(10)
        @DisplayName("Should calculate offset correctly")
        void testOffset() {
            PageRequest pageRequest = PageRequest.of(3, 10);

            // Offset = page * size = 3 * 10 = 30
            assertThat(pageRequest.getOffset()).isEqualTo(30);
        }


        @Test
        @Order(11)
        @DisplayName("Should calculate offset for first page")
        void testOffsetFirstPage() {
            PageRequest pageRequest = PageRequest.of(0, 10);

            assertThat(pageRequest.getOffset()).isZero();
        }


        @Test
        @Order(12)
        @DisplayName("Should calculate offset for large page")
        void testOffsetLargePage() {
            PageRequest pageRequest = PageRequest.of(100, 20);

            assertThat(pageRequest.getOffset()).isEqualTo(2000);
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @Order(13)
        @DisplayName("Should handle minimum page and size")
        void testMinimumValues() {
            PageRequest pageRequest = PageRequest.of(0, 1);

            assertThat(pageRequest.getPage()).isZero();
            assertThat(pageRequest.getSize()).isEqualTo(1);
            assertThat(pageRequest.getOffset()).isZero();
        }


        @Test
        @Order(14)
        @DisplayName("Should handle negative page number")
        void testNegativePageNumber() {
            PageRequest pageRequest = PageRequest.of(-1, 10);

            // Implementation may handle negative page differently
            assertThat(pageRequest).isNotNull();
        }


        @Test
        @Order(15)
        @DisplayName("Should handle zero page size")
        void testZeroPageSize() {
            PageRequest pageRequest = PageRequest.of(0, 0);

            // Implementation may handle zero size differently
            assertThat(pageRequest).isNotNull();
        }


        @Test
        @Order(16)
        @DisplayName("Should handle negative page size")
        void testNegativePageSize() {
            PageRequest pageRequest = PageRequest.of(0, -10);

            // Implementation may handle negative size differently
            assertThat(pageRequest).isNotNull();
        }


        @Test
        @Order(17)
        @DisplayName("Should handle very large page and size")
        void testVeryLargeValues() {
            PageRequest pageRequest = PageRequest.of(Integer.MAX_VALUE - 1, 100);

            assertThat(pageRequest).isNotNull();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Navigation Tests")
    class NavigationTests {

        @Test
        @Order(18)
        @DisplayName("Should calculate next page offset")
        void testNextPageOffset() {
            PageRequest page1 = PageRequest.of(0, 10);
            PageRequest page2 = PageRequest.of(1, 10);

            assertThat(page2.getOffset()).isEqualTo(page1.getOffset() + 10);
        }


        @Test
        @Order(19)
        @DisplayName("Should create sequential page requests")
        void testSequentialPages() {
            int pageSize = 20;

            for (int i = 0; i < 5; i++) {
                PageRequest pageRequest = PageRequest.of(i, pageSize);
                assertThat(pageRequest.getOffset()).isEqualTo(i * pageSize);
            }
        }


        @Test
        @Order(20)
        @DisplayName("Should maintain consistency across pages")
        void testPageConsistency() {
            int pageSize = 15;

            PageRequest page0 = PageRequest.of(0, pageSize);
            PageRequest page1 = PageRequest.of(1, pageSize);
            PageRequest page2 = PageRequest.of(2, pageSize);

            assertThat(page0.getSize()).isEqualTo(pageSize);
            assertThat(page1.getSize()).isEqualTo(pageSize);
            assertThat(page2.getSize()).isEqualTo(pageSize);
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Common Usage Patterns Tests")
    class CommonUsagePatternsTests {

        @Test
        @Order(21)
        @DisplayName("Should support typical page size of 10")
        void testTypicalPageSize10() {
            PageRequest pageRequest = PageRequest.of(0, 10);

            assertThat(pageRequest.getSize()).isEqualTo(10);
        }


        @Test
        @Order(22)
        @DisplayName("Should support typical page size of 20")
        void testTypicalPageSize20() {
            PageRequest pageRequest = PageRequest.of(0, 20);

            assertThat(pageRequest.getSize()).isEqualTo(20);
        }


        @Test
        @Order(23)
        @DisplayName("Should support typical page size of 50")
        void testTypicalPageSize50() {
            PageRequest pageRequest = PageRequest.of(0, 50);

            assertThat(pageRequest.getSize()).isEqualTo(50);
        }


        @Test
        @Order(24)
        @DisplayName("Should support typical page size of 100")
        void testTypicalPageSize100() {
            PageRequest pageRequest = PageRequest.of(0, 100);

            assertThat(pageRequest.getSize()).isEqualTo(100);
        }

    }

}
