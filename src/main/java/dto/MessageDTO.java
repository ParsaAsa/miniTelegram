package dto;

import java.util.List;

public class MessageDTO {
    public Long messageId;          // set by server
    public Long senderId;           // set by server
    public Long chatId;             // set by server
    public Long sentAt;             // set by server
    public String content;
    public Long replyToId;          // optional
    public List<String> mediaUrls;  // optional
}