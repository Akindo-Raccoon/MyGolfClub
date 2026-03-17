package com.ud.mygolfclub.ui.data.db

import androidx.room.Insert
import androidx.room.Query
import com.ud.mygolfclub.ui.data.Client

interface ClientDAO {

    @Insert
    suspend fun insertClient(client: Client)

    @Query("SELECT * FROM clients WHERE idClient = :idUser AND pswdClient = :pwdUser LIMIT 1")
    suspend fun login(idUser: String, pwdUser: String): Client?

}