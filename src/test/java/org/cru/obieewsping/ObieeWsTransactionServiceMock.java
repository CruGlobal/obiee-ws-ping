package org.cru.obieewsping;

import io.quarkus.test.Mock;

@Mock
public class ObieeWsTransactionServiceMock extends ObieeWsTransactionService {

    @Override
    public void performSyntheticTransaction(String username, String password) {}

}
