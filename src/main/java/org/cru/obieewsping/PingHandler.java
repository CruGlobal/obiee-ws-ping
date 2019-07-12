package org.cru.obieewsping;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import javax.inject.Inject;

public class PingHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObieeWsTransactionService service;
    private final ObjectReader reader = new ObjectMapper()
        .readerFor(PingRequest.class);

    @Inject
    public PingHandler(ObieeWsTransactionService service) {
        this.service = service;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String body = event.getBody();
        try {
            PingRequest pingRequest = reader.readValue(body);

            service.performSyntheticTransaction(pingRequest.getUsername(), pingRequest.getPassword());
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                .withBody("not ok:" + e.toString())
                .withStatusCode(500);
        }

        return new APIGatewayProxyResponseEvent()
            .withBody("ok")
            .withStatusCode(200);
    }
}
