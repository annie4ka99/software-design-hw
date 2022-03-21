package handler;

import db.CatalogRxMongoDriver;
import rx.Observable;

import java.util.List;
import java.util.Map;

public interface CatalogMethod {
    Observable<String> execute(CatalogRxMongoDriver driverCatalog,
                               Map<String, List<String>> queryParameters);
}
