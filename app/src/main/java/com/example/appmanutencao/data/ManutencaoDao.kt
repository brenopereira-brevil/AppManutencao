package com.example.appmanutencao.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ManutencaoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(manutencao: Manutencao)

    @Query("SELECT * FROM manutencoes ORDER BY id DESC")
    fun buscarTodas(): Flow<List<Manutencao>>
}
