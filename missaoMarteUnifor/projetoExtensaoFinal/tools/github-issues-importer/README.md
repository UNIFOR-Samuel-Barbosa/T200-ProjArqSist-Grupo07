# GitHub Issue Importer

Aplicação local para validar um planejamento JSON, criar labels/milestones/issues no GitHub e, opcionalmente, adicionar as issues a um GitHub Project V2.

## Executar no Windows

Requer Python 3 instalado. O servidor usa apenas a biblioteca padrão.

```powershell
cd tools\github-issues-importer
python server.py
```

Abra <http://127.0.0.1:4173/> no navegador. Encerre o servidor com `Ctrl+C`.

## Uso

1. Informe o owner e o repositório, ou carregue um JSON com `repository.owner` e `repository.name`.
2. Carregue o JSON de planejamento e valide a prévia.
3. Informe um Personal Access Token e use **Conferir acesso** antes de publicar.
4. Selecione as issues desejadas e escolha se quer criar labels e milestones que ainda não existem. As issues recebem o milestone da fase correspondente.
5. Para preencher o campo `Sprint` e adicionar as issues ao Project V2, informe o número do projeto, sua URL ou o Node ID `PVT_...`. Número isolado usa o owner do repositório; a URL identifica o owner do projeto. O importador resolve o Node ID e cria/completa o campo nativo Iteration `Sprint`.
6. Confirme a operação. O app ignora issues existentes pelo título quando essa opção está selecionada.

Para a API de Issues, use um token com permissão **Issues: Read and write** e metadados do repositório. A criação de labels e milestones exige a permissão de escrita correspondente. A inclusão no Projects V2 também exige acesso de escrita a Projects. A organização pode exigir aprovação para o fine-grained token.

## Segurança

- O servidor escuta apenas em `127.0.0.1`; não o exponha em uma interface de rede pública.
- O proxy aceita somente `GET` e `POST`, e encaminha caminhos exclusivamente para `https://api.github.com`.
- O token é enviado pelo navegador ao proxy local e encaminhado ao GitHub. Ele não é salvo em arquivo nem incluído nos logs.
- A página apaga o campo de token após a importação ou quando é fechada. Limpe-o manualmente após apenas conferir o acesso.
- Não cole nem compartilhe o token em conversas, issues, capturas de tela ou arquivos do projeto.

O importador usa milestones do repositório para as fases e um campo do tipo Iteration chamado `Sprint` no Project V2. A issue é criada pela REST API; em seguida, o app adiciona o item ao Project e define sua iteração via GraphQL usando `iterationId`.
