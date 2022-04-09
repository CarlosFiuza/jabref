package org.jabref.logic.importer.fetcher;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.http.client.utils.URIBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.jabref.logic.importer.FetcherException;
import org.jabref.logic.importer.ParseException;
import org.jabref.logic.importer.Parser;
import org.jabref.logic.importer.SearchBasedParserFetcher;
import org.jabref.logic.importer.fetcher.transformers.DefaultQueryTransformer;
import org.jabref.logic.util.OS;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BiodiversityLibraryFetcher implements SearchBasedParserFetcher {

    private static final String apiKey = "377e59ae-67f6-4228-8688-c4eb7187c4c9";
    private static final String SEARCH_URL = "https://www.biodiversitylibrary.org/api3?op=PublicationSearch&apikey=" + apiKey;

    public BiodiversityLibraryFetcher() {}

    @Override
    public Parser getParser() {
        return inputStream -> {
            String response = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining(OS.NEWLINE));
            JSONObject jsonObject = new JSONObject(response);

            JSONArray jsonArray = jsonObject.getJSONArray("Result");
            List<BibEntry> bibEntries = new ArrayList<>();

            for (int index = 0; index < jsonArray.length(); index++) {
                bibEntries.add(jsonItemToBibEntry(jsonArray.getJSONObject(index)));
            }
            return bibEntries;
        };
    }

    private BibEntry jsonItemToBibEntry(JSONObject item) throws ParseException {
        BibEntry bibEntry = new BibEntry();

        if (item.has("Genre")) {
            String genre = item.getString("Genre");
            if (genre.equals("Article")) {
                bibEntry.setType(StandardEntryType.Article);
            } else if (genre.equals("Book")) {
                bibEntry.setType(StandardEntryType.Book);
            }
        }

        if (item.has("Authors")) {
            JSONArray authors = item.getJSONArray("Authors");
            String authorsName = "";
            for (int index = 0; index < authors.length(); index++) {
                JSONObject author = authors.getJSONObject(index);
                if (index > 0) {
                    authorsName = authorsName.concat(", ");
                }
                authorsName = authorsName.concat(author.getString("Name"));
            }
            bibEntry.withField(StandardField.AUTHOR, authorsName);
        }

        if (item.has("BHLType")) {
            String bhlType = item.getString("BHLType");
            String url;
            if (bhlType.equals("Part")) {
                url = item.getString("PartUrl");
                bibEntry.withField(StandardField.URL, url);
            } else if (bhlType.equals("Item")) {
                url = item.getString("ItemUrl");
                bibEntry.withField(StandardField.URL, url);
            }
        }

        if (item.has("Title")) {
            String title = item.getString("Title");
            bibEntry.withField(StandardField.TITLE, title);
        }

        if (item.has("Date")) {
            String date = item.getString("Date");
            bibEntry.withField(StandardField.YEAR, date);
        }

        if (item.has("Volume")) {
            String volume = item.getString("Volume");
            bibEntry.withField(StandardField.VOLUME, volume);
        }

        if (item.has("PageRange")) {
            String pageRange = item.getString("PageRange");
            bibEntry.withField(StandardField.PAGES, pageRange);
        }

        return bibEntry;
    }


    @Override
    public URL getURLForQuery(QueryNode luceneQuery) throws URISyntaxException, MalformedURLException, FetcherException {
        URIBuilder uriBuilder = new URIBuilder(SEARCH_URL);
        String query = new DefaultQueryTransformer().transformLuceneQuery(luceneQuery).orElse("");
        uriBuilder.addParameter("searchterm", query);
        uriBuilder.addParameter("format", "json");
        return uriBuilder.build().toURL();
    }

    @Override
    public String getName() {
        return "Biodiversity Library";
    }
}
