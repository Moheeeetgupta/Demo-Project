package com.demo.demoproject.repo

import com.demo.demoproject.api.ApiService
import com.demo.demoproject.datastore.DataStoreManager
import com.demo.demoproject.localdb.Dao
import com.demo.demoproject.model.Post

class MainRepository(
    private val api: ApiService,
    private val dao: Dao,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getPost(): List<Post> {
        val data = api.getPosts()
        if (data.isNotEmpty()) {
            insert(data)
        }
        return data
    }
    suspend fun getAllLocalPosts() = dao.getAllPosts()
    private suspend fun insert(posts: List<Post>) = dao.insert(posts)
    suspend fun delete(post: Post) = dao.delete(post)

}