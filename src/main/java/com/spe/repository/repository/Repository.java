package com.spe.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    // Salva uma entidade e devolve ela mesma.
    T salvar(T entidade);

    // Busca pelo id.
    Optional<T> buscar(String id);

    // Lista tudo que estiver guardado.
    List<T> listar();
}
