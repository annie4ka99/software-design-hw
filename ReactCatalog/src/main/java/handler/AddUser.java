package handler;

import db.CatalogRxMongoDriver;
import model.Currency;
import model.User;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class AddUser implements CatalogMethod {
    @Override
    public Observable<String> execute(CatalogRxMongoDriver catalogDriver,
                                      Map<String, List<String>> queryParameters) {
        User user = new User(
                Integer.parseInt(queryParameters.get(User.ID).get(0)),
                queryParameters.get(User.NAME).get(0),
                queryParameters.get(User.LOGIN).get(0),
                Currency.getCurrencyByCode(queryParameters.get(User.CURRENCY).get(0)));
        return catalogDriver.addUser(user).map(success -> "added user:\n" + user);
    }
}
