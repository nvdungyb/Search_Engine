package dzung.trie.spell_checker.repository;

import dzung.trie.spell_checker.document.QuoteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends MongoRepository<QuoteDocument, String> {
}
