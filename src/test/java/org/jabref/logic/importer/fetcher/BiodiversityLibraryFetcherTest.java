package org.jabref.logic.importer.fetcher;

import org.apache.lucene.queryparser.flexible.core.parser.SyntaxParser;
import org.apache.lucene.queryparser.flexible.standard.parser.StandardSyntaxParser;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;
import java.util.Optional;

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
        String expected = "https://www.biodiversitylibrary.org/api3?op=PublicationSearch&apikey=377e59ae-67f6-4228-8688-c4eb7187c4c9&searchterm=test+query+url&format=json";
        assertEquals(expected, url.toString());
    }

    @Test
    void testGetCorrectName() {
        String expected = "Biodiversity Library";
        String result = fetcher.getName();
        assertEquals(expected, result);
    }

    @Test
    void searchByQueryFindsEntry() throws Exception{
        BibEntry searchEntry = new BibEntry(StandardEntryType.Article)
                .withField(StandardField.AUTHOR, "Gifford, Edward Winslow,")
                .withField(StandardField.URL, "https://www.biodiversitylibrary.org/part/69838")
                .withField(StandardField.TITLE, "Field notes on the land birds of the Galapagos Islands, and of Cocos Island,Costa Rica")
                .withField(StandardField.YEAR, "1919")
                .withField(StandardField.VOLUME, "2")
                .withField(StandardField.PAGES, "189--258");

        List<BibEntry> fetchedEntries = fetcher.performSearch("cocos island costa rica birds");
        assertEquals(Optional.of(searchEntry), fetchedEntries.stream().findFirst());
    }

}
