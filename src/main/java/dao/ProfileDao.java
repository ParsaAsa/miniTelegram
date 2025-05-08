package dao;

import entity.Profile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class ProfileDao {

    private EntityManager entityManager;

    public ProfileDao() {
        this.entityManager = Persistence.createEntityManagerFactory("your-persistence-unit-name").createEntityManager();
    }

    public void save(Profile profile) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(profile);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Profile findById(Long id) {
        return entityManager.find(Profile.class, id);
    }

    public Profile findByUserId(Long userId) {
        try {
            return entityManager.createQuery("FROM Profile WHERE user.id = :userId", Profile.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Profile> findAll() {
        return entityManager.createQuery("FROM Profile", Profile.class).getResultList();
    }

    public void update(Profile profile) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(profile);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(Profile profile) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.remove(profile);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
