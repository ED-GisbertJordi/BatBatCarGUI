package es.batbatcar.v2p4.modelo.repositories;

import es.batbatcar.v2p4.exceptions.ReservaAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ReservaNotFoundException;
import es.batbatcar.v2p4.exceptions.ViajeAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ViajeNotCancelableException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dao.inmemorydao.InMemoryReservaDAO;
import es.batbatcar.v2p4.modelo.dao.inmemorydao.InMemoryViajeDAO;
import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.dao.interfaces.ReservaDAO;
import es.batbatcar.v2p4.modelo.dao.interfaces.ViajeDAO;
import es.batbatcar.v2p4.modelo.dao.sqldao.SQLReservaDAO;
import es.batbatcar.v2p4.modelo.dao.sqldao.SQLViajeDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class ViajesRepository {

    private final ViajeDAO viajeDAO;
    private final ReservaDAO reservaDAO;

    // public ViajesRepository(@Autowired InMemoryViajeDAO viajeDAO, @Autowired InMemoryReservaDAO reservaDAO) {
    //     this.viajeDAO = viajeDAO;
    //     this.reservaDAO = reservaDAO;
    // }
    public ViajesRepository(@Autowired SQLViajeDAO viajeDAO, @Autowired SQLReservaDAO reservaDAO) {
        this.viajeDAO = viajeDAO;
        this.reservaDAO = reservaDAO;
    }
    /** 
     * Obtiene un conjunto de todos los viajes
     * @return
     */
    public Set<Viaje> findAll() {
        
    	// Se recuperan todos los viajes del DAO de viajes
    	Set<Viaje> viajes = viajeDAO.findAll();
        
    	// Se completa la información acerca de las reservas de cada viaje a través del DAO de reservas
        for (Viaje viaje : viajes) {
        	if (this.reservaDAO.findAllByTravel(viaje).size() > 0) {
            	viaje.setSeHanRealizadoReservas(true);
            }
		}
        return viajes;
    }

    /** 
     * Obtiene un viaje por su código
     * @return
     */
    public Viaje findByCod(int codViaje) {
        
    	// Se recuperan todos los viajes del DAO de viajes
    	Set<Viaje> viajes = viajeDAO.findAll();
        
        for (Viaje viaje : viajes) {
        	if (viaje.getCodViaje() == codViaje){
            	return viaje;
            }
		}
        return null;
    }

     /** 
     * Obtiene un listado de viajes por su ruta
     * @return
     */
    public Set<Viaje> findAllByRuta(String ruta) {
    	// Se recuperan todos los viajes del DAO de viajes
    	Set<Viaje> viajes = viajeDAO.findAll();
        Set<Viaje> viajesRuta = new HashSet<>();
        for (Viaje viaje : viajes) {
        	if (viaje.getRuta().equals(ruta)){
            	viajesRuta.add(viaje);
            }
		}
        return viajesRuta;
    }
    
    /**
     * Obtiene el código del siguiente viaje
     * @return
     */
    public int getNextCodViaje() {
        return this.viajeDAO.findAll().size() + 1;
    }

    public void cancelarViaje(int codViaje) throws ViajeNotCancelableException, ViajeNotFoundException {
        Viaje viaje = this.findByCod(codViaje);
        if (viaje!=null) {
            viaje.cancelar();
            viajeDAO.update(viaje);   
        }
    }
    
    /**
     * Guarda el viaje (actualiza si ya existe o añade si no existe)
     * @param viaje
     * @throws ViajeAlreadyExistsException
     * @throws ViajeNotFoundException
     */
    public void save(Viaje viaje) throws ViajeAlreadyExistsException, ViajeNotFoundException {
    	
    	if (viajeDAO.findById(viaje.getCodViaje()) == null) {
    		viajeDAO.add(viaje);
    	} else {
    		viajeDAO.update(viaje);
    	}
    }
	
    /**
     * Encuentra todas las reservas de @viaje
     * @param viaje
     * @return
     */
	public List<Reserva> findReservasByViaje(Viaje viaje) {
		return reservaDAO.findAllByTravel(viaje);
	}

    /**
     * Encuentra la reserva de un @viaje
     * @param viaje
     * @return
     */
	public Reserva findReservaByCode(String codigo) {
		Set<Reserva> r = reservaDAO.findAll();
        for (Reserva reserva : r) {
            if (reserva.getCodigoReserva().equals(codigo)) {
                return reserva;
            }
        }
        return null;
	}
	
    /**
     * Se permite hacer la reserva del viaje
     * @param codViaje
     * @param usuario
     * @param plazas
     * @return
     */
    public boolean findViajeSiPermiteReserva(int codViaje, String usuario, int plazas) {
        Viaje viaje = this.findByCod(codViaje);
        List<Reserva> reservas = this.findReservasByViaje(viaje);
        for (Reserva reserva : reservas) {
            if (reserva.getUsuario().equals(usuario)) {
                
                return false;
            }
        }
        if (viaje.getPropietario().equals(usuario)) {
            return false;            
        }

        if (!viaje.estaDisponible()) {
            return false;            
        }

        if (viaje.isCancelado()) {
            return false;            
        }
        
        return ((plazas + reservaDAO.getNumPlazasReservadasEnViaje(viaje)) <= viaje.getPlazasOfertadas());
    }


	/**
	 * Guarda la reserva
	 * @param reserva
	 * @throws ReservaAlreadyExistsException
	 * @throws ReservaNotFoundException
	 */
    public void save(Reserva reserva) throws ReservaAlreadyExistsException, ReservaNotFoundException {
    	
    	if (reservaDAO.findById(reserva.getCodigoReserva()) == null) {
    		reservaDAO.add(reserva);
    	} else {
    		reservaDAO.update(reserva);
    	}
    }
    
    public String getNextCodReserva(Viaje viaje) {
        return viaje.getCodViaje()+"-"+(this.reservaDAO.findAllByTravel(viaje).size() + 1);
    }
    
    /**
     * Elimina la reserva
     * @param reserva
     * @throws ReservaNotFoundException
     */
	public void remove(Reserva reserva) throws ReservaNotFoundException {
		reservaDAO.remove(reserva);
	}
}
