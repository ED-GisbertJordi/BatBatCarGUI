package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class ReservaController {

    @Autowired
    private ViajesRepository viajesRepository;

    @GetMapping("/viaje/reserva")
    public String getViajeReserva(@RequestParam Map<String, String> params, Model model) {
        String codigo = params.get("codigo");
        if (codigo != null) {
            Reserva r = viajesRepository.findReservaByCode(codigo);
            params.put("codigoViaje", String.valueOf(r.getViaje().getCodViaje()));
            params.put("codigo", r.getCodigoReserva());
            params.put("usuario", r.getUsuario());
            params.put("plazas", String.valueOf(r.getPlazasSolicitadas()));
            model.addAttribute("reserva", params);
            return "reserva/reserva_detalle";
        }
        return "redirect:/viajes";
    }


    @GetMapping("/viaje/reserva/add")
    public String getViajeReservaAdd(@RequestParam Map<String, String> params, Model model) {
        if (params.get("codigo") != null) {
            if (params.get("error") != null) {
                model.addAttribute("error", params.get("error"));
            }
            model.addAttribute("codigo", params.get("codigo"));
            return "reserva/reserva_form";
        }
        return "redirect:/viajes";
    }



    @PostMapping("/viaje/reserva/add")
    public String postViajeReservaAdd(@RequestParam Map<String, String> params, Model model, RedirectAttributes redirectAttributes) {
        int codigo = Integer.parseInt(params.get("codigo"));
        String nombre = params.get("usuario");
        int plazas = Integer.parseInt(params.get("plazas"));

        Viaje viaje = viajesRepository.findByCod(codigo);
        // System.out.println(viajesRepository.findViajeSiPermiteReserva(codigo, nombre, plazas));
        
        if (viaje != null && viajesRepository.findViajeSiPermiteReserva(codigo, nombre, plazas)) {
            Reserva reserva = new Reserva(viajesRepository.getNextCodReserva(viaje), nombre, plazas, viaje);
            try {
                viajesRepository.save(reserva);
                redirectAttributes.addAttribute("mensaje", "Reserva realizada correctamente");

            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addAttribute("error", "Error al realizar la reserva, "+ e.getMessage());
            }
        }else{
            redirectAttributes.addAttribute("error", "Error al realizar la reserva");
        }

        return "redirect:/viajes";
    }


    @GetMapping("/viaje/reservas")
    public String getViajeReservas(@RequestParam Map<String, String> param, Model model) {
        if (param.get("codigo")==null) {
            return "redirect:/viajes";
        }else{
            model.addAttribute("reservas", viajesRepository.findReservasByViaje(viajesRepository.findByCod(Integer.parseInt(param.get("codigo")))));
            model.addAttribute("titulo", "Reservas del viaje " + param.get("codigo"));
            
        }
        if (param.get("mensaje") != null) {
            model.addAttribute("mensaje", param.get("mensaje"));
        }
        if (param.get("error") != null) {
            model.addAttribute("error", param.get("error"));
        }
        return "reserva/listado";
    }


    @PostMapping("/viaje/reserva/remove")
    public String postViajeReservaRemove(@RequestParam Map<String, String> params, Model model, RedirectAttributes redirectAttributes) {
        int codigoViaje = Integer.parseInt(params.get("codViaje"));
        String codReserva = params.get("codigo");

        Viaje viaje = viajesRepository.findByCod(codigoViaje);
        if (viaje != null) {
            Reserva reserva = viajesRepository.findReservaByCode(codReserva);
            if (reserva != null) {
                try {
                    viajesRepository.remove(reserva);
                    redirectAttributes.addAttribute("mensaje", "Reserva eliminada correctamente");
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("error", "Error al eliminar la reserva, "+ e.getMessage());
                }
                
            }else{
                redirectAttributes.addAttribute("error", "Error al eliminar la reserva");
            }
        }else{
            redirectAttributes.addAttribute("error", "Error al realizar la reserva");
        }

        return "redirect:/viajes";
    }
    
}