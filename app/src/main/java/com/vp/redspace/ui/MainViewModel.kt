package com.vp.redspace.ui
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vp.redspace.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository): ViewModel(){

    private var _firstcharacterResponses = MutableLiveData<Any>()
    private var _nextcharacterResponses = MutableLiveData<Any>()

    val firstcharacterResponses : LiveData<Any>
        get() = _firstcharacterResponses

    val nextcharacterResponses : LiveData<Any>
        get() = _nextcharacterResponses

    fun getFirstCharacterResponses(pageIndex: Int){
        repository.loadCharacters(_firstcharacterResponses, pageIndex)
    }

    fun getNextCharacterResponses(pageIndex: Int){
        repository.loadCharacters(_nextcharacterResponses, pageIndex)
    }
}