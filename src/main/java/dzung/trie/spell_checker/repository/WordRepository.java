package dzung.trie.spell_checker.repository;

import dzung.trie.spell_checker.document.Document;
import dzung.trie.spell_checker.document.WordDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends MongoRepository<WordDocument, String> {
    @Query("{'key' : ?0}")
    List<Document> findByKey(String key);
}
