package dzung.trie.spell_checker.repository;

import dzung.trie.spell_checker.document.WordDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends MongoRepository<WordDocument, String> {
    @Query("{'key' : ?0}")
    WordDocument findByKey(String key);
}
