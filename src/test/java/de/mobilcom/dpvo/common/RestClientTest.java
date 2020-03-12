package de.mobilcom.dpvo.common;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

public class RestClientTest {

  @Rule
  public MockServerRule mockServerRule = new MockServerRule(this, 20000);

  private MockServerClient mockServerClient;

  private String baseUrl = "http://localhost:20000";

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() {
    mockServerClient.reset();
  }

  @Test
  public void testParam() {
    try {
      RestClient restClient = new RestClient(baseUrl + "/users/angie/repos");
      restClient.addParam("projects", "DUMMY");
    } catch (Exception ex) {
      ex.printStackTrace();
      fail("Exception " + ex.getMessage());
    }
  }

  @Test
  public void testSendGetRequest() {
    RestClient restClient = null;
    try {
      mockServerClient
        .when(HttpRequest.request(baseUrl).withPath("/users/angie/repos"))
        .respond(
          HttpResponse
            .response()
            .withBody("Test")
            .withHeader(new Header("Content-Type", "application/json"))
            .withStatusCode(200));
      restClient = new RestClient(baseUrl + "/users/angie/repos");
      restClient.setLogEnabled(true);
      restClient.setBody("Test");
      restClient.sendGetRequest();
    } catch (Exception ex) {
      ex.printStackTrace();
      fail("Exception " + ex.getMessage());
    } finally {
      if (restClient != null) {
        restClient.close();
      }
    }
  }

  @Test
  public void testParameterError() {
    RestClient restClient = null;
    try {
      restClient = new RestClient(null);
      restClient.setLogEnabled(true);
      restClient.sendGetRequest();
      fail("Exception has to be thrown");
    } catch (Exception ex) {
    } finally {
      if (restClient != null) {
        restClient.close();
      }
    }
  }

  @Test
  public void testSendGetRequestAuth() {
    RestClient restClient = null;
    try {
      mockServerClient
        .when(HttpRequest.request(baseUrl).withPath("/users/angie/repos"))
        .respond(
          HttpResponse
            .response()
            .withBody("")
            .withHeader(new Header("Content-Type", "application/json"))
            .withStatusCode(200));
      restClient = new RestClient(baseUrl + "/users/angie/repos", "test", "test");
      restClient.setLogEnabled(true);
      restClient.sendGetRequest();
    } catch (Exception ex) {
      ex.printStackTrace();
      fail("Exception " + ex.getMessage());
    } finally {
      if (restClient != null) {
        restClient.close();
      }
    }
  }

  @Test
  public void testSendGetRequestError() {
    RestClient restClient = null;
    try {
      mockServerClient
        .when(HttpRequest.request(baseUrl).withPath("/users/angie/repos"))
        .respond(
          HttpResponse
            .response()
            .withBody("")
            .withHeader(new Header("Content-Type", "application/json"))
            .withStatusCode(500));
      restClient = new RestClient(baseUrl + "/users/angie/repos");
      restClient.setLogEnabled(true);
      restClient.sendGetRequest();
      fail("Exception has to be thrown");
    } catch (Exception ex) {
    } finally {
      if (restClient != null) {
        restClient.close();
      }
    }
  }

  @Test
  public void testWriteResponse() {
    RestClient restClient = null;
    try {
      mockServerClient
        .when(HttpRequest.request(baseUrl).withPath("/users/dummy/repos"))
        .respond(
          HttpResponse
            .response()
            .withBody("Test Content")
            .withHeader(new Header("Content-Type", "application/json"))
            .withStatusCode(200));
      restClient = new RestClient(baseUrl + "/users/dummy/repos");
      restClient.setLogEnabled(true);
      org.apache.http.HttpResponse response = restClient.sendGetRequest();
      restClient.writeResponse(response, "./target/testresponse.txt");
    } catch (Exception ex) {
      fail("Exception");
    } finally {
      if (restClient != null) {
        restClient.close();
      }
    }
  }

}
