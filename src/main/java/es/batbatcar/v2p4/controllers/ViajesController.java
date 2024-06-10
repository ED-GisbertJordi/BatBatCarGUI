package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;
import es.batbatcar.v2p4.utils.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViajesController {

    @Autowired
    private ViajesRepository viajesRepository;
    
    
    @GetMapping("/viaje/add")
    public String getViajesAdd(@RequestParam Map<String, String> params, Model model) {
        if (params.get("error") == null) {
			params.put("error", "");
		}
        model.addAttribute("viaje", params);
        return "viaje/viaje_form";
    }

    @PostMapping("/viaje/add")
    public String postViajesAdd(@RequestParam Map<String, String> params, Model model) {
        model.addAttribute("viajes", viajesRepository.findAll());
        model.addAttribute("titulo", "Listado de viajes");


        String ruta = params.get("ruta");

        int plazas = Integer.parseInt(params.get("plazas"));
        String propietario = params.get("propietario");
        double precio = Double.parseDouble(params.get("precio"));
        int duracion = Integer.parseInt(params.get("duracion"));
        LocalDate fecha = LocalDate.parse(params.get("fecha"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int horas = Integer.parseInt(params.get("horas"));
        int minutos = Integer.parseInt(params.get("minutos"));
        LocalDateTime fechaSalida = fecha.atTime(horas, minutos);
        

        if (Validator.isValidRuta(ruta) && Validator.isValidPlazas(plazas) && Validator.isValidPropietario(propietario) && Validator.isValidPrecio(precio) && Validator.isValidDuracion(duracion) && Validator.isValidFecha(fecha, horas, minutos)){
            try {
                viajesRepository.save(new Viaje(1, propietario, ruta, fechaSalida, duracion, (float) precio, plazas));
                return "viaje/listado";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/viaje/add?error=Error en los datos introducidos";
    }


    /**
     * Endpoint que muestra el listado de todos los viajes disponibles
     *
     * */
    @GetMapping("/viajes")
    public String getViajesAction(Model model) {
        model.addAttribute("viajes", viajesRepository.findAll());
        model.addAttribute("titulo", "Listado de viajes");
        return "viaje/listado";
    }
}
