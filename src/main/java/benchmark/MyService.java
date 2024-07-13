package benchmark;

import com.dzung.search_engine.repository.QuoteRepository;
import com.dzung.search_engine.repository.WordRepository;
import org.springframework.stereotype.Component;

@Component
public class MyService {
    private WordRepository wordRepository;
    private QuoteRepository quoteRepository;
}
