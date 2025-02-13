package com.example.sosotalot.ui.tarot

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentQuestionInputBinding
import com.example.sosotalot.network.OpenAIService
import com.example.sosotalot.utils.NetworkUtils
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.launch

class QuestionInputFragment : Fragment() {
    private var _binding: FragmentQuestionInputBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tarotMasterId = arguments?.getInt("tarotMasterId") ?: -1

        val imageResource = when (tarotMasterId) {
            0 -> R.drawable.master_one_image
            1 -> R.drawable.master_two_image
            2 -> R.drawable.master_three_image
            3 -> R.drawable.master_four_image
            else -> R.drawable.not_found
        }

        binding.imageTop.setImageResource(imageResource)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionInputBinding.inflate(inflater, container, false)
        binding.submitQuestionButton.setOnClickListener {
            val question = binding.editTextQuestion.text.toString().trim()
            if (question.isNotEmpty() && NetworkUtils.isNetworkAvailable(requireContext())) {
                checkVersionAndProceed(question)
            } else {
                Toast.makeText(requireContext(), "请输入问题并连接网络", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun convertVersionToDouble(version: String): Double {
        val parts = version.split(".")
        return when (parts.size) {
            3 -> "${parts[0]}.${parts[1]}${parts[2]}".toDoubleOrNull() ?: 1.0
            2 -> "${parts[0]}.${parts[1]}".toDoubleOrNull() ?: 1.0
            1 -> parts[0].toDoubleOrNull() ?: 1.0
            else -> 1.0
        }
    }


    private fun checkVersionAndProceed(question: String) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0) // 每次都向 Firebase 取得最新數據
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetch(0) // 直接向 Firebase 伺服器獲取最新數據
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    remoteConfig.activate() // 立即啟用新數據
                    val minimumVersion = remoteConfig.getString("minimum_app_version") // 取得最新版本
                    val currentVersion = getCurrentAppVersion() // APP 目前的版本

                    Log.e("currentVersion", " $currentVersion")
                    Log.e("minimumVersion", " $minimumVersion")

                    if (isUpdateRequired(currentVersion, minimumVersion)) {
                        showUpdateDialog() // 強制更新
                    } else {
                        showLoading(true)
                        fetchAndNavigate(question)
                    }
                } else {
                    // Firebase 無法取得數據，仍允許用戶繼續使用
                    Log.e("RemoteConfig", "Firebase 取得數據失敗，使用快取數據")
                    showLoading(true)
                    fetchAndNavigate(question)
                }
            }
    }


    /**
     * 取得 APP 版本號 (String)
     */
    private fun getCurrentAppVersion(): String {
        return try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            packageInfo.versionName ?: "1.0.0"  // 預設為 "1.0.0"
        } catch (e: Exception) {
            "1.0.0" // 例外情況回傳預設值
        }
    }

    /**
     * 判斷是否需要更新版本
     * @param currentVersion 當前版本 (1.0.1)
     * @param latestVersion 最低要求版本 (1.0.2)
     * @return 若需要更新則回傳 true，否則回傳 false
     */
    private fun isUpdateRequired(currentVersion: String, latestVersion: String): Boolean {
        val currentParts = currentVersion.split(".").map { it.toIntOrNull() ?: 0 }
        val latestParts = latestVersion.split(".").map { it.toIntOrNull() ?: 0 }

        for (i in 0 until maxOf(currentParts.size, latestParts.size)) {
            val curr = currentParts.getOrElse(i) { 0 }
            val latest = latestParts.getOrElse(i) { 0 }
            if (curr < latest) return true // 當前版本小於最新版本，應更新
            if (curr > latest) return false // 當前版本較新，不需更新
        }
        return false // 版本相同，不需更新
    }

    /**
     * 顯示強制更新對話框
     */
    private fun showUpdateDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("需要更新")
            .setMessage("請更新至最新版本，以繼續使用本應用。")
            .setPositiveButton("更新") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireContext().packageName}"))
                startActivity(intent)
            }
            .setCancelable(false)
            .show()
    }

    /**
     * 取得塔羅解釋並導航
     */
    private fun fetchAndNavigate(question: String) {
        lifecycleScope.launch {
            showLoading(true) // 顯示加載狀態

            val responseJson = OpenAIService.fetchTarotData(
                context = requireContext(),
                question = question,
                meaning = "",
                type = OpenAIService.TarotRequestType.RECOMMENDED_LAYOUTS // ✅ 使用新 API
            )

            if (!responseJson.isNullOrEmpty()) {
                try {
                    val bundle = Bundle().apply {
                        putString("layoutsKeyJson", responseJson) // 直接傳遞 JSON 字符串
                        putString("question", question) // 直接傳遞 JSON 字符串
                    }
                    findNavController().navigate(R.id.action_questionInputFragment_to_layoutSelectionFragment, bundle)
                } catch (e: Exception) {
                    Log.e("fetchAndNavigate", "JSON 解析錯誤", e)
                    Toast.makeText(requireContext(), "數據解析失敗，請稍後再試", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "無法獲取推薦的塔羅陣型，請稍後再試", Toast.LENGTH_LONG).show()
            }

            showLoading(false) // 關閉加載狀態
        }
    }

    /**
     * 顯示或隱藏加載進度條
     */
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

