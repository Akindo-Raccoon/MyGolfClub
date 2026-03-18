package com.ud.mygolfclub.data.repository

import androidx.lifecycle.LiveData
import com.ud.mygolfclub.data.dao.ClientDAO
import com.ud.mygolfclub.data.entity.Client

//Good Practice - class abstract access to Multiple data sources
class ClientRepository (private val clientDao: ClientDAO){

    val getAllClient: LiveData<List<Client>> = clientDao.getAllClients()
    suspend fun addClient(client: Client): Result<Long> = runCatching {
        val existing = clientDao.findById(client.idClient)
        require(existing == null) {
            "El cliente ya existe en la Base de Datos"
        }
        clientDao.insertClient(client)
    }

    suspend fun login(id: String, pwd: String): Client? {
        return clientDao.login(id, pwd)
    }
}