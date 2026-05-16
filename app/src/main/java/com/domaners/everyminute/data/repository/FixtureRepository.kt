package com.domaners.everyminute.data.repository

import com.domaners.everyminute.data.model.Fixture
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FixtureRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun getCollection(userId: String) = 
        firestore.collection("users").document(userId).collection("fixtures")

    fun getFixtures(userId: String): Flow<List<Fixture>> = callbackFlow {
        val subscription = getCollection(userId)
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

    suspend fun addFixture(userId: String, fixture: Fixture): Result<Unit> {
        return try {
            val docRef = getCollection(userId).document()
            val fixtureWithId = fixture.copy(id = docRef.id)
            docRef.set(fixtureWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFixture(userId: String, fixture: Fixture): Result<Unit> {
        return try {
            getCollection(userId).document(fixture.id).set(fixture).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFixture(userId: String, fixtureId: String): Result<Unit> {
        return try {
            getCollection(userId).document(fixtureId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFixture(userId: String, fixtureId: String): Fixture? {
        return try {
            getCollection(userId).document(fixtureId).get().await().toObject(Fixture::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
