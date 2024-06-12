package es.batbatcar.v2p4.modelo.dao.sqldao;

import es.batbatcar.v2p4.exceptions.ReservaAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ReservaNotFoundException;
import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.services.MySQLConnection;
import es.batbatcar.v2p4.modelo.dao.interfaces.ReservaDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SQLReservaDAO implements ReservaDAO {

    @Autowired
    private MySQLConnection mySQLConnection;
	private final String NOMBRE_TABLA = "reservas";

    @Override
    public Set<Reserva> findAll() {
        Connection connection = mySQLConnection.getConnection();
        try (
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM "+NOMBRE_TABLA);
        ){
            Set<Reserva> reservas = new HashSet<>();
            while (rs.next()) {
                reservas.add(mapToReserva(rs));
            }
            return reservas;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Reserva findById(String id) {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM "+NOMBRE_TABLA+" WHERE cod_reserva = ?");
        ) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToReserva(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Reserva getById(String id) throws ReservaNotFoundException {
        Reserva reserva = findById(id);
        if (reserva == null) {
            throw new ReservaNotFoundException(id);
        }
        return reserva;
    }

    @Override
    public List<Reserva> findAllByUser(String user) {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM "+NOMBRE_TABLA+" WHERE usuario = ?");
        ) {
            pstmt.setString(1, user);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Reserva> reservas = new ArrayList<>();
                while (rs.next()) {
                    reservas.add(mapToReserva(rs));
                }
                return reservas;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Reserva> findAllByTravel(Viaje viaje) {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM "+NOMBRE_TABLA+" WHERE cod_viaje = ?");
        ) {
            pstmt.setInt(1, viaje.getCodViaje());
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Reserva> reservas = new ArrayList<>();
                while (rs.next()) {
                    reservas.add(mapToReserva(rs));
                }
                return reservas;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int getNumPlazasReservadasEnViaje(Viaje viaje) {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("SELECT SUM(plazas_solicitadas) FROM "+NOMBRE_TABLA+" WHERE cod_viaje = ?");
        ) {
            pstmt.setInt(1, viaje.getCodViaje());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Reserva findByUserInTravel(String usuario, Viaje viaje) {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM "+NOMBRE_TABLA+" WHERE usuario = ? AND cod_viaje = ?");
        ) {
            pstmt.setString(1, usuario);
            pstmt.setInt(2, viaje.getCodViaje());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToReserva(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Reserva> findAllBySearchParams(Viaje viaje, String searchParams) {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM "+NOMBRE_TABLA+" WHERE cod_viaje = ? AND (usuario LIKE ? OR cod_reserva LIKE ?)");
        ) {
            pstmt.setInt(1, viaje.getCodViaje());
            String searchPattern = "%" + searchParams.toLowerCase() + "%";
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Reserva> reservas = new ArrayList<>();
                while (rs.next()) {
                    reservas.add(mapToReserva(rs));
                }
                return reservas;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void add(Reserva reserva) throws ReservaAlreadyExistsException {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO "+NOMBRE_TABLA+" (cod_reserva, usuario, plazas_solicitadas, fecha_realizacion, cod_viaje) VALUES (?, ?, ?, ?, ?)");
        ) {
            pstmt.setString(1, reserva.getCodigoReserva());
            pstmt.setString(2, reserva.getUsuario());
            pstmt.setInt(3, reserva.getPlazasSolicitadas());
            pstmt.setTimestamp(4, Timestamp.valueOf(reserva.getFechaRealizacion()));
            pstmt.setInt(5, reserva.getViaje().getCodViaje());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                throw new ReservaAlreadyExistsException(reserva);
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public void update(Reserva reserva) throws ReservaNotFoundException {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("UPDATE "+NOMBRE_TABLA+" SET usuario = ?, plazas_solicitadas = ?, fecha_realizacion = ?, cod_viaje = ? WHERE cod_reserva = ?");
        ) {
            pstmt.setString(1, reserva.getUsuario());
            pstmt.setInt(2, reserva.getPlazasSolicitadas());
            pstmt.setTimestamp(3, Timestamp.valueOf(reserva.getFechaRealizacion()));
            pstmt.setInt(4, reserva.getViaje().getCodViaje());
            pstmt.setString(5, reserva.getCodigoReserva());
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new ReservaNotFoundException(reserva.getCodigoReserva());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void remove(Reserva reserva) throws ReservaNotFoundException {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM "+NOMBRE_TABLA+" WHERE cod_reserva = ?");
        ) {
            pstmt.setString(1, reserva.getCodigoReserva());
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new ReservaNotFoundException(reserva.getCodigoReserva());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Reserva mapToReserva(ResultSet rs) throws SQLException {
        String cod = rs.getString("cod_reserva");
        String usuario = rs.getString("usuario");
        int plazas = rs.getInt("plazas_solicitadas");
        LocalDateTime fecha = rs.getTimestamp("fecha_realizacion").toLocalDateTime();
        int codViaje = rs.getInt("cod_viaje");
        return new Reserva(cod, usuario, plazas, fecha, new Viaje(codViaje));
    }
}
