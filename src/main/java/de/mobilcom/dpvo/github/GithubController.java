package de.mobilcom.dpvo.github;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mobilcom.dpvo.common.RestClient;
import de.mobilcom.dpvo.github.datamodel.Repository;

@RestController
@RequestMapping("/dpvo/github")
public class GithubController {

  private static final Logger LOG = LoggerFactory.getLogger(GithubController.class);
    	
  private String serverUrl = "https://api.github.com";
  
  @Value("${username}")
  private String userName;

  @Value("${token}")
  private String token;
  
  @GetMapping("/{userId}")
  public List<Repository> getRepoList(@PathVariable String userId) {
    LOG.info("Starting getRepoList {}", userId);
    return getRepolistoryList(userId);
  }
 
  /**
   * Get the list of repositores.
   * 
   * @param userId The github user identifier.
   * 
   * @return The list of repositories of the user.
   * 
   */
  List<Repository> getRepolistoryList(String userId) {
    List<Repository> repoList = new ArrayList<>();
    RestClient restClient = null;
    JsonNode node = null;
    try {
      LOG.info(getUserName());
      restClient = new RestClient(getServerUrl() + "/users/" + userId + "/repos", 
        getUserName(), getToken());
      org.apache.http.HttpResponse response = restClient.sendGetRequest();
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode rootNode = objectMapper.readTree(EntityUtils.toString(response.getEntity()));
      Iterator<JsonNode> valuesIter = rootNode.iterator();
      while (valuesIter.hasNext()) {
        node = valuesIter.next();
        repoList.add(createRepositoryFromNode(node));
      }
    } catch (Exception ex) {
      LOG.info("Exception ignored");
    } finally {
      if (restClient != null) {
        restClient.close();
      }
    }
    return repoList;
  }

  
  /**
   * Create a repository object from a node.
   * 
   * @param node The json node
   * @return
   */
  private Repository createRepositoryFromNode(JsonNode node) {
    Repository repo = new Repository();
    LOG.info("Repository found Name={} owner={}", node.get("name"), node.get("owner").get("login"));
    repo.setRepositoryName(node.get("name").textValue());
    repo.setOwner(node.get("owner").get("login").textValue());
    return repo;
  }
  
  public String getServerUrl() {
    return this.serverUrl;
  }

  public String getUserName() {
    return this.userName;
  }

  public String getToken() {
    return this.token;
  }
  
}
