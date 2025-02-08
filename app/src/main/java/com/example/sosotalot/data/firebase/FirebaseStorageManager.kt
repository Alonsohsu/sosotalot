package com.example.sosotalot.data.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object FirebaseStorageManager {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    /**
     * 上传用户头像
     * @param imageUri 要上传的图片 Uri
     * @param onSuccess 上传成功回调（返回下载 URL）
     * @param onFailure 上传失败回调
     */
    fun uploadProfileImage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val profileImageRef = storageRef.child("users/$userId/profile.jpg")
        profileImageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // 获取下载 URL
                profileImageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.d("FirebaseStorage", "头像上传成功: $uri")
                        onSuccess(uri.toString()) // 返回下载 URL
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseStorage", "获取下载 URL 失败", e)
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseStorage", "头像上传失败", e)
                onFailure(e)
            }
    }

    /**
     * 获取用户头像的下载 URL
     * @param onSuccess 获取成功回调（返回 URL）
     * @param onFailure 获取失败回调
     */
    fun getProfileImage(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val profileImageRef = storageRef.child("users/$userId/profile.jpg")
        profileImageRef.downloadUrl
            .addOnSuccessListener { uri ->
                Log.d("FirebaseStorage", "头像下载 URL: $uri")
                onSuccess(uri.toString())
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseStorage", "获取头像 URL 失败", e)
                onFailure(e)
            }
    }

    /**
     * 删除用户头像
     * @param onSuccess 删除成功回调
     * @param onFailure 删除失败回调
     */
    fun deleteProfileImage(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val profileImageRef = storageRef.child("users/$userId/profile.jpg")
        profileImageRef.delete()
            .addOnSuccessListener {
                Log.d("FirebaseStorage", "头像删除成功")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseStorage", "头像删除失败", e)
                onFailure(e)
            }
    }
}
