/**
 * Copyright © 2022 | Florin Dornig | Licensed under the GNU General Public license Version 3<p>
 * <p>
 * This program is free software: you can redistribute it and/or modify<p>
 * it under the terms of the GNU General Public License as published by<p>
 * the Free Software Foundation, either version 3 of the License, or<p>
 * (at your option) any later version.<p>
 * <p>
 * This program is distributed in the hope that it will be useful,<p>
 * but WITHOUT ANY WARRANTY; without even the implied warranty of<p>
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<p>
 * GNU General Public License for more details.<p>
 * <p>
 * You should have received a copy of the GNU General Public License<p>
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.<p>
 * <p>
 */

package me.infinity.cloudnetmongodb;

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import de.dytanic.cloudnet.database.AbstractDatabaseProvider
import com.mongodb.client.MongoDatabase as MongoDB

class MongoDatabaseProvider(
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val database: String,
    val authMechanism: String,
    val authDB: String
) : AbstractDatabaseProvider() {

    lateinit var client: MongoClient
    lateinit var mongoDatabase: MongoDB

    override fun getDatabase(name: String): MongoDatabase = MongoDatabase(this, name)

    override fun containsDatabase(name: String): Boolean = getDatabaseNames().contains(name)

    override fun deleteDatabase(name: String): Boolean = if (!getDatabaseNames().contains(name)) {
        false
    } else {
        mongoDatabase.getCollection(name).drop()
        true
    }

    override fun getDatabaseNames(): MutableCollection<String> =
        mongoDatabase.listCollectionNames().filter { true }.toMutableList()

    override fun getName(): String = "MongoDB-Provider"

    override fun close() = client.close()

    override fun init(): Boolean {
        client = MongoClients.create(
            MongoClientSettings.builder().credential(
                when (authMechanism) {
                    "SCRAM-SHA-1" -> {
                        MongoCredential.createScramSha1Credential(user, authDB, password.toCharArray())
                    }
                    "SCRAM-SHA-256" -> {
                        MongoCredential.createScramSha256Credential(user, authDB, password.toCharArray())
                    }
                    "MONGODB_X509" -> {
                        MongoCredential.createMongoX509Credential(user)
                    }
                    else -> {
                        MongoCredential.createCredential(user, authDB, password.toCharArray())
                    }
                }
            ).applyToClusterSettings { cluster ->
                cluster.hosts(listOf(ServerAddress(host, port)))
            }.build()
        )
        mongoDatabase = client.getDatabase(database)
        return true
    }
}