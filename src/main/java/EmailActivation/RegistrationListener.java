package EmailActivation;

import docSharing.entities.User;
import docSharing.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private AuthService authService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        try {
            this.confirmRegistration(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) throws Exception {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        authService.createVerificationToken(user, token);

        String email = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = "localhost:8080/" + event.getAppUrl() + "auth/registrationConfirm?token=" + token;
//        String message = messages.getMessage("message.regSucc", null, event.getLocale());

        new GMailer().sendMail(email, subject, "Please follow the link to activate your account: " + confirmationUrl );
    }
}