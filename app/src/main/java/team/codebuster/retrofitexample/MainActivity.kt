package team.codebuster.retrofitexample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), PostAdapter.RecyclerViewItemClick, CoroutineScope {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var postAdapter: PostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            postAdapter?.clearAll()
            getPostCoroutine()
        }

        postAdapter = PostAdapter(itemClickListener = this)
        recyclerView.adapter = postAdapter

        getPostCoroutine()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun itemClick(position: Int, item: Post) {
        val intent = Intent(this, PostDetailActivity::class.java)
        intent.putExtra("post_id", item.postId)
        startActivity(intent)
    }

    private fun getPosts() {
        RetrofitService.getPostApi().getPostList().enqueue(object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("My_post_list", response.body().toString())
                if (response.isSuccessful) {
                    val list = response.body()
                    postAdapter?.list = list
                    postAdapter?.notifyDataSetChanged()
                }
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun getPostCoroutine() {
        launch {
            swipeRefreshLayout.isRefreshing = true
            val response = RetrofitService.getPostApi().getPostListCoroutine()
            if (response.isSuccessful) {
                val list = response.body()
                postAdapter?.list = list
                postAdapter?.notifyDataSetChanged()
            } else {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }

}
