package docSharing.controller;

import docSharing.entities.User;
import docSharing.entities.UserDTO;
import docSharing.event.OnRegistrationSuccessEvent;
import docSharing.service.IUserService;
import docSharing.service.UserService;
import javafx.concurrent.Service;
import jdk.jfr.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.apache.juli.logging.Log;


@Controller
public class AccountController {

    private static Logger logger = LogManager.getLogger(AuthController.class.getName());
    @Autowired
    private IUserService service;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @PostMapping("/registration")
    public String registerNewUser(@ModelAttribute("user") UserDTO userDto, BindingResult result, WebRequest request, Model model) throws Exception {
        User registeredUser = new User();
        String userEmail = userDto.getEmail();
//        if (result.hasErrors()) {
//            return "registration";
//        }
        registeredUser = UserService.findByEmail(userEmail);
        if(registeredUser!=null) {
            model.addAttribute("error","There is already an account with this email: " + userEmail);
            logger.info("There is already an account with this email: " + userEmail);
            return "registration";
        }

        registeredUser = service.registerUser(userDto);
        try {
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationSuccessEvent(registeredUser, request.getLocale(),appUrl));
        }catch(Exception re) {
            re.printStackTrace();
			throw new Exception("Error while sending confirmation email");
        }
        return "registrationSuccess";
    }


}

