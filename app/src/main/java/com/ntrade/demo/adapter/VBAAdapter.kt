package com.ntrade.demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ntrade.demo.databinding.ItemRvfLayoutBinding

/** * 创建者：leiwu
 * * 时间：2022/6/10 10:33
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class VBAAdapter (val list: ArrayList<String>) :
    RecyclerView.Adapter<VBAAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VBAAdapter.ViewHolder {
        return ViewHolder(
            ItemRvfLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VBAAdapter.ViewHolder, position: Int) {
        holder.apply {
            ivTv.text = "item - ${list[position]}"
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(binding: ItemRvfLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivTv: TextView = binding.itemTv
    }
}