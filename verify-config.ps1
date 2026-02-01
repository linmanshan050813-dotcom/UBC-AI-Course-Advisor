# Docker配置验证脚本 (PowerShell)
# 用于验证阶段2的配置是否正确

Write-Host "=== Docker 配置验证 (阶段2) ===" -ForegroundColor Cyan
Write-Host ""

$errors = 0
$warnings = 0

# 1. 检查必要文件
Write-Host "1. 检查必要文件..." -ForegroundColor Yellow
$files = @(
    "docker-compose.yml",
    "backend/Dockerfile",
    "frontend/Dockerfile",
    "backend/.dockerignore",
    "frontend/.dockerignore",
    ".env.example"
)

foreach ($file in $files) {
    if (Test-Path $file) {
        Write-Host "  ✓ $file" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $file (缺失)" -ForegroundColor Red
        $errors++
    }
}

# 2. 检查 docker-compose.yml 配置
Write-Host ""
Write-Host "2. 检查 docker-compose.yml 配置..." -ForegroundColor Yellow

$composeContent = Get-Content "docker-compose.yml" -Raw

# 检查 VITE_API_BASE_URL
if ($composeContent -match "VITE_API_BASE_URL") {
    Write-Host "  ✓ VITE_API_BASE_URL 已配置" -ForegroundColor Green
    if ($composeContent -match "build:\s*args:\s*- VITE_API_BASE_URL") {
        Write-Host "    ✓ 构建参数已设置" -ForegroundColor Green
    } else {
        Write-Host "    ⚠ 构建参数可能缺失" -ForegroundColor Yellow
        $warnings++
    }
} else {
    Write-Host "  ✗ VITE_API_BASE_URL 未配置" -ForegroundColor Red
    $errors++
}

# 检查 GEMINI_API_KEY
if ($composeContent -match "GEMINI_API_KEY") {
    Write-Host "  ✓ GEMINI_API_KEY 已配置" -ForegroundColor Green
} else {
    Write-Host "  ⚠ GEMINI_API_KEY 未配置（可选）" -ForegroundColor Yellow
    $warnings++
}

# 检查健康检查配置
if ($composeContent -match "start_period:\s*60s") {
    Write-Host "  ✓ 健康检查 start_period 已设置为 60s" -ForegroundColor Green
} else {
    Write-Host "  ⚠ 健康检查 start_period 可能不是 60s" -ForegroundColor Yellow
    $warnings++
}

# 3. 检查前端 Dockerfile
Write-Host ""
Write-Host "3. 检查前端 Dockerfile..." -ForegroundColor Yellow

$frontendDockerfile = Get-Content "frontend/Dockerfile" -Raw

if ($frontendDockerfile -match "ARG VITE_API_BASE_URL") {
    Write-Host "  ✓ ARG VITE_API_BASE_URL 已定义" -ForegroundColor Green
} else {
    Write-Host "  ✗ ARG VITE_API_BASE_URL 未定义" -ForegroundColor Red
    $errors++
}

if ($frontendDockerfile -match "ENV VITE_API_BASE_URL") {
    Write-Host "  ✓ ENV VITE_API_BASE_URL 已设置" -ForegroundColor Green
} else {
    Write-Host "  ✗ ENV VITE_API_BASE_URL 未设置" -ForegroundColor Red
    $errors++
}

# 4. 检查后端 Dockerfile
Write-Host ""
Write-Host "4. 检查后端 Dockerfile..." -ForegroundColor Yellow

$backendDockerfile = Get-Content "backend/Dockerfile" -Raw

if ($backendDockerfile -match "COPY --from=builder.*\*\.jar") {
    Write-Host "  ✓ JAR 文件使用通配符复制" -ForegroundColor Green
} else {
    Write-Host "  ✗ JAR 文件可能仍使用硬编码名称" -ForegroundColor Red
    $errors++
}

# 5. 检查 .dockerignore 文件
Write-Host ""
Write-Host "5. 检查 .dockerignore 文件..." -ForegroundColor Yellow

if (Test-Path "backend/.dockerignore") {
    $backendIgnore = Get-Content "backend/.dockerignore" -Raw
    if ($backendIgnore -match "target/") {
        Write-Host "  ✓ backend/.dockerignore 排除 target/" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ backend/.dockerignore 可能未排除 target/" -ForegroundColor Yellow
        $warnings++
    }
}

if (Test-Path "frontend/.dockerignore") {
    $frontendIgnore = Get-Content "frontend/.dockerignore" -Raw
    if ($frontendIgnore -match "node_modules/") {
        Write-Host "  ✓ frontend/.dockerignore 排除 node_modules/" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ frontend/.dockerignore 可能未排除 node_modules/" -ForegroundColor Yellow
        $warnings++
    }
}

# 6. 检查 .env.example
Write-Host ""
Write-Host "6. 检查 .env.example..." -ForegroundColor Yellow

if (Test-Path ".env.example") {
    $envExample = Get-Content ".env.example" -Raw
    if ($envExample -match "GEMINI_API_KEY") {
        Write-Host "  ✓ .env.example 包含 GEMINI_API_KEY 说明" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ .env.example 可能缺少 GEMINI_API_KEY 说明" -ForegroundColor Yellow
        $warnings++
    }
}

# 总结
Write-Host ""
Write-Host "=== 验证结果 ===" -ForegroundColor Cyan
Write-Host "错误: $errors" -ForegroundColor $(if ($errors -eq 0) { "Green" } else { "Red" })
Write-Host "警告: $warnings" -ForegroundColor $(if ($warnings -eq 0) { "Green" } else { "Yellow" })

if ($errors -eq 0) {
    Write-Host ""
    Write-Host "✓ 配置验证通过！可以进行Docker构建测试。" -ForegroundColor Green
    Write-Host ""
    Write-Host "下一步:" -ForegroundColor Cyan
    Write-Host "  1. 运行: docker compose build" -ForegroundColor White
    Write-Host "  2. 运行: docker compose up -d" -ForegroundColor White
    Write-Host "  3. 测试: curl http://localhost:8080/health" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "✗ 发现配置错误，请修复后重试。" -ForegroundColor Red
    exit 1
}

