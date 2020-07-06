package com.dreamgyf.dim.utils;

import com.dreamgyf.dim.base.http.HttpConverterFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

public class RetrofitUtils {

	public static <T> T createApi(Class<T> clz, String url, String token) {
		OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
		httpClientBuilder.addInterceptor((chain) -> {
			Request original = chain.request();
			Request request = original.newBuilder()
					.header("token",token)
					.method(original.method(),original.body())
					.build();
			return chain.proceed(request);
		});
		OkHttpClient client = httpClientBuilder.build();
		return new Retrofit.Builder()
				.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
				.addConverterFactory(HttpConverterFactory.create())
				.baseUrl(url)
				.client(client)
				.build()
				.create(clz);
	}
}
