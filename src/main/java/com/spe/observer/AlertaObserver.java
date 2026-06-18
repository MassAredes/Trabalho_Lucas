package com.spe.observer;

import com.spe.model.Alerta;

public interface AlertaObserver {
    // Toda classe que quiser ouvir alertas precisa implementar esse metodo.
    void notificar(Alerta alerta);
}
