package es.batbatcar.v2p4.modelo.dao.sqldao;

import es.batbatcar.v2p4.exceptions.ReservaAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ReservaNotFoundException;
import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.services.MySQLConnection;
import es.batbatcar.v2p4.modelo.dao.interfaces.ReservaDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.Cache.Connection;
import org.springframework.stereotype.Repository;

import java.sql.*;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SQLReservaDAO implements ReservaDAO {

	@Autowired
	private MySQLConnection mySQLConnection;

    @Override
    public Set<Reserva> findAll() {
        Connection connection = (Connection) mySQLConnection.getConnection();
		try (
			java.sql.Statement stmt = ((java.sql.Connection) connection).createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Reserva");
		){
			Set<Reserva> reservas = new HashSet<>();
			while (rs.next()) {
				reservas.add(mapToReserva(rs));
			}
			return (Set<Reserva>) reservas;
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
    }

    @Override
    public Reserva findById(String id) {
    	throw new RuntimeException("Not yet implemented");
    }

    @Override
    public ArrayList<Reserva> findAllByUser(String user) {
    	throw new RuntimeException("Not yet implemented");
    }

    @Override
    public ArrayList<Reserva> findAllByTravel(Viaje viaje) {
    	throw new RuntimeException("Not yet implemented");
    }

    @Override
    public Reserva getById(String id) throws ReservaNotFoundException {
    	throw new RuntimeException("Not yet implemented");
    }

    @Override
    public List<Reserva> findAllBySearchParams(Viaje viaje, String searchParams) {
    	throw new RuntimeException("Not yet implemented");
    }

    @Override
	public void add(Reserva reserva) throws ReservaAlreadyExistsException {
    	throw new RuntimeException("Not yet implemented");
		
	}
	@Override
	public void update(Reserva reserva) throws ReservaNotFoundException {
		throw new RuntimeException("Not yet implemented");
		
	}
	@Override
	public void remove(Reserva reserva) throws ReservaNotFoundException {
		throw new RuntimeException("Not yet implemented");
		
	}
	@Override
	public int getNumPlazasReservadasEnViaje(Viaje viaje) {
		throw new RuntimeException("Not yet implemented");
	}
	@Override
	public Reserva findByUserInTravel(String usuario, Viaje viaje) {
		throw new RuntimeException("Not yet implemented");	
	}

	public Reserva mapToReserva(ResultSet rs) throws SQLException {
		String cod = rs.getString("cod_reserva");
		String usuario = rs.getString("usuario");
		int plazas = rs.getInt("plazas_solicitadas");
		LocalDateTime fecha = rs.getTimestamp("fecha_realizacion").toLocalDateTime();
		int codViaje = rs.getInt("cod_viaje");
		return new Reserva(cod, usuario, plazas, fecha, new Viaje(codViaje));
	}
}
