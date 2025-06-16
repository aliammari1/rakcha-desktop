package com.esprit.services.base;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Generic base service providing common CRUD operations for entities. Reduces
 * boilerplate code across service classes.
 *
 * @param <T>
 *            The entity type this service manages
 * @param <ID>
 *            The ID type of the entity
 */
public abstract class BaseService<T, ID> {

    protected static final Logger logger = Logger.getLogger(BaseService.class.getName());
    private static EntityManagerFactory entityManagerFactory;

    protected final Class<T> entityClass;

    static {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("default");
        } catch (Exception e) {
            Logger.getLogger(BaseService.class.getName()).log(Level.SEVERE, "Failed to create EntityManagerFactory", e);
        }
    }

    protected BaseService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Get EntityManager instance
     */
    protected EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Create a new entity
     */
    public void create(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            logger.info("Created entity: " + entity.getClass().getSimpleName());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error creating entity", e);
            throw new PersistenceException("Failed to create entity", e);
        } finally {
            em.close();
        }
    }

    /**
     * Find entity by ID
     */
    public Optional<T> findById(ID id) {
        EntityManager em = getEntityManager();
        try {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding entity by ID: " + id, e);
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Find all entities
     */
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> rootEntry = cq.from(entityClass);
            CriteriaQuery<T> all = cq.select(rootEntry);
            TypedQuery<T> allQuery = em.createQuery(all);
            return allQuery.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all entities", e);
            throw new PersistenceException("Failed to retrieve entities", e);
        } finally {
            em.close();
        }
    }

    /**
     * Update an existing entity
     */
    public T update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T mergedEntity = em.merge(entity);
            em.getTransaction().commit();
            logger.info("Updated entity: " + entity.getClass().getSimpleName());
            return mergedEntity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error updating entity", e);
            throw new PersistenceException("Failed to update entity", e);
        } finally {
            em.close();
        }
    }

    /**
     * Delete entity by ID
     */
    public void deleteById(ID id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
                logger.info("Deleted entity with ID: " + id);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error deleting entity by ID: " + id, e);
            throw new PersistenceException("Failed to delete entity", e);
        } finally {
            em.close();
        }
    }

    /**
     * Delete an entity
     */
    public void delete(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(entity)) {
                entity = em.merge(entity);
            }
            em.remove(entity);
            em.getTransaction().commit();
            logger.info("Deleted entity: " + entity.getClass().getSimpleName());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error deleting entity", e);
            throw new PersistenceException("Failed to delete entity", e);
        } finally {
            em.close();
        }
    }

    /**
     * Check if entity exists by ID
     */
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    /**
     * Count total entities
     */
    public long count() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> rootEntry = cq.from(entityClass);
            cq.select(cb.count(rootEntry));
            return em.createQuery(cq).getSingleResult();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error counting entities", e);
            return 0L;
        } finally {
            em.close();
        }
    }

    /**
     * Find entities with pagination
     */
    public List<T> findWithPagination(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> rootEntry = cq.from(entityClass);
            CriteriaQuery<T> all = cq.select(rootEntry);
            TypedQuery<T> allQuery = em.createQuery(all);
            allQuery.setFirstResult(page * size);
            allQuery.setMaxResults(size);
            return allQuery.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding entities with pagination", e);
            throw new PersistenceException("Failed to retrieve entities with pagination", e);
        } finally {
            em.close();
        }
    }

    /**
     * Execute a custom query
     */
    protected List<T> executeQuery(String jpql, Object... parameters) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error executing custom query", e);
            throw new PersistenceException("Failed to execute custom query", e);
        } finally {
            em.close();
        }
    }

    /**
     * Execute a custom update/delete query
     */
    protected int executeUpdate(String jpql, Object... parameters) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            var query = em.createQuery(jpql);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            int result = query.executeUpdate();
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error executing update query", e);
            throw new PersistenceException("Failed to execute update query", e);
        } finally {
            em.close();
        }
    }

    /**
     * Batch insert entities for better performance
     */
    public void batchInsert(List<T> entities, int batchSize) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            for (int i = 0; i < entities.size(); i++) {
                em.persist(entities.get(i));
                if (i % batchSize == 0 && i > 0) {
                    em.flush();
                    em.clear();
                }
            }
            em.getTransaction().commit();
            logger.info("Batch inserted " + entities.size() + " entities");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error during batch insert", e);
            throw new PersistenceException("Failed to batch insert entities", e);
        } finally {
            em.close();
        }
    }

    /**
     * Close the EntityManagerFactory
     */
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
