# Sistema de Prevenção de Perdas em Estoque (SPE)

Projeto acadêmico — UC Engenharia de Software. Solução back-end em **Java 17** para prevenção de perdas em estoque por vencimento, extravio e erro humano. Indicado para farmácias, supermercados e almoxarifados.

## 1. Problema

Estoques manuais ou pouco digitalizados sofrem com:
- Produtos vencidos não retirados a tempo
- Extravios sem rastreabilidade
- Erros humanos em entradas/saídas sem auditoria

## 2. Atores

- **Operador**: registra entradas, saídas e ajustes.
- **Gerente**: consulta auditoria, recebe alertas, valida perdas.

## 3. Requisitos Funcionais (escopo implementado)

| ID  | Requisito |
|-----|-----------|
| RF01 | Cadastrar produtos com lote e validade |
| RF02 | Registrar movimentações (ENTRADA, SAIDA, AJUSTE, DESCARTE) |
| RF03 | Manter histórico/auditoria imutável de toda movimentação |
| RF04 | Cadastrar e autenticar usuários (Operador / Gerente) |
| RF05 | Gerar alertas automáticos para produtos vencidos ou próximos do vencimento |
| RF06 | Gerar alertas para estoque abaixo do mínimo (possível extravio) |

## 4. Requisitos Não Funcionais

- Java 17, sem framework externo (apenas JUnit 5 para testes)
- Princípios SOLID aplicados
- Testes unitários com JUnit 5

## 5. Padrões de Projeto

- **Singleton** — `AlertaService` (ponto único de notificação)
- **Factory Method** — `MovimentacaoFactory` (criação de movimentações)
- **Observer** — `EstoqueService` notifica `AlertaObserver` (alertas automáticos)
- **Strategy** — `ValidacaoMovimentacaoStrategy` (regras por tipo de movimentação)
- **Repository** — abstração de persistência (in-memory; trocável)

## 6. SOLID

- **SRP**: cada classe tem responsabilidade única (ex.: `AuditoriaService` só audita).
- **OCP**: novos tipos de alerta/validação entram via novas Strategies/Observers sem alterar `EstoqueService`.
- **LSP**: implementações de `Repository<T>` são intercambiáveis.
- **ISP**: interfaces pequenas (`AlertaObserver`, `ValidacaoMovimentacaoStrategy`).
- **DIP**: serviços dependem de abstrações (`ProdutoRepository`, etc.), não de implementações.

## 7. Estrutura

```
src/main/java/com/spe/
 ├── model/         # Produto, Movimentacao, Usuario, Alerta, enums
 ├── repository/    # Interfaces + InMemory*
 ├── service/       # EstoqueService, AuditoriaService, UsuarioService
 ├── observer/      # AlertaObserver + AlertaService (Singleton)
 ├── factory/       # MovimentacaoFactory
 ├── strategy/      # ValidacaoMovimentacaoStrategy
 ├── exception/     # Exceções de domínio
 └── app/Main.java  # Demonstração via CLI
```

## 8. Como executar

Pré-requisitos: **JDK 17+** e **Maven 3.8+**.

```bash
mvn clean package
java -jar target/sistema-prevencao-estoque-1.0.0.jar
```

## 9. Como rodar os testes

```bash
mvn test
```

## 10. Diagrama de Classes

Ver `docs/diagrama-classes.puml` (PlantUML).

## 11. Equipe


