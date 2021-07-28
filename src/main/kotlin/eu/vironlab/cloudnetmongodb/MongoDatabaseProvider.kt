/**
 * Copyright © 2020 | vironlab.eu | Licensed under the GNU General Public license Version 3<p>
 * <p>
 * ___    _______                        ______         ______  <p>
 * __ |  / /___(_)______________ _______ ___  / ______ ____  /_ <p>
 * __ | / / __  / __  ___/_  __ \__  __ \__  /  _  __ `/__  __ \<p>
 * __ |/ /  _  /  _  /    / /_/ /_  / / /_  /___/ /_/ / _  /_/ /<p>
 * _____/   /_/   /_/     \____/ /_/ /_/ /_____/\__,_/  /_.___/ <p>
 * <p>
 * ____  _______     _______ _     ___  ____  __  __ _____ _   _ _____ <p>
 * |  _ \| ____\ \   / / ____| |   / _ \|  _ \|  \/  | ____| \ | |_   _|<p>
 * | | | |  _|  \ \ / /|  _| | |  | | | | |_) | |\/| |  _| |  \| | | |  <p>
 * | |_| | |___  \ V / | |___| |__| |_| |  __/| |  | | |___| |\  | | |  <p>
 * |____/|_____|  \_/  |_____|_____\___/|_|   |_|  |_|_____|_| \_| |_|  <p>
 * <p>
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
 *<p>
 *   Creation: Mittwoch 28 Juli 2021 16:05:55<p>
 *<p>
 * <p>
 * Contact:<p>
 * <p>
 * Discordserver:   https://discord.gg/wvcX92VyEH<p>
 * Website:         https://vironlab.eu/ <p>
 * Mail:            contact@vironlab.eu<p>
 * <p>
 */

package eu.vironlab.cloudnetmongodb;

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
    val authMechanism: String
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
        client = MongoClients.create("mongodb://$user:$password@$host:$port/$database?authMechanism=$authMechanism")
        mongoDatabase = client.getDatabase(database)
        return true
    }
}