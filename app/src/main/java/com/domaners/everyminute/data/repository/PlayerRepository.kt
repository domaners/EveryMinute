package com.domaners.everyminute.data.repository

import com.domaners.everyminute.data.model.Player
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PlayerRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun getCollection(userId: String) = 
        firestore.collection("users").document(userId).collection("players")

    fun getPlayers(userId: String): Flow<List<Player>> = callbackFlow {
        val subscription = getCollection(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val players = snapshot?.toObjects(Player::class.java) ?: emptyList()
            trySend(players)
        }
        awaitClose { subscription.remove() }
    }

    suspend fun addPlayer(userId: String, player: Player): Result<Unit> {
        return try {
            val docRef = getCollection(userId).document()
            val playerWithId = player.copy(id = docRef.id)
            docRef.set(playerWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePlayer(userId: String, player: Player): Result<Unit> {
        return try {
            getCollection(userId).document(player.id).set(player).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePlayer(userId: String, playerId: String): Result<Unit> {
        return try {
            getCollection(userId).document(playerId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlayer(userId: String, playerId: String): Player? {
        return try {
            getCollection(userId).document(playerId).get().await().toObject(Player::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
