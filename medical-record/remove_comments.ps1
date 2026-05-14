$path = 'd:\University_NBU\Medical_Record_System_F113327\medical-record\src\main\resources\static\app.js'
$lines = Get-Content -Path $path
$newLines = @()
foreach ($line in $lines) {
    $stripped = $line -replace '\s*//.*$', ''
    if ([string]::IsNullOrWhiteSpace($line)) {
        $newLines += $line
    } elseif (-not [string]::IsNullOrWhiteSpace($stripped)) {
        $newLines += $stripped
    }
}
$newLines | Set-Content -Path $path -Encoding UTF8
