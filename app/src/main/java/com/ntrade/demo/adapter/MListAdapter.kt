package com.ntrade.demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ntrade.demo.databinding.ItemViewBinding

/** * 创建者：leiwu
 * * 时间：2022/6/10 10:20
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class MListAdapter(val list: ArrayList<String>, val onItemClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<MListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        ItemViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            list[position].apply {
                item_tv.text = this
            }
            binding.root.setOnClickListener {
                onItemClickListener.invoke(position)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val item_tv: TextView = binding.itemTv
    }
}