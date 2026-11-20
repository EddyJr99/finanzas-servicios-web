package co.edu.sena.finanzas.api.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.finanzas.api.dto.ResumenResponse;
import co.edu.sena.finanzas.api.repositorio.ResumenRepositorio;

/** Arma el resumen general de las finanzas a partir de las consultas de agregacion. */
@Service
public class ResumenServicio {

    private final ResumenRepositorio repositorio;

    public ResumenServicio(ResumenRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public ResumenResponse obtener() {
        return new ResumenResponse(repositorio.contarMovimientos(),
                repositorio.sumarMovimientos(), repositorio.totalesPorCategoria(),
                repositorio.totalesPorCuenta());
    }
}
