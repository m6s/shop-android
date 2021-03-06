package info.mschmitt.shop.core.network;

import info.mschmitt.shop.core.network.messages.RefreshIdTokenResponseBody;
import info.mschmitt.shop.core.util.HttpExceptionMapping;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author Matthias Schmitt
 */
public interface SecureTokenService {
    String BASE_URL = "https://securetoken.googleapis.com/v1/";
    String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    /**
     * Refresh a Firebase ID token
     */
    //@formatter:off
    //curl 'https://securetoken.googleapis.com/v1/token?key=AIzaSyBhfQrEexLAvIBO3vd4fkwZu9i3u1KP3ek' -H 'Content-Type: application/x-www-form-urlencoded' --data 'grant_type=refresh_token&refresh_token=xyz'
    //@formatter:on
    @POST("./token")
    @FormUrlEncoded
    @HttpExceptionMapping(errorCode = 400, exceptionClass = FirebaseServiceException.class)
    Single<RefreshIdTokenResponseBody> refreshIdToken(@Field("grant_type") String grantType,
                                                      @Field("refresh_token") String refreshToken);
}
