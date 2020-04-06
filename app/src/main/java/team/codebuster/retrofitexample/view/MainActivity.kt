package team.codebuster.retrofitexample.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.*
import team.codebuster.retrofitexample.R
import team.codebuster.retrofitexample.model.Post
import team.codebuster.retrofitexample.model.PostDao
import team.codebuster.retrofitexample.model.PostDatabase
import team.codebuster.retrofitexample.model.RetrofitService
import team.codebuster.retrofitexample.view_model.PostListViewModel
import team.codebuster.retrofitexample.view_model.ViewModelProviderFactory
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), PostAdapter.RecyclerViewItemClick {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var postAdapter: PostAdapter? = null

    private lateinit var postListViewModel: PostListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModelProviderFactory = ViewModelProviderFactory(context = this)
        postListViewModel = ViewModelProvider(this, viewModelProviderFactory)
            .get(PostListViewModel::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            postAdapter?.clearAll()
            postListViewModel.getPosts()
        }

        postAdapter = PostAdapter(itemClickListener = this)
        recyclerView.adapter = postAdapter

        postListViewModel.getPosts()
        postListViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is PostListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is PostListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is PostListViewModel.State.Result -> {
                    postAdapter?.list = result.list
                    postAdapter?.notifyDataSetChanged()
                }
            }
        })
    }

    override fun itemClick(position: Int, item: Post) {
        val intent = Intent(this, PostDetailActivity::class.java)
        intent.putExtra("post_id", item.postId)
        startActivity(intent)
    }

}
