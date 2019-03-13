package team.codebuster.retrofitexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), PostAdapter.RecyclerViewItemClick {

    lateinit var recyclerView: RecyclerView

    private var postAdapter: PostAdapter? = null

    private val myJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + myJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        postAdapter = PostAdapter(itemClickListener = this)
        recyclerView.adapter = postAdapter

        getPost2()
    }

    override fun itemClick(position: Int, item: Post) {
        Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        myJob.cancel()
    }

    private fun getPosts() {
        RetrofitService.getPostApi().getPostList().enqueue(object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("My_post_list", response.body().toString())
                val list = response.body()
                postAdapter?.list = list
                postAdapter?.notifyDataSetChanged()
            }
        })
    }

    private fun getPost2() {
        uiScope.launch {
            val list = withContext(Dispatchers.IO) {
                RetrofitService.getPostApi().getPostCoroutine()
            }.await()
            postAdapter?.list = list
            postAdapter?.notifyDataSetChanged()
        }
    }
}
