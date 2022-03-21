package handler;

import db.CatalogRxMongoDriver;
import model.Currency;
import model.Product;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class AddProduct implements CatalogMethod {
    @Override
    public Observable<String> execute(CatalogRxMongoDriver catalogDriver,
                                      Map<String, List<String>> queryParameters) {
        Product product = new Product(
                Integer.parseInt(queryParameters.get(Product.ID).get(0)),
                queryParameters.get(Product.NAME).get(0),
                Double.parseDouble(queryParameters.get(Product.PRICE).get(0)),
                Currency.getCurrencyByCode(queryParameters.get(Product.CURRENCY).get(0)));
        return catalogDriver.addProduct(product).map(success -> "added product:\n" + product);
    }
}
