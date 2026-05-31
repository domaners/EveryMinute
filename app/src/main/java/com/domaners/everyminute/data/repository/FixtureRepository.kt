package com.domaners.everyminute.data.repository

import com.domaners.everyminute.data.model.Fixture
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FixtureRepository(
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("fixtures")

    fun getFixturesForTeam(teamId: String): Flow<List<Fixture>> = callbackFlow {
        val subscription = collection
            .whereEqualTo("teamId", teamId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val fixtures = snapshot?.toObjects(Fixture::class.java) ?: emptyList()
                trySend(fixtures)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addFixture(fixture: Fixture): Result<Unit> {
        return try {
            val docRef = collection.document()
            val fixtureWithId = fixture.copy(id = docRef.id)
            docRef.set(fixtureWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFixture(fixture: Fixture): Result<Unit> {
        return try {
            collection.document(fixture.id).set(fixture).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
