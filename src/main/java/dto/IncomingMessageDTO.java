package dto;

import java.util.List;

public class IncomingMessageDTO {
    public Long senderId;
    public Long chatId;
    public String content;
    public Long replyToId; // optional
    public List<String> mediaUrls; // optional
}
