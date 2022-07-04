package com.leelu.shadow.ablum_lib

/**
 *
 * CreateDate: 2022/5/9 9:57
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
interface UserManager {
    fun getUserName(): String
    fun isLogin(): Boolean
    fun editUser(id: String, user: User):Boolean
    fun getUserPhone(): String
    fun getUserId(): String
}