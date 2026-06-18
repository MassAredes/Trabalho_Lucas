# Sistema de Prevenção de Perdas em Estoque (SPE)

Este projeto foi desenvolvido com a ideia de ajudar no controle de estoque e evitar perdas causadas por vencimento de produtos, estoque baixo, saídas sem controle e erros humanos. A proposta do sistema é registrar tudo o que acontece com os produtos e avisar quando surgir alguma situação de risco.

## 1. Sobre o projeto

Em muitos estoques, principalmente quando o controle é manual ou muito simples, alguns problemas acontecem com frequência:

- produtos vencem e ninguém percebe a tempo;
- o estoque fica abaixo do mínimo sem aviso;
- acontecem saídas erradas ou sem rastreio;
- depois fica difícil saber quem fez cada movimentação.

Pensando nisso, o sistema foi feito para:

- cadastrar produtos com lote, validade e estoque mínimo;
- cadastrar usuários;
- registrar entradas, saídas, ajustes e descartes;
- guardar histórico das movimentações;
- gerar alertas automáticos;
- facilitar auditoria e conferência.

## 2. Tecnologias usadas e por quê

### Java

O sistema foi desenvolvido em **Java** porque a linguagem ajuda bastante a organizar o projeto em classes, objetos e camadas. Como a ideia do trabalho era aplicar orientação a objetos e padrões de projeto, Java fez bastante sentido.

### Maven

O **Maven** foi usado para organizar o projeto, compilar, empacotar e rodar os testes. Ele também facilita o controle das dependências.

Trecho do `pom.xml`:

```xml
<properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

O que esse trecho mostra:

- o projeto está configurado para usar Java 21;
- a codificação está em UTF-8;
- os testes usam JUnit 5.

### JUnit 5

O **JUnit 5** foi usado para testar o sistema. Eu usei essa tecnologia porque ela ajuda a verificar se as regras de negócio realmente estão funcionando, sem precisar testar tudo manualmente toda hora.

### Padrões usados no projeto

No desenvolvimento do sistema, foram usados padrões de projeto para deixar o código mais organizado e fácil de manter.

- **Strategy**: foi aplicado para separar as regras de cada tipo de movimentação do estoque.

Exemplo de código:

```java
estrategias.put(TipoMovimentacao.ENTRADA, new EntradaStrategy());
estrategias.put(TipoMovimentacao.SAIDA, new SaidaStrategy());
estrategias.put(TipoMovimentacao.AJUSTE, new AjusteStrategy());
estrategias.put(TipoMovimentacao.DESCARTE, new DescarteStrategy());
```

Nesse trecho, cada tipo de movimentação recebe sua própria regra. Isso ajuda porque entrada, saída, ajuste e descarte não precisam ser tratados todos no mesmo bloco de `if`.

- **Factory**: foi usado para criar os registros de movimentação de forma padronizada e segura.

Exemplo de código:

```java
if (quantidade <= 0 && tipo != TipoMovimentacao.AJUSTE)
    throw new DominioException("quantidade deve ser positiva");

return new Movimentacao(produtoId, tipo, quantidade, usuarioId, observacao);
```

Esse trecho mostra que a movimentação só é criada se os dados forem válidos. Assim, o sistema evita criar registros errados logo no começo.

- **Observer**: permitiu gerar alertas automáticos para situações como estoque baixo e produtos vencidos.

Exemplo de código:

```java
public void disparar(Alerta alerta) {
    historico.add(alerta);
    for (AlertaObserver o : observers) o.notificar(alerta);
}
```

Aqui, quando um alerta é criado, ele é salvo no histórico e depois enviado para todos os observadores cadastrados. Foi assim que o sistema conseguiu avisar automaticamente quando alguma situação importante aconteceu.

- **Repository**: organizou o acesso aos dados de produtos, usuários e movimentações.

Exemplo de código:

```java
public Produto salvar(Produto p) {
    dados.put(p.getId(), p);
    return p;
}
```

Esse trecho mostra um produto sendo salvo no repositório em memória usando o `id` como chave. A ideia aqui foi separar o armazenamento dos dados da regra principal do sistema.

Esses padrões foram importantes para melhorar a estrutura do sistema, evitar erros e facilitar futuras melhorias.

## 3. Arquitetura do projeto

O sistema foi organizado em camadas para não deixar tudo misturado no mesmo lugar.

```text
src/main/java/com/spe/
 ├── app/          -> ponto de entrada do sistema
 ├── model/        -> classes principais, como Produto e Usuario
 ├── service/      -> regras de negócio
 ├── repository/   -> acesso aos dados
 ├── strategy/     -> regras separadas por tipo de movimentação
 ├── factory/      -> criação padronizada de movimentações
 ├── observer/     -> alertas automáticos
 └── exception/    -> exceções de regra de negócio
```

Por que usar essa arquitetura:

- deixa o código mais organizado;
- facilita entender a função de cada parte;
- ajuda na manutenção;
- permite trocar uma parte sem destruir o resto;
- facilita os testes.

### Exemplo de montagem do sistema

Trecho de [Main.java](/d:/Trabalho%20lucas/spe/src/main/java/com/spe/app/Main.java):

```java
ProdutoRepository produtoRepo = new InMemoryProdutoRepository();
MovimentacaoRepository movRepo = new InMemoryMovimentacaoRepository();
UsuarioRepository usuarioRepo = new InMemoryUsuarioRepository();

AlertaService alertas = AlertaService.getInstance();
alertas.registrar(new ConsoleAlertaObserver());

AuditoriaService auditoria = new AuditoriaService(movRepo);
UsuarioService usuarios = new UsuarioService(usuarioRepo);
EstoqueService estoque = new EstoqueService(produtoRepo, auditoria, alertas);
```

O que esse trecho faz:

- cria os repositórios em memória;
- cria o serviço de alertas;
- registra um observador para mostrar alertas no console;
- monta os serviços principais do sistema.

Na prática, esse pedaço mostra como a aplicação foi separada em partes e depois conectada. Isso ajuda porque cada classe fica responsável só pelo seu papel.

## 4. Padrões e estrutura usados no código

### 4.1 Strategy

O padrão **Strategy** foi usado para separar as regras de cada tipo de movimentação. Em vez de fazer um `if` enorme para tudo, cada operação tem sua própria classe.

Trecho de [EstoqueService.java](/d:/Trabalho%20lucas/spe/src/main/java/com/spe/service/EstoqueService.java):

```java
this.estrategias = new EnumMap<>(TipoMovimentacao.class);

estrategias.put(TipoMovimentacao.ENTRADA, new EntradaStrategy());
estrategias.put(TipoMovimentacao.SAIDA, new SaidaStrategy());
estrategias.put(TipoMovimentacao.AJUSTE, new AjusteStrategy());
estrategias.put(TipoMovimentacao.DESCARTE, new DescarteStrategy());
```

O que esse trecho faz:

- cria um mapa de estratégias;
- liga cada tipo de movimentação à sua regra;
- permite que entrada, saída, ajuste e descarte tenham comportamentos diferentes.

Por que usar:

- deixa o código mais limpo;
- facilita adicionar novos tipos de movimentação depois;
- evita concentrar toda a lógica em um lugar só.

### 4.2 Regra principal do estoque

Trecho de [EstoqueService.java](/d:/Trabalho%20lucas/spe/src/main/java/com/spe/service/EstoqueService.java):

```java
public void registrarMovimentacao(Movimentacao m) {
    Produto p = buscarProduto(m.getProdutoId());
    ValidacaoMovimentacaoStrategy s = estrategias.get(m.getTipo());

    if (s == null) throw new DominioException("tipo de movimentacao nao suportado");

    s.validar(p, m);
    s.aplicar(p, m);
    auditoria.registrar(m);
    verificarAlertas(p);
}
```

O que acontece aqui, passo a passo:

- o sistema descobre qual produto será alterado;
- escolhe a estratégia certa para aquele tipo de movimentação;
- valida se a operação pode acontecer;
- aplica a mudança no estoque;
- salva no histórico;
- verifica se precisa gerar alerta.

Esse é um dos trechos mais importantes do projeto, porque ele mostra o fluxo principal do sistema.

### 4.3 Factory

O padrão **Factory** foi usado para criar movimentações de forma padronizada e segura.

Trecho de [MovimentacaoFactory.java](/d:/Trabalho%20lucas/spe/src/main/java/com/spe/factory/MovimentacaoFactory.java):

```java
public static Movimentacao criar(String produtoId, TipoMovimentacao tipo, int quantidade,
                                 String usuarioId, String observacao) {
    if (produtoId == null || produtoId.isBlank())
        throw new DominioException("produtoId obrigatorio");
    if (usuarioId == null || usuarioId.isBlank())
        throw new DominioException("usuarioId obrigatorio");
    if (tipo == null) throw new DominioException("tipo obrigatorio");

    if (quantidade <= 0 && tipo != TipoMovimentacao.AJUSTE)
        throw new DominioException("quantidade deve ser positiva");

    if (tipo == TipoMovimentacao.AJUSTE && quantidade < 0)
        throw new DominioException("ajuste nao pode ser negativo");

    return new Movimentacao(produtoId, tipo, quantidade, usuarioId, observacao);
}
```

O que esse trecho faz:

- valida os dados antes de criar a movimentação;
- impede criação com produto vazio, usuário vazio ou quantidade inválida;
- devolve uma movimentação pronta para uso.

Por que usar:

- evita criar objetos errados;
- centraliza as validações;
- deixa a criação mais organizada.

### 4.4 Observer e Singleton

O sistema também usa **Observer** para os alertas e **Singleton** para manter um único serviço central de notificação.

Trecho de [AlertaService.java](/d:/Trabalho%20lucas/spe/src/main/java/com/spe/observer/AlertaService.java):

```java
private static volatile AlertaService instance;
private final List<AlertaObserver> observers = new ArrayList<>();

public static AlertaService getInstance() {
    if (instance == null) {
        synchronized (AlertaService.class) {
            if (instance == null) instance = new AlertaService();
        }
    }
    return instance;
}

public void disparar(Alerta alerta) {
    historico.add(alerta);
    for (AlertaObserver o : observers) o.notificar(alerta);
}
```

O que esse trecho faz:

- garante que exista uma instância principal do serviço de alertas;
- mantém uma lista de observadores;
- quando um alerta acontece, salva no histórico;
- depois avisa todos os observadores cadastrados.

Por que usar:

- centraliza os alertas;
- deixa o sistema flexível;
- permite mudar a forma de aviso sem mexer na lógica principal do estoque.

### 4.5 Repository

O padrão **Repository** foi usado para separar a lógica de negócio da forma como os dados são guardados.

Trecho de [InMemoryProdutoRepository.java](/d:/Trabalho%20lucas/spe/src/main/java/com/spe/repository/InMemoryProdutoRepository.java):

```java
private final Map<String, Produto> dados = new HashMap<>();

public Produto salvar(Produto p) {
    dados.put(p.getId(), p);
    return p;
}

public Optional<Produto> buscar(String id) {
    return Optional.ofNullable(dados.get(id));
}

public List<Produto> listar() {
    return new ArrayList<>(dados.values());
}
```

O que esse trecho faz:

- usa um `Map` para simular um banco de dados em memória;
- salva produtos usando o `id` como chave;
- permite buscar um produto específico;
- também permite listar todos os produtos.

Por que usar:

- deixa o acesso aos dados separado da regra de negócio;
- facilita trocar depois o armazenamento em memória por banco de dados;
- deixa o projeto mais organizado.

## 5. Regras de negócio representadas no código

Uma parte importante do projeto foi transformar o problema do estoque em regras reais no sistema.

Trecho de [EstoqueService.java](/d:/Trabalho%20lucas/spe/src/main/java/com/spe/service/EstoqueService.java):

```java
if (p.getValidade().isBefore(hoje)) {
    alertas.disparar(new Alerta(TipoAlerta.VENCIDO,
            "Produto " + p.getNome() + " (lote " + p.getLote() + ") vencido em " + p.getValidade()));
} else {
    long dias = ChronoUnit.DAYS.between(hoje, p.getValidade());

    if (dias <= diasAvisoVencimento) {
        alertas.disparar(new Alerta(TipoAlerta.VENCIMENTO_PROXIMO,
                "Produto " + p.getNome() + " vence em " + dias + " dia(s)"));
    }
}

if (p.getQuantidade() <= p.getEstoqueMinimo()) {
    alertas.disparar(new Alerta(TipoAlerta.ESTOQUE_BAIXO,
            "Produto " + p.getNome() + " com estoque " + p.getQuantidade()
                    + " (minimo " + p.getEstoqueMinimo() + ") - possivel extravio"));
}
```

Esse trecho mostra bem o objetivo do sistema, porque ele verifica:

- se o produto já venceu;
- se o vencimento está próximo;
- se o estoque está abaixo do mínimo.

Ou seja, esse código representa diretamente a ideia de prevenção de perdas.

## 6. Testes do sistema

Os testes foram importantes porque eles ajudam a provar que o sistema não está funcionando só “na teoria”, mas também na prática.

### 6.1 Teste de entrada no estoque

Trecho de [EstoqueServiceTest.java](/d:/Trabalho%20lucas/spe/src/test/java/com/spe/EstoqueServiceTest.java):

```java
@Test
void entradaAumentaQuantidade() {
    Produto p = estoque.cadastrarProduto(
            new Produto("X", "L1", LocalDate.now().plusMonths(6), 0, 5));

    estoque.registrarMovimentacao(MovimentacaoFactory.criar(
            p.getId(), TipoMovimentacao.ENTRADA, 10, "u1", ""));

    assertEquals(10, p.getQuantidade());
    assertEquals(1, auditoria.historicoPorProduto(p.getId()).size());
}
```

O que esse teste verifica:

- cria um produto com estoque zerado;
- registra uma entrada de 10 unidades;
- confirma se a quantidade virou 10;
- confirma se a movimentação foi salva no histórico.

Esse teste é importante porque mostra que o sistema movimenta o estoque e registra auditoria ao mesmo tempo.

### 6.2 Teste de bloqueio de saída inválida

Trecho de [EstoqueServiceTest.java](/d:/Trabalho%20lucas/spe/src/test/java/com/spe/EstoqueServiceTest.java):

```java
@Test
void saidaAcimaDoEstoqueFalha() {
    Produto p = estoque.cadastrarProduto(
            new Produto("X", "L1", LocalDate.now().plusMonths(6), 2, 0));

    assertThrows(DominioException.class, () -> estoque.registrarMovimentacao(
            MovimentacaoFactory.criar(p.getId(), TipoMovimentacao.SAIDA, 5, "u1", "")));
}
```

O que esse teste verifica:

- cria um produto com apenas 2 unidades;
- tenta retirar 5;
- espera uma exceção.

Esse teste mostra que o sistema protege o estoque contra saídas impossíveis.

### 6.3 Teste de cadastro e autenticação de usuário

Trecho de [UsuarioServiceTest.java](/d:/Trabalho%20lucas/spe/src/test/java/com/spe/UsuarioServiceTest.java):

```java
@Test
void cadastroEAutenticacaoOk() {
    UsuarioService s = new UsuarioService(new InMemoryUsuarioRepository());

    s.cadastrar("ana", "senha123", PapelUsuario.GERENTE);

    assertTrue(s.autenticar("ana", "senha123").isPresent());
    assertFalse(s.autenticar("ana", "errada").isPresent());
}
```

O que esse teste verifica:

- o usuário consegue ser cadastrado;
- a autenticação funciona com senha correta;
- a autenticação falha com senha incorreta.

Esse teste é importante porque mostra que a parte de usuário também foi validada, e não só o estoque.

### 6.4 Teste da Factory

Trecho de [MovimentacaoFactoryTest.java](/d:/Trabalho%20lucas/spe/src/test/java/com/spe/MovimentacaoFactoryTest.java):

```java
@Test
void rejeitaQuantidadeInvalida() {
    assertThrows(DominioException.class,
            () -> MovimentacaoFactory.criar("p1", TipoMovimentacao.SAIDA, 0, "u1", ""));
}
```

O que esse teste verifica:

- tenta criar uma movimentação com quantidade zero;
- espera erro de regra de negócio.

Esse teste mostra que a validação começa já na criação do objeto, antes mesmo de chegar na regra principal do estoque.

## 7. Como executar

Pré-requisitos:

- JDK compatível com a versão configurada no `pom.xml`;
- Maven instalado.

Para compilar e gerar o `.jar`:

```bash
mvn clean package
```

Para executar:

```bash
java -jar target/sistema-prevencao-estoque-1.0.0.jar
```

## 8. Como rodar os testes

```bash
mvn test
```

Os testes cobrem principalmente:

- cadastro e autenticação de usuários;
- criação e validação de movimentações;
- entrada e saída de estoque;
- alerta de vencimento;
- alerta de estoque baixo;
- proteção contra regras inválidas.

## 9. Conclusão

De forma simples, este projeto não foi feito só para cadastrar produto. A ideia principal foi construir um sistema que ajudasse a prevenir perdas no estoque e ao mesmo tempo mostrasse conceitos importantes de programação orientada a objetos, padrões de projeto, arquitetura em camadas e testes.

Foi por isso que eu usei:

- camadas separadas, para organizar melhor;
- Strategy, para separar as regras de cada movimentação;
- Factory, para criar movimentações com validação;
- Observer e Singleton, para os alertas;
- Repository, para separar o acesso aos dados;
- JUnit, para testar se tudo realmente funciona.

## 10. Aplicação dos Princípios SOLID

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


## 11. Equipe

- Gabriel Arthur Marcos Verlangieri - 12413726
- Júlio César De Lima Moreira - 1232021411
- Matheus Aredes Santos Silva - 12415406
- Ramez Marques de Souza Lima - 12419563
- Suzana Patrícia Morais Pereira - 124115854
