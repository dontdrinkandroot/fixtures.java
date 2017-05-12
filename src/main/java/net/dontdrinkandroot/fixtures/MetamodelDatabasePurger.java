package net.dontdrinkandroot.fixtures;

import net.dontdrinkandroot.fixtures.dependencyresolution.DirectedGraph;
import net.dontdrinkandroot.fixtures.dependencyresolution.TopologicalSort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.*;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A DatabasePurger that purges all entities that are resolved from the {@link EntityManager}. Tries to automatically
 * determine the correct purge order.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class MetamodelDatabasePurger implements DatabasePurger
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public void purge()
    {
        List<Class<?>> orderedEntityClasses = this.getOrderedEntityClasses();
        for (Class<?> entityClass : orderedEntityClasses) {
            this.purge(entityClass);
        }
        this.entityManager.flush();
    }

    private <T> void purge(Class<T> entityClass)
    {
        this.logger.info("Purging " + entityClass.getCanonicalName());
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        criteriaQuery.from(entityClass);
        List<T> entities = this.entityManager.createQuery(criteriaQuery).getResultList();
        for (T entity : entities) {
            this.entityManager.remove(entity);
        }
    }

    private List<Class<?>> getOrderedEntityClasses()
    {
        DirectedGraph<Class<?>> dependencyGraph = new DirectedGraph<>();
        Metamodel metamodel = this.entityManager.getMetamodel();
        Set<EntityType<?>> entities = metamodel.getEntities();
        for (EntityType<?> entityType : entities) {
            Class<?> javaType = entityType.getJavaType();
            dependencyGraph.addVertex(javaType);
            Set<Class<?>> associatedClasses = this.getAssociatedClasses(entityType);
            for (Class<?> associatedClass : associatedClasses) {
                dependencyGraph.addEdge(javaType, associatedClass);
            }
        }

        return TopologicalSort.getTopologialOrder(dependencyGraph);
    }

    private <T> Set<Class<?>> getAssociatedClasses(EntityType<T> entityType)
    {
        // System.out.println(entityType.getJavaType());
        Set<Class<?>> associatedClasses = new HashSet<Class<?>>();
        Set<Attribute<? super T, ?>> attributes = entityType.getAttributes();
        for (Attribute<? super T, ?> attribute : attributes) {
            if (attribute.isAssociation()) {
                // System.out.println("\tName: " + attribute.getName());
                PersistentAttributeType attributeType = attribute.getPersistentAttributeType();
                // System.out.println(attributeType);
                switch (attributeType) {
                    case MANY_TO_ONE:
                        SingularAttribute<? super T, ?> singularAttribute = (SingularAttribute<? super T, ?>) attribute;
                        // TODO hmm. not really.
                        // if (!singularAttribute.isOptional()) {
                        associatedClasses.add(singularAttribute.getJavaType());
                        // }
                        break;
                    case ONE_TO_ONE:
                        singularAttribute = (SingularAttribute<? super T, ?>) attribute;
                        if (!singularAttribute.isOptional()) {
                            associatedClasses.add(singularAttribute.getJavaType());
                        }
                        break;
                    case MANY_TO_MANY:
                        PluralAttribute<? super T, ?, ?> pluralAttribute = (PluralAttribute<? super T, ?, ?>) attribute;
                        //TODO: Ignoring
                        break;
                    case ELEMENT_COLLECTION:
                    case EMBEDDED:
                        throw new RuntimeException(
                                String.format(
                                        "%s not supported. Class: %s, attribute: %s",
                                        attributeType.name(),
                                        entityType.getJavaType().getCanonicalName(),
                                        attribute.getName()
                                ));
                    case ONE_TO_MANY:
                    case BASIC:
                }
            }
        }

        return associatedClasses;
    }
}
