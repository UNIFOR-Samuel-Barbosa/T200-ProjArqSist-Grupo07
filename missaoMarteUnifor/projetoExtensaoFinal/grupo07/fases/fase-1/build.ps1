param(
    [switch]$Run
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$sourceRoot = Join-Path $root "src\main\java"
$output = Join-Path $root "build\classes"
$sources = Get-ChildItem -Path $sourceRoot -Recurse -Filter *.java | ForEach-Object FullName

if ($sources.Count -eq 0) {
    throw "Nenhum fonte Java encontrado em $sourceRoot"
}

New-Item -ItemType Directory -Force -Path $output | Out-Null
javac --release 17 -encoding UTF-8 -d $output $sources
if ($LASTEXITCODE -ne 0) {
    throw "A compilação Java falhou."
}

if ($Run) {
    java -cp $output br.edu.unifor.missaomarte.Main
    if ($LASTEXITCODE -ne 0) {
        throw "A execução do jogo falhou."
    }
}
