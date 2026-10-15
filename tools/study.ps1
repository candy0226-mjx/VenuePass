# Locate an existing Python without installing or changing system settings.
$ErrorActionPreference = 'Stop'
$studyScript = Join-Path $PSScriptRoot 'study.py'
$studyCandidates = @()
if ($env:VENUEPASS_PYTHON) {
    $studyCandidates += $env:VENUEPASS_PYTHON
}
$studyCommand = Get-Command python -ErrorAction SilentlyContinue
if ($studyCommand -and $studyCommand.Source -notmatch 'WindowsApps') {
    $studyCandidates += $studyCommand.Source
}
if ($env:USERPROFILE) {
    $studyCandidates += Join-Path $env:USERPROFILE '.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe'
}
foreach ($studyCandidate in $studyCandidates) {
    if (Test-Path -LiteralPath $studyCandidate -PathType Leaf) {
        & $studyCandidate $studyScript @args
        exit $LASTEXITCODE
    }
}
$studyLauncher = Get-Command py -ErrorAction SilentlyContinue
if ($studyLauncher) {
    & $studyLauncher.Source -3 --version *> $null
    if ($LASTEXITCODE -eq 0) {
        & $studyLauncher.Source -3 $studyScript @args
        exit $LASTEXITCODE
    }
}
throw 'Python 3.10+ was not found. Install it or set VENUEPASS_PYTHON to an existing executable. Markdown and JSON can still be edited directly.'
