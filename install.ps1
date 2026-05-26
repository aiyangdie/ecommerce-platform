# 一键安装脚本 (Windows PowerShell)
# 用法: 右键「使用 PowerShell 运行」或在项目根目录执行: .\install.ps1

$ErrorActionPreference = "Stop"
$Root = $PSScriptRoot

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  开源电商系统 - 一键安装" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 检查 Docker
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "[错误] 未检测到 Docker，请先安装 Docker Desktop:" -ForegroundColor Red
    Write-Host "  https://www.docker.com/products/docker-desktop/" -ForegroundColor Yellow
    exit 1
}

docker info | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "[错误] Docker 未运行，请启动 Docker Desktop 后重试" -ForegroundColor Red
    exit 1
}

# 环境配置
$envFile = Join-Path $Root ".env"
$envExample = Join-Path $Root ".env.example"
if (-not (Test-Path $envFile)) {
    Copy-Item $envExample $envFile
    Write-Host "[OK] 已创建 .env 配置文件" -ForegroundColor Green
}

Set-Location $Root
Write-Host "[..] 正在构建并启动全部服务（MySQL + Redis + API + 管理端 + H5）..." -ForegroundColor Yellow
docker compose -f docker/docker-compose.yml up -d --build

if ($LASTEXITCODE -ne 0) {
    Write-Host "[错误] 启动失败，请检查 Docker 日志" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "等待 API 就绪..." -ForegroundColor Yellow
$ok = $false
for ($i = 0; $i -lt 30; $i++) {
    Start-Sleep -Seconds 3
    try {
        $r = Invoke-RestMethod -Uri "http://127.0.0.1:8080/api/health" -TimeoutSec 3
        if ($r.data.status -eq "UP") { $ok = $true; break }
    } catch { }
}
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  安装完成！访问地址：" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "  H5 商城:     http://127.0.0.1:8082" -ForegroundColor White
Write-Host "  管理后台:    http://127.0.0.1:8081" -ForegroundColor White
Write-Host "  后端 API:    http://127.0.0.1:8080" -ForegroundColor White
Write-Host "  API 文档:    http://127.0.0.1:8080/doc.html" -ForegroundColor White
Write-Host ""
Write-Host "  管理员: admin / admin123" -ForegroundColor Cyan
Write-Host "  用户登录: 任意手机号 + 验证码 123456" -ForegroundColor Cyan
Write-Host ""
Write-Host "  停止服务: docker compose -f docker/docker-compose.yml down" -ForegroundColor Gray
if (-not $ok) { Write-Host "  [提示] API 仍在启动中，请稍候刷新" -ForegroundColor Yellow }
