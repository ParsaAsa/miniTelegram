package dao;

import entity.Chat;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class ChatDao {

    public void saveChat(Chat chat) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.save(chat);  // Save entity
            transaction.commit(); // ✅ Commit transaction

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();  // Roll back on error
            throw e;
        }
    }
}
