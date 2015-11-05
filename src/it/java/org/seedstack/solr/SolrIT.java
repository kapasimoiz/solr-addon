/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.seedstack.seed.it.AbstractSeedIT;
import org.seedstack.solr.fixtures.Person;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class SolrIT extends AbstractSeedIT {
    @Inject
    SolrClient solrClient;

    @Inject
    @Named("client1")
    SolrClient solrClient1;

    @Test
    public void solr_client_is_injected() {
        assertThat(solrClient).isNotNull();
        assertThat(solrClient1).isNotNull();
    }

    @Test
    public void index_and_join_search_two_different_embedded_collection() throws SolrServerException, IOException {
        addDocuments1();
        try {
            addDocuments2();

            try {
                QueryResponse queryResponse = transactional_multicore_query();
                SolrDocumentList docs = queryResponse.getResults();
                assertThat(docs).isNotNull();
                assertThat(docs.getNumFound()).isEqualTo(1);
            } finally {
                clean2();
            }
        } finally {
            clean1();
        }
    }

    @Test
    public void query_without_transaction() throws SolrServerException, IOException {
        addDocuments1();

        try {
            QueryResponse queryResponse = solrClient1.query(new SolrQuery("name:Gerard"));
            SolrDocumentList docs = queryResponse.getResults();
            assertThat(docs).isNotNull();
            assertThat(docs.getNumFound()).isEqualTo(1);
        } finally {
            clean1();
        }
    }

    @Transactional
    @Solr("client1")
    protected QueryResponse transactional_multicore_query() throws SolrServerException, IOException {
        return solrClient.query(new SolrQuery("{!join from=id to=id fromIndex=core1}name:Gerard"));
    }

    @Transactional
    @Solr("client1")
    protected void clean1() throws SolrServerException, IOException {
        solrClient.deleteByQuery("*:*");
    }

    @Transactional
    @Solr("client2")
    protected void clean2() throws SolrServerException, IOException {
        solrClient.deleteByQuery("*:*");
    }

    @Transactional
    @Solr("client1")
    protected void addDocuments1() throws SolrServerException, IOException {
        assertThat(solrClient).isNotNull();

        Person person1 = new Person();
        person1.setId("1");
        person1.setName("Gerard");

        Person person2 = new Person();
        person2.setId("2");
        person2.setName("Sarah");

        Collection<Person> persons = new ArrayList<Person>();
        persons.add(person1);
        persons.add(person2);

        solrClient.addBeans(persons);
    }

    @Transactional
    @Solr("client2")
    protected void addDocuments2() throws SolrServerException, IOException {
        Assertions.assertThat(solrClient).isNotNull();

        Person person1 = new Person();
        person1.setId("2");
        person1.setName("Gerard");

        Person person2 = new Person();
        person2.setId("1");
        person2.setName("Sarah");

        Collection<Person> persons = new ArrayList<Person>();
        persons.add(person1);
        persons.add(person2);

        solrClient.addBeans(persons);
    }
}
