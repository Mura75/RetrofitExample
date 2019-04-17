package team.codebuster.retrofitexample

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel : ViewModel() {

    val liveData = MutableLiveData<PostResult>()

    val postRepository: PostRepository = PostRepositoryImpl()

    private val myJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + myJob)

    fun getPosts() {
        liveData.value = PostResult.ShowLoading
        uiScope.launch {
            val postList = withContext(Dispatchers.IO) {
                postRepository.getPosts().await()
            }
            liveData.value = PostResult.PostList(postList)
            liveData.value = PostResult.HideLoading
        }
//        RetrofitService.getPostApi().getPostList().enqueue(object : Callback<List<Post>> {
//            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
//                liveData.value = PostResult.HideLoading
//                liveData.value = PostResult.Error(t.toString())
//            }
//
//            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
//                Log.d("Mvvm_post_list", response.body().toString())
//                val list = response.body()
//                liveData.value = PostResult.PostList(list)
//                liveData.value = PostResult.HideLoading
//            }
//        })

    }

    override fun onCleared() {
        super.onCleared()
        myJob.cancel()
    }
}


sealed class PostResult {
    object ShowLoading: PostResult()
    object HideLoading: PostResult()
    data class PostList(val list: List<Post>?): PostResult()
    data class Error(val error : String): PostResult()
}