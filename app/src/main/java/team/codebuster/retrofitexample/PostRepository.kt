package team.codebuster.retrofitexample

import kotlinx.coroutines.Deferred

interface PostRepository {
    fun getPosts(): Deferred<List<Post>>
}

class PostRepositoryImpl: PostRepository {

    override fun getPosts(): Deferred<List<Post>> {
        return RetrofitService.getPostApi().getPostCoroutine()
    }

}