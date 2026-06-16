package com.spe.observer;

import com.spe.model.Alerta;

public class ConsoleAlertaObserver implements AlertaObserver {
    @Override
    public void notificar(Alerta alerta) {
        // Neste observer o alerta so e mostrado na tela.
        System.out.println(alerta);
    }
}
