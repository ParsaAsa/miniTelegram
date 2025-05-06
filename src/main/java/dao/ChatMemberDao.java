package dao;

import entity.ChatMember;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class ChatMemberDao {
    public void save(ChatMember chatMember) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(chatMember);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}
