package edu.uam.gestion.service;

import edu.uam.gestion.config.HibernateUtil;
import edu.uam.gestion.model.Cliente;
import edu.uam.gestion.repository.ClienteRepository;

import java.util.List;

public class ClienteService {

    private final ClienteRepository repo = new ClienteRepository();

    public List<Cliente> listar() {
        try (var session = HibernateUtil.factory().openSession()) {
            return repo.listar(session);
        }
    }

    public void crear(String nombre, String correo) {
        validar(nombre, correo);
        try (var session = HibernateUtil.factory().openSession()) {
            var tx = session.beginTransaction();
            try {
                if (repo.existeCorreo(session, correo.trim(), null)) {
                    throw new IllegalArgumentException("Ya existe un cliente con ese correo");
                }
                repo.guardar(session, new Cliente(nombre.trim(), correo.trim()));
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    public void actualizar(Long id, String nombre, String correo) {
        validar(nombre, correo);
        try (var session = HibernateUtil.factory().openSession()) {
            var tx = session.beginTransaction();
            try {
                Cliente cliente = repo.obtener(session, id);
                if (cliente == null) {
                    throw new IllegalArgumentException("Cliente no encontrado");
                }
                if (repo.existeCorreo(session, correo.trim(), id)) {
                    throw new IllegalArgumentException("Ya existe otro cliente con ese correo");
                }
                cliente.actualizar(nombre.trim(), correo.trim());
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    public void eliminar(Long id) {
        try (var session = HibernateUtil.factory().openSession()) {
            var tx = session.beginTransaction();
            try {
                Cliente cliente = repo.obtener(session, id);
                if (cliente == null) {
                    throw new IllegalArgumentException("Cliente no encontrado");
                }
                repo.eliminar(session, cliente);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    private void validar(String nombre, String correo) {
        if (nombre == null || nombre.isBlank() || nombre.trim().length() > 120) {
            throw new IllegalArgumentException("Nombre obligatorio (máx. 120)");
        }
        if (correo == null || !correo.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") || correo.trim().length() > 150) {
            throw new IllegalArgumentException("Correo inválido");
        }
    }
}
