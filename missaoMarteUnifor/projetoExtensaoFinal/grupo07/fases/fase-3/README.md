# Fase 3 — libGDX: jogo com janela gráfica

Esta versão abre uma janela desktop. O projeto é multi-módulo: `core` contém o jogo e as telas; `lwjgl3` inicia a aplicação. Execute os comandos a partir da raiz de `fase-3` (não de `core` nem de `lwjgl3`).

## 1. Preparar o ambiente

- Instale e selecione **JDK 17**.
- É necessário acesso à internet na primeira execução para baixar Gradle/dependências.
- Para abrir a janela, use um desktop com suporte a OpenGL 2.0 ou superior.
- Abra `fase-3` no VS Code ou inicie um terminal PowerShell nessa pasta.

Confirme o Java e a JVM usada pelo Wrapper:

```powershell
java -version
.\gradlew.bat --version
```

Confirme `Gradle 8.7` e `JVM 17`. Se necessário, ajuste `JAVA_HOME` e `PATH` na janela atual do PowerShell (substitua pelo caminho onde instalou o JDK):

```powershell
$env:JAVA_HOME = "C:\caminho\para\seu\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

## 2. Compilar e testar

```powershell
.\gradlew.bat clean build
```

O comando compila os dois módulos e executa os **17 testes automatizados** do `core`; os testes não abrem uma janela. O relatório está em `core\build\reports\tests\test\index.html`.

## 3. Abrir o jogo

```powershell
.\gradlew.bat :lwjgl3:run --console=plain
```

Espere a janela **Missão Marte — Grupo 07** abrir. Se a primeira execução estiver baixando dependências, aguarde o Gradle terminar. O `ranking.json` desta versão é gravado na raiz de `fase-3`.

## 4. Roteiro para jogar e testar

1. No menu, pressione `2` para abrir o ranking e `Enter` para voltar.
2. Pressione `3` para limpar o ranking; a mensagem “Ranking limpo.” deve aparecer. Pressione `2` para verificar que aparece “Sem pontuacoes registradas.” sem fechar o jogo.
3. Pressione `1` para nova partida, digite um nome e escolha a dificuldade com `F1` (fácil), `F2` (médio) ou `F3` (difícil). `Backspace` apaga; `Enter` inicia.
4. Na partida, observe mapa e indicadores. `W/A/S/D` ou setas movem; `E` embarca. Uma tecla sem ação não deve consumir turno.
5. Embarque os passageiros e volte à base verde no centro para entregá-los. A vitória exige entregar todos; ao vencer, pressione `Enter` para consultar o ranking.
6. Para testar saída/derrota sem completar a missão, pressione `Q` ou `Esc` para abortar (volta ao menu). Em caso de derrota, `Enter` também volta ao menu.
7. Pressione `4` no menu para fechar o jogo.

## 5. Controles

| Tela | Teclas |
|---|---|
| Menu | `1` jogar, `2` ranking, `3` limpar, `4` sair |
| Nova partida | Digitar nome, `Backspace`, `F1` fácil, `F2` médio, `F3` difícil, `Enter` iniciar, `Esc` voltar |
| Partida | `W/A/S/D` ou setas mover, `E` embarcar, `Q` ou `Esc` abortar |
| Vitória/derrota | `Enter` segue para ranking ou menu |

## 6. Problemas comuns

- **O Wrapper falha ao iniciar:** confira `java -version` e `.\gradlew.bat --version`; esta fase usa Gradle 8.7 e requer JDK 17.
- **Não baixa Gradle ou bibliotecas:** confira a conexão com a internet e tente novamente.
- **Build funciona, mas janela não aparece:** confira se o comando foi executado na raiz de `fase-3` e se o ambiente gráfico/OpenGL está disponível.
- **Comandos não funcionam:** dê foco à janela do jogo antes de pressionar teclas.

As decisões arquiteturais, diferenças em relação à referência e limitações estão em [docs/tutorial.md](docs/tutorial.md).
