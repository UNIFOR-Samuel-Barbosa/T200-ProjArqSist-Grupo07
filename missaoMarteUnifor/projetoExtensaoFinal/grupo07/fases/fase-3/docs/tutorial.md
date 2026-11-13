# Fase 3 — Introdução ao libGDX

## De console a aplicação gráfica

A Fase 2 usa aplicação Java de módulo único e entrada pelo terminal. A Fase 3 separa `core` (regras e UI libGDX portável) de `lwjgl3` (launcher desktop). O backend traduz chamadas da API para a plataforma; o core não precisa conhecer o launcher. A versão libGDX está centralizada em `gdxVersion = 1.12.1`.

| Fase 2 | Fase 3 |
|---|---|
| `Main` | `Lwjgl3Launcher` cria as dependências e inicia a janela |
| `ConsoleGame` | `MenuScreen`, `NovaPartidaScreen`, `PartidaScreen`, `RankingScreen` |
| `ConsoleRenderer` | `PartidaScreen` com `ShapeRenderer` e `BitmapFont` |
| Teclas lidas como texto | `TecladoParaAcao` adapta teclas a `Acao` |
| Aplicação módulo único | `core` e `lwjgl3` |

## Comparação com a implementação do professor

A referência do professor tem 33 fontes Java, com `GameScreen` como tela de partida, e o README anuncia 39 testes. A implementação do Grupo 07 tem 30 fontes Java de produção e 12 testes. As duas seguem `Game`/`Screen`, backend LWJGL3, entrada convertida em `Acao` e regras fora das telas, mas o escopo visual e a cobertura automatizada não são equivalentes.

| Tema | Professor | Grupo 07 | Decisão e vantagem |
|---|---|---|---|
| Ranking | `RankingService` passa para `application` nesta fase, junto com a porta/entrada, para liberar o console do acoplamento. | Serviço, porta e entrada já estavam em `application.ranking` desde a Fase 1; o launcher injeta a persistência em arquivo. | A interface gráfica reutiliza uma política já isolada e mantém o domínio/aplicação independentes da infraestrutura. |
| Telas de jogo | `GameScreen` recebe uma `GameSession` pronta; `NovaPartidaScreen` aceita nome limitado a 12 caracteres e troca dificuldade com setas. | `PartidaScreen` cria sua sessão usando a fábrica; a dificuldade é escolhida por F1/F2/F3 e o nome aceita até 24 caracteres. | Menos passagem de objetos entre telas e implementação direta do fluxo; como trade-off, a tela de partida conhece a fábrica e a seleção de dificuldade é menos discoverable que as setas. |
| Apresentação | Grade de 48 px, objetos coloridos por camada, painel lateral separado, mensagens e janela de fim de partida. | Grade 11×11 mais simples, cores/símbolos e indicadores no mesmo espaço da tela. | Menos código de layout para validar a integração do libGDX; visual e legibilidade são mais simples que a referência. |
| Desenho de entidades | Agrupa objetos em camadas para desenhar fundo, categorias e nave por último. | Usa a ordem da lista renderizável (base, passageiros, perigos e nave) para manter a nave à frente. | Reutiliza o contrato do domínio com menos estruturas auxiliares; não permite configurar cores/camadas de modo tão independente. |
| Testes | 39 testes anunciados no README do professor, cobrindo build e mais comportamentos do cliente. | 12 testes: 10 de domínio/aplicação/persistência e 2 para teclado/geometria. | Os testes são headless, sem OpenGL; a cobertura é menor e a janela não foi validada visualmente nesta execução. |

```mermaid
flowchart LR
  c["console"] --> a["application"] --> d["domain"]
  i["infrastructure.ranking"] --> a
```

```mermaid
flowchart LR
  lwjgl["lwjgl3 launcher"] --> gdx["core.gdx / Screens"]
  gdx --> app["application"]
  app --> domain["domain"]
  infra["infrastructure.ranking"] --> app
  gdx --> api["libGDX API"]
  lwjgl --> backend["LWJGL3 backend"]
```

## Conceitos da API presentes

`Game` centraliza os recursos compartilhados e seleciona telas; cada `Screen` tem renderização e entrada próprias. `setScreen` delega o comportamento à tela atual, aproximando o padrão State. `TelaBase` concentra log e ciclo de vida (`show`, `resize`, `pause`, `resume`, `hide`, `dispose`), instala/remove o `InputProcessor` e aplica o viewport.

O jogo cria câmera, `FitViewport(800, 600)`, `SpriteBatch`, `ShapeRenderer` e `BitmapFont` uma vez em `create()`. `FitViewport` mantém a proporção ao redimensionar. Recursos gráficos são liberados por `MissaoMarteGame.dispose()`. A partida atualiza regras apenas ao receber tecla válida, mas desenha o estado em cada `render()`.

`MapaGeometria` converte coordenadas sem depender de libGDX: o eixo y do domínio cresce para baixo, enquanto a tela gráfica cresce para cima. `TecladoParaAcao` converte letras e setas em ações, e tecla não mapeada vira `INVALIDA`, que não consome turno. Os símbolos vêm de `ObjetoRenderizavel`; a nave é adicionada ao fim da lista para ficar sobre outros desenhos.

### Decisões de arquitetura

- As regras, `GameSession` e entidades foram copiadas do código do Grupo 07 da Fase 2 para `core`; fontes comuns de aplicação/domínio/infraestrutura permanecem iguais.
- `RankingRepository`, `RankingEntry` e `RankingService` já estavam em `application.ranking` desde a Fase 1 para respeitar a direção das dependências; por isso não precisaram ser movidos nesta fase. O launcher injeta a implementação em arquivo e o serviço é compartilhado pelas telas.
- `MapaGeometria` é uma fabricação pura para isolar conversão/testes. `TecladoParaAcao` é um adaptador entre códigos de tecla e ações do caso de uso.
- O `lwjgl3` é o composition root: cria fábrica, ranking, `Random`, configura janela 800×600, VSync e 60 FPS. A tarefa `run` usa a raiz da fase como diretório; `-XstartOnFirstThread` é condicional para macOS.

## Testes e evidências

Os 15 testes herdados da Fase 2 exercitam regras, validação de dificuldade, ranking e retorno de feedback sem abrir janela. `MapaGeometriaTest` cobre eixo x, inversão de y e centro da célula; `TecladoParaAcaoTest` cobre letras, setas, embarque, saída e tecla sem ação. `clean build` com Gradle 9.7.1/Java 25 foi concluído com sucesso, incluindo **17 testes**. Os testes não requerem OpenGL.

O ambiente não tem JDK 17; o wrapper está fixado em Gradle 8.7 e o build foi validado com Gradle 9.7.1 já instalado em cache. A janela não foi executada/inspecionada visualmente nesta validação; portanto, os logs de lifecycle, controles e proporção precisam de conferência manual com JDK 17 e ambiente gráfico. Ao iniciar, cada tela registra os eventos recebidos com `Gdx.app.log`; a ordem efetiva de `pause()`/`dispose()` só é evidenciada ao fechar a janela.

As correções de feedback da Fase 1 também foram copiadas para o código comum do `core`: os movimentos reportam posição/limite e inimigos não podem ocupar células com passageiros.

Comandos previstos:

```powershell
.\gradlew --version
.\gradlew clean build
.\gradlew :lwjgl3:run --console=plain
```

## Limitações

1. A interface usa formas e fonte padrão; sprites, animações e som ficam para a Fase 4.
2. O ranking continua persistido em arquivo local e a tela mostra somente texto simples.
3. A seleção de dificuldade usa teclas de função e requer polimento visual/feedback.
4. A conversão de coordenadas e o desenho assumem raio fixo 5; um viewport configurável e layouts responsivos são possíveis em evolução futura.

## Materiais didáticos e fontes consultadas

**Referências da implementação:** enunciado da Fase 3 (`exercicio10/fases/fase-03-libgdx/docs/fase-03-enunciado.md`), README da fase e fontes da Fase 2 do Grupo 07.

**Referências consultadas na revisão desta documentação:** `missaoMarteUnifor/grasp/apostilas-grasp/tutorial-grasp-missao-marte.md`, `missaoMarteUnifor/apostilas-gof/factory-method/APOSTILA.md` e `missaoMarteUnifor/apostilas-gof/adapter/APOSTILA.md`, para conferir a terminologia e os limites dos padrões citados. `TecladoParaAcao` faz uma conversão entre a interface do teclado e as ações da aplicação; trata-se de uma adaptação simples.
