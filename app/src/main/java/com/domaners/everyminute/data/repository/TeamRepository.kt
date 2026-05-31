package com.domaners.everyminute.data.repository

import com.domaners.everyminute.data.model.Team
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TeamRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("teams")

    fun getTeamsForUser(userId: String): Flow<List<Team>> = callbackFlow {
        // Query memberIds to find all teams where the user is a coach or parent
        val subscription = collection.whereArrayContains("memberIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val teams = snapshot?.toObjects(Team::class.java) ?: emptyList()
                trySend(teams)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun createTeam(team: Team): Result<Unit> {
        return try {
            val docRef = collection.document()
            // Ensure memberIds is populated with all coaches and parents
            val allMembers = (team.coachIds + team.parentIds).distinct()
            val teamWithId = team.copy(id = docRef.id, memberIds = allMembers)
            docRef.set(teamWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTeam(team: Team): Result<Unit> {
        return try {
            val allMembers = (team.coachIds + team.parentIds).distinct()
            val teamWithMembers = team.copy(memberIds = allMembers)
            collection.document(team.id).set(teamWithMembers).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTeam(teamId: String): Team? {
        return try {
            collection.document(teamId).get().await().toObject(Team::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun findUserByEmail(email: String): Map<String, String>? {
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()
            if (!snapshot.isEmpty) {
                val doc = snapshot.documents.first()
                mapOf("id" to doc.id, "name" to (doc.getString("name") ?: "Unknown"))
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
