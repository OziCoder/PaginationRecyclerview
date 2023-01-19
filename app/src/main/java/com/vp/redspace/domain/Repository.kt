package com.vp.redspace.domain

import androidx.lifecycle.MutableLiveData

interface Repository {
    fun loadCharacters(characterResponse: MutableLiveData<Any>, page : Int)
}