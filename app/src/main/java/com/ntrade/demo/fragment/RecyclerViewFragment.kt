package com.ntrade.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ntrade.demo.adapter.VBAAdapter
import com.ntrade.demo.databinding.FragmentLayoutBinding

/** * 创建者：leiwu
 * * 时间：2022/6/10 10:04
 * * 类描述：
 * * 修改人：
 * * 修改时间：
 * * 修改备注：
 */
class RecyclerViewFragment : Fragment() {

    val binding by lazy { FragmentLayoutBinding.inflate(layoutInflater) }
    val list = ArrayList<String>()
    val mAdapter by lazy { VBAAdapter(list) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onStart() {
        super.onStart()

        for (x in 0..1000) {
            list.add("$x")
        }
        binding.flRecyclerView.adapter = mAdapter
    }

}