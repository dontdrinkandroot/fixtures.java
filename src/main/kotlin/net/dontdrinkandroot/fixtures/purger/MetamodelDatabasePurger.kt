package net.dontdrinkandroot.fixtures.purger

import net.dontdrinkandroot.fixtures.dependencyresolution.DirectedGraph
import net.dontdrinkandroot.fixtures.dependencyresolution.getTopologialOrder
import org.slf4j.LoggerFactory
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.metamodel.Attribute.PersistentAttributeType
import javax.persistence.metamodel.EntityType
import javax.persistence.metamodel.SingularAttribute

/**
 * A DatabasePurger that purges all entities that are resolved from the [EntityManager]. Tries to automatically
 * determine the correct purge order.
 */
class MetamodelDatabasePurger : DatabasePurger {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PersistenceContext
    lateinit var entityManager: EntityManager

    override fun purge() {
        val orderedEntityClasses = orderedEntityClasses
        for (entityClass in orderedEntityClasses) {
            this.purge(entityClass)
        }
        entityManager.flush()
    }

    private fun <T> purge(entityClass: Class<T>) {
        logger.info("Purging " + entityClass.canonicalName)
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(entityClass)
        criteriaQuery.from(entityClass)
        val entities = entityManager.createQuery(criteriaQuery).resultList
        for (entity in entities) {
            entityManager.remove(entity)
        }
    }

    private val orderedEntityClasses: List<Class<*>>
        private get() {
            val dependencyGraph = DirectedGraph<Class<*>>()
            val metamodel = entityManager.metamodel
            val entities = metamodel.entities
            for (entityType in entities) {
                val javaType = entityType.javaType
                dependencyGraph.addVertex(javaType)
                val associatedClasses = getAssociatedClasses(entityType)
                for (associatedClass in associatedClasses) {
                    dependencyGraph.addEdge(javaType, associatedClass)
                }
            }
            return dependencyGraph.getTopologialOrder()
        }

    private fun <T> getAssociatedClasses(entityType: EntityType<T>): Set<Class<*>> {
        val javaType: Class<*> = entityType.javaType
        val associatedClasses: MutableSet<Class<*>> = HashSet()
        val attributes = entityType.attributes
        for (attribute in attributes) {
            val name = attribute.name
            if (!isIgnored(javaType, name) && attribute.isAssociation) {
                val attributeType = attribute.persistentAttributeType
                var singularAttribute: SingularAttribute<in T, *>
                when (attributeType) {
                    PersistentAttributeType.MANY_TO_ONE -> associatedClasses.add(attribute.javaType)
                    PersistentAttributeType.ONE_TO_ONE -> {
                        singularAttribute = attribute as SingularAttribute<in T, *>
                        if (!singularAttribute.isOptional) {
                            associatedClasses.add(singularAttribute.javaType)
                        }
                    }
                    PersistentAttributeType.ELEMENT_COLLECTION, PersistentAttributeType.EMBEDDED -> throw RuntimeException(
                        String.format(
                            "%s not supported. Class: %s, attribute: %s",
                            attributeType.name,
                            entityType.javaType.canonicalName,
                            attribute.name
                        )
                    )
                    PersistentAttributeType.MANY_TO_MANY, PersistentAttributeType.ONE_TO_MANY, PersistentAttributeType.BASIC -> Unit
                }
            }
        }
        return associatedClasses
    }

    /**
     * Overwrite this method in order to ignore certain properties. This can be usefull if cycles in the dependency
     * graph need to be resolved manually.
     *
     * @param clazz    The class to check.
     * @param property The property of the class to check.
     * @return Whether the property should be included in building the dependency graph.
     */
    protected fun isIgnored(clazz: Class<*>?, property: String?): Boolean {
        return false
    }
}