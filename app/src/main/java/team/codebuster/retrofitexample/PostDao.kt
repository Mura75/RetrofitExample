package team.codebuster.retrofitexample

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Post>)

    @Query("SELECT * FROM post_table")
    fun getAll(): List<Post>
}