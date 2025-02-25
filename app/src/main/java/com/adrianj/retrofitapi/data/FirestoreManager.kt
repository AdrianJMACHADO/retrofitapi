package com.adrianj.retrofitapi.data

import android.util.Log
import com.adrianj.retrofitapi.model.CharacterDB
import com.adrianj.retrofitapi.model.Character
import com.adrianj.retrofitapi.model.Pokemon
import com.adrianj.retrofitapi.model.PokemonDB
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreManager(private val auth: AuthManager, context: android.content.Context) {
    private val firestore = FirebaseFirestore.getInstance()
    
    // Modificar para obtener el userId en tiempo real
    private fun getCurrentUserId(): String? {
        return auth.getCurrentUser()?.uid
    }

    companion object{
        private const val COLLECTION_POKEMON = "pokemon"
        private const val COLLECTION_CHARACTERS = "characters"
    }

    //    Funciones de los pokemon

    fun getCharacters(): Flow<List<Character>> {
//        val currentUserId = getCurrentUserId()
        return firestore.collection(COLLECTION_CHARACTERS)
            .whereEqualTo("userId", getCurrentUserId())
//            .whereIn("userId", listOf(currentUserId, "all"))
            .snapshots()
            .map { qs ->
                qs.documents.mapNotNull { ds ->
                    ds.toObject(CharacterDB::class.java)?.let { characterDB ->
                        com.adrianj.retrofitapi.model.Character(
                            id = ds.id,
                            userId = characterDB.userId,
                            name = characterDB.name,
                            region = characterDB.region
                        )
                    }
                }
            }
    }

    suspend fun addCharacter(name: String, region: String) {
        val characterDB = CharacterDB(
            name = name,
            region = region,
            userId = getCurrentUserId() ?: throw Exception("Usuario no autenticado")
        )
        firestore.collection(COLLECTION_CHARACTERS).add(characterDB).await()
    }

    suspend fun deleteCharacter(characterId: String) {
        firestore.collection(COLLECTION_CHARACTERS).document(characterId).delete().await()
    }

    // ------------------------- Operaciones para Pokémon -------------------------
    suspend fun addPokemon(name: String, tipo1: String, tipo2: String, idpersonaje: String) {
        val pokemonDB = PokemonDB(
            name = name,
            userId = getCurrentUserId() ?: throw Exception("Usuario no autenticado"),
            idpersonaje = idpersonaje,
            tipo1 = tipo1,
            tipo2 = tipo2
        )
        firestore.collection(COLLECTION_POKEMON).add(pokemonDB).await()
    }

    fun getPokemonByCharacter(characterId: String): Flow<List<Pokemon>> {
//        val currentUserId = getCurrentUserId()
            return firestore.collection(COLLECTION_POKEMON)
                .whereEqualTo("userId", getCurrentUserId())
//                .whereIn("userId", listOf(currentUserId, "all"))
                .whereEqualTo("idpersonaje", characterId)
                .snapshots()
                .map { qs ->
                    qs.documents.mapNotNull { ds ->
                        ds.toObject(PokemonDB::class.java)?.let { pokemonDB ->
                            Pokemon(
                                id = ds.id,
                                idpersonaje = pokemonDB.idpersonaje,
                                userId = pokemonDB.userId,
                                name = pokemonDB.name,
                                tipo1 = pokemonDB.tipo1,
                                tipo2 = pokemonDB.tipo2
                            )
                        }
                    }
                }
                .catch { e ->
                    Log.e("FirestoreError", "Error al obtener documentos", e)
                    emit(emptyList())  // Emitir una lista vacía en caso de error
                }
    }

    suspend fun addPokemon(pokemon: Pokemon) {
        val currentUserId = getCurrentUserId() ?: throw Exception("Usuario no autenticado")
        val pokemonDB = PokemonDB(
            name = pokemon.name ?: "",
            userId = currentUserId,  // Usar el ID actual
            tipo1 = pokemon.tipo1 ?: "",
            tipo2 = pokemon.tipo2 ?: "Nada",
            idpersonaje = pokemon.idpersonaje ?: ""
        )
        firestore.collection(COLLECTION_POKEMON).add(pokemonDB).await()
    }

    suspend fun updatePokemon(pokemon: Pokemon) {
        val currentUserId = getCurrentUserId()
        if (pokemon.userId == currentUserId) {  // Solo actualizar si el pokemon pertenece al usuario actual
            val pokemonRef = pokemon.id?.let {
                firestore.collection(COLLECTION_POKEMON).document(it)
            }
            pokemonRef?.set(pokemon)?.await()
        }
    }

    suspend fun deletePokemonById(pokemonId: String) {
        val currentUserId = getCurrentUserId()
        val pokemon = firestore.collection(COLLECTION_POKEMON)
            .document(pokemonId)
            .get()
            .await()
            .toObject(PokemonDB::class.java)

        // Solo eliminar si el pokemon pertenece al usuario actual
        if (pokemon?.userId == currentUserId) {
            firestore.collection(COLLECTION_POKEMON)
                .document(pokemonId)
                .delete()
                .await()
        }
    }

    suspend fun getCharacterById(characterId: String): Character? {
        return firestore.collection(COLLECTION_CHARACTERS)
            .document(characterId)
            .get()
            .await()
            .toObject(CharacterDB::class.java)?.let { characterDB ->
                Character(
                    id = characterId,
                    userId = characterDB.userId,
                    name = characterDB.name,
                    region = characterDB.region
                )
            }
    }

    fun getPokemon(): Flow<List<Pokemon>> {
        println("DEBUG: Current userId: ${getCurrentUserId()}")
        return firestore.collection(COLLECTION_POKEMON)
            .whereEqualTo("userId", getCurrentUserId())
            .snapshots()
            .map { querySnapshot ->
                println("DEBUG: Query snapshot size: ${querySnapshot.size()}")
                querySnapshot.documents.mapNotNull { document ->
                    document.toObject(PokemonDB::class.java)?.let { pokemonDB ->
                        println("DEBUG: Pokemon userId: ${pokemonDB.userId}")
                        if (pokemonDB.userId == getCurrentUserId()) {
                            Pokemon(
                                id = document.id,
                                userId = pokemonDB.userId,
                                name = pokemonDB.name,
                                tipo1 = pokemonDB.tipo1,
                                tipo2 = pokemonDB.tipo2,
                                idpersonaje = pokemonDB.idpersonaje
                            )
                        } else null
                    }
                }
            }
    }

    suspend fun getPokemonById(id: String): Pokemon? {
        return firestore.collection(COLLECTION_POKEMON)
            .document(id)
            .get()
            .await()
            .toObject(PokemonDB::class.java)?.let { pokemonDB ->
                Pokemon(
                    id = id,
                    userId = pokemonDB.userId,
                    name = pokemonDB.name,
                    tipo1 = pokemonDB.tipo1,
                    tipo2 = pokemonDB.tipo2,
                    idpersonaje = pokemonDB.idpersonaje
                )
            }
    }

    suspend fun updateCharacter(character: Character) {
        character.id?.let { id ->
            val characterDB = CharacterDB(
                name = character.name ?: "",
                userId = character.userId ?: "",
                region = character.region ?: ""
            )
            firestore.collection(COLLECTION_CHARACTERS)
                .document(id)
                .set(characterDB)
                .await()
        }
    }
}