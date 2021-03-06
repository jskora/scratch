package jfskora.wiz.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

public class WizMessage {
    private long id;

    @Length(max = 3)
    private String content;

    public WizMessage() {

    }

    public WizMessage(long id, String content) {
        this.id = id;
        this.content = content;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String content() {
        return content;
    }
}
