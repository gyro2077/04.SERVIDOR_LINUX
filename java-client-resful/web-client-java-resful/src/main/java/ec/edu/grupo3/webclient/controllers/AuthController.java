package ec.edu.grupo3.webclient.controllers;

import ec.edu.grupo3.webclient.models.LoginViewModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final String VALID_USER = "MONSTER";
    private static final String VALID_PASS = "MONSTER9";

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("login", new LoginViewModel());
        return "auth/login";
    }

    @PostMapping("/login")
    public String loginPost(@ModelAttribute("login") LoginViewModel login,
                            HttpSession session,
                            Model model) {

        if (VALID_USER.equals(login.getUsername().trim())
                && VALID_PASS.equals(login.getPassword())) {
            session.setAttribute("USER", login.getUsername().trim());
            return "redirect:/conversion";
        }

        model.addAttribute("errorMessage", "Usuario o contraseña incorrectos.");
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}