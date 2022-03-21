package db;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.Success;
import model.Product;
import model.User;
import org.bson.Document;
import rx.Observable;

import static com.mongodb.client.model.Filters.eq;


public class CatalogRxMongoDriver {
    private static final String DB_NAME = "rxcatalog";
    private static final String USERS_COLLECTION = "users";
    private static final String PRODUCTS_COLLECTION = "products";

    private static MongoClient client = createMongoClient();

    public Observable<Success> addUser(User user) {
        return getUserCollection().insertOne(user.toDocument());
    }

    public Observable<Success> addProduct(Product product) {
        return getProductCollection().insertOne(product.toDocument());
    }

    public Observable<Product> getAllProducts() {
        return getProductCollection().find().toObservable().map(Product::new);
    }

    public Observable<User> getUser(int id) {
        return getUserCollection().find(eq(User.ID, id)).toObservable().map(User::new);
    }

    private MongoCollection<Document> getUserCollection() {
        return client.getDatabase(DB_NAME).getCollection(USERS_COLLECTION);
    }

    private MongoCollection<Document> getProductCollection() {
        return client.getDatabase(DB_NAME).getCollection(PRODUCTS_COLLECTION);
    }

    private static MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
}

