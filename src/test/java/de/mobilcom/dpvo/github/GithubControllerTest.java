package de.mobilcom.dpvo.github;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.test.context.ActiveProfiles;

import de.mobilcom.dpvo.github.datamodel.Repository;

@ActiveProfiles("dev")
public class GithubControllerTest {

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
  public void testGithubController() {
    try {
      mockServerClient
        .when(HttpRequest.request(baseUrl).withPath("/users/angie/repos"))
        .respond(
          HttpResponse
            .response()
            .withBody("[{\"id\": 10000,\"node_id\": \"abcdefg\",\"name\": \"buhtig-info\"," 
            + "\"owner\": {\"login\": \"dummy\",\"id\": 60977472}" 
            + "}]")
            .withHeader(new Header("Content-Type", "application/json"))
            .withStatusCode(200));
      
      GithubController controller = mock(GithubController.class);
      when(controller.getServerUrl()).thenReturn(baseUrl);      
      when(controller.getToken()).thenReturn("assaas");
      doCallRealMethod().when(controller).getRepolistoryList("angie");
      doCallRealMethod().when(controller).getRepoList("angie");
      
      List<Repository> repoList = controller.getRepoList("angie");      
      assertEquals(1, repoList.size());
      assertEquals("buhtig-info", repoList.get(0).getRepositoryName());
      assertEquals("dummy", repoList.get(0).getOwner());      
    } catch (Exception ex) {
      ex.printStackTrace();
      fail("Exception " + ex.getMessage());
    } finally {
    }
    
  }

  @Test
  public void testCreateGithubController() {
    try {
      GithubController controller = new GithubController();
      assertNotNull(controller.getServerUrl());
      assertNull(controller.getUserName());
      assertNull(controller.getToken());      
    } catch (Exception ex) {
      fail("Exception " + ex.getMessage());
    } finally {
    }
    
  }
  
}
