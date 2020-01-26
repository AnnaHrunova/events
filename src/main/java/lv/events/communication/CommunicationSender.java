package lv.events.communication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequiredArgsConstructor
public class CommunicationSender {

    @Value("${smtp.user}")
    private String smtpUser;

    @Value("${smtp.password}")
    private String smtpPassword;

    public void sendEmail(String to, String userName) throws Exception {
        new EmailSender(smtpUser, smtpPassword).sendMail(to, userName);
    }
}
