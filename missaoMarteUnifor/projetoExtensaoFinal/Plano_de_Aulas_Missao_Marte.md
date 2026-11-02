# Projeto Missão Marte — Plano de ensino e evolução arquitetural

## Objetivo
Evoluir o Exercício 10, um jogo Java em console, para um jogo gráfico multiplataforma (desktop e Android) com autenticação e ranking global. Preservar a lógica existente e ensinar evolução incremental, arquitetura e boas práticas.

## Sete fases

1. **Refatoração em Java (console):** analisar o código existente; aplicar SOLID, GRASP e padrões de projeto pertinentes; separar regras de jogo, entrada e apresentação. Entrega: jogo funcional e diagrama antes/depois.
2. **Gradle:** migrar a compilação e execução para Gradle, mantendo comportamento. Entrega: build reproduzível e instruções de execução.
3. **Estrutura LibGDX:** introduzir ciclo de vida, `Game`, `Screen`, `create`, `render` e `dispose`; adaptar a organização do projeto preservando a lógica, inicialmente sem exigir gráficos completos. Entrega: projeto LibGDX executável.
4. **Interface gráfica:** incorporar sprites, movimento, entrada, colisões, HUD e sons. Entrega: jogo desktop gráfico jogável.
5. **Spring Boot e ranking:** implementar cadastro, login obrigatório, autenticação, API REST e persistência do histórico de partidas. Ranking global exibe os dez melhores jogadores, considerando a maior pontuação de cada um. O servidor gerencia usuários e pontuações, não a lógica do jogo. Entrega: integração LibGDX–API com tela de ranking.
6. **Android:** reutilizar módulo compartilhado do LibGDX, adaptar entrada por toque e telas, manter versão desktop e Android no mesmo ecossistema. Controle Bluetooth é opcional. Entrega: jogo funcional em ambas as plataformas, consumindo o mesmo ranking.
7. **Evolução livre:** equipes adicionam nova fase, inimigos, chefão, dificuldade, personalização da nave ou outra mecânica, justificando as decisões arquiteturais. Multiplayer fica como trabalho futuro.

## Cronograma de cinco sprints semanais
Período: **02/11/2026 a 04/12/2026**. Cada sprint vai de segunda a sexta e inclui uma aula prática semanal de **100 minutos (9h–10h40)**, acompanhada por uma aula teórica separada. O desenvolvimento e os testes continuam fora da sala. **02/11 é feriado nacional**; a primeira aula prática deve ocorrer em 03/11 ou ser remarcada, sem alterar a janela do sprint.

| Sprint | Período | Fases | Aula prática (100 min) | Trabalho extraclasse / entrega |
|---|---|---|---|---|
| 1 | 02–06/11 | 1 e 2 | Diagnóstico e diagrama da arquitetura; refatoração orientada por SOLID/GRASP; Gradle Wrapper e ciclo de build | Jogo de console preservado; build, testes e execução reproduzíveis. Issues MM-01 a MM-03 |
| 2 | 09–13/11 | 3 e 4 | Ciclo de vida LibGDX; `Game`/`Screen`; entrada, sprites, colisões e HUD | Versão desktop jogável com interface gráfica e testes das regras preservadas. Issues MM-04 a MM-06 |
| 3 | 16–20/11 | 5 | API REST, persistência, autenticação e integração cliente-servidor | Cadastro/login, histórico e ranking funcionando com testes de API e cliente. Issues MM-07 a MM-09 |
| 4 | 23–27/11 | 6 | Módulo Android, controles por toque, orientação e ciclo de vida | Desktop mantido e APK de depuração gerado; registrar testes em aparelho ou declarar o que não foi verificado. Issues MM-10 a MM-12 |
| 5 | 30/11–04/12 | 7 | Escolha de uma melhoria de escopo fechado; desenho da solução e estratégia de testes | Uma melhoria implementada, testada, documentada e demonstrada; não tentar concluir várias ideias de evolução livre. Issues MM-13 a MM-15 |

O cronograma é intensivo: as fases 3 e 4 compartilham um sprint, e a Fase 7 deve ficar limitada a uma melhoria. Se a integração do servidor ou o teste Android excederem a estimativa, priorizar uma entrega mínima funcional e mover refinamentos para o backlog posterior, sem reduzir os testes de regressão.

O backlog detalhado, com títulos, descrições, dependências e critérios de aceite prontos para cadastro no GitHub, está em [`Issues_GitHub_Missao_Marte.md`](Issues_GitHub_Missao_Marte.md). No GitHub Projects, o campo nativo **Iteration `Sprint`** registra os cinco períodos semanais de sete dias corridos; milestones do repositório representam as **Fases 1 a 7**. O trabalho e a entrega da sprint permanecem planejados de segunda a sexta-feira. Sugestão de fluxo Kanban: **Backlog → Pronto → Em andamento → Em revisão → Concluído**.

### Estrutura de cada encontro
- 15 min: objetivos e ligação com teoria.
- 30 min: demonstração orientada pelo professor.
- 40 min: prática assistida e revisão de código.
- 15 min: verificação das entregas e tarefas extraclasse.

## Entregáveis de cada fase
- Código versionado no Git, com tag ou branch por fase.
- Tutorial `docs/fase-XX-tutorial.md`: objetivos, conceitos, arquitetura antes/depois, alterações passo a passo, testes e exercícios.
- Evidência de execução (captura de tela, log ou vídeo curto) e breve reflexão sobre arquitetura.

## Prompt mestre para Copilot, Codex ou Claude Code

> Atue como arquiteto de software Java e professor universitário. Analise o Exercício 10 (Missão Marte) no workspace. Evolua o código existente, sem reescrever do zero, respeitando o plano de sete fases neste documento. Execute **somente a fase que eu indicar**. Antes de editar, apresente diagnóstico, classes afetadas, plano de mudanças e riscos e aguarde aprovação. Preserve o comportamento existente sempre que possível. Após a aprovação, implemente, rode os testes possíveis e documente o que não pôde testar. Gere `docs/fase-XX-tutorial.md` com objetivos, teoria (SOLID, GRASP, padrões, Gradle, LibGDX, Spring Boot ou Android conforme a fase), código antes/depois, diagramas Mermaid, instruções de execução, testes e exercícios. Use linguagem didática para alunos que conhecem Java, mas ainda não dominam as demais tecnologias. Não introduza Angular nem JavaScript. Comece pelo diagnóstico da fase 1.

### Prompts de continuação
- **Fase 2:** “Migre o projeto refatorado para Gradle e documente a migração, sem alterar o comportamento.”
- **Fase 3:** “Adapte a estrutura ao LibGDX e explique o ciclo de vida, preservando as regras do jogo.”
- **Fase 4:** “Implemente a apresentação gráfica no LibGDX e documente cada componente.”
- **Fase 5:** “Implemente servidor Spring Boot, login obrigatório, histórico de partidas e Top 10 por melhor pontuação de cada jogador; integre o cliente LibGDX.”
- **Fase 6:** “Adicione execução Android mantendo desktop e núcleo compartilhado, com controles por toque.”
- **Fase 7:** “Proponha e implemente uma nova mecânica escolhida pela equipe, demonstrando extensibilidade.”

## Observações pedagógicas
O prazo de quatro semanas é intensivo e pressupõe atividades extraclasse e exemplos-base preparados pelo professor. Avaliar tanto a execução quanto a justificativa das decisões arquiteturais. Para um ranking competitivo, pontuações enviadas pelo cliente não são totalmente confiáveis; tratar prevenção de fraude como evolução futura.
