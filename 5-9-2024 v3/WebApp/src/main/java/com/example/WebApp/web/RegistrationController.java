package com.example.WebApp.web;

import com.example.WebApp.entity.Registration;
import com.example.WebApp.repository.RegistrationRepository;
import com.example.WebApp.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    private Boolean active = false;
    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/submitForm")
    public String submitForm(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpServletRequest request,
            Model model
    ) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = "";
        Registration useCheck = registrationRepository.findByEmail(email);

        if (useCheck == null) {
            // Email doesn't exist in the database, proceed with registration
            if (password.equals(confirmPassword)) {
                // Encrypt the password
                encodedPassword = passwordEncoder.encode(password);
            } else {
                // Passwords do not match, handle the error
                model.addAttribute("passwordMismatch", true);
                return "regi.html";
            }

            // Process the form data here (e.g., save to a database, send confirmation email, etc.)
            Registration user = new Registration();
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(encodedPassword);
            user.setSuperUser(false);

            // Save the registration object to the database using the repository instance
            registrationRepository.save(user);

            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("active", true);

            // Send registration confirmation email
            emailService.sendRegistrationEmail(email);

            // Set session attributes
            session.setAttribute("email", user.getEmail());
            session.setAttribute("firstName", user.getFirstname());
            session.setAttribute("lastName", user.getLastname());
            session.setAttribute("phoneNumber", user.getPhone());

            // Redirect the user to a success page
            return "home.html";
        } else {
            // Email already exists in the database, notify the user
            model.addAttribute("emailFound", true);
            return "regi.html";
        }
    }


    @RequestMapping("/profile")
    public String editAccount() {
        return "edit_profile.html";
    }

    @RequestMapping("/delete-profile")
    public String deleteAccount() {
        return "delete_account.html";
    }

    @PostMapping("/edit-profile")
    public String editProfile() {
        return "home.html";
    }

    @PostMapping("/delete-account")
    public String deleteProfile(@RequestParam("email") String email, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Registration user = registrationRepository.findByEmail(email);
        registrationRepository.delete(user);
        active = false;
        session.invalidate();
        return "index.html";
    }
}

