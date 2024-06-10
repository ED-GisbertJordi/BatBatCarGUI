package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainControler {

    @GetMapping("/")
    public String getRaiz(@RequestParam Map<String, String> params, Model model) {
		String accion = params.get("accion");
		if (accion == null) {
            return "index";
        }

		switch(accion) {
		case "crear":
			return "redirect:/viaje/add";
		// case "eliminar":
		// 	return "redirect:/borrar";
		// case "mostrar":
		// 	return "redirect:/buscar";
		case "mostrarTodos":
			return "redirect:/viajes";
		default:
			return "notImplemented_View";
		}
	}
}
