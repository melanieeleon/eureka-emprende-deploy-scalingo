package com.example.eureka.formulario.port.in;

import com.example.eureka.formulario.infrastructure.dto.response.FormularioResponseDTO;

import java.util.List;

public interface FormularioService {

    FormularioResponseDTO getFormularioByTipo(String tipoFormulario);


}