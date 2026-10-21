param([switch]$Tests)
$ErrorActionPreference = 'Stop'
$candidates = @()
if ($env:JAVA_HOME) { $candidates += $env:JAVA_HOME }
$adoptium = Join-Path $env:ProgramFiles 'Eclipse Adoptium'
if (Test-Path -LiteralPath $adoptium) {
    $candidates += Get-ChildItem -LiteralPath $adoptium -Directory -Filter 'jdk-25*' |
        Sort-Object Name -Descending | Select-Object -ExpandProperty FullName
}
$jdk = $null
foreach ($candidate in $candidates) {
    $executable = Join-Path $candidate 'bin/java.exe'
    if (Test-Path -LiteralPath $executable) {
        $info = & $executable -version 2>&1 | Out-String
        if ($info -match 'version "25(?:\.|\")') { $jdk = $candidate; break }
    }
}
if (!$jdk) { throw 'Un JDK 25 est requis. Installez-le puis définissez JAVA_HOME vers son dossier.' }
$env:JAVA_HOME = $jdk
$tasks = @('build', '-x', 'runGameTest', '-x', 'runClientGameTest')
if ($Tests) { $tasks = @('build', 'runGameTest', 'runClientGameTest') }
& (Join-Path $PSScriptRoot 'gradlew.bat') -p $PSScriptRoot @tasks --console=plain
exit $LASTEXITCODE
