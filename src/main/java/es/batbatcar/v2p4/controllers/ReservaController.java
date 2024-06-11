package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class ReservaController {

    @Autowired
    private ViajesRepository viajesRepository;


    @GetMapping("/viaje/reserva/add")
    public String getViajeAdd(@RequestParam Map<String, String> params, Model model) {
        if (params.get("codigo") != null) {
            if (params.get("error") == null) {
                params.put("error", "");
            }
            model.addAttribute("reserva", params);
            return "reserva/reserva_form";
        }
        return "redirect:/viajes";
    }



    @PostMapping("/viaje/reserva/add")
    public String postViajeAdd(@RequestParam Map<String, String> params, Model model) {
        if (params.get("codigo") != null) {
            if (params.get("error") == null) {
                params.put("error", "");
            }
            model.addAttribute("reserva", params);
            return "reserva/reserva_form";
        }
        return "redirect:/viajes";
    }
    
}