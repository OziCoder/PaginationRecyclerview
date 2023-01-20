package com.vp.redspace.data.remote

import com.vp.redspace.models.Characters
import com.vp.redspace.utils.AppConstants.METHOD_CHARACTER
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET(METHOD_CHARACTER)
    fun getCharacters(@Query("page") pageIndex: Int): Call<Characters>
}