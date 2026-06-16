package com.spe.observer;

import com.spe.model.Alerta;

public class ConsoleAlertaObserver implements AlertaObserver {
    @Override
    public void notificar(Alerta alerta) {
        System.out.println(alerta);
    }
}
