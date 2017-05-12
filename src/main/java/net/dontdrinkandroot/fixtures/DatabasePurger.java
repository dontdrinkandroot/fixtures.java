package net.dontdrinkandroot.fixtures;

import net.dontdrinkandroot.fixtures.dependencyresolution.DirectedGraph;
import net.dontdrinkandroot.fixtures.dependencyresolution.TopologicalSort;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.*;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabasePurger
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected EntityManager entityManager;

    private Set<Pair<Class<?>, Class<?>>> purgeIgnoreAssociations;

    public DatabasePurger(EntityManager entityManager, Set<Pair<Class<?>, Class<?>>> purgeIgnoreAssociations)
    {
        this.entityManager = entityManager;
        this.purgeIgnoreAssociations = purgeIgnoreAssociations;
    }

    public void purge()
    {
        List<Class<?>> orderedEntityClasses = this.getOrderedEntityClasses();
        this.printPurgeOrder(orderedEntityClasses);

        ReverseListIterator<Class<?>> reverseFixturesIterator = new ReverseListIterator<Class<?>>(orderedEntityClasses);
        while (reverseFixturesIterator.hasNext()) {
            Class<?> entityClass = reverseFixturesIterator.next();
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
                        if (!this.isIgnored(entityType.getJavaType(), singularAttribute.getJavaType())) {
                            associatedClasses.add(singularAttribute.getJavaType());
                        }
                        // }
                        break;
                    case ONE_TO_ONE:
                        singularAttribute = (SingularAttribute<? super T, ?>) attribute;
                        if (!singularAttribute.isOptional()) {
                            if (!this.isIgnored(entityType.getJavaType(), singularAttribute.getJavaType())) {
                                associatedClasses.add(singularAttribute.getJavaType());
                            }
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

        this.printAssociations(entityType, associatedClasses);

        return associatedClasses;
    }

    private boolean isIgnored(Class<?> source, Class<?> target)
    {
        if (null != this.purgeIgnoreAssociations) {
            for (Pair<Class<?>, Class<?>> tuple : this.purgeIgnoreAssociations) {
                Class<?> left = tuple.getLeft();
                if (left.equals(source)) {
                    Class<?> right = tuple.getRight();
                    if (right.equals(target)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private <T> void printAssociations(EntityType<T> entityType, Set<Class<?>> associatedClasses)
    {
        Class<T> type = entityType.getJavaType();
        for (Class<?> associatedClass : associatedClasses) {
            System.out.println(type.getName() + " -> " + associatedClass.getName());
        }
    }

    private void printPurgeOrder(List<Class<?>> entityClasses)
    {
        StringBuffer outBuffer = new StringBuffer("Purge order:\n");
        ReverseListIterator<Class<?>> reverseFixturesIterator = new ReverseListIterator<Class<?>>(entityClasses);
        while (reverseFixturesIterator.hasNext()) {
            Class<?> entityClass = reverseFixturesIterator.next();
            outBuffer.append("\t" + entityClass.getName() + "\n");
        }
        this.logger.info(outBuffer.toString());
    }
}
