package org.jabref.logic.importer.fetcher;

import org.apache.http.client.utils.URIBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.jabref.logic.importer.FetcherException;
import org.jabref.logic.importer.Parser;
import org.jabref.logic.importer.SearchBasedParserFetcher;
import org.jabref.logic.importer.fetcher.transformers.DefaultQueryTransformer;

import java.net.*;

public class BiodiversityLibraryFetcher implements SearchBasedParserFetcher {

    private static final String apiKey = "377e59ae-67f6-4228-8688-c4eb7187c4c9";
    private static final String SEARCH_URL = "https://www.biodiversitylibrary.org/api3?op=PublicationSearch&apikey=" + apiKey;

    public BiodiversityLibraryFetcher() {}

    @Override
    public Parser getParser() {
        return null;
    }

    @Override
    public URL getURLForQuery(QueryNode luceneQuery) throws URISyntaxException, MalformedURLException, FetcherException {
        URIBuilder uriBuilder = new URIBuilder(SEARCH_URL);
        String query = new DefaultQueryTransformer().transformLuceneQuery(luceneQuery).orElse("");
        uriBuilder.addParameter("searchterm", query);
        return uriBuilder.build().toURL();
    }

    @Override
    public String getName() {
        return "Biodiversity Library";
    }
}

