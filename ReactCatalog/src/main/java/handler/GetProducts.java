package handler;

import db.CatalogRxMongoDriver;
import model.User;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class GetProducts implements CatalogMethod {
    @Override
    public Observable<String> execute(CatalogRxMongoDriver driverCatalog,
                                      Map<String, List<String>> queryParameters) {
        int id = Integer.parseInt(queryParameters.get(User.ID).get(0));

        return driverCatalog.getUser(id).map(User::getCurrency)
                .flatMap(currency -> driverCatalog.getAllProducts()
                        .map(product -> product.getName() + ": " + product.getPrice(currency) + '\n'));
    }
}
