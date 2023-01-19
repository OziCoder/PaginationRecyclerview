package com.vp.redspace.ui
import androidx.lifecycle.ViewModel
import com.vp.redspace.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository): ViewModel(){


}