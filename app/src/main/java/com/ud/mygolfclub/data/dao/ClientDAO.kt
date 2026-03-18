package com.ud.mygolfclub.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ud.mygolfclub.data.entity.Client

@Dao
interface ClientDAO {
    // Add New Client
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertClient(client: Client): Long

    @Query("SELECT idClient, name, phoneClient, pwdClient FROM clients ORDER BY name ASC")
    fun getAllClients(): LiveData<List<Client>>

    @Query("SELECT * FROM clients WHERE idClient = :id LIMIT 1")
    suspend fun findById(id: String): Client?

    @Query("SELECT * FROM clients WHERE idClient = :idUser AND pwdClient = :pwdUser LIMIT 1")
    suspend fun login(idUser: String, pwdUser: String): Client?
}