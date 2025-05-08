package dao;

import entity.Media;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class MediaDAO {

    private final SessionFactory sessionFactory;

    public MediaDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Media media) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(media);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public Media findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Media.class, id);
        }
    }
}
