package edu.uam.gestion.config;

import edu.uam.gestion.model.Cliente;
import edu.uam.gestion.model.Pedido;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.Properties;

public final class HibernateUtil {

    private static final SessionFactory FACTORY = crear();

    private HibernateUtil() {
    }

    private static SessionFactory crear() {
        try {
            return new Configuration()
                    .addProperties(cargar())
                    .addAnnotatedClass(Cliente.class)
                    .addAnnotatedClass(Pedido.class)
                    .buildSessionFactory();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo iniciar Hibernate. Revise PostgreSQL y hibernate.properties", e);
        }
    }

    private static Properties cargar() {
        try (var archivo = HibernateUtil.class.getResourceAsStream("/hibernate.properties")) {
            if (archivo == null) {
                throw new IllegalStateException("Falta hibernate.properties");
            }
            var propiedades = new Properties();
            propiedades.load(archivo);
            return propiedades;
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo configuración", e);
        }
    }

    public static SessionFactory factory() {
        return FACTORY;
    }

    public static void cerrar() {
        if (FACTORY != null && !FACTORY.isClosed()) {
            FACTORY.close();
        }
    }
}
