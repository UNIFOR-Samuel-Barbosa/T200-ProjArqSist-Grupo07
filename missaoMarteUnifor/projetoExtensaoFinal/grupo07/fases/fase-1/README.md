# Fase 1 — Refatoração

A aplicação de console separa o domínio, os casos de uso, a apresentação e a persistência do ranking.

## Requisitos

- JDK 17 ou superior com suporte a `--release 17`.
- Windows PowerShell.

## Executar

Na pasta `fase-1`:

```powershell
.\build.ps1
.\build.ps1 -Run
```

Controles: `W/A/S/D` movem a nave, `E` (ou `C`) embarca um passageiro e `Q` aborta a missão. Os dados do Top 5 ficam em `ranking.json`.

As decisões arquiteturais, alterações de regras e limitações estão em [docs/tutorial.md](docs/tutorial.md).

O diretório também possui `.project` e `.classpath` para ser reconhecido como projeto Java pelo VS Code/Eclipse, com nível de linguagem 17. Se o aviso antigo continuar após a atualização, execute **Java: Clean Java Language Server Workspace** e reabra o projeto.
