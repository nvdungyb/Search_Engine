package dzung.trie.spell_checker.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class FilePath {
    @Value("${wordsPath}")
    private String wordsPath;

    @Value("${quotesPath}")
    private String quotesPath;
}
