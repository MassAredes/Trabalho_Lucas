package com.spe.observer;

import com.spe.model.Alerta;
import java.util.ArrayList;
import java.util.List;

/** Singleton + Observer subject. Centraliza disparo de alertas. */
public class AlertaService {
    private static volatile AlertaService instance;
    private final List<AlertaObserver> observers = new ArrayList<>();
    private final List<Alerta> historico = new ArrayList<>();

    private AlertaService() {}

    public static AlertaService getInstance() {
        if (instance == null) {
            synchronized (AlertaService.class) {
                if (instance == null) instance = new AlertaService();
            }
        }
        return instance;
    }

    /** Apenas para testes — permite resetar o singleton. */
    static void resetForTests() { instance = null; }

    public void registrar(AlertaObserver obs) { observers.add(obs); }
    public void remover(AlertaObserver obs) { observers.remove(obs); }

    public void disparar(Alerta alerta) {
        historico.add(alerta);
        for (AlertaObserver o : observers) o.notificar(alerta);
    }

    public List<Alerta> getHistorico() { return List.copyOf(historico); }
}
