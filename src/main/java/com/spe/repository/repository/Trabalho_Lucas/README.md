# Trabalho_Lucas
Esse repositório foi criado com o intuito de fazer os commits do trabalho.
## 21/05 Definição do tema - Sistema de Prevenção de Perdas em Estoque 
Problema: Perdas por vencimento, extravio e erro humano.
Funcionalidades:
Controle de validade
Histórico de alterações
Registro de usuários
Auditoria de movimentações
Alertas automáticos
Boa ideia para: Farmácias, supermercados e almoxarifados.
## 11/06 - Complemento de descrição final sobre o projeito, estrutura e forma de execução
## 1. Problema

Estoques manuais ou pouco digitalizados sofrem com:
- Produtos vencidos não retirados a tempo
- Extravios sem rastreabilidade
- Erros humanos em entradas/saídas sem auditoria
## Objetivo da Solução

O Sistema de Prevenção de Perdas em Estoque (SPE) foi desenvolvido para auxiliar empresas no controle preventivo de estoque, reduzindo perdas causadas por vencimento de produtos, erros operacionais e falta de rastreabilidade.

A solução permite registrar movimentações, monitorar níveis mínimos de estoque, controlar vencimentos e gerar alertas automáticos, fornecendo maior segurança e confiabilidade ao processo de gestão de estoque.

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

## 6. Aplicação dos Princípios SOLID

### SRP – Single Responsibility Principle
Cada classe possui uma responsabilidade específica.
Exemplos:
- EstoqueService: gerenciamento do estoque.
- UsuarioService: autenticação e cadastro de usuários.
- AuditoriaService: registro do histórico de movimentações.

### OCP – Open/Closed Principle
O sistema permite extensão sem alteração do código existente.
Novas regras de movimentação podem ser adicionadas por meio de novas implementações de Strategy.

### LSP – Liskov Substitution Principle
Todas as implementações de Repository e Strategy podem substituir suas interfaces sem comprometer o funcionamento do sistema.

### ISP – Interface Segregation Principle
Foram utilizadas interfaces pequenas e específicas, como:
- AlertaObserver
- ValidacaoMovimentacaoStrategy

### DIP – Dependency Inversion Principle
As camadas de serviço dependem de abstrações (interfaces Repository) e não de implementações concretas.

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
O projeto possui testes unitários desenvolvidos com JUnit 5.

Classes testadas:

- EstoqueServiceTest
- UsuarioServiceTest
- MovimentacaoFactoryTest

Os testes validam:

- Cadastro de usuários;
- Autenticação;
- Movimentações de estoque;
- Validações de domínio;
- Criação correta de movimentações.

## 10. Diagrama de Classes

Ver `docs/diagrama-classes.puml` (PlantUML).

## 11. Equipe

Gabriel Arthur Marcos Verlangieri - 12413726
Júlio César De Lima Moreira - 1232021411
Matheus Aredes Santos Silva - 12415406
Ramez Marques de Souza Lima - 12419563
Suzana Patrícia Morais Pereira - 124115854
