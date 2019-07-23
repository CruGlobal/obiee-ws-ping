# obiee-ws-ping

## What?
A small quarkus-based AWS lambda that performs a synthetic transaction against Cru's OBIEE/Answers
SOAP API.
The transaction is three steps: log in, execute a query execution, and log out;
it is triggered via an API Gateway request.

## Why?
DataDog Synthetic's API tests don't support stateful multi-step APIs,
and their Browser tests don't support SOAP.
In the past we just used Browser tests to check OBIEE's web UI,
but it seems enabling SAML introduced some occasional flakiness
that makes such a test unsuitable for PagerDuty alerts.
OBIEE's SOAP API, on the other hand, has not shown such flakiness,
and it's actually what our tier 1 apps use anyway.

## How?
  * AWS Lambda: this seems like a great usecase for serverless.
  * Serverless Framework: it simplifies AWS Lambda, and we it elsewhere.
  * Java: we already have a java library for interacting with the Answers API.
  * Quarkus: because it's neat, and because maybe we'll eventually get compile-to-native working
    to make this even cheaper to run than it already is.
    (Native compilation not working due to a lack of Quarkus' support for SOAP.)

## Where?
The API test lives at 
https://app.datadoghq.com/synthetics/details/s2q-hug-ght.


## No CI/CD?
No, not unless I keep needing to update this, which seems unlikely.
Just manually build via `mvn package` and deploy via `serverless deploy`.
