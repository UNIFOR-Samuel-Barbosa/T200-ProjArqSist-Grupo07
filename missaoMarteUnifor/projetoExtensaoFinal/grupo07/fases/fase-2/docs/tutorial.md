# Fase 2 — Migração do build para Gradle

## Origem e destino

A Fase 1 compilava fontes com `javac` por `build.ps1`; não tinha dependências externas nem testes automatizados. Esta pasta mantém os fontes da Fase 1 e adota o layout padrão `src/main/java` e `src/test/java`, o plugin `application`, JUnit 5 e o Wrapper Gradle 8.7.

| Fase 1 | Fase 2 |
|---|---|
| `build.ps1` | `gradlew clean build` |
| `src/main/java` do projeto | `src/main/java` do Gradle |
| `build\classes` | `build/classes` gerado pelo Gradle |
| Validação manual | JUnit em `src/test/java` |
| JDK disponível na máquina | Wrapper fixa a distribuição Gradle 8.7 |

Os fontes Java de produção foram copiados da implementação do Grupo 07 na Fase 1, sem modificação nesta migração.

## Comparação com a implementação do professor

A versão do professor declara que os fontes da Fase 1 são somente movidos para `src/main/java` e entrega 26 classes Java de produção. Aqui também mantivemos os fontes próprios sem alteração na migração, mas a origem é a Fase 1 do Grupo 07, com 24 classes; portanto, eles não são cópias dos fontes do professor. A configuração essencial do build segue os requisitos comuns: Java 17, UTF-8, `application`, JUnit 5 e Wrapper Gradle 8.7.

| Tema | Professor | Grupo 07 | Decisão e vantagem |
|---|---|---|---|
| Fontes migrados | 26 classes herdadas da refatoração do professor. | 24 classes herdadas da nossa Fase 1, preservadas por hash nesta migração. | Mantém a continuidade da nossa implementação independente e facilita verificar que a Fase 2 não mudou as regras da Fase 1 do grupo. |
| Testes | 28 casos de teste na versão de referência (conforme a introdução do enunciado da Fase 3). | 10 casos distribuídos em sete classes de teste. | Priorizamos os fluxos principais e execução rápida; a cobertura é menor que a referência e deixa mais cenários de borda para trabalho futuro. |
| Ranking | `RankingRepository` e `RankingEntry` ficam em `infrastructure`; a regra Top 10 continua no fluxo de `ConsoleGame`, conforme o enunciado da Fase 3. | Porta, entrada e `RankingService` ficam em `application.ranking` desde a Fase 1 do grupo. | O console e uma futura UI podem reutilizar a regra sem importá-la ou copiá-la da apresentação; antecipamos a separação pedida para a Fase 3. |
| Build e teclado | Wrapper 8.7, Java 17, `application`, testes JUnit e encaminhamento de `System.in`. | Mesmos requisitos centrais e configurações. | Mantivemos o padrão pedido para que os comandos sejam familiares e o console continue interativo. |
| Validação no ambiente | O roteiro do professor espera verificação com JDK 17. | Testamos com Gradle 9.7.1/Java 25 disponível, além de registrar a limitação do ambiente. | Foi possível validar compilação, testes e execução sem alterar a versão fixada no Wrapper; falta confirmar com JDK 17. |

## Configuração Gradle

`settings.gradle` define o nome do projeto. `build.gradle` aplica `application`, configura `mainClass`, Java 17, UTF-8, `mavenCentral()` e `standardInput = System.in`.

- Tasks formam um DAG: `build` depende de `check` e `assemble`; `check` depende de `test`; `test` depende das tasks de compilação/test classes; `assemble` gera classes e artefatos.
- `application` fornece `run`, `installDist` e `distZip`.
- `implementation` seria para dependências da aplicação; aqui o jogo não tem bibliotecas de runtime.
- `testImplementation` fornece JUnit ao compilar testes; `testRuntimeOnly` fornece o launcher ao executar o mecanismo JUnit.
- O Wrapper versionado baixa e executa a distribuição declarada independentemente de uma instalação global. A instalação local pode ter outra versão.
- No ciclo Gradle, inicialização localiza o projeto e módulos; configuração avalia scripts e constrói o grafo; execução roda apenas as tasks necessárias.

## Testes adicionados

`PosicaoTest` verifica imutabilidade/equivalência; `NaveTest` capacidade e entrega; `AcaoTest` conversão e entrada inválida; `DificuldadeTest` valida nomes de dificuldade e rejeita desconhecidos; `MissaoFactoryTest` quantidades e posições distintas; `MissaoTest` impede inimigos de encobrir passageiros; `GameSessionTest` embarque, entrega/vitória, colisão, movimento/bordas e ação inválida; `FileRankingRepositoryTest` round-trip isolado em `@TempDir`; `RankingServiceTest` ordenação, limite, corte e limpeza.

Foram executados `clean test` com Gradle 9.7.1 e Java 25, resultando em **15 testes aprovados**. O relatório HTML é gerado em `build/reports/tests/test/index.html`.

Os fontes da Fase 1 foram corrigidos nesta rodada e atualizados aqui para manter a migração coerente. Além das regras, a partida agora redesenha o estado antes de pedir o próximo comando, reexibe o menu após aborto e valida a dificuldade em vez de escolher uma opção padrão sem avisar.

## Validação e ambiente

O Wrapper desta fase aponta para Gradle 8.7 conforme o enunciado. O ambiente disponível não tem JDK 17 e `gradle` global; possui Java 25. Como Gradle 8.7 não é compatível com essa JVM, a validação de tarefas foi feita com Gradle 9.7.1 já disponível na máquina, mantendo `sourceCompatibility`/`targetCompatibility` em 17. Portanto, `gradlew --version` e a execução real no Java 17 ainda precisam ser confirmadas em um ambiente com JDK 17. A saída do smoke test pelo PowerShell exibiu caracteres acentuados substituídos; a compilação usa UTF-8, então configure o terminal Windows para UTF-8 (por exemplo, `chcp 65001`) ao conferir a saída interativa.

Comandos esperados na pasta:

```powershell
.\gradlew --version
.\gradlew clean build
.\gradlew test
.\gradlew run --console=plain
.\gradlew installDist
```

Na execução, `standardInput` encaminha os comandos do terminal ao `Scanner`. O arquivo `ranking.json` é relativo ao diretório de execução, que é a raiz da fase.

## Limitações e próximos passos

1. O projeto ainda é um único módulo; a Fase 3 o separa em `core` e `lwjgl3`.
2. As regras têm cobertura focada, não exaustiva; os relatórios HTML ajudam a localizar testes e falhas.
3. A UI continua em console; a Fase 3 troca somente a apresentação e preserva as regras de domínio/aplicação.

## Materiais didáticos e fontes consultadas

**Referências da implementação:** enunciado da Fase 2 (`exercicio10/fases/fase-02-gradle/docs/fase-02-enunciado.md`), configuração Gradle de referência (`exercicio10/fases/fase-02-gradle/build.gradle`) e fontes da Fase 1 do Grupo 07.

**Referências consultadas na revisão desta documentação:** `missaoMarteUnifor/grasp/apostilas-grasp/tutorial-grasp-missao-marte.md`, `missaoMarteUnifor/apostilas-gof/factory-method/APOSTILA.md` e `missaoMarteUnifor/apostilas-gof/adapter/APOSTILA.md`, para contextualizar os padrões mencionados nas fases.
