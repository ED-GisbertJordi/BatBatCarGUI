package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;
import es.batbatcar.v2p4.utils.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViajesController {

    @Autowired
    private ViajesRepository viajesRepository;
    
    @GetMapping("/viaje")
    public String getViaje(@RequestParam Map<String, String> params, Model model) {
        if (params.get("codigo") == null) {
            return "redirect:/viajes";
        }
        model.addAttribute("viaje", viajesRepository.findByCod(Integer.parseInt(params.get("codigo"))));
        return "viaje/viaje_detalle";
    }
    
    @GetMapping("/viaje/add")
    public String getViajeAdd(@RequestParam Map<String, String> params, Model model) {
        if (params.get("error") == null) {
			params.put("error", "");
		}
        model.addAttribute("viaje", params);
        return "viaje/viaje_form";
    }

    @PostMapping("/viaje/add")
    public String postViajeAdd(@RequestParam Map<String, String> params, Model model) {
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
                viajesRepository.save(new Viaje(viajesRepository.getNextCodViaje(), propietario, ruta, fechaSalida, duracion, (float) precio, plazas));
                return "viaje/listado";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("error", "Error en los datos introducidos");
        return "redirect:/viaje/add";
    }


    /**
     * Endpoint que muestra el listado de todos los viajes disponibles
     *
     * */
    @GetMapping("/viajes")
    public String getViajesAction(@RequestParam Map<String, String> param, Model model) {
        if (param.get("ruta")==null) {
            model.addAttribute("viajes", viajesRepository.findAll());
            model.addAttribute("titulo", "Listado de Viajes");   
        }else{
            Set<Viaje> viajes = viajesRepository.findAllByRuta(param.get("ruta"));
            model.addAttribute("titulo", "Viajes a " + param.get("ruta"));
            for (Viaje viaje : viajes) {
                model.addAttribute("viajes", viaje);
            }
        }
        return "viaje/listado";
    }
}
