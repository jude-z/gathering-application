package api.common.event;

import org.springframework.context.ApplicationEvent;

public class MailSendEvent extends ApplicationEvent {

    public MailSendEvent(Object source) {
        super(source);
    }
}
