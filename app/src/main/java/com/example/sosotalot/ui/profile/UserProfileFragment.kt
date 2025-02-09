package com.example.sosotalot.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sosotalot.BuildConfig
import com.example.sosotalot.databinding.FragmentUserProfileBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.android.play.core.appupdate.*
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var appUpdateManager: AppUpdateManager
    private val UPDATE_REQUEST_CODE = 123

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserProfile()

        // 設定檢查更新按鈕
        binding.checkUpdateButton.setOnClickListener {
            checkForUpdate()
        }
    }

    private fun setupUserProfile() {
        val prefs = requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)

        // 會員 ID
        val userId = prefs.getString("userId", prefs.getString("guestUserId", "Unknown")) ?: "Unknown"
        binding.memberID.text = userId

        // 暱稱
        val nickname = prefs.getString("nickname", "未定義") ?: "未定義"
        binding.userNickname.text = nickname

        // 用戶身份
        val userRole = prefs.getString("userRole", "普通用戶") ?: "普通用戶"
        binding.userRole.text = userRole

        // 認證狀態
        val verificationStatus = prefs.getString("verificationStatus", "未認證") ?: "未認證"
        binding.verificationStatus.text = verificationStatus

        // 背包物品數
        val backpackItems = prefs.getInt("backpackItems", 0)
        binding.backpackItems.text = backpackItems.toString()

        // 能量點
        val energyPoints = prefs.getInt("energyPoints", 0)
        binding.energyPoints.text = energyPoints.toString()

        // 星光點
        val starlightPoints = prefs.getInt("starlightPoints", 0)
        binding.starlightPoints.text = starlightPoints.toString()

        // 福報
        val fortunePoints = prefs.getInt("fortunePoints", 0)
        binding.fortunePoints.text = fortunePoints.toString()

        // 祝福能量
        val blessingEnergy = prefs.getInt("blessingEnergy", 0)
        binding.blessingEnergy.text = blessingEnergy.toString()

        val versionName = BuildConfig.VERSION_NAME
        binding.versionInfo.text = "版本資訊: v$versionName"

    }

    private fun checkForUpdate() {
        // 1️⃣ 初始化 Firebase Remote Config
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // 1 小時更新一次
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        // 2️⃣ 取得遠端最新版本號
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val latestVersion = remoteConfig.getString("latest_version")
                val currentVersion = BuildConfig.VERSION_NAME
                Log.d("UpdateCheck", "Firebase 最新版本: $latestVersion，本機版本: $currentVersion")

                if (latestVersion.isNotEmpty() && latestVersion != currentVersion) {
                    showUpdateDialog()
                } else {
                    Snackbar.make(requireView(), "您的應用程式已是最新版本", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(requireView(), "檢查更新失敗，請稍後再試", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showUpdateDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("應用程式更新")
            .setMessage("有新版本可用，是否立即更新？")
            .setPositiveButton("更新") { _, _ -> startInAppUpdate() }
            .setNegativeButton("稍後") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun startInAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(requireContext())
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    requireActivity(),
                    UPDATE_REQUEST_CODE
                )
            } else {
                Snackbar.make(requireView(), "目前沒有可用的更新", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
