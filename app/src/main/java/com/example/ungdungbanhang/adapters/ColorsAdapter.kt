package com.example.ungdungbanhang.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ungdungbanhang.databinding.ColorRvItemBinding

class ColorsAdapter:RecyclerView.Adapter<ColorsAdapter.ColorsViewHodler>() {

    private var selectedPosition = -1

    inner class ColorsViewHodler(private val binding: ColorRvItemBinding) :
            ViewHolder(binding.root){
                fun bind(color: Int, position: Int){
                    val imageDrawable = ColorDrawable(color)
                    binding.imageColor.setImageDrawable(imageDrawable)
                    // mau dang duoc chon
                    if(position == selectedPosition){
                        binding.apply {
                            imageShadow.visibility = View.VISIBLE
                            imagePicked.visibility = View.VISIBLE
                        }
                    }else{
                        // mau khong duoc chon
                        binding.apply {
                            imageShadow.visibility = View.INVISIBLE
                            imagePicked.visibility = View.INVISIBLE
                        }
                    }
                }
            }

    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHodler {
        return ColorsViewHodler(
            ColorRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ColorsViewHodler, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color,position)

        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0)
                notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(color)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((Int) -> Unit)? = null
}