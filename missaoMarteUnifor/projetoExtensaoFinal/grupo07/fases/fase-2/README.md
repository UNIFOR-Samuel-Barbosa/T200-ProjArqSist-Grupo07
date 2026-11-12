# Fase 2 — Build Gradle

Esta versão conserva os fontes da Fase 1 e passa a compilar, testar e executar pelo Gradle Wrapper.

## Comandos

```powershell
.\gradlew --version
.\gradlew clean build
.\gradlew test
.\gradlew run --console=plain
.\gradlew installDist
```

O projeto usa Java 17, JUnit 5 e `mavenCentral()`. Leia [docs/tutorial.md](docs/tutorial.md) para as decisões e a validação.
