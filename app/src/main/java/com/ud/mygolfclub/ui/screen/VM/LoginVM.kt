package com.ud.mygolfclub.ui.screen.VM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.mygolfclub.ui.data.db.ClientDAO
import kotlinx.coroutines.launch

class LoginVM(private val dao: ClientDAO): ViewModel() {
    var verification = false

    fun Login(idUser: String, pwdUser: String){
        viewModelScope.launch {
            val client = dao.login(idUser,pwdUser)
            verification = client != null
        }
    }
}