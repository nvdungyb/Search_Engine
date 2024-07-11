package benchmark;

import dzung.trie.spell_checker.repository.QuoteRepository;
import dzung.trie.spell_checker.repository.WordRepository;
import org.springframework.stereotype.Component;

@Component
public class MyService {
    private WordRepository wordRepository;
    private QuoteRepository quoteRepository;
}
