package co.edu.sena.finanzas.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de los servicios web REST del SGFP (usuarios, cuentas, medios de pago,
 * categorias, movimientos y resumen).
 *
 * {@code @SpringBootApplication} activa la configuracion automatica de Spring Boot
 * y busca los componentes (controladores, servicios, repositorios) de este paquete.
 */
@SpringBootApplication
public class FinanzasApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanzasApiApplication.class, args);
    }
}
