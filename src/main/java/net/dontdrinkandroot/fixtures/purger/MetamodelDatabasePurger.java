/*
 * Copyright (C) 2017 Philip Washington Sorst <philip@sorst.net>
 * and individual contributors as indicated
 * by the @authors tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dontdrinkandroot.fixtures.purger;

import net.dontdrinkandroot.fixtures.dependencyresolution.DirectedGraph;
import net.dontdrinkandroot.fixtures.dependencyresolution.TopologicalSort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
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
        Class<?> javaType = entityType.getJavaType();
        Set<Class<?>> associatedClasses = new HashSet<>();
        Set<Attribute<? super T, ?>> attributes = entityType.getAttributes();
        for (Attribute<? super T, ?> attribute : attributes) {
            String name = attribute.getName();
            if (!this.isIgnored(javaType, name) && attribute.isAssociation()) {
                PersistentAttributeType attributeType = attribute.getPersistentAttributeType();
                SingularAttribute<? super T, ?> singularAttribute;
                switch (attributeType) {
                    case MANY_TO_ONE:
                        associatedClasses.add(attribute.getJavaType());
                        break;
                    case ONE_TO_ONE:
                        singularAttribute = (SingularAttribute<? super T, ?>) attribute;
                        if (!singularAttribute.isOptional()) {
                            associatedClasses.add(singularAttribute.getJavaType());
                        }
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
                    case MANY_TO_MANY:
                    case ONE_TO_MANY:
                    case BASIC:
                        /* Ignored */
                }
            }
        }

        return associatedClasses;
    }

    /**
     * Overwrite this method in order to ignore certain properties. This can be usefull if cycles in the dependency
     * graph need to be resolved manually.
     *
     * @param clazz    The class to check.
     * @param property The property of the class to check.
     * @return Whether the property should be included in building the dependency graph.
     */
    protected boolean isIgnored(Class<?> clazz, String property)
    {
        return false;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
}
