//package benchmark;
//
//import com.dzung.search_engine.document.Document;
//import com.dzung.search_engine.document.QuoteDocument;
//import com.dzung.search_engine.document.WordDocument;
//import com.dzung.search_engine.repository.QuoteRepository;
//import com.dzung.search_engine.repository.WordRepository;
//import org.openjdk.jmh.annotations.*;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.context.ConfigurableApplicationContext;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@State(Scope.Benchmark)
//public class SuggestTest {
//
//    private WordRepository wordRepo;
//    private QuoteRepository quoteRepo;
//    private ConfigurableApplicationContext context;
//    private String word = "I have";
//
//    @Setup(Level.Trial)  // Initialize once per trial
//    public void init() {
//        context = new SpringApplicationBuilder(BenchmarkConfig.class)
//                .web(org.springframework.boot.WebApplicationType.NONE)
//                .run();
//
//        // Autowire the repositories manually
//        wordRepo = context.getBean(WordRepository.class);
//        quoteRepo = context.getBean(QuoteRepository.class);
//    }
//
//    @TearDown(Level.Trial)  // Clean up once per trial
//    public void tearDown() {
//        if (context != null) {
//            context.close();
//        }
//    }
//
//    public WordDocument wordSuggest(String word) {
//        String[] keys = word.split("\\s+");
//        String key = keys[keys.length - 1];
//
//        for (int i = keys.length - 1; i >= 0; i--) {
//            WordDocument doc = wordRepo.findByKey(key.substring(0, i + 1));
//            if (doc != null)
//                return doc;
//        }
//        return null;
//    }
//
//    public QuoteDocument quoteSuggest(String word) {
//        String[] keys = word.split("\\s+");
//
//        StringBuilder builder = new StringBuilder();
//        for (String key : keys) {
//            builder.append(key).append(" ");
//        }
//
//        int len = builder.length();
//        builder.delete(builder.length() - 1, len);
//        for (int i = keys.length - 1; i >= 0; i--) {
//            String key = builder.toString().trim();
//            List<QuoteDocument> docs = quoteRepo.findByKey(key);
//            if (docs.size() > 0)
//                return docs.stream().findFirst().orElse(null);
//
//            len = builder.length();
//            builder.delete(len - keys[i].length() - 1, len);
//        }
//        return null;
//    }
//
//    @Benchmark
//    @Fork(value = 1, warmups = 1)
//    public List<Document> suggest() {
//        String keyWord = word.toLowerCase().trim();
//        List<Document> suggests = new ArrayList<>();
//        suggests.add(wordSuggest(keyWord));
//        suggests.add(quoteSuggest(keyWord));
//
//        return suggests;
//    }
//}
