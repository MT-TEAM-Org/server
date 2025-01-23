package org.myteam.server.common.certification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.util.CertifyMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificationService {
    private final CertifyMailSender certifyMailSender;

    public void send(String email) {
        certifyMailSender.send(email);
    }

    public boolean certify(String email, String certificationCode) {
        certifyMailSender.print();
        return certifyMailSender.isCodeValid(email, certificationCode);
    }

    public String generateCertificationUrl(String email, String certificationCode) {
        return "http://localhost:8080/api/users/" + email + "/certify?certificationCode=" + certificationCode;
    }
}
