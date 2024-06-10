package es.batbatcar.v2p4.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {

	// Incluye aquí el resto de métodos de validación que necesites
	
    public static boolean isValidDateTime(String dateTime) {
        try {
            LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidTime(String time) {
        try {
            LocalDate.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidRuta(String ruta) {
        return ruta.matches("^[A-Z][a-z]+-[A-Z][a-z]+$");
    }

    public static boolean isValidPlazas(int plazas) {
        return plazas > 0;
    }

    public static boolean isValidPropietario(String propietario) {
        return propietario.matches("^[A-Z][a-z]+ [A-Z][a-z]+$");
    }

    public static boolean isValidPrecio(double precio) {
        return precio > 0;
    }

    public static boolean isValidDuracion(int duracion) {
        return duracion > 0;
    }

    public static boolean isValidFecha(LocalDate salida, int horas, int minutos) {
        return salida != null && horas >= 0 && horas < 24 && minutos >= 0 && minutos < 60;
    }

}

