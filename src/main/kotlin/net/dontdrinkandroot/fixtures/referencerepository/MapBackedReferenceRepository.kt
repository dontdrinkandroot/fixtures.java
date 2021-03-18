package net.dontdrinkandroot.fixtures.referencerepository

import java.util.*

/**
 * [ReferenceRepository] that uses a [HashMap] as storage.
 */
class MapBackedReferenceRepository : ReferenceRepository {

    private val objects: MutableMap<String, Any> = HashMap()

    override fun <T : Any> store(name: String, obj: T) {
        objects[name] = obj
    }

    override fun <T : Any> retrieve(name: String): T =
        (objects[name] ?: throw RuntimeException("Reference not found")) as T
}