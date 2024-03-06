package socialmedia.chatapp.websocket.mto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
public class MessageTransferObject {
    private String type;
    private Object content;

    public MessageTransferObject() {
    }

    public MessageTransferObject(String type, Object content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
