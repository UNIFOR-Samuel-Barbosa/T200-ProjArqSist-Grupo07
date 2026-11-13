# Fase 2 — Gradle: jogo no console

Esta fase contém a mesma versão de console da Fase 1, mas usa Gradle Wrapper e inclui testes automatizados. Execute-a na própria pasta `fase-2`; ela tem build e ranking separados da Fase 1.

## 1. Preparar o ambiente

- Instale e selecione o **JDK 17**.
- Abra a pasta `fase-2` no VS Code ou use um terminal PowerShell nela.
- Verifique a JVM:

```powershell
java -version
.\gradlew.bat --version
```

Na saída do Wrapper, confirme `Gradle 8.7` e `JVM 17`. Se o projeto não encontrar o Java correto, ajuste `JAVA_HOME` e `PATH` para a pasta do JDK 17 nesta janela do PowerShell:

```powershell
$env:JAVA_HOME = "C:\caminho\para\seu\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

Na primeira execução, o Wrapper pode baixar Gradle e as dependências do Maven Central; é necessário acesso à internet.

## 2. Compilar e executar os testes

Rode na pasta `fase-2`:

```powershell
.\gradlew.bat clean build
.\gradlew.bat test
```

`clean build` compila e executa os testes JUnit. O relatório detalhado fica em `build\reports\tests\test\index.html` (abra esse arquivo no navegador). O teste do repositório usa pasta temporária e não grava no ranking do jogo.

## 3. Jogar e testar manualmente

```powershell
.\gradlew.bat run --console=plain
```

1. Digite `8` no menu para testar opção inválida.
2. Digite `2` para consultar o ranking; `3` limpa e `2` permite conferir que ficou vazio.
3. Digite `1`, informe o piloto e uma dificuldade (`facil`, `medio` ou `dificil`).
4. Observe mapa e indicadores; use `W/A/S/D` para mover e `E` ou `C` para tentar embarcar na posição atual.
5. Volte à base central `L` com passageiros a bordo para entregá-los. A missão vence quando todos forem entregues; posições e perigos mudam a cada partida.
6. Use `Q` para abortar uma partida de teste ou escolha `4` no menu para sair.
7. Depois de uma vitória, escolha `2` e confira a pontuação. O arquivo desta versão fica em `fase-2\ranking.json`.

Se quiser validar a distribuição executável, rode `.\gradlew.bat installDist`; os scripts e dependências são gerados em `build\install\`.

## 4. Controles

| Tecla | Ação |
|---|---|
| `W/A/S/D` | Move a nave |
| `E` ou `C` | Tenta embarcar passageiro |
| `Q` | Aborta a missão |
| `1` / `2` / `3` / `4` | Nova missão / ranking / limpar / sair |

Para as decisões arquiteturais e comparação com a referência, leia [docs/tutorial.md](docs/tutorial.md).
