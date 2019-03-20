package team.codebuster.retrofitexample

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), PostAdapter.RecyclerViewItemClick {

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar

    private var postAdapter: PostAdapter? = null

    private val myJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + myJob)

    private lateinit var viewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PostViewModel::class.java)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        postAdapter = PostAdapter(itemClickListener = this)
        recyclerView.adapter = postAdapter

        progressBar = findViewById(R.id.progressBar)

        viewModel.getPosts()
        viewModel.liveData.observe(this, Observer { result ->
            when(result) {
                is PostResult.ShowLoading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is PostResult.HideLoading -> {
                    progressBar.visibility = View.GONE
                }
                is PostResult.PostList -> {
                    postAdapter?.list = result.list
                    postAdapter?.notifyDataSetChanged()
                }
                is PostResult.Error -> {}
            }
        })
    }

    override fun itemClick(position: Int, item: Post) {
        Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        myJob.cancel()
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
