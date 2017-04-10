package net.dontdrinkandroot.fixtures;

import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.*;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import java.util.*;

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
        List<Class<?>> l = new ArrayList<Class<?>>();
        Set<Class<?>> s = new HashSet<Class<?>>();
        Map<Class<?>, Set<Class<?>>> edges = new HashMap<Class<?>, Set<Class<?>>>();
        Map<Class<?>, Set<Class<?>>> reverseEdges = new HashMap<Class<?>, Set<Class<?>>>();

        Metamodel metamodel = this.entityManager.getMetamodel();
        Set<EntityType<?>> entities = metamodel.getEntities();
        for (EntityType<?> entityType : entities) {
            Class<?> javaType = entityType.getJavaType();
            Set<Class<?>> associatedClasses = this.getAssociatedClasses(entityType);
            if (associatedClasses.isEmpty()) {
                s.add(javaType);
            } else {
                edges.put(javaType, associatedClasses);
                for (Class<?> associatedClass : associatedClasses) {
                    this.addReverseEdge(reverseEdges, associatedClass, javaType);
                }
            }
        }

        while (!s.isEmpty()) {
            Class<?> n = this.pop(s);
            l.add(n);
            // System.out.println("Popping " + n);
            if (reverseEdges.containsKey(n)) {
                for (Class<?> m : reverseEdges.get(n)) {
                    edges.get(m).remove(n);
                    // System.out.println("Removing edge " + m + " -> " + n);
                    if (edges.get(m).isEmpty()) {
                        // System.out.println("Adding " + m);
                        edges.remove(m);
                        s.add(m);
                    }
                }
            }
        }

        if (!edges.isEmpty()) {
            for (Class<?> entry : edges.keySet()) {
                Set<Class<?>> set = edges.get(entry);
                for (Class<?> other : set) {
                    this.logger.error("Remaining edge " + entry + " -> " + other);
                }
            }
            throw new RuntimeException("Having a cycle");
        }

        return l;
    }

    private void addReverseEdge(Map<Class<?>, Set<Class<?>>> reverseEdges, Class<?> associatedClass, Class<?> javaType)
    {
        Set<Class<?>> set = reverseEdges.get(associatedClass);
        if (null == set) {
            set = new HashSet<Class<?>>();
            reverseEdges.put(associatedClass, set);
        }
        set.add(javaType);
    }

    private Class<?> pop(Set<Class<?>> s)
    {
        Iterator<Class<?>> iterator = s.iterator();
        Class<?> n = iterator.next();
        iterator.remove();

        return n;
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
