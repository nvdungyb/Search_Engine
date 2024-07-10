package dzung.trie.spell_checker.repository;

import dzung.trie.spell_checker.document.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseRepository extends MongoRepository<Document, String> {
}
