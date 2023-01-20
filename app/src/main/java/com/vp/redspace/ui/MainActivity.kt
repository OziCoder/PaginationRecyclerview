package com.vp.redspace.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vp.redspace.R
import com.vp.redspace.databinding.ActivityMainBinding
import com.vp.redspace.models.Characters
import com.vp.redspace.models.Results
import com.vp.redspace.utils.AppUtils
import com.vp.redspace.utils.PaginationListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeoutException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var mAdapter: CharacterAdapter
    private val pageStart: Int = 1
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 1
    private var currentPage: Int = pageStart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
        observeResult()
        binding.lyError.errorBtnRetry.setOnClickListener {
            if (currentPage == pageStart) {
                loadFirstPage()
            } else {
                loadNextPage()
            }
        }
    }

    private fun initRecyclerView() {
        mAdapter = CharacterAdapter(this@MainActivity)
        binding.adapterChatracters = mAdapter
        binding.recyclerCharacters.setHasFixedSize(true)
        binding.recyclerCharacters.itemAnimator = DefaultItemAnimator()
        loadFirstPage()

        binding.recyclerCharacters.addOnScrollListener(object :
            PaginationListener(binding.recyclerCharacters.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                Handler(Looper.myLooper()!!).postDelayed({
                    loadNextPage()
                }, 1000)
            }

            override fun getTotalPageCount(): Int {
                return totalPages
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })
    }

    private fun observeResult() {
        viewModel.firstcharacterResponses.observe(this) {
            if (it is Characters) {
                hideErrorView()
                val results: MutableList<Results> = it.results
                binding.mainProgress.visibility = View.GONE
                mAdapter.addAll(results)
                totalPages = it.info?.pages!!

                if (currentPage <= totalPages) mAdapter.addLoadingFooter()
                else isLastPage = true
            } else if (it is Throwable) {
                showErrorView(it)
            } else {
                Snackbar.make(
                    this.findViewById(android.R.id.content),
                    getString(R.string.error_msg_unknown),
                    Snackbar.LENGTH_LONG
                )
                    .setTextColor(Color.WHITE).setBackgroundTint(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.snack_red
                        )
                    )
                    .show()
            }
        }
        viewModel.nextcharacterResponses.observe(this) {
            if (it is Characters) {

                val results: MutableList<Results> = it.results
                mAdapter.removeLoadingFooter()
                isLoading = false
                mAdapter.addAll(results)

                if (currentPage != totalPages) mAdapter.addLoadingFooter()
                else isLastPage = true

            } else if (it is Throwable) {
                mAdapter.showRetry(true, fetchErrorMessage(it))
            } else {
                Snackbar.make(
                    this.findViewById(android.R.id.content),
                    getString(R.string.error_msg_unknown),
                    Snackbar.LENGTH_LONG
                )
                    .setTextColor(Color.WHITE).setBackgroundTint(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.snack_red
                        )
                    )
                    .show()
            }
        }
    }

    private fun loadFirstPage() {
        hideErrorView()
        if (AppUtils.isNetworkConnected(this)) {
            viewModel.getFirstCharacterResponses(currentPage)
        } else {
            showErrorView(null)
        }
    }

    fun loadNextPage() {
        if (AppUtils.isNetworkConnected(this)) {
            viewModel.getNextCharacterResponses(currentPage)
        } else {
            mAdapter.showRetry(true, fetchErrorMessage(null))
        }
    }

    private fun hideErrorView() {
        if (binding.lyError.errorLayout.visibility == View.VISIBLE) {
            binding.lyError.errorLayout.visibility = View.GONE
            binding.mainProgress.visibility = View.VISIBLE
        }
    }

    private fun showErrorView(throwable: Throwable?) {
        if (binding.lyError.errorLayout.visibility == View.GONE) {
            binding.lyError.errorLayout.visibility = View.VISIBLE
            binding.mainProgress.visibility = View.GONE

            if (!AppUtils.isNetworkConnected(this)) {
                binding.lyError.errorTxtCause.setText(R.string.error_msg_no_internet)
            } else {
                if (throwable is TimeoutException) {
                    binding.lyError.errorTxtCause.setText(R.string.error_msg_timeout)
                } else {
                    binding.lyError.errorTxtCause.setText(R.string.error_msg_unknown)
                }
            }
        }
    }

    private fun fetchErrorMessage(throwable: Throwable?): String {
        var errorMsg: String = resources.getString(R.string.error_msg_unknown)

        if (!AppUtils.isNetworkConnected(this)) {
            errorMsg = resources.getString(R.string.error_msg_no_internet)
        } else if (throwable is TimeoutException) {
            errorMsg = resources.getString(R.string.error_msg_timeout)
        }

        return errorMsg
    }
}