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
 *   Creation: Mittwoch 28 Juli 2021 15:53:30<p>
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

import com.google.gson.reflect.TypeToken
import de.dytanic.cloudnet.common.document.gson.JsonDocument
import de.dytanic.cloudnet.database.AbstractDatabaseProvider
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle
import de.dytanic.cloudnet.driver.module.ModuleTask
import de.dytanic.cloudnet.module.NodeCloudNetModule


class CloudNetMongoDB : NodeCloudNetModule() {

    val TYPE = object : TypeToken<List<String?>?>() {}.type

    private lateinit var mongoDatabaseProvider: MongoDatabaseProvider

    @ModuleTask(order = 126, event = ModuleLifeCycle.LOADED)
    fun initConfig() {
        getConfig().getDocument(
            "connection",
            JsonDocument.newDocument("host", "localhost").append("port", 27017).append("user", "cloudnet")
                .append("password", "cloudnet").append("database", "cloudnet").append("authMechanism", "SCRAM-SHA-1")
        )
        saveConfig()
    }

    @ModuleTask(order = 125, event = ModuleLifeCycle.LOADED)
    fun registerDatabaseProvider() {
        val connection = getConfig().getDocument("connection")
        mongoDatabaseProvider =
            MongoDatabaseProvider(
                System.getenv("CLOUDNET_MONGODB_HOST") ?: connection.getString("host"),
                System.getenv("CLOUDNET_MONGODB_PORT")?.toInt() ?: connection.getInt("port"),
                System.getenv("CLOUDNET_MONGODB_USER") ?: connection.getString("user"),
                System.getenv("CLOUDNET_MONGODB_PASSWORD") ?: connection.getString("password"),
                System.getenv("CLOUDNET_MONGODB_DATABASE") ?: connection.getString("database"),
                System.getenv("CLOUDNET_MONGODB_AUTHMECHANISM") ?: connection.getString("authMechanism")
            )
        registry.registerService(
            AbstractDatabaseProvider::class.java, "mongodb", mongoDatabaseProvider
        )
        logger.info("[MongoDB] Using the MongoDB Database Provider")
    }

    @ModuleTask(order = 127, event = ModuleLifeCycle.UNLOADED)
    fun unregisterDatabaseProvider() {
        if (mongoDatabaseProvider != null) {
            mongoDatabaseProvider?.close()
        }
        registry.unregisterService(AbstractDatabaseProvider::class.java, "mongodb")
    }


}