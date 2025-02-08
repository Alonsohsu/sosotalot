package com.example.sosotalot.ui.bug

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sosotalot.databinding.FragmentBugBinding

class BugFragment : Fragment() {

    private var _binding: FragmentBugBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBugBinding.inflate(inflater, container, false)
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        // 这里可以添加一些事件监听器，比如按钮点击等
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 这里可以执行一些初始化工作，比如从网络加载数据等
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 清除对绑定类的引用以防止内存泄漏
    }
}
