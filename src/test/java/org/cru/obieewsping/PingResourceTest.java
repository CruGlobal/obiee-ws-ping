package org.cru.obieewsping;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class PingResourceTest {

    @Test
    public void testPinger() {
        given()
          .when().get("/ping/pinger")
          .then()
             .statusCode(200)
             .body(is("ok"));
    }

    @Test
    public void testObiee() {
        given()
            .param("username", "bob")
            .param("password", "secret")
          .when().post("/ping/obiee")
          .then()
             .statusCode(200)
             .body(is("ok"));
    }

}
