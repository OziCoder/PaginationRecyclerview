package com.vp.redspace.data.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.vp.redspace.data.remote.ApiInterface
import com.vp.redspace.domain.Repository
import com.vp.redspace.models.Characters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiInterface: ApiInterface,
    private val appContext: Application
) : Repository {
    override fun loadCharacters(characterResponse: MutableLiveData<Any>, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = apiInterface.getCharacters(page).enqueue(object : Callback<Characters> {
                override fun onResponse(call: Call<Characters>, response: Response<Characters>) {
                    if (response.isSuccessful) {
                        characterResponse.value = response.body()
                    } else {
                        characterResponse.value = "error"
                    }
                }

                override fun onFailure(call: Call<Characters>, t: Throwable) {
                    characterResponse.value = t
                }

            })
        }
    }
}