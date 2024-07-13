package redis_data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.util.List;

public class Main {
    public Jedis getRedis() {
        String redisHost = "127.0.0.1";
        int redisPort = 6379;
        Jedis conn = new Jedis(redisHost, redisPort);

        return conn;
    }

    public static void insertDataToRedis(Jedis conn, MongoCursor<Document> cursor) {
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String key = doc.getString("key");

            if (conn.exists(key) && !conn.type(key).equals("zset")) {
                conn.del(key);
            }

            List<Document> res = (List<Document>) doc.get("value");
            for (Document ans : res) {
                String suggestion = ans.getString("suggestion");
                double score = Double.parseDouble(ans.getInteger("score") + "");
                conn.zadd(key, score, suggestion);
            }
        }
    }

    public static void main(String[] args) {
        Jedis conn = new Main().getRedis();

        String dbUri = "mongodb://localhost:27017";
        String dbName = "data";
        String wordDocuments = "word_documents";
        String quoteDocuments = "quote_documents";
        MongoClient client = MongoClients.create(dbUri);
        MongoCollection<Document> wordCollection = client.getDatabase(dbName).getCollection(wordDocuments);
        MongoCollection<Document> quoteCollection = client.getDatabase(dbName).getCollection(quoteDocuments);

        MongoCursor<Document> cursor = wordCollection.find().iterator();
        insertDataToRedis(conn, cursor);
        cursor = quoteCollection.find().iterator();
        insertDataToRedis(conn, cursor);
    }
}
