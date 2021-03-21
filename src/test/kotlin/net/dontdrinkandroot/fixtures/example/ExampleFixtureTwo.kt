package net.dontdrinkandroot.fixtures.example

import net.dontdrinkandroot.fixtures.Fixture
import net.dontdrinkandroot.fixtures.referencerepository.ReferenceRepository
import javax.persistence.EntityManager
import kotlin.reflect.KClass

class ExampleFixtureTwo : Fixture {

    override val dependencies: Collection<KClass<out Fixture>> = setOf(ExampleFixtureThree::class)

    override fun load(entityManager: EntityManager, referenceRepository: ReferenceRepository) {
        referenceRepository.store(this.javaClass.canonicalName, true)
    }
}