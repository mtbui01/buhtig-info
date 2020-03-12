package de.mobilcom.dpvo.common;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.annotation.Timed;

/**
 * The class <code>RestClient</code> is a helper class used to send Rest-Request
 * to services. Currently the class uses the components from Restassured.
 */
public class RestClient {

  private static final Logger LOG = LoggerFactory.getLogger(RestClient.class);

  public static final String CONTENTTYPE_JSON = "application/json";

  private static final String AUTH_HEADER = "Authorization";

  private final String baseURL;

  private String username;

  private String password;
    

  private HashMap<String, String> parameters = new HashMap<>();

  private CloseableHttpClient httpClient = null;

  private CloseableHttpResponse httpresponse = null;

  private Object body = null;

  private boolean logEnabled = false;

  public RestClient(String baseURL, String username, String password) {
    this.baseURL = baseURL;
    this.username = username;
    this.password = password;
  }

  public RestClient(String baseURL) {
    this.baseURL = baseURL;
  }
  
  private void checkParams() throws DPVOException {
    if (getBaseURL() == null) {
      throw new DPVOException("Base URL is not specified");
    }
  }

  private void addHeader(HttpRequestBase request) {
    if (getUsername() != null) {
      String authHeader = "Basic "
          + Base64.encodeBase64String((getUsername() + ":" + getPassword()).getBytes(StandardCharsets.UTF_8));
      request.addHeader(AUTH_HEADER, authHeader);
    }
    request.addHeader("Accept", CONTENTTYPE_JSON);
    request.addHeader("Content-type", CONTENTTYPE_JSON);
  }

  private CloseableHttpClient createHttpClient() {
    LOG.debug("Entering createHttpClient");
    if (getUsername() != null) {
      CredentialsProvider credsProvider = new BasicCredentialsProvider();
      UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(
        getUsername(),
        getPassword());
      credsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
      return HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
    } else {
      return HttpClientBuilder.create().build();
    }
  }

  private URI createURI() throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder(getBaseURL());
    for (Map.Entry<String, String> param : parameters.entrySet()) {
      uriBuilder.addParameter(param.getKey(), param.getValue());
    }
    return uriBuilder.build();
  }

  private HttpGet createGetRequest() throws URISyntaxException {
    LOG.debug("Entering createGetRequest");
    HttpGet getRequest = new HttpGet(createURI());
    addHeader(getRequest);
    return getRequest;
  }

  @Timed("sendGetRequest")
  public HttpResponse sendGetRequest() throws DPVOException {
    try {
      checkParams();
      this.httpClient = createHttpClient();
      HttpGet getRequest = createGetRequest();
      this.httpresponse = httpClient.execute(getRequest);
      if (httpresponse != null && httpresponse.getStatusLine().getStatusCode() != 200) {
        throw new DPVOException("Error in sending request " + httpresponse.getStatusLine().getStatusCode());
      }
      return httpresponse;
    } catch (Exception ex) {
      throw new DPVOException(ex);
    }
  }

  public void close() {
    if (httpresponse != null) {
      try {
        httpresponse.close();
        httpresponse = null;
      } catch (Exception ex) {
        LOG.info("Exception in close response ignored", ex);
      }
    }
    if (httpClient != null) {
      try {
        httpClient.close();
        httpClient = null;
      } catch (Exception ex) {
        LOG.info("Exception in close", ex);
      }
    }
  }

  /**
   * Write a response to file
   * 
   * @param response The Response object
   * @param fileName The name of the output file
   */

  public void writeResponse(HttpResponse response, String fileName) {
    try (Writer writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
      writer.write(EntityUtils.toString(response.getEntity()));
    } catch (Exception ex) {
      LOG.info("Exception ", ex);
    }
  }

  public String getBaseURL() {
    return baseURL;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public void addParam(String key, String value) {
    parameters.put(key, value);
  }

  public Object getBody() {
    return body;
  }

  public void setBody(Object body) {
    this.body = body;
  }

  public boolean isLogEnabled() {
    return logEnabled;
  }

  public void setLogEnabled(boolean logEnabled) {
    this.logEnabled = logEnabled;
  }

}
