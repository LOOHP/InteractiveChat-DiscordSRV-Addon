param([string]$JavaBin = '')
$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path $PSScriptRoot -Parent
$taskOutput = Join-Path $taskRoot 'target/presentation-verification'
New-Item -ItemType Directory -Force -Path $taskOutput | Out-Null
$taskInputs = @(
    @{
        Name = 'InteractiveChat-2026.1.2.0.jar'
        Url = 'https://repo.loohpjames.com/repository/com/loohp/InteractiveChat/2026.1.2.0/InteractiveChat-2026.1.2.0.jar'
        Hash = 'F076A84F16406D9827F247B8BC1948CA28618099B5B6E9F7F46CA20A07E194FA'
    },
    @{
        Name = 'DiscordSRV-Build-1.30.5.jar'
        Url = 'https://github.com/DiscordSRV/DiscordSRV/releases/download/v1.30.5/DiscordSRV-Build-1.30.5.jar'
        Hash = 'EF2FA1F2EB146C7C77412B7190A7DD33F1FC91282683FE45407739554C8AEFEF'
    }
)
$taskClasspath = @()
foreach ($taskInput in $taskInputs) {
    $taskPath = Join-Path $taskOutput $taskInput.Name
    if (-not (Test-Path -LiteralPath $taskPath)) {
        Invoke-WebRequest -Uri $taskInput.Url -OutFile $taskPath
    }
    if ((Get-FileHash -LiteralPath $taskPath -Algorithm SHA256).Hash -ne $taskInput.Hash) {
        throw "Dependency checksum mismatch: $($taskInput.Name)"
    }
    $taskClasspath += $taskPath
}
$taskClasses = Join-Path $taskOutput 'classes'
New-Item -ItemType Directory -Force -Path $taskClasses | Out-Null
$taskPackage = 'com/loohp/interactivechatdiscordsrvaddon/utils'
$taskSources = @(
    "common/src/main/java/$taskPackage/DiscordItemNamePresentation.java",
    "common/src/main/java/$taskPackage/DiscordPlainChat.java",
    "common/src/test/java/$taskPackage/DiscordItemNamePresentationProof.java",
    "common/src/test/java/$taskPackage/DiscordPlainChatProof.java"
) | ForEach-Object { Join-Path $taskRoot $_ }
$taskJavac = if ($JavaBin) { Join-Path $JavaBin 'javac.exe' } else { 'javac' }
$taskJava = if ($JavaBin) { Join-Path $JavaBin 'java.exe' } else { 'java' }
& $taskJavac --release 8 -cp ($taskClasspath -join [IO.Path]::PathSeparator) -d $taskClasses @taskSources
if ($LASTEXITCODE -ne 0) { throw 'Presentation compilation failed' }
$taskClasspath = (@($taskClasses) + $taskClasspath) -join [IO.Path]::PathSeparator
foreach ($taskProof in @('DiscordItemNamePresentationProof', 'DiscordPlainChatProof')) {
    & $taskJava -cp $taskClasspath "com.loohp.interactivechatdiscordsrvaddon.utils.$taskProof"
    if ($LASTEXITCODE -ne 0) { throw "Presentation proof failed: $taskProof" }
}
Write-Output 'Presentation checks only; no full plugin release or live Discord acceptance claimed.'
