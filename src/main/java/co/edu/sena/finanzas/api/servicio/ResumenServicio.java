package co.edu.sena.finanzas.api.servicio;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.finanzas.api.dto.ResumenResponse;
import co.edu.sena.finanzas.api.repositorio.ResumenRepositorio;

/** Arma el resumen de las finanzas (general o de un usuario) con las consultas de agregacion. */
@Service
public class ResumenServicio {

    private final ResumenRepositorio repositorio;

    public ResumenServicio(ResumenRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public ResumenResponse obtener(Integer idUsuario) {
        BigDecimal ingresos = repositorio.sumarPorTipo("INGRESO", idUsuario);
        BigDecimal gastos = repositorio.sumarPorTipo("GASTO", idUsuario);
        return new ResumenResponse(repositorio.contarMovimientos(idUsuario), ingresos, gastos,
                ingresos.subtract(gastos), repositorio.totalesPorCategoria(idUsuario),
                repositorio.totalesPorCuenta(idUsuario));
    }
}
