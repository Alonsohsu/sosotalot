import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sosotalot.databinding.FragmentLayoutSelectionBinding


class LayoutSelectionFragment : Fragment() {

    private var _binding: FragmentLayoutSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLayoutSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layouts = arguments?.getStringArrayList("layoutsKey") ?: listOf()
        // 在这里更新 UI 以显示推荐的阵型列表
        binding.layoutDescription.text = "推荐阵型：\n" + layouts.joinToString("\n")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

