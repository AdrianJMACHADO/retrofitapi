package com.adrianj.retrofitapi.data

import com.adrianj.retrofitapi.model.Pokemon
import com.adrianj.retrofitapi.model.PokemonDB
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreManager(auth: AuthManager, context: android.content.Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = auth.getCurrentUser()?.uid

    companion object{
        private const val COLLECTION_POKEMON = "pokemon"
    }

    //    Funciones de los pokemon
    fun getPokemon(): Flow<List<Pokemon>> {
        return firestore.collection(COLLECTION_POKEMON)
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { qs ->
                qs.documents.mapNotNull { ds ->
                    ds.toObject(PokemonDB::class.java)?.let { PokemonDB ->
                        Pokemon(
                            id = ds.id,
                            userId = PokemonDB.userId,
                            name = PokemonDB.name,
                            tipo1 = PokemonDB.tipo1,
                            tipo2 = PokemonDB.tipo2,
                        )
                    }
                }
            }
    }

    suspend fun addPokemon(pokemon: Pokemon) {
        firestore.collection(COLLECTION_POKEMON).add(pokemon).await()
    }

    suspend fun updatePokemon(pokemon: Pokemon) {
        val pokemonRef = pokemon.id?.let {
            firestore.collection("pokemon").document(it)
        }
        pokemonRef?.set(pokemon)?.await()
    }

    suspend fun deletePokemonById(pokemonId: String) {
        firestore.collection("pokemon").document(pokemonId).delete().await()
    }

    suspend fun getPokemonById(id: String): Pokemon? {
        return firestore.collection(COLLECTION_POKEMON).document(id)
            .get().await().toObject(PokemonDB::class.java)?.let {
                Pokemon(
                    id = id,
                    userId = it.userId,
                    name = it.name,
                    tipo1 = it.tipo1,
                    tipo2 = it.tipo2,
                )
            }!!
    }
}