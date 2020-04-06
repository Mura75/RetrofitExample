package team.codebuster.retrofitexample.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import team.codebuster.retrofitexample.model.Post
import team.codebuster.retrofitexample.model.PostDao
import team.codebuster.retrofitexample.model.PostDatabase
import team.codebuster.retrofitexample.model.RetrofitService
import kotlin.coroutines.CoroutineContext

class PostListViewModel(
    private val context: Context
) : ViewModel(), CoroutineScope {

    private val job = Job()

    private val postDao: PostDao

    val liveData = MutableLiveData<State>()

    init {
        postDao = PostDatabase.getDatabase(context = context).postDao()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getPosts() {
        launch {
            liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getPostApi().getPostListCoroutine()
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (!result.isNullOrEmpty()) {
                            postDao.insertAll(result)
                        }
                        result
                    } else {
                        postDao.getAll() ?: emptyList()
                    }
                } catch (e: Exception) {
                    postDao.getAll() ?: emptyList<Post>()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list)
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<Post>?) : State()
    }
}