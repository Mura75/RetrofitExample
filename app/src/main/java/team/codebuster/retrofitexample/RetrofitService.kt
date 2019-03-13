package team.codebuster.retrofitexample

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object RetrofitService {

    const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    fun getPostApi(): PostApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .build()
        return retrofit.create(PostApi::class.java)
    }
}

interface PostApi {

    @GET("posts")
    fun getPostList(): Call<List<Post>>

    @GET("posts")
    fun getPostCoroutine(): Deferred<List<Post>>
}