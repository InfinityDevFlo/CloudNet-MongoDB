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

package me.infinity.cloudnetmongodb.extension;

import de.dytanic.cloudnet.common.concurrent.ITask
import de.dytanic.cloudnet.common.concurrent.ListenableTask
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch

val scope = CoroutineScope(Executors.newCachedThreadPool().asCoroutineDispatcher())

internal fun <T> schedule(callable: () -> T): ITask<T> {
    val task: ITask<T> = ListenableTask(callable)
    scope.launch {
        try {
            task.call()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
    return task
}
