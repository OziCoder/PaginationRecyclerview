package com.vp.redspace.data.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.vp.redspace.data.remote.ApiInterface
import com.vp.redspace.domain.Repository
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiInterface: ApiInterface,
    private val appContext: Application
): Repository {
    override fun loadCharacters(characterResponse: MutableLiveData<Any>, page: Int) {
        TODO("Not yet implemented")
    }
}