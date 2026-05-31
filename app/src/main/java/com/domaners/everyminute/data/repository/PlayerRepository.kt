package com.domaners.everyminute.data.repository

import com.domaners.everyminute.data.model.Player
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PlayerRepository(
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("players")

    fun getPlayersForTeam(teamId: String): Flow<List<Player>> = callbackFlow {
        val subscription = collection
            .whereEqualTo("teamId", teamId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val players = try {
                    snapshot?.toObjects(Player::class.java) ?: emptyList()
                } catch (e: Exception) {
                    android.util.Log.e("PlayerRepository", "Error deserializing players", e)
                    emptyList()
                }
                trySend(players)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addPlayer(player: Player): Result<Unit> {
        return try {
            val docRef = collection.document()
            val playerWithId = player.copy(id = docRef.id)
            docRef.set(playerWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePlayer(player: Player): Result<Unit> {
        return try {
            collection.document(player.id).set(player).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
