package handler;

import db.CatalogRxMongoDriver;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class CatalogQueryHandler {
    private static CatalogRxMongoDriver catalogDriver = new CatalogRxMongoDriver();

    public Observable<String> handleQuery(String query, Map<String, List<String>> queryParameters) {
        return getMethodByName(query).execute(catalogDriver, queryParameters);
    }

    private CatalogMethod getMethodByName(String request) {
        switch (request) {
            case "add-user":
                return new AddUser();
            case "add-product":
                return new AddProduct();
            case "get-products":
                return new GetProducts();
            default:
                throw new RuntimeException("Unknown method");
        }
    }
}
