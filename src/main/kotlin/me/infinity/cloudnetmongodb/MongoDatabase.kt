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

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.InsertOneOptions
import de.dytanic.cloudnet.common.concurrent.ITask
import de.dytanic.cloudnet.common.concurrent.ITaskListener
import de.dytanic.cloudnet.common.concurrent.ListenableTask
import de.dytanic.cloudnet.common.concurrent.function.ThrowableFunction
import de.dytanic.cloudnet.common.document.gson.JsonDocument
import de.dytanic.cloudnet.driver.database.Database
import me.infinity.cloudnetmongodb.extension.schedule
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer
import java.util.function.BiPredicate
import org.bson.Document

class MongoDatabase(mongoDatabaseProvider: MongoDatabaseProvider, private val name: String) : Database {

    companion object {
        @JvmStatic
        var COLLECTION_KEY = "_id"
    }

    val collection: MongoCollection<Document>

    init {
        if (!mongoDatabaseProvider.containsDatabase(name)) {
            mongoDatabaseProvider.mongoDatabase.createCollection(name)
        }
        this.collection = mongoDatabaseProvider.mongoDatabase.getCollection(name)
    }

    override fun getName(): String = name

    override fun close() {}

    override fun insert(key: String, document: JsonDocument): Boolean = if (contains(key)) {
        update(key, document)
    } else {
        collection.insertOne(Document.parse(document.toJson()).append(COLLECTION_KEY, key)).wasAcknowledged()
    }

    override fun update(key: String, document: JsonDocument): Boolean = if (contains(key)) {
        collection.replaceOne(BasicDBObject(COLLECTION_KEY, key), Document.parse(document.toJson()).append(
            COLLECTION_KEY, key)).wasAcknowledged()
    } else {
        insert(key, document)
    }

    override fun contains(key: String): Boolean = collection.find(BasicDBObject(COLLECTION_KEY, key)).cursor().hasNext()

    override fun delete(key: String): Boolean =
        collection.deleteMany(BasicDBObject(COLLECTION_KEY, key)).wasAcknowledged()

    override fun get(key: String): JsonDocument? {
        val cursor = collection.find(BasicDBObject(COLLECTION_KEY, key)).cursor()
        if (!cursor.hasNext()) {
            return null
        }
        return JsonDocument.newDocument(cursor.next().toJson())
    }

    override fun get(fieldName: String, fieldValue: Any): MutableList<JsonDocument> {
        val cursor = collection.find(BasicDBObject(fieldName, fieldName)).cursor()
        val rs = ArrayList<JsonDocument>()
        while (cursor.hasNext()) {
            rs += JsonDocument.newDocument(cursor.next().toJson())
        }
        return rs
    }

    override fun get(filters: JsonDocument): MutableList<JsonDocument> {
        val cursor = collection.find(Document.parse(filters.toJson())).cursor()
        val rs = ArrayList<JsonDocument>()
        while (cursor.hasNext()) {
            rs += JsonDocument.newDocument(cursor.next().toJson())
        }
        return rs
    }

    override fun keys(): MutableCollection<String> {
        val rs = ArrayList<String>()
        collection.find().forEach { doc ->
            rs.add(doc.getString(COLLECTION_KEY))
        }
        return rs
    }

    override fun documents(): MutableCollection<JsonDocument> {
        val rs = ArrayList<JsonDocument>()
        collection.find().forEach { doc ->
            rs.add(JsonDocument.newDocument(doc.getString(COLLECTION_KEY)))
        }
        return rs
    }

    override fun entries(): MutableMap<String, JsonDocument> {
        val rs = ConcurrentHashMap<String, JsonDocument>()
        collection.find().forEach { doc ->
            rs += Pair(doc.getString(COLLECTION_KEY), JsonDocument.newDocument(doc.toJson()))
        }
        return rs
    }

    override fun filter(predicate: BiPredicate<String, JsonDocument>): MutableMap<String, JsonDocument> {
        val rs = ConcurrentHashMap<String, JsonDocument>()
        collection.find().forEach { doc ->
            val jsonDoc = JsonDocument.newDocument(doc.toJson())
            val key = doc.getString(COLLECTION_KEY)
            if (predicate.test(key, jsonDoc))
                rs[key] = jsonDoc
        }
        return rs
    }

    override fun iterate(consumer: BiConsumer<String, JsonDocument>) {
        collection.find().forEach { doc ->
            consumer.accept(doc.getString(COLLECTION_KEY), JsonDocument.newDocument(doc.toJson()))
        }
    }

    override fun clear() = collection.deleteMany(BasicDBObject()).let { Unit }

    override fun getDocumentsCount(): Long = collection.countDocuments()

    override fun isSynced(): Boolean = true

    override fun insertAsync(key: String, document: JsonDocument): ITask<Boolean> =
        schedule { insert(key, document) }

    override fun updateAsync(key: String, document: JsonDocument): ITask<Boolean> = schedule { update(key, document) }

    override fun containsAsync(key: String): ITask<Boolean> = schedule { contains(key) }

    override fun deleteAsync(key: String): ITask<Boolean> = schedule { delete(key) }

    override fun getAsync(key: String): ITask<JsonDocument?> = schedule { get(key) }

    override fun getAsync(fieldName: String, fieldValue: Any): ITask<MutableList<JsonDocument>> =
        schedule { get(fieldName, fieldValue) }

    override fun getAsync(filters: JsonDocument): ITask<MutableList<JsonDocument>> = schedule { get(filters) }

    override fun keysAsync(): ITask<MutableCollection<String>> = schedule { keys() }

    override fun documentsAsync(): ITask<MutableCollection<JsonDocument>> = schedule { documents() }

    override fun entriesAsync(): ITask<MutableMap<String, JsonDocument>> = schedule { entries() }

    override fun filterAsync(predicate: BiPredicate<String, JsonDocument>): ITask<MutableMap<String, JsonDocument>> =
        schedule { filter(predicate) }

    override fun iterateAsync(consumer: BiConsumer<String, JsonDocument>): ITask<Void?> = schedule { iterate(consumer); null }

    override fun clearAsync(): ITask<Void?> = schedule { clear(); null }

    override fun getDocumentsCountAsync(): ITask<Long> = schedule { documentsCount }
}
