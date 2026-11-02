# Backlog de issues — Missão Marte

Este arquivo contém 15 issues prontas para cadastrar no repositório `marcelobezerra-dotcom/T200-ProjArqSist` e acompanhar em um GitHub Project no formato Kanban. Os identificadores `MM-01` a `MM-15` são referências locais; substitua-os pelos números reais (`#N`) depois de criar as issues.

## Configuração sugerida do GitHub Project

- **Nome:** Missão Marte — Sprints de evolução arquitetural
- **Período:** 02/11/2026 a 04/12/2026
- **Campo do board:** `Sprint` do tipo **Iteration**, com cinco períodos semanais de 7 dias corridos, iniciando nas segundas-feiras de 02/11 a 30/11. As aulas/trabalho da sprint encerram na sexta-feira; a iteração do GitHub inclui o fim de semana.
- **Milestones do repositório:** um por fase (`Fase 1` a `Fase 7`), com data de vencimento até o fim do sprint em que a fase é entregue. Cada issue deve ter o milestone da sua fase.
- **Colunas/status:** Backlog, Pronto, Em andamento, Em revisão, Concluído.
- **Campos recomendados:** `Sprint` (campo Iteration criado/completado pelo importador), Prioridade (alta/média/baixa) e Status.
- **Rótulos:** criar apenas os tipos `tipo:analise`, `tipo:desenvolvimento`, `tipo:teste` e `tipo:documentacao`; sprint e fase ficam nos campos/milestones correspondentes.
- **Primeiro sprint:** 02/11 é feriado nacional no Brasil; planejar a aula prática em 03/11 ou remarcar.

As dependências abaixo são referências entre os identificadores deste arquivo. Ao cadastrar, substitua `MM-xx` por `#N` no corpo da issue, quando os números reais estiverem disponíveis.

---

## Sprint 1 — Base (02–06/11/2026)

### MM-01 — Diagnosticar e registrar a arquitetura do jogo de console
**Título para GitHub:** `[Fase 1] Mapear arquitetura e comportamento atual do Missão Marte`
**Labels:** `tipo:analise`
**Depende de:** nenhuma

**Descrição**
Como equipe, queremos entender o Exercício 10 antes de refatorar, para preservar as regras e localizar responsabilidades e acoplamentos.

**Critérios de aceite**
- [ ] O fluxo de uma partida e as regras principais estão descritos.
- [ ] Existe diagrama da arquitetura atual e proposta.
- [ ] Há uma lista de comportamentos de referência e comandos para compilar/executar.
- [ ] `exercicio10/src` permanece intacto.

### MM-02 — Refatorar a arquitetura sem alterar as regras
**Título para GitHub:** `[Fase 1] Separar domínio, aplicação e interface de console`
**Labels:** `tipo:desenvolvimento`, `tipo:teste`
**Depende de:** MM-01

**Descrição**
Como equipe, queremos separar regras do jogo, coordenação da partida e entrada/saída de console para facilitar evolução e testes.

**Critérios de aceite**
- [ ] Regras e apresentação têm responsabilidades separadas.
- [ ] SOLID, GRASP e padrões são aplicados onde agregam valor e justificados.
- [ ] O jogo continua executável no console e mantém o comportamento observado em MM-01.
- [ ] Testes automatizados cobrem as regras principais.

### MM-03 — Migrar compilação e execução para Gradle
**Título para GitHub:** `[Fase 2] Configurar Gradle Wrapper e build reproduzível`
**Labels:** `tipo:desenvolvimento`, `tipo:teste`, `tipo:documentacao`
**Depende de:** MM-02

**Descrição**
Como equipe, queremos compilar, testar e executar o jogo pelo Gradle Wrapper para padronizar o ambiente de desenvolvimento.

**Critérios de aceite**
- [ ] O projeto tem `build.gradle` e Gradle Wrapper versionado.
- [ ] Os comandos de build, teste e execução funcionam em instalação limpa com JDK documentado.
- [ ] O jogo mantém o comportamento da versão anterior.
- [ ] O README explica tasks, dependências e comandos utilizados.

---

## Sprint 2 — LibGDX e interface (09–13/11/2026)

### MM-04 — Criar a estrutura inicial LibGDX
**Título para GitHub:** `[Fase 3] Introduzir módulos core e desktop com LibGDX`
**Labels:** `tipo:desenvolvimento`, `tipo:teste`
**Depende de:** MM-03

**Descrição**
Como equipe, queremos introduzir o ciclo de vida do LibGDX e separar o código compartilhado do launcher desktop, preservando o domínio existente.

**Critérios de aceite**
- [ ] O projeto inicia pelo launcher desktop.
- [ ] O ciclo de vida `create`, `render`, `resize` e `dispose` está documentado.
- [ ] As regras do domínio não dependem de classes LibGDX.
- [ ] O núcleo pode ser testado sem abrir janela ou usar OpenGL.

### MM-05 — Integrar partida e entrada do jogador à tela do jogo
**Título para GitHub:** `[Fase 3] Conectar GameSession à tela e aos controles desktop`
**Labels:** `tipo:desenvolvimento`, `tipo:teste`
**Depende de:** MM-04

**Descrição**
Como jogador, quero iniciar e controlar uma partida na janela LibGDX, enquanto as regras permanecem no domínio/aplicação.

**Critérios de aceite**
- [ ] A tela encaminha ações à sessão e apresenta o estado recebido.
- [ ] Entrada de teclado não contém regras de negócio.
- [ ] O modo de console ou os testes de domínio continuam funcionando.
- [ ] O ciclo de troca e descarte de telas não deixa recursos ativos.

### MM-06 — Entregar a interface gráfica jogável
**Título para GitHub:** `[Fase 4] Implementar sprites, HUD, movimento, colisões e áudio`
**Labels:** `tipo:desenvolvimento`, `tipo:teste`
**Depende de:** MM-05

**Descrição**
Como jogador, quero visualizar uma partida gráfica com nave, mapa, perigos, informações e retorno visual/sonoro das ações.

**Critérios de aceite**
- [ ] A partida desktop é jogável com teclado.
- [ ] Sprites, HUD e estados de vitória/derrota refletem o estado do domínio.
- [ ] Colisões e efeitos visuais não duplicam regras de negócio na tela.
- [ ] Assets têm origem documentada ou são gerados pelo projeto.
- [ ] Testes relevantes passam sem depender de janela, áudio ou relógio real.

---

## Sprint 3 — Servidor e ranking (16–20/11/2026)

### MM-07 — Implementar API e persistência de usuários
**Título para GitHub:** `[Fase 5] Criar API Spring Boot com cadastro, login e persistência`
**Labels:** `tipo:desenvolvimento`, `tipo:teste`
**Depende de:** MM-06

**Descrição**
Como jogador, quero criar uma conta e autenticar-me para que meu histórico e pontuações sejam associados à minha identidade.

**Critérios de aceite**
- [ ] O servidor é um subprojeto independente e inicia conforme o README.
- [ ] Cadastro e login têm validação e respostas de erro apropriadas.
- [ ] Senhas são armazenadas com hash seguro, nunca em texto puro.
- [ ] Testes cobrem os fluxos válidos e inválidos da API.

### MM-08 — Registrar partidas e calcular o ranking global
**Título para GitHub:** `[Fase 5] Persistir histórico e expor ranking Top 10`
**Labels:** `tipo:desenvolvimento`, `tipo:teste`
**Depende de:** MM-07

**Descrição**
Como jogador autenticado, quero consultar meu histórico e o ranking global para acompanhar meu desempenho.

**Critérios de aceite**
- [ ] Cada partida concluída é associada ao jogador autenticado.
- [ ] O histórico é consultável sem expor dados de outros usuários indevidamente.
- [ ] O Top 10 considera a maior pontuação de cada jogador.
- [ ] O servidor armazena usuários e pontuações, mas não executa a lógica do jogo.
- [ ] Testes cobrem ordenação, empate, limites do Top 10 e isolamento por usuário.

### MM-09 — Integrar cliente LibGDX à API
**Título para GitHub:** `[Fase 5] Integrar autenticação, histórico e ranking no cliente`
**Labels:** `tipo:desenvolvimento`, `tipo:teste`
**Depende de:** MM-08

**Descrição**
Como jogador, quero autenticar-me no jogo e enviar/consultar resultados pela API, com erros de rede apresentados de forma compreensível.

**Critérios de aceite**
- [ ] O cliente consome endpoints de cadastro/login e ranking/histórico.
- [ ] Sessões e tokens são tratados sem registrar credenciais em logs.
- [ ] Erros de conexão e respostas inválidas não encerram a partida de forma inesperada.
- [ ] Testes cobrem o adaptador HTTP e a integração principal.
- [ ] O servidor não é necessário para executar testes do domínio do cliente.

---

## Sprint 4 — Android (23–27/11/2026)

### MM-10 — Configurar o módulo Android
**Título para GitHub:** `[Fase 6] Adicionar launcher Android e gerar APK de depuração`
**Labels:** `tipo:desenvolvimento`, `tipo:teste`
**Depende de:** MM-09

**Descrição**
Como equipe, queremos reutilizar o núcleo LibGDX em Android e manter o launcher desktop existente.

**Critérios de aceite**
- [ ] O módulo Android usa o mesmo código de domínio e aplicação.
- [ ] O desktop continua compilando e executando.
- [ ] O APK de depuração é gerado quando Android SDK/JDK estão disponíveis.
- [ ] Versões de SDK, requisitos e comandos estão documentados.

### MM-11 — Adaptar controles, layout e ciclo de vida para toque
**Título para GitHub:** `[Fase 6] Adaptar interface e controles para telas sensíveis ao toque`
**Labels:** `tipo:desenvolvimento`, `tipo:teste`
**Depende de:** MM-10

**Descrição**
Como jogador em celular, quero controlar a nave por toque e visualizar a partida sem controles ou informações cortados.

**Critérios de aceite**
- [ ] Controles por toque encaminham ações sem colocar regras de jogo na interface.
- [ ] Os elementos principais cabem em retrato e paisagem.
- [ ] Pausa, retomada e retorno do aplicativo são tratados.
- [ ] Os alvos de toque são utilizáveis e não se sobrepõem de modo ambíguo.
- [ ] O comportamento desktop permanece disponível.

### MM-12 — Validar distribuição e registrar evidências Android
**Título para GitHub:** `[Fase 6] Validar APK e documentar limitações de dispositivo`
**Labels:** `tipo:teste`, `tipo:documentacao`
**Depende de:** MM-11

**Descrição**
Como equipe, queremos registrar evidências de execução Android e deixar explícito o que foi ou não testado em aparelho real.

**Critérios de aceite**
- [ ] O APK instala e abre em aparelho/emulador, ou a indisponibilidade do ambiente é registrada.
- [ ] Há evidência de controles, rotação e comunicação com o servidor quando o ambiente permite.
- [ ] Limitações, falhas conhecidas e requisitos para execução estão no README.
- [ ] Nenhum segredo de produção é incluído nos arquivos ou evidências.

---

## Sprint 5 — Evolução livre (30/11–04/12/2026)

### MM-13 — Escolher uma melhoria e fechar o escopo
**Título para GitHub:** `[Fase 7] Definir melhoria, arquitetura e critérios de aceite`
**Labels:** `tipo:analise`
**Depende de:** MM-12

**Descrição**
Como equipe, queremos escolher uma melhoria pequena e demonstrável, identificando pontos de extensão, riscos e comportamento que deve permanecer compatível.

**Critérios de aceite**
- [ ] A melhoria escolhida cabe em um sprint e tem comportamento observável.
- [ ] Camadas/classes afetadas e pontos de extensão estão descritos.
- [ ] Critérios de aceite e casos de teste estão definidos antes da implementação.
- [ ] O modo clássico, os testes anteriores e a arquitetura são considerados na análise de impacto.

### MM-14 — Implementar a melhoria selecionada
**Título para GitHub:** `[Fase 7] Implementar melhoria com testes de regressão`
**Labels:** `tipo:desenvolvimento`, `tipo:teste`
**Depende de:** MM-13

**Descrição**
Como jogador, quero usar a melhoria definida em MM-13 sem perder os comportamentos existentes do jogo.

**Critérios de aceite**
- [ ] A melhoria funciona no fluxo previsto e respeita a separação de responsabilidades.
- [ ] Testes automatizados cobrem o caso normal, limites e comportamento que não deve ocorrer.
- [ ] Testes anteriores continuam passando ou cada alteração necessária é justificada.
- [ ] Não há dependência de relógio real, janela gráfica ou rede nos testes de regras.

### MM-15 — Documentar, demonstrar e revisar a evolução
**Título para GitHub:** `[Fase 7] Documentar arquitetura, testes e demonstração final`
**Labels:** `tipo:documentacao`, `tipo:teste`
**Depende de:** MM-14

**Descrição**
Como equipe e docente, queremos entender como a melhoria foi projetada, validada e demonstrada, incluindo limitações e próximos passos.

**Critérios de aceite**
- [ ] README/tutorial explicam execução, controles, decisão arquitetural e testes.
- [ ] A demonstração mostra a melhoria e a preservação dos comportamentos anteriores.
- [ ] O que não foi verificado (por exemplo, aparelho real) está declarado sem alegar validação inexistente.
- [ ] A revisão final confirma build, testes, documentação e ausência de alterações nas fases anteriores.

---

## Cadastrar e colocar no Kanban

1. Crie o GitHub Project e os rótulos/milestones sugeridos acima. O importador cria ou completa o campo `Sprint` quando recebe o ID do Project V2.
2. Para cada item `MM-xx`, crie uma issue com o título indicado e copie a descrição e os critérios de aceite.
3. Atribua a opção `Sprint` no Project, o milestone da fase e os rótulos de tipo; registre as dependências no corpo usando os números reais das issues.
4. Adicione cada issue ao Project e inicie em **Backlog** ou **Pronto**.
5. Durante a semana, mova o card conforme o fluxo; ao final do sprint, revise critérios de aceite e feche apenas o que foi demonstrado.

Este arquivo prepara o conteúdo das issues; ele não cria issues nem adiciona cards ao Project automaticamente.

