/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
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
import org.seedstack.seed.transaction.spi.ExceptionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;

/**
 * Exception handler for Solr transactions.
 */
public interface SolrExceptionHandler extends ExceptionHandler<SolrClient> {

    @Override
    boolean handleException(Exception exception, TransactionMetadata associatedTransactionMetadata, SolrClient associatedTransaction);

}
