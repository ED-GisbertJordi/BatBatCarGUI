package es.batbatcar.v2p4.modelo.dao.sqldao;

import es.batbatcar.v2p4.exceptions.ViajeAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.services.MySQLConnection;
import es.batbatcar.v2p4.modelo.dao.interfaces.ViajeDAO;
import es.batbatcar.v2p4.modelo.dto.viaje.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

@Repository
public class SQLViajeDAO implements ViajeDAO {

    @Autowired
    private MySQLConnection mySQLConnection;
    private final String NOMBRE_TABLA = "viajes";

    @Override
    public Set<Viaje> findAll() {
        Connection connection = mySQLConnection.getConnection();
        try (
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM "+NOMBRE_TABLA);
        ){
            Set<Viaje> viajes = new TreeSet<>();
            while (rs.next()) {
                viajes.add(mapToViaje(rs));
            }
            return viajes;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Set<Viaje> findAll(String city) {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM "+NOMBRE_TABLA+" WHERE ruta LIKE ?");
        ) {
            pstmt.setString(1, "%" + city + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                Set<Viaje> viajes = new TreeSet<>();
                while (rs.next()) {
                    viajes.add(mapToViaje(rs));
                }
                return viajes;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Set<Viaje> findAll(EstadoViaje estadoViaje) {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM "+NOMBRE_TABLA+" WHERE estado_viaje = ?");
        ) {
            pstmt.setString(1, estadoViaje.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                Set<Viaje> viajes = new TreeSet<>();
                while (rs.next()) {
                    viajes.add(mapToViaje(rs));
                }
                return viajes;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Set<Viaje> findAll(Class<? extends Viaje> viajeClass) {
        // Este método necesita ser implementado de acuerdo a la lógica específica de la aplicación
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public Viaje findById(int codViaje) {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM "+NOMBRE_TABLA+" WHERE cod_viaje = ?");
        ) {
            pstmt.setInt(1, codViaje);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToViaje(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Viaje getById(int codViaje) throws ViajeNotFoundException {
        Viaje viaje = findById(codViaje);
        if (viaje == null) {
            throw new ViajeNotFoundException("El viaje seleccionado no existe");
        }
        return viaje;
    }

    @Override
    public void add(Viaje viaje) throws ViajeAlreadyExistsException {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO viajes (cod_viaje, propietario, ruta, fecha_salida, duracion, precio, plazas_ofertadas, estado_viaje) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
        ) {
            pstmt.setInt(1, viaje.getCodViaje());
            pstmt.setString(2, viaje.getPropietario());
            pstmt.setString(3, viaje.getRuta());
            pstmt.setTimestamp(4, Timestamp.valueOf(viaje.getFechaSalida()));
            pstmt.setLong(5, viaje.getDuracion());
            pstmt.setFloat(6, viaje.getPrecio());
            pstmt.setInt(7, viaje.getPlazasOfertadas());
            pstmt.setString(8, viaje.getEstado().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                throw new ViajeAlreadyExistsException(viaje.getCodViaje());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public void update(Viaje viaje) throws ViajeNotFoundException {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE viajes SET propietario = ?, ruta = ?, fecha_salida = ?, duracion = ?, precio = ?, plazas_ofertadas = ?, estado_viaje = ? WHERE cod_viaje = ?"
            );
        ) {
            pstmt.setString(1, viaje.getPropietario());
            pstmt.setString(2, viaje.getRuta());
            pstmt.setTimestamp(3, Timestamp.valueOf(viaje.getFechaSalida()));
            pstmt.setLong(4, viaje.getDuracion());
            pstmt.setFloat(5, viaje.getPrecio());
            pstmt.setInt(6, viaje.getPlazasOfertadas());
            pstmt.setString(7, viaje.getEstado().name());
            pstmt.setInt(8, viaje.getCodViaje());
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new ViajeNotFoundException(viaje.getCodViaje());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void remove(Viaje viaje) throws ViajeNotFoundException {
        Connection connection = mySQLConnection.getConnection();
        try (
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM "+NOMBRE_TABLA+" WHERE cod_viaje = ?");
        ) {
            pstmt.setInt(1, viaje.getCodViaje());
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new ViajeNotFoundException(viaje.getCodViaje());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Viaje mapToViaje(ResultSet rs) throws SQLException {
        int codViaje = rs.getInt("cod_viaje");
        String propietario = rs.getString("propietario");
        String ruta = rs.getString("ruta");
        LocalDateTime fechaSalida = rs.getTimestamp("fecha_salida").toLocalDateTime();
        long duracion = rs.getLong("duracion");
        float precio = rs.getFloat("precio");
        int plazasOfertadas = rs.getInt("plazas_ofertadas");
        EstadoViaje estado = EstadoViaje.valueOf(rs.getString("estado_viaje"));
        return new Viaje(codViaje, propietario, ruta, fechaSalida, duracion, precio, plazasOfertadas, estado);
    }
}
