package com.esprit.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for Page utility class.
 * Tests pagination data structure, metadata, and edge cases.
 * <p>
 * Test Categories:
 * - Page Creation
 * - Page Metadata
 * - Content Management
 * - Edge Cases
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PageTest {

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Page Creation Tests")
    class PageCreationTests {

        @Test
        @Order(1)
        @DisplayName("Should create page with content")
        void testCreatePageWithContent() {
            List<String> content = Arrays.asList("item1", "item2", "item3");
            Page<String> page = new Page<>(content, 0, 10, 3);

            assertThat(page).isNotNull();
            assertThat(page.getContent()).isEqualTo(content);
        }


        @Test
        @Order(2)
        @DisplayName("Should create empty page")
        void testCreateEmptyPage() {
            Page<String> page = new Page<>(Collections.emptyList(), 0, 10, 0);

            assertThat(page).isNotNull();
            assertThat(page.getContent()).isEmpty();
            assertThat(page.getTotalElements()).isZero();
        }


        @Test
        @Order(3)
        @DisplayName("Should store page number correctly")
        void testPageNumber() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 2, 10, 50);

            assertThat(page.getPage()).isEqualTo(2);
        }


        @Test
        @Order(4)
        @DisplayName("Should store page size correctly")
        void testPageSize() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 0, 20, 50);

            assertThat(page.getSize()).isEqualTo(20);
        }


        @Test
        @Order(5)
        @DisplayName("Should store total elements correctly")
        void testTotalElements() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 0, 10, 100);

            assertThat(page.getTotalElements()).isEqualTo(100);
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Page Metadata Tests")
    class PageMetadataTests {

        @Test
        @Order(6)
        @DisplayName("Should calculate total pages correctly")
        void testTotalPages() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 0, 10, 25);

            assertThat(page.getTotalPages()).isEqualTo(3);
        }


        @Test
        @Order(7)
        @DisplayName("Should handle exact page division")
        void testExactPageDivision() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 0, 10, 30);

            assertThat(page.getTotalPages()).isEqualTo(3);
        }


        @Test
        @Order(8)
        @DisplayName("Should detect first page")
        void testIsFirst() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 0, 10, 50);

            assertThat(page.isFirst()).isTrue();
        }


        @Test
        @Order(9)
        @DisplayName("Should detect non-first page")
        void testIsNotFirst() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 1, 10, 50);

            assertThat(page.isFirst()).isFalse();
        }


        @Test
        @Order(10)
        @DisplayName("Should detect last page")
        void testIsLast() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 2, 10, 25);

            assertThat(page.isLast()).isTrue();
        }


        @Test
        @Order(11)
        @DisplayName("Should detect non-last page")
        void testIsNotLast() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 0, 10, 50);

            assertThat(page.isLast()).isFalse();
        }


        @Test
        @Order(12)
        @DisplayName("Should detect when has next page")
        void testHasNext() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 0, 10, 50);

            assertThat(page.hasNext()).isTrue();
        }


        @Test
        @Order(13)
        @DisplayName("Should detect when no next page")
        void testHasNoNext() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 2, 10, 25);

            assertThat(page.hasNext()).isFalse();
        }


        @Test
        @Order(14)
        @DisplayName("Should detect when has previous page")
        void testHasPrevious() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 1, 10, 50);

            assertThat(page.hasPrevious()).isTrue();
        }


        @Test
        @Order(15)
        @DisplayName("Should detect when no previous page")
        void testHasNoPrevious() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 0, 10, 50);

            assertThat(page.hasPrevious()).isFalse();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Content Management Tests")
    class ContentManagementTests {

        @Test
        @Order(16)
        @DisplayName("Should return correct number of elements")
        void testNumberOfElements() {
            List<String> content = Arrays.asList("item1", "item2", "item3");
            Page<String> page = new Page<>(content, 0, 10, 50);

            assertThat(page.getNumberOfElements()).isEqualTo(3);
        }


        @Test
        @Order(17)
        @DisplayName("Should detect empty page")
        void testIsEmpty() {
            Page<String> page = new Page<>(Collections.emptyList(), 0, 10, 0);

            assertThat(page.isEmpty()).isTrue();
        }


        @Test
        @Order(18)
        @DisplayName("Should detect non-empty page")
        void testIsNotEmpty() {
            List<String> content = Arrays.asList("item1");
            Page<String> page = new Page<>(content, 0, 10, 50);

            assertThat(page.isEmpty()).isFalse();
        }


        @Test
        @Order(19)
        @DisplayName("Should preserve content order")
        void testContentOrder() {
            List<String> content = Arrays.asList("first", "second", "third");
            Page<String> page = new Page<>(content, 0, 10, 50);

            assertThat(page.getContent()).containsExactly("first", "second", "third");
        }


        @Test
        @Order(20)
        @DisplayName("Should handle different content types")
        void testDifferentContentTypes() {
            List<Integer> content = Arrays.asList(1, 2, 3);
            Page<Integer> page = new Page<>(content, 0, 10, 50);

            assertThat(page.getContent()).containsExactly(1, 2, 3);
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @Order(21)
        @DisplayName("Should handle single element page")
        void testSingleElement() {
            List<String> content = Arrays.asList("only");
            Page<String> page = new Page<>(content, 0, 1, 1);

            assertThat(page.getContent()).hasSize(1);
            assertThat(page.isFirst()).isTrue();
            assertThat(page.isLast()).isTrue();
            assertThat(page.getTotalPages()).isEqualTo(1);
        }


        @Test
        @Order(22)
        @DisplayName("Should handle large page size")
        void testLargePageSize() {
            List<String> content = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                content.add("item" + i);
            }
            Page<String> page = new Page<>(content, 0, 1000, 1000);

            assertThat(page.getContent()).hasSize(1000);
            assertThat(page.getTotalPages()).isEqualTo(1);
        }


        @Test
        @Order(23)
        @DisplayName("Should handle zero page size edge case")
        void testZeroPageSize() {
            List<String> content = Collections.emptyList();
            Page<String> page = new Page<>(content, 0, 0, 0);

            assertThat(page.getContent()).isEmpty();
        }


        @Test
        @Order(24)
        @DisplayName("Should handle last page with fewer elements")
        void testLastPagePartial() {
            List<String> content = Arrays.asList("item1", "item2");
            Page<String> page = new Page<>(content, 2, 10, 22);

            assertThat(page.getContent()).hasSize(2);
            assertThat(page.isLast()).isTrue();
            assertThat(page.getTotalPages()).isEqualTo(3);
        }


        @Test
        @Order(25)
        @DisplayName("Should handle negative page number")
        void testNegativePageNumber() {
            List<String> content = Arrays.asList("item1");
            Page<String> page = new Page<>(content, -1, 10, 50);

            assertThat(page.getPage()).isEqualTo(-1);
        }


        @Test
        @Order(26)
        @DisplayName("Should handle content size exceeding page size")
        void testContentExceedingPageSize() {
            List<String> content = Arrays.asList("item1", "item2", "item3");
            Page<String> page = new Page<>(content, 0, 2, 10);

            // Even if content is larger, it should be stored as-is
            assertThat(page.getContent()).hasSize(3);
        }


        @Test
        @Order(27)
        @DisplayName("Should calculate pages with remainder correctly")
        void testPagesWithRemainder() {
            List<String> content = Arrays.asList("item1");
            Page<String> page = new Page<>(content, 0, 3, 10);

            // 10 elements / 3 per page = 4 pages (3 full + 1 partial)
            assertThat(page.getTotalPages()).isEqualTo(4);
        }


        @Test
        @Order(28)
        @DisplayName("Should handle very large total elements")
        void testLargeTotalElements() {
            List<String> content = Arrays.asList("item1");
            Page<String> page = new Page<>(content, 0, 10, 1_000_000);

            assertThat(page.getTotalPages()).isEqualTo(100_000);
            assertThat(page.hasNext()).isTrue();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Boundary Tests")
    class BoundaryTests {

        @Test
        @Order(29)
        @DisplayName("Should handle page 0")
        void testPageZero() {
            List<String> content = Arrays.asList("item1");
            Page<String> page = new Page<>(content, 0, 10, 50);

            assertThat(page.getPage()).isZero();
            assertThat(page.isFirst()).isTrue();
            assertThat(page.hasPrevious()).isFalse();
        }


        @Test
        @Order(30)
        @DisplayName("Should handle maximum page")
        void testMaximumPage() {
            List<String> content = Arrays.asList("item1");
            Page<String> page = new Page<>(content, 9, 10, 100);

            assertThat(page.isLast()).isTrue();
            assertThat(page.hasNext()).isFalse();
        }


        @Test
        @Order(31)
        @DisplayName("Should handle single page scenario")
        void testSinglePageScenario() {
            List<String> content = Arrays.asList("item1", "item2", "item3");
            Page<String> page = new Page<>(content, 0, 10, 3);

            assertThat(page.isFirst()).isTrue();
            assertThat(page.isLast()).isTrue();
            assertThat(page.hasPrevious()).isFalse();
            assertThat(page.hasNext()).isFalse();
            assertThat(page.getTotalPages()).isEqualTo(1);
        }

    }

}
