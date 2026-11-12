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

## Configuração Gradle

`settings.gradle` define o nome do projeto. `build.gradle` aplica `application`, configura `mainClass`, Java 17, UTF-8, `mavenCentral()` e `standardInput = System.in`.

- Tasks formam um DAG: `build` depende de `check` e `assemble`; `check` depende de `test`; `test` depende das tasks de compilação/test classes; `assemble` gera classes e artefatos.
- `application` fornece `run`, `installDist` e `distZip`.
- `implementation` seria para dependências da aplicação; aqui o jogo não tem bibliotecas de runtime.
- `testImplementation` fornece JUnit ao compilar testes; `testRuntimeOnly` fornece o launcher ao executar o mecanismo JUnit.
- O Wrapper versionado baixa e executa a distribuição declarada independentemente de uma instalação global. A instalação local pode ter outra versão.
- No ciclo Gradle, inicialização localiza o projeto e módulos; configuração avalia scripts e constrói o grafo; execução roda apenas as tasks necessárias.

## Testes adicionados

`PosicaoTest` verifica imutabilidade/equivalência; `NaveTest` capacidade e entrega; `AcaoTest` conversão e entrada inválida; `MissaoFactoryTest` quantidades e posições distintas; `GameSessionTest` embarque, entrega/vitória, colisão e ação inválida; `FileRankingRepositoryTest` round-trip isolado em `@TempDir`; `RankingServiceTest` ordenação, limite, corte e limpeza.

Foram executados `clean test` com Gradle 9.7.1 e Java 25, resultando em **10 testes aprovados**. O relatório HTML é gerado em `build/reports/tests/test/index.html`.

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
