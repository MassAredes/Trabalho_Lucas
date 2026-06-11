package com.spe.observer;

import com.spe.model.Alerta;

public interface AlertaObserver {
    void notificar(Alerta alerta);
}
