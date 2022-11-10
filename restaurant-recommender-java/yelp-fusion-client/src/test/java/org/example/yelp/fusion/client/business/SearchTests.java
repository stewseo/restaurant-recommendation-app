package org.example.yelp.fusion.client.business;

import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.transport.*;

import com.fasterxml.jackson.databind.node.*;

import org.example.lowlevel.restclient.*;
import org.example.yelp.fusion.client.*;
import org.junit.jupiter.api.*;

import javax.management.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class SearchTests extends AbstractRequestTestCase {
    String index = "yelp-businesses-restaurants-nyc";
    static String indexNyc = "yelp-businesses-restaurants-nyc";


    @Test
    public void searchOldestDocumentsTest() throws IOException {

        SearchResponse<ObjectNode> response = esClient.search(s->s
                        .index(indexNyc)
                        .query(q->q
                                .matchAll(t->t
                                        .queryName("categories")
                                )
                        )
                        .source(src-> src
                                .filter(f->f
                                        .includes("categories.alias")
                                        .includes("id")
                                        .includes("timestamp")
                                )
                        )
                        .sort(sort->sort
                                .field(field->field
                                        .field("timestamp").order(SortOrder.Asc)
                                )
                        )
                        .size(100)
                ,
                ObjectNode.class
        );

        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            PrintUtils.green("There are " + total.value() + " results");
        } else {
            PrintUtils.green("There are more than " + total.value() + " results");
        }

        List<Hit<ObjectNode>> hits = response.hits().hits();
        for (Hit<ObjectNode> hit: hits) {
            ObjectNode product = hit.source();
            PrintUtils.green("Found product " + product + ", score " + hit.score());
        }
    }

    @Test
    void searchMostRecentDocumentsTest() throws IOException {
        SearchResponse<ObjectNode> response = esClient.search(s->s
                        .index(indexNyc)
                        .query(q->q
                                .matchAll(t->t
                                        .queryName("categories")
                                )
                        )
                        .source(src-> src
                                .filter(f->f
                                        .includes("categories.alias")
                                        .includes("id")
                                        .includes("timestamp")
                                )
                        )
                        .sort(sort->sort
                                .field(field->field
                                        .field("timestamp").order(SortOrder.Desc)
                                )
                        )
                        .size(20)
                ,
                ObjectNode.class
        );

        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            PrintUtils.green("There are " + total.value() + " results");
        } else {
            PrintUtils.green("There are more than " + total.value() + " results");
        }

        List<Hit<ObjectNode>> hits = response.hits().hits();
        for (Hit<ObjectNode> hit: hits) {
            ObjectNode product = hit.source();
            PrintUtils.green("Found product " + product + ", score " + hit.score());
        }
    }

    @Test
    void filterPathTest() throws IOException {
        TransportOptions filterOptions = esClient._transport().options().with(b -> b
                .setParameter("filter_path", "-hits.hits._source")
        );

        SearchRequest request = SearchRequest.of(u-> u
                        .index(index)
                        .size(5)
                        .query(q-> q
                                .matchAll(MatchAllQuery.of(m->m.queryName("{}")))
                        ));

        esClient.withTransportOptions(filterOptions).search(request, Business.class);
    }
    

    @Test
    public void testContainerTaggedUnion() {
        String json = "{" +
                "    \"term\": {" +
                "      \"user.id\": {" +
                "        \"value\": \"kimchy\"," +
                "        \"boost\": 1.0" +
                "      }" +
                "    }" +
                "  }";

        Query q = Query.of(b -> b
                .withJson(new StringReader(json))
        );

        TermQuery tq = q.term();
        assertEquals("user.id", tq.field());
        assertEquals("kimchy", tq.value().stringValue());
        assertEquals(1.0, tq.boost(), 0.001);
    }
}

