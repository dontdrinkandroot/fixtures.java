package net.dontdrinkandroot.fixtures.example

import net.dontdrinkandroot.fixtures.Fixture
import net.dontdrinkandroot.fixtures.referencerepository.ReferenceRepository
import javax.persistence.EntityManager

class ExampleFixtureFive : Fixture {

    override val dependencies: Collection<Class<out Fixture>> = setOf(ExampleFixtureFour::class.java)

    override fun load(entityManager: EntityManager, referenceRepository: ReferenceRepository) {
        referenceRepository.store(this.javaClass.canonicalName, true)
    }
}