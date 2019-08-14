package info.mschmitt.shop.core.network.firebase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import info.mschmitt.shop.core.network.RxJava2ErrorCallAdapterFactory;
import okhttp3.*;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * @author Matthias Schmitt
 */
public class FirebaseServiceFactory {
    private static final String API_KEY = "AIzaSyBhfQrEexLAvIBO3vd4fkwZu9i3u1KP3ek";
    private final Gson gson;
    private final OkHttpClient httpClient;

    public FirebaseServiceFactory(OkHttpClient httpClient) {
        this.httpClient = httpClient;
        gson = new GsonBuilder().create();
    }

    private static Response addApiKeyQueryParam(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url().newBuilder().addQueryParameter("key", API_KEY).build();
        // Request customization: add request headers
        return chain.proceed(request.newBuilder().url(url).build());
    }

    public IdentityToolkitService createIdentityToolkitService() {
        OkHttpClient httpClient =
                this.httpClient.newBuilder().addInterceptor(FirebaseServiceFactory::addApiKeyQueryParam).build();
        RxJava2ErrorCallAdapterFactory callAdapterFactory =
                new RxJava2ErrorCallAdapterFactory(RxJava2CallAdapterFactory.createAsync(),
                        this::mapIdentityToolkitError);
        return new Retrofit.Builder().addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .baseUrl(IdentityToolkitService.BASE_URL)
                .build()
                .create(IdentityToolkitService.class);
    }

    public SecureTokenService createSecureTokenService() {
        OkHttpClient httpClient =
                this.httpClient.newBuilder().addInterceptor(FirebaseServiceFactory::addApiKeyQueryParam).build();
        RxJava2ErrorCallAdapterFactory callAdapterFactory =
                new RxJava2ErrorCallAdapterFactory(RxJava2CallAdapterFactory.createAsync(),
                        this::mapSecureTokenException);
        return new Retrofit.Builder().addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .baseUrl(SecureTokenService.BASE_URL)
                .build()
                .create(SecureTokenService.class);
    }

    private Throwable mapIdentityToolkitError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return new IdentityToolkitServiceException();
        }
        return throwable;
    }

    private Throwable mapSecureTokenException(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return new SecureTokenServiceException();
        }
        return throwable;
    }
}
