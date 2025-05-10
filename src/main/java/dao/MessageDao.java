package dao;

import entity.Message;
import entity.Media;
import entity.User;
import entity.Chat;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.sql.Timestamp;
import java.util.List;

public class MessageDao {
    public Message saveMessage(
            Long senderId,
            Long chatId,
            String content,
            Long replyToId,
            List<String> mediaUrls
    ) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // reattach full entities
            User sender = session.get(User.class, senderId);
            Chat chat   = session.get(Chat.class, chatId);
            Message replyTo = null;
            if (replyToId != null) {
                replyTo = session.get(Message.class, replyToId);
            }

            // build the message
            Message msg = new Message();
            msg.setSender(sender);
            msg.setChat(chat);
            msg.setContent(content);
            msg.setSentAt(new Timestamp(System.currentTimeMillis()));
            msg.setReplyTo(replyTo);

            session.persist(msg);

            // handle media URLs
            if (mediaUrls != null) {
                for (String url : mediaUrls) {
                    Media m = new Media();
                    m.setUrl(url);
                    m.setMessage(msg);
                    session.persist(m);
                }
            }

            tx.commit();
            return msg;
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
}
