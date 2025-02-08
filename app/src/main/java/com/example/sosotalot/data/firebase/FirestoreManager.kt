package com.example.sosotalot.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object FirebaseManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * ✅ 登录成功后，将用户信息存入 Firestore
     * 只在新用户时存入默认点数
     */
    fun saveUserDataToFirestore(user: FirebaseUser) {
        val userDocRef = db.collection("users").document(user.uid)

        userDocRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val userData = hashMapOf(
                    "profileImage" to (user.photoUrl?.toString() ?: ""),  // 用户头像
                    "memberID" to user.uid.takeLast(8),  // 会员 ID（UID 后 8 位）
                    "userRole" to "普通用戶",  // 默认角色
                    "verificationStatus" to "未認證",
                    "backpackItems" to 10,
                    "energyPoints" to 500, // 能量点
                    "starlightPoints" to 300, // 星光点
                    "fortunePoints" to 200, // 福报
                    "blessingEnergy" to 400 // 祝福能量
                )

                userDocRef.set(userData, SetOptions.merge())
                    .addOnSuccessListener { Log.d("Firestore", "用户数据已存入 Firestore") }
                    .addOnFailureListener { e -> Log.e("Firestore", "存入失败", e) }
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "读取用户数据失败", e)
        }
    }

    /**
     * ✅ 获取 Firebase 当前登录用户
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * ✅ 登出用户
     */
    fun signOut() {
        auth.signOut()
    }
}
