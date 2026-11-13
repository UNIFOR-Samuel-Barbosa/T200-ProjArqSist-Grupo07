# Fase 1 — Refatoração: jogo no console

Esta é a versão mais simples para começar: o jogo roda no terminal, sem Gradle e sem janela gráfica. Cada fase tem sua própria pasta e seu próprio `ranking.json`.

## 1. Preparar o ambiente

1. Instale um JDK 17 ou superior.
2. No VS Code, abra um terminal PowerShell (**Terminal > Novo Terminal**).
3. Entre na pasta `grupo07\fases\fase-1` (ou abra essa pasta diretamente no VS Code).
4. Confira se Java e compilador estão disponíveis:

```powershell
java -version
javac -version
```

O script compila para Java 17 (`--release 17`). Se o terminal não encontrar `java`/`javac`, configure o `PATH` para a pasta `bin` do JDK.

## 2. Compilar e executar

Execute os comandos dentro de `fase-1`:

```powershell
.\build.ps1
.\build.ps1 -Run
```

O primeiro comando só compila; o segundo compila e abre o jogo. A saída compilada fica em `build\classes`.

## 3. Roteiro para jogar e testar

1. No menu, digite `8` para ver a mensagem de opção inválida.
2. Digite `2` para consultar o ranking.
3. Digite `3` para limpar o ranking e, depois, `2` para confirmar que está vazio; deve aparecer `Ainda não há pontuações.` sem encerrar o jogo.
4. Digite `1`, informe um nome de piloto e escolha `facil`, `medio` ou `dificil`. Se digitar outro texto, o jogo explica as opções válidas e pergunta novamente.
5. Observe o mapa, a legenda e os indicadores: pontos, vidas, passageiros a bordo, entregues e no mapa.
6. Use `W`, `A`, `S`, `D` para mover; após cada comando o mapa e os indicadores devem ser redesenhados antes do próximo prompt. `E` ou `C` tenta embarcar na célula atual. Uma ação inválida não deve avançar o turno.
7. Para concluir a missão, embarque os passageiros e volte à base `L`, no centro do mapa. A posição e os perigos são sorteados a cada partida; se quiser apenas encerrar o teste, use `Q`. O menu principal deve aparecer novamente com as opções após o aborto.
8. Depois de uma vitória, volte ao menu e use `2` para consultar a pontuação. `ranking.json` é criado dentro de `fase-1`.
9. Para sair normalmente, escolha `4`.

## 4. Controles

| Tecla | Ação |
|---|---|
| `W/A/S/D` | Move a nave |
| `E` ou `C` | Tenta embarcar passageiro na célula |
| `Q` | Aborta a missão |
| `1` | Nova missão |
| `2` | Consulta o Top 10 |
| `3` | Limpa o ranking |
| `4` | Sai do jogo |

O jogo usa mapa de raio 5. A dificuldade controla a quantidade de passageiros/perigos e a pontuação inicial; a capacidade da nave corresponde ao total de passageiros.

## 5. O que verificar no código

Para entender a separação, veja `application\GameSession.java` (turnos e regras), `console\ConsoleRenderer.java` (mapa), `application\MissaoFactory.java` (cenário) e `infrastructure\ranking\FileRankingRepository.java` (arquivo).

As decisões arquiteturais e diferenças em relação à solução de referência estão em [docs/tutorial.md](docs/tutorial.md).

O diretório também possui `.project` e `.classpath` para ser reconhecido como projeto Java pelo VS Code/Eclipse, com nível de linguagem 17. Se o aviso antigo continuar após a atualização, execute **Java: Clean Java Language Server Workspace** e reabra o projeto.
