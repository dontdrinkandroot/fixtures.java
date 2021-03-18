package net.dontdrinkandroot.fixtures.example

import javax.persistence.*
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaDelete
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.CriteriaUpdate
import javax.persistence.metamodel.Metamodel

class NoopEntityManager : EntityManager {

    override fun persist(entity: Any) {}
    override fun <T> merge(entity: T): T? {
        return null
    }

    override fun remove(entity: Any) {}
    override fun <T> find(entityClass: Class<T>, primaryKey: Any): T? {
        return null
    }

    override fun <T> find(entityClass: Class<T>, primaryKey: Any, properties: Map<String, Any>): T? {
        return null
    }

    override fun <T> find(entityClass: Class<T>, primaryKey: Any, lockMode: LockModeType): T? {
        return null
    }

    override fun <T> find(
        entityClass: Class<T>,
        primaryKey: Any,
        lockMode: LockModeType,
        properties: Map<String, Any>
    ): T? {
        return null
    }

    override fun <T> getReference(entityClass: Class<T>, primaryKey: Any): T? {
        return null
    }

    override fun flush() {}
    override fun setFlushMode(flushMode: FlushModeType) {}
    override fun getFlushMode(): FlushModeType? {
        return null
    }

    override fun lock(entity: Any, lockMode: LockModeType) {}
    override fun lock(entity: Any, lockMode: LockModeType, properties: Map<String, Any>) {}
    override fun refresh(entity: Any) {}
    override fun refresh(entity: Any, properties: Map<String, Any>) {}
    override fun refresh(entity: Any, lockMode: LockModeType) {}
    override fun refresh(entity: Any, lockMode: LockModeType, properties: Map<String, Any>) {}
    override fun clear() {}
    override fun detach(entity: Any) {}
    override fun contains(entity: Any): Boolean {
        return false
    }

    override fun getLockMode(entity: Any): LockModeType? {
        return null
    }

    override fun setProperty(propertyName: String, value: Any) {}
    override fun getProperties(): Map<String, Any>? {
        return null
    }

    override fun createQuery(qlString: String): Query? {
        return null
    }

    override fun <T> createQuery(criteriaQuery: CriteriaQuery<T>): TypedQuery<T>? {
        return null
    }

    override fun createQuery(updateQuery: CriteriaUpdate<*>?): Query? {
        return null
    }

    override fun createQuery(deleteQuery: CriteriaDelete<*>?): Query? {
        return null
    }

    override fun <T> createQuery(qlString: String, resultClass: Class<T>): TypedQuery<T>? {
        return null
    }

    override fun createNamedQuery(name: String): Query? {
        return null
    }

    override fun <T> createNamedQuery(name: String, resultClass: Class<T>): TypedQuery<T>? {
        return null
    }

    override fun createNativeQuery(sqlString: String): Query? {
        return null
    }

    override fun createNativeQuery(sqlString: String, resultClass: Class<*>?): Query? {
        return null
    }

    override fun createNativeQuery(sqlString: String, resultSetMapping: String): Query? {
        return null
    }

    override fun createNamedStoredProcedureQuery(name: String): StoredProcedureQuery? {
        return null
    }

    override fun createStoredProcedureQuery(procedureName: String): StoredProcedureQuery? {
        return null
    }

    override fun createStoredProcedureQuery(
        procedureName: String,
        resultClasses: Array<Class<*>?>?
    ): StoredProcedureQuery? {
        return null
    }

    override fun createStoredProcedureQuery(
        procedureName: String,
        vararg resultSetMappings: String
    ): StoredProcedureQuery? {
        return null
    }

    override fun joinTransaction() {}
    override fun isJoinedToTransaction(): Boolean {
        return false
    }

    override fun <T> unwrap(cls: Class<T>): T? {
        return null
    }

    override fun getDelegate(): Any? {
        return null
    }

    override fun close() {}
    override fun isOpen(): Boolean {
        return false
    }

    override fun getTransaction(): EntityTransaction? {
        return null
    }

    override fun getEntityManagerFactory(): EntityManagerFactory? {
        return null
    }

    override fun getCriteriaBuilder(): CriteriaBuilder? {
        return null
    }

    override fun getMetamodel(): Metamodel? {
        return null
    }

    override fun <T> createEntityGraph(rootType: Class<T>): EntityGraph<T>? {
        return null
    }

    override fun createEntityGraph(graphName: String): EntityGraph<*>? {
        return null
    }

    override fun getEntityGraph(graphName: String): EntityGraph<*>? {
        return null
    }

    override fun <T> getEntityGraphs(entityClass: Class<T>): List<EntityGraph<in T>>? {
        return null
    }
}