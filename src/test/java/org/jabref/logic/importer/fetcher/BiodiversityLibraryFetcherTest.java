package org.jabref.logic.importer.fetcher;

import org.apache.lucene.queryparser.flexible.core.parser.SyntaxParser;
import org.apache.lucene.queryparser.flexible.standard.parser.StandardSyntaxParser;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.jabref.logic.importer.fetcher.transformers.AbstractQueryTransformer.NO_EXPLICIT_FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;

@FetcherTest
class BiodiversityLibraryFetcherTest {
    BiodiversityLibraryFetcher fetcher;

    @BeforeEach
    void setUp() {
        fetcher = new BiodiversityLibraryFetcher();
    }

    @Test
    void testGetURLForQuery() throws Exception {
        String testQuery = "test query url";
        SyntaxParser parser = new StandardSyntaxParser();
        URL url = fetcher.getURLForQuery(parser.parse(testQuery, NO_EXPLICIT_FIELD));
        String expected = "https://www.biodiversitylibrary.org/api3?op=PublicationSearch&apikey=377e59ae-67f6-4228-8688-c4eb7187c4c9&searchterm=test+query+url";
        assertEquals(expected, url.toString());
    }

    @Test
    void testGetCorrectName() {
        String expected = "Biodiversity Library";
        String result = fetcher.getName();
        assertEquals(expected, result);
    }
}
