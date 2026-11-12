# Fase 3 — Cliente gráfico libGDX

Projeto Gradle multi-módulo: `core` contém as regras reutilizadas e a apresentação libGDX; `lwjgl3` é o inicializador desktop.

## Executar

Requer Java 17 e acesso ao Maven Central.

```powershell
.\gradlew --version
.\gradlew clean build
.\gradlew :lwjgl3:run --console=plain
```

Menu: `1` nova partida, `2` ranking, `3` limpar ranking, `4` sair. Na partida, `W/A/S/D` ou setas movem, `E` embarca, `Q`/`Esc` aborta. Em nova partida, `F1`/`F2`/`F3` escolhem a dificuldade e `Enter` inicia.

Consulte [docs/tutorial.md](docs/tutorial.md) para a arquitetura, ciclo de vida e validação.
