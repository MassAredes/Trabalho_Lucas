package com.spe.observer;

import com.spe.model.Alerta;

import java.util.ArrayList;
import java.util.List;

// Esse servico centraliza todos os alertas do sistema.
// Foi feito como singleton para existir uma instancia compartilhada.
public class AlertaService {
    // "volatile" ajuda na seguranca quando mais de uma thread acessa a instancia.
    private static volatile AlertaService instance;
    // Lista de objetos interessados em receber os alertas.
    private final List<AlertaObserver> observers = new ArrayList<>();
    // Historico para consultar depois o que ja foi avisado.
    private final List<Alerta> historico = new ArrayList<>();

    private AlertaService() {}

    public static AlertaService getInstance() {
        if (instance == null) {
            synchronized (AlertaService.class) {
                // Double check para nao criar duas instancias ao mesmo tempo.
                if (instance == null) instance = new AlertaService();
            }
        }
        return instance;
    }

    // Metodo usado nos testes para comecar com o singleton limpo.
    static void resetForTests() { instance = null; }

    // Adiciona um novo "ouvinte" que vai receber os alertas.
    public void registrar(AlertaObserver obs) { observers.add(obs); }

    // Remove um observer quando ele nao precisa mais receber aviso.
    public void remover(AlertaObserver obs) { observers.remove(obs); }

    public void disparar(Alerta alerta) {
        // Primeiro salva no historico.
        historico.add(alerta);
        // Depois avisa todos os observers cadastrados.
        for (AlertaObserver o : observers) o.notificar(alerta);
    }

    public List<Alerta> getHistorico() {
        // Devolve uma copia da lista de alertas.
        // Isso evita que outra parte do programa altere o historico original sem querer.
        return List.copyOf(historico);
    }
}
