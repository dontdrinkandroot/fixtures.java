package net.dontdrinkandroot.fixtures

import net.dontdrinkandroot.fixtures.referencerepository.MapBackedReferenceRepository
import org.junit.Assert
import org.junit.Test

class MapBackedReferenceRepositoryTest {

    @Test
    fun canStoreAndRetrieve() {
        val referenceRepository = MapBackedReferenceRepository()
        referenceRepository.store("test", "asdf")
        Assert.assertEquals("asdf", referenceRepository.retrieve("test"))
    }

    @Test(expected = RuntimeException::class)
    fun nullReferenceThrowsException() {
        val referenceRepository = MapBackedReferenceRepository()
        referenceRepository.retrieve<Any>("asdf")
    }

    @Test(expected = ClassCastException::class)
    fun wrongClassCastThrowsException() {
        val referenceRepository = MapBackedReferenceRepository()
        referenceRepository.store("test", "asdf")
        val foo = referenceRepository.retrieve<Int>("test")
    }
}