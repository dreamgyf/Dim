package com.dreamgyf.dim.base.http;

import com.dreamgyf.dim.base.http.exception.HttpRespException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

public final class HttpConverterFactory extends Converter.Factory {

	public static HttpConverterFactory create() {
		return create(new Gson());
	}

	public static HttpConverterFactory create(Gson gson) {
		if (gson == null) throw new NullPointerException("gson == null");
		return new HttpConverterFactory(gson);
	}

	private final Gson gson;

	private HttpConverterFactory(Gson gson) {
		this.gson = gson;
	}

	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
		TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
		return new HttpResponseBodyConverter<>(gson, adapter);
	}

	@Override
	public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
		TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
		return new HttpRequestBodyConverter<>(gson, adapter);
	}

	static final class HttpResponseBodyConverter<T> implements Converter<ResponseBody, T> {
		private final Gson gson;
		private final TypeAdapter<T> adapter;

		HttpResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
			this.gson = gson;
			this.adapter = adapter;
		}

		@Override
		public T convert(ResponseBody value) throws IOException {
			JsonObject root = new JsonParser().parse(value.charStream()).getAsJsonObject();
			String code = root.get("code").getAsString();
			if (!"0".equals(code)) {
				String message = root.get("msg").getAsString();
				throw new HttpRespException(message);
			}
			JsonElement dataJson = root.get("data");
			JsonReader jsonReader = gson.newJsonReader(new StringReader(dataJson.toString()));
			try {
				T result = adapter.read(jsonReader);
				if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
					throw new JsonIOException("JSON document was not fully consumed.");
				}
				return result;
			} finally {
				value.close();
			}
		}
	}

	static final class HttpRequestBodyConverter<T> implements Converter<T, RequestBody> {
		private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");
		private static final Charset UTF_8 = Charset.forName("UTF-8");

		private final Gson gson;
		private final TypeAdapter<T> adapter;

		HttpRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
			this.gson = gson;
			this.adapter = adapter;
		}

		@Override
		public RequestBody convert(T value) throws IOException {
			Buffer buffer = new Buffer();
			Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
			JsonWriter jsonWriter = gson.newJsonWriter(writer);
			adapter.write(jsonWriter, value);
			jsonWriter.close();
			return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
		}
	}

}
