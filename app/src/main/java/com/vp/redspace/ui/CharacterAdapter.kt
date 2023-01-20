package com.vp.redspace.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vp.redspace.BR
import com.vp.redspace.R
import com.vp.redspace.databinding.ItemListCharactersBinding
import com.vp.redspace.databinding.ItemLoadingBinding
import com.vp.redspace.models.Results

class CharacterAdapter(private val activity: MainActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), PaginationAdapterCallback {

    private val item: Int = 0
    private val loading: Int = 1

    private var isLoadingAdded: Boolean = false
    private var retryPageLoad: Boolean = false

    private var errorMsg: String? = ""

    private var characterModels: MutableList<Results> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == item) {
            val binding: ItemListCharactersBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_list_characters,
                parent,
                false
            )
            CharactersVH(binding)
        } else {
            val binding: ItemLoadingBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_loading,
                parent,
                false
            )
            LoadingVH(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = characterModels[position]
        if (getItemViewType(position) == item) {
            val myOrderVH: CharactersVH = holder as CharactersVH
            myOrderVH.bind(model)
        } else {
            val loadingVH: LoadingVH = holder as LoadingVH
            if (retryPageLoad) {
                loadingVH.itemRowBinding.loadmoreErrorlayout.visibility = View.VISIBLE
                loadingVH.itemRowBinding.loadmoreProgress.visibility = View.GONE

                if (errorMsg != null) loadingVH.itemRowBinding.loadmoreErrortxt.text = errorMsg
                else loadingVH.itemRowBinding.loadmoreErrortxt.text =
                    activity.getString(R.string.error_msg_unknown)

            } else {
                loadingVH.itemRowBinding.loadmoreErrorlayout.visibility = View.GONE
                loadingVH.itemRowBinding.loadmoreProgress.visibility = View.VISIBLE
            }

            loadingVH.itemRowBinding.loadmoreRetry.setOnClickListener {
                showRetry(false, "")
                retryPageLoad()
            }
            loadingVH.itemRowBinding.loadmoreErrorlayout.setOnClickListener {
                showRetry(false, "")
                retryPageLoad()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (characterModels.size > 0) characterModels.size else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            item
        } else {
            if (position == characterModels.size - 1 && isLoadingAdded) {
                loading
            } else {
                item
            }
        }
    }

    class CharactersVH(binding: ItemListCharactersBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemListCharactersBinding = binding
        fun bind(obj: Any?) {
            Glide.with(itemRowBinding.root.context).load((obj as Results).image)
                .into(itemRowBinding.imgChar)
            itemRowBinding.setVariable(BR.characters, obj)
            itemRowBinding.executePendingBindings()
        }
    }

    class LoadingVH(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemLoadingBinding = binding
    }

    override fun retryPageLoad() {
        activity.loadNextPage()
    }

    fun showRetry(show: Boolean, errorMsg: String) {
        retryPageLoad = show
        notifyItemChanged(characterModels.size - 1)
        this.errorMsg = errorMsg
    }

    fun addAll(characters: MutableList<Results>) {
        for (character in characters) {
            add(character)
        }
    }

    fun add(characters: Results) {
        characterModels.add(characters)
        notifyItemInserted(characterModels.size - 1)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Results())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position: Int = characterModels.size - 1
        val movie: Results = characterModels[position]

        if (movie != null) {
            characterModels.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
