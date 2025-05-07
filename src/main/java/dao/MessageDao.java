package dao;

import entity.Message;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class MessageDao {

    public void saveMessage(Message message) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(message);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public Message getMessageById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Message.class, id);
        }
    }


}
