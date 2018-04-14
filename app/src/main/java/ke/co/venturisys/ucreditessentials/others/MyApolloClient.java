package ke.co.venturisys.ucreditessentials.others;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by victor on 4/10/18.
 * Client for the apollo android library that is used to query and mutate GraphQL
 */

public class MyApolloClient {

    public static ApolloClient getMyApolloClient() {

        // intercepts and logs our request for viewing of request
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        String GRAPH_QL_BASE_URL = "https://api.graph.cool/simple/v1/cjfwcnsuj6dh801163hszyhsx";
        return ApolloClient.builder()
                .serverUrl(GRAPH_QL_BASE_URL)
                .okHttpClient(okHttpClient)
                .build();
    }

}
