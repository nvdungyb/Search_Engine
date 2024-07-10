package dzung.trie.spell_checker.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dzung.trie.spell_checker.service.SpellCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private SpellCheckerService spellCheckerService;
    @Autowired
    private Configuration configuration;

    @GetMapping("")
    public String homePage() {
        return "index";
    }

    @GetMapping("/spellcheck")
    public @ResponseBody List<String> spellCheck(@RequestParam("word") String word) {
        return spellCheckerService.suggest(word);
    }

    @GetMapping("/savedb")
    public @ResponseBody boolean saveToMongoDb() {
        return spellCheckerService.saveDb();
    }

    @PostMapping("/azure")
    public @ResponseBody String getTranslation(@RequestParam("message") String message) throws IOException, InterruptedException {
        String azureKey = configuration.getAzureKey();
        String apiUrl = configuration.getApiUrl();
        String region = configuration.getRegion();

        HttpClient client = HttpClient.newHttpClient();

        String resquestBody = "[{\"Text\": \"" + message + "\"}]";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Ocp-Apim-Subscription-Key", azureKey)
                .header("Ocp-Apim-Subscription-Region", region)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(resquestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());
            String translation = jsonNode.get(0).get("translations").get(0).get("text").asText();
            return translation;
        } else {
            System.out.println("Error: " + response.statusCode());
            return "Error";
        }
    }
}
