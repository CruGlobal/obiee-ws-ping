package org.cru.obieewsping;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import javax.inject.Inject;
import org.jboss.resteasy.util.BasicAuthHelper;

public class PingHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObieeWsTransactionService service;
    private final ObjectReader reader = new ObjectMapper()
        .readerFor(PingRequestCredentials.class);

    @Inject
    public PingHandler(ObieeWsTransactionService service) {
        this.service = service;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            PingRequestCredentials credentials = getCredentialsFromHeader(event);
            if (credentials == null) {
                credentials = getCredentialsFromBody(event);
            }

            service.performSyntheticTransaction(credentials.getUsername(), credentials.getPassword());
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                .withBody("not ok:" + e.toString())
                .withStatusCode(500);
        }

        return new APIGatewayProxyResponseEvent()
            .withBody("ok")
            .withStatusCode(200);
    }

    private PingRequestCredentials getCredentialsFromHeader(APIGatewayProxyRequestEvent event) {
        PingRequestCredentials credentials = null;
        final String authorization = event.getHeaders().get("Authorization");
        if (authorization != null) {
            final String[] pair = BasicAuthHelper.parseHeader(authorization);
            if (pair != null) {
                assert pair.length == 2;
                credentials = new PingRequestCredentials();
                credentials.setUsername(pair[0]);
                credentials.setPassword(pair[1]);
            }
        }
        return credentials;
    }

    private PingRequestCredentials getCredentialsFromBody(APIGatewayProxyRequestEvent event) throws IOException {
        String body = event.getBody();
        return reader.readValue(body);
    }
}
