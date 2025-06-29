package com.example.foodbridge.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodbridge.data.Product
import com.example.foodbridge.databinding.FoodCardBinding

class NearYouAdapter : RecyclerView.Adapter<NearYouAdapter.NearYouViewHolder>() {

    inner class NearYouViewHolder(private val binding: FoodCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(foodItem: Product) {
            binding.apply {
                tvFoodName.text = foodItem.name
                tvVenue.text = foodItem.location.toString()

                Glide.with(itemView)
                    .load(foodItem.images)
                    .into(imgFood)

                btnDetails.setOnClickListener {
                    onItemClickListener?.let { click ->
                        click(foodItem)
                    }
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearYouViewHolder {
        return NearYouViewHolder(
            FoodCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NearYouViewHolder, position: Int) {
        val foodItem = differ.currentList[position]
        holder.bind(foodItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Product) -> Unit)? = null

    fun setOnItemClickListener(listener: (Product) -> Unit) {
        onItemClickListener = listener
    }
}