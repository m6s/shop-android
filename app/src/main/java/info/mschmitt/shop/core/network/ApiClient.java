package info.mschmitt.shop.core.network;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import info.mschmitt.shop.app.BuildConfig;
import info.mschmitt.shop.core.storage.Article;
import info.mschmitt.shop.core.storage.DataStore;
import io.reactivex.Single;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author Matthias Schmitt
 */
public class ApiClient {
    private static final int MBYTE = 1024 * 1024;
    private final ShopService shopService;
    private final IdentityToolkitService identityToolkitService;
    private final SecureTokenService secureTokenService;
    private final DataStore dataStore;
    private final UserAgent userAgent;

    public ApiClient(ShopService shopService, IdentityToolkitService identityToolkitService,
                     SecureTokenService secureTokenService) {
        this.shopService = shopService;
        this.identityToolkitService = identityToolkitService;
        this.secureTokenService = secureTokenService;
        dataStore = null;
        userAgent = new UserAgent();
    }

    public ApiClient(OkHttpClient httpClient, DataStore dataStore, UserAgent userAgent) {
        this.dataStore = dataStore;
        this.userAgent = userAgent;
        FirebaseServiceFactory firebaseServiceFactory = new FirebaseServiceFactory(httpClient);
        identityToolkitService = firebaseServiceFactory.createIdentityToolkitService();
        secureTokenService = firebaseServiceFactory.createSecureTokenService();
        shopService = new ShopServiceFactory(httpClient, secureTokenService, dataStore,
                userAgent).createShopService();
    }

    public static OkHttpClient createHttpClient(File cacheDir) {
        Cache cache = new Cache(cacheDir, 10 * MBYTE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addNetworkInterceptor(loggingInterceptor);
        }
        return builder.build();
    }

    public Single<List<Article>> getArticles() {
        // TODO Fetch from network using shopService. Adapt in/out parameters and propagate HTTP
        //  errors to throwables.
        List<Article> articles = new ArrayList<>();
        Article article = new Article();
        article.name = "Article A";
        articles.add(article);
        article = new Article();
        article.name = "Article B";
        articles.add(article);
        article = new Article();
        article.name = "Article C";
        articles.add(article);
        return Single.just(articles).delay(2, TimeUnit.SECONDS);
    }
}
