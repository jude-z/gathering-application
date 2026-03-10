package mail.service;

import common.event.Event;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import mail.common.provider.MailProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSendService {
    private final MailProvider mailProvider;

    private void emailCertification(Event<> event) throws MessagingException {
        String certificationNumber = certificationNumber();
        String email = emailCertificationRequest.getEmail();
        Certification certification = Certification.of(email,certificationNumber);
        certificationRepository.save(certification);
        emailProvider.sendCertificationMail(emailCertificationRequest.getEmail(), certificationNumber);
    }

    private String certificationNumber(){
        int number = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(number);
    }
}
