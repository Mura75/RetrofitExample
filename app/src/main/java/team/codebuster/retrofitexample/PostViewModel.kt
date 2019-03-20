package team.codebuster.retrofitexample

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel : ViewModel() {

    val liveData = MutableLiveData<PostResult>()

    fun getPosts() {
        liveData.value = PostResult.ShowLoading
        RetrofitService.getPostApi().getPostList().enqueue(object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                liveData.value = PostResult.HideLoading
                liveData.value = PostResult.Error(t.toString())
            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("Mvvm_post_list", response.body().toString())
                val list = response.body()
                liveData.value = PostResult.PostList(list)
                liveData.value = PostResult.HideLoading
            }
        })
    }
}


sealed class PostResult {
    object ShowLoading: PostResult()
    object HideLoading: PostResult()
    data class PostList(val list: List<Post>?): PostResult()
    data class Error(val error : String): PostResult()
}