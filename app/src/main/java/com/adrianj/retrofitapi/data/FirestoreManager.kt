package com.adrianj.retrofitapi.data

import com.adrianj.retrofitapi.model.CharacterDB
import com.adrianj.retrofitapi.model.Character
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
        private const val COLLECTION_CHARACTERS = "characters"
    }

    //    Funciones de los pokemon
    fun getCharacters(): Flow<List<Character>> {
        return firestore.collection(COLLECTION_CHARACTERS)
            .whereEqualTo("userId", userId)
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
            userId = userId ?: throw Exception("Usuario no autenticado")
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
            userId = userId ?: throw Exception("Usuario no autenticado"),
            idpersonaje = idpersonaje,
            tipo1 = tipo1,
            tipo2 = tipo2
        )
        firestore.collection(COLLECTION_POKEMON).add(pokemonDB).await()
    }

    fun getPokemonByCharacter(characterId: String): Flow<List<Pokemon>> {
        return firestore.collection(COLLECTION_POKEMON)
            .whereEqualTo("userId", userId)
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
        return firestore.collection(COLLECTION_POKEMON)
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    document.toObject(PokemonDB::class.java)?.let { pokemonDB ->
                        Pokemon(
                            id = document.id,
                            userId = pokemonDB.userId,
                            name = pokemonDB.name,
                            tipo1 = pokemonDB.tipo1,
                            tipo2 = pokemonDB.tipo2,
                            idpersonaje = pokemonDB.idpersonaje
                        )
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