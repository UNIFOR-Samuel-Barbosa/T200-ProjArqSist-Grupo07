# Projeto Integrador — Evolução do Missão Marte

## Apresentação da atividade

Nesta atividade, sua equipe irá evoluir o jogo **Missão Marte** em etapas, partindo de uma aplicação Java executada no console até chegar a um jogo gráfico multiplataforma, com servidor de ranking e uma melhoria própria.

O desenvolvimento será incremental: em vez de reescrever o sistema, a equipe irá compreender o código existente, preservar o comportamento que já funciona e aplicar novos conceitos de arquitetura e tecnologia. O cliente será desenvolvido em Java com Gradle e libGDX; o servidor será uma aplicação independente com Spring Boot e API REST; a versão móvel usará Android.

A equipe organizará o trabalho com Scrum e um quadro Kanban no GitHub Projects. O código, as decisões técnicas, os testes e o progresso das etapas devem permanecer visíveis no repositório.

## Objetivos de aprendizagem

Ao concluir a atividade, você será capaz de:

- Explicar a evolução de uma aplicação Java de console para um jogo gráfico multiplataforma.
- Aplicar princípios de orientação a objetos, SOLID e GRASP para organizar responsabilidades.
- Gerenciar dependências, compilação e testes com Gradle.
- Explicar o ciclo de vida de uma aplicação libGDX e separar regras de jogo da apresentação e da plataforma.
- Construir uma interface gráfica com entrada, animações, recursos de imagem e áudio.
- Criar uma API REST com Spring Boot, persistência de dados e autenticação por token.
- Integrar um cliente Java a um servidor sem bloquear a interface do jogo.
- Adaptar o jogo para Android e implementar controles por toque.
- Escrever testes automatizados e documentar decisões, limitações e resultados.
- Planejar e acompanhar o trabalho em equipe com issues e Kanban.

## Formação das equipes

- Grupos de **2 a 3 integrantes**, salvo orientação diferente do professor.
- Cada equipe deve criar um **repositório público no GitHub** para o trabalho.
- Todos os integrantes devem ter permissão de escrita no repositório.
- Registre a participação de cada integrante nas issues, pull requests e commits. O histórico deve refletir o trabalho realizado, não ser produzido somente ao final.
- Não inclua senhas, tokens, chaves ou outros segredos no código ou no histórico do Git.

## Parte 1 — Organização do trabalho no GitHub

Antes de começar a implementação:

1. Crie o repositório da equipe e adicione os integrantes.
2. Crie um **GitHub Project** associado ao repositório.
3. Configure as colunas:

| Coluna | Significado |
|---|---|
| Backlog | Trabalho identificado, mas ainda não iniciado |
| Em andamento | Trabalho que está sendo executado |
| Em revisão | Trabalho concluído pelo responsável, aguardando revisão da equipe |
| Concluído | Trabalho revisado, validado e aceito |

4. Registre as tarefas como issues, com responsável, etapa/sprint e critérios de aceite.
5. Organize as issues em marcos (milestones) conforme o calendário abaixo.
6. Atualize o quadro conforme o trabalho realmente avança. Uma issue só deve ser concluída depois de revisada e validada.

O projeto começa em **03/11/2026**. Cada sprint dura uma semana, de terça-feira a segunda-feira:

| Sprint | Período |
|---|---|
| Sprint 1 | 03/11/2026 a 09/11/2026 |
| Sprint 2 | 10/11/2026 a 16/11/2026 |
| Sprint 3 | 17/11/2026 a 23/11/2026 |
| Sprint 4 | 24/11/2026 a 30/11/2026 |
| Sprint 5 | 01/12/2026 a 07/12/2026 |

As **apresentações finais acontecerão na semana de 30/11 a 04/12/2026**, em horário a ser definido pelo professor. A equipe deve chegar a esse período com a implementação concluída e reservar a Sprint 5 para estabilização, documentação final, apresentação e fechamento do Kanban. A data de 30/11 corresponde ao último dia da Sprint 4; de 01/12 a 04/12, as apresentações ocorrem durante a Sprint 5.

## Parte 2 — Roteiro de desenvolvimento

### Sprint 1 — Compreensão do sistema, arquitetura e Gradle (03/11 a 09/11)

**Etapas relacionadas:** Fases 0, 1 e 2 — projeto inicial, refatoração e Gradle.

- Execute o projeto inicial em modo texto e registre como o jogo funciona.
- Identifique as classes, suas responsabilidades e os principais fluxos de uma partida.
- Analise problemas de acoplamento e proponha uma organização melhor.
- Refatore a aplicação preservando as regras e o comportamento observável do jogo.
- Separe, quando apropriado, domínio, aplicação, entrada e apresentação.
- Migre a compilação e a execução para Gradle, use o Gradle Wrapper e configure os testes com JUnit 5.
- Registre as decisões arquiteturais e compare a estrutura antes e depois.

**Entrega da sprint:** aplicação de console executável por Gradle, código organizado, testes executáveis e registro da arquitetura no repositório.

### Sprint 2 — libGDX e interface gráfica (10/11 a 16/11)

**Etapas relacionadas:** Fases 3 e 4 — introdução ao libGDX e interface gráfica.

- Adapte a aplicação para libGDX sem transferir as regras do jogo para as telas.
- Explique o ciclo de vida da aplicação e o papel das telas e dos módulos de plataforma.
- Implemente uma versão gráfica jogável para desktop.
- Utilize recursos gráficos e de áudio de forma controlada, respeitando o ciclo de vida dos recursos.
- Implemente entrada, estado visual do jogo e HUD necessários para jogar e compreender a partida.
- Mantenha o domínio testável sem abrir janela ou depender de OpenGL.

**Entrega da sprint:** jogo gráfico desktop executável, com interface jogável, recursos liberados corretamente e testes automatizados.

### Sprint 3 — Servidor e ranking global (17/11 a 23/11)

**Etapa relacionada:** Fase 5 — servidor e ranking.

- Crie um servidor independente com Spring Boot e uma API REST para cadastro e autenticação de jogadores.
- Persista usuários e partidas com JPA e H2.
- Armazene senhas como hash; nunca armazene nem exponha a senha em texto puro.
- Proteja as operações autenticadas com token e valide os dados recebidos.
- Implemente o registro de partidas, o histórico do jogador e o ranking global.
- Integre o cliente libGDX à API sem bloquear a thread responsável pela interface.
- Preserve o modo convidado e o ranking local conforme o comportamento definido na versão-base.
- Registre limitações relevantes, incluindo o fato de que o servidor não executa a lógica do jogo para verificar se uma pontuação enviada pelo cliente é legítima.

**Entrega da sprint:** cliente e servidor executáveis, API documentada, fluxo de cadastro/login/partida/ranking demonstrável e testes do servidor e do cliente.

### Sprint 4 — Android e evolução do jogo (24/11 a 30/11)

**Etapas relacionadas:** Fases 6 e 7 — Android e evolução livre.

- Adicione o módulo Android reutilizando o código compartilhado do jogo e preserve a execução desktop.
- Implemente controles por toque e adapte a interação às telas móveis.
- Permita configurar o endereço do servidor sem incluir segredos no aplicativo.
- Escolha e implemente uma melhoria de escopo compatível com o prazo, como uma nova mecânica, inimigo, arma, campanha ou personalização.
- Use os pontos de extensão existentes, acrescente testes e demonstre que o comportamento anterior continua funcionando ou documente alterações intencionais.
- Prepare o servidor para publicação e documente a configuração necessária, incluindo variáveis de ambiente, porta, verificação de saúde e persistência de dados.
- A publicação real em um serviço de nuvem só é obrigatória se o professor determinar.

**Entrega da sprint:** funcionalidades previstas implementadas e testadas, build desktop preservado, APK de debug ou evidência da limitação de ambiente, melhoria documentada e versão candidata à apresentação.

### Sprint 5 — Estabilização, apresentação e fechamento (01/12 a 07/12)

**Apresentações finais:** semana de 30/11 a 04/12.

- Congele novas funcionalidades antes da apresentação e corrija apenas problemas que impeçam a execução ou a demonstração.
- Faça uma revisão final dos testes e confirme os comandos de execução a partir do README.
- Atualize README, tutorial e quadro Kanban.
- Apresente o sistema entre 30/11 e 04/12, demonstrando o jogo, a integração com o servidor e a melhoria da equipe.
- Entregue o relatório técnico e os links do repositório e do GitHub Project no AVA até 04/12, salvo orientação diferente do professor.
- Até 07/12, finalize o quadro Kanban, registre a retrospectiva da equipe e documente limitações ou itens que não puderam ser verificados.

**Entrega da sprint:** apresentação final realizada, documentação e relatório entregues, testes e instruções revisados e Kanban encerrado.

## Parte 3 — Requisitos técnicos e de qualidade

- Use **Java 17** e os Gradle Wrappers fornecidos ou definidos pelo projeto-base.
- Preserve o histórico das etapas: não apague a versão inicial nem altere fases anteriores quando a atividade solicitar uma nova pasta ou cópia de trabalho.
- Mantenha as regras do jogo em classes de domínio/aplicação e a apresentação nas telas/adaptadores.
- Evite dependências de libGDX nas regras de domínio.
- Mantenha o cliente e o servidor independentes; o servidor não deve depender do código do cliente.
- Não exponha entidades de persistência diretamente na API; use DTOs.
- Padronize respostas de erro e valide entradas no servidor.
- Não registre senhas, tokens ou segredos em logs, arquivos de configuração versionados ou capturas de tela.
- Faça commits frequentes, pequenos e com mensagens descritivas.
- Execute os testes e comandos de build antes de marcar uma entrega como concluída.
- Se um requisito não puder ser verificado (por exemplo, falta de SDK Android ou dispositivo), informe o motivo e apresente as evidências alternativas disponíveis.

## Parte 4 — Entregáveis

Entregue no AVA:

1. **Link do repositório GitHub** da equipe, contendo:
   - Código-fonte organizado por etapa/fase.
   - Instruções para executar cliente e servidor.
   - Requisitos de ambiente e comandos de build/teste.
   - Documentação dos endpoints e das configurações necessárias.
   - Tutoriais ou registros técnicos das etapas desenvolvidas.
2. **Link do GitHub Project**, com backlog, issues, responsáveis e progresso das sprints.
3. **Relatório técnico** em PDF ou Markdown, de aproximadamente 2 a 4 páginas, com:
   - Arquitetura e principais decisões técnicas, com exemplos do código.
   - Padrões e princípios aplicados, explicando onde aparecem e por que foram úteis.
   - Testes realizados e resultados.
   - Dificuldades, limitações e melhorias futuras.
   - Participação dos integrantes.
4. **Demonstração ao vivo** de 5 a 10 minutos, na semana de **30/11 a 04/12/2026**, mostrando o jogo, a integração com o servidor e a melhoria da equipe. O horário será definido pelo professor. Se algum componente não puder ser demonstrado, apresente evidências e explique a limitação.

## Critérios de avaliação

| Critério | Descrição | Pontos |
|---|---|---:|
| Evolução funcional do jogo | Etapas executáveis e progressão coerente de console para jogo gráfico, preservando o comportamento esperado | 20 |
| Arquitetura e qualidade do código | Separação de responsabilidades, aplicação pertinente de princípios/padrões e ausência de acoplamento indevido entre domínio e interface | 20 |
| Servidor e integração | API REST, persistência, validação, autenticação e integração demonstrável entre cliente e servidor | 15 |
| Android e experiência de uso | Reutilização do código, controles por toque, adaptação móvel e evidência de build/execução | 10 |
| Testes e confiabilidade | Testes relevantes, build reproduzível e tratamento de falhas documentado | 15 |
| Evolução livre | Melhoria funcional, bem integrada à arquitetura e coberta por testes | 10 |
| Gestão, documentação e apresentação | Kanban com histórico real, README/tutorial, relatório e clareza na demonstração | 10 |
| **Total** |  | **100** |

## Não conformidades que podem reduzir a nota

As ocorrências abaixo serão consideradas na avaliação do critério relacionado, sem ultrapassar o total de pontos:

- O sistema não executa conforme as instruções documentadas.
- Regras de negócio estão concentradas em telas ou controllers.
- Senhas, tokens ou segredos estão versionados ou aparecem em logs.
- A equipe apresenta entidades do banco diretamente como resposta da API, sem justificar ou proteger o contrato.
- O quadro Kanban não apresenta evidência de acompanhamento durante o desenvolvimento.
- Não é possível identificar a contribuição dos integrantes no histórico do repositório.
- A melhoria final altera comportamentos anteriores sem teste ou explicação.

## Perguntas para a apresentação

- Como as responsabilidades mudaram entre a versão console e a versão gráfica?
- Onde ficam as regras de negócio? Como os testes demonstram que elas não dependem da interface?
- Qual é a diferença entre o cliente libGDX e o servidor Spring Boot neste projeto?
- Como o cliente se comunica com a API e o que acontece quando o servidor está indisponível?
- Por que as senhas são armazenadas como hash? Onde o token é validado?
- O que é um DTO e por que ele é usado na API?
- Como o jogo reutiliza código entre desktop e Android?
- Qual decisão de arquitetura facilitou a melhoria desenvolvida pela equipe?
- O que vocês fariam diferente se tivessem mais tempo?

## Dicas e boas práticas

- Divida tarefas pequenas e mantenha uma pessoa responsável por cada issue.
- Revise o trabalho de outro integrante antes de fechar uma issue.
- Execute os testes após mudanças em regras compartilhadas.
- Teste a API separadamente antes de investigar problemas de integração com o jogo.
- Valide a execução em outra máquina ou com outro integrante antes da entrega.
- Atualize o README quando mudar comandos, requisitos ou configurações.
- Nunca publique credenciais reais. Use dados de teste na demonstração.

## Recursos de apoio

- Repositório do projeto e instruções gerais: [README principal](README.md).
- Planejamento didático e etapas: [Plano de Aulas do Missão Marte](Plano_de_Aulas_Missao_Marte.md).
- Tutoriais, enunciados específicos e exemplos: consulte a pasta `exercicio10/fases/` e o README de cada fase.
- Documentação oficial: [Gradle](https://docs.gradle.org/), [JUnit 5](https://junit.org/junit5/docs/current/user-guide/), [libGDX](https://libgdx.com/wiki/), [Spring Boot](https://spring.io/projects/spring-boot) e [Android Developers](https://developer.android.com/).
