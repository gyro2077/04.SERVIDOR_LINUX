package ec.edu.grupo3.webclient.controllers;

import ec.edu.grupo3.webclient.models.ConversionResponse;
import ec.edu.grupo3.webclient.models.ConversionViewModel;
import ec.edu.grupo3.webclient.services.RestServiceModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/conversion")
public class ConversionController {

    private final RestServiceModel restServiceModel;

    public ConversionController(RestServiceModel restServiceModel) {
        this.restServiceModel = restServiceModel;
    }

    @GetMapping
    public String index(HttpSession session, Model model) {
        if (session.getAttribute("USER") == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("conversion", ConversionViewModel.defaultModel());
        return "conversion/index";
    }

    @PostMapping
    public String convert(@ModelAttribute("conversion") ConversionViewModel form,
                          HttpSession session,
                          Model model) {
        if (session.getAttribute("USER") == null) {
            return "redirect:/auth/login";
        }

        try {
            ConversionResponse response = restServiceModel.convert(
                form.getCategory(),
                form.getInputValue(),
                form.getFromUnit(),
                form.getToUnit()
            );

            form.setResultValue(response.resultValue());
            form.setSuccessMessage("Conversion exitosa: "
                + form.getInputValue() + " " + form.getFromUnit()
                + " = " + response.resultValue() + " " + form.getToUnit());
        } catch (Exception ex) {
            form.setErrorMessage(ex.getMessage());
        }

        model.addAttribute("conversion", form);
        return "conversion/index";
    }
}