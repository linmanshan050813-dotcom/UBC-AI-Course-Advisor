# Docker build and run verification script
# For Phase 2 verification

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Phase 2: Docker Verification Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is available
Write-Host "[1/7] Checking Docker environment..." -ForegroundColor Yellow
try {
    $dockerVersion = docker --version
    $composeVersion = docker compose version
    Write-Host "  ✓ Docker: $dockerVersion" -ForegroundColor Green
    Write-Host "  ✓ Docker Compose: $composeVersion" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Docker is not installed or not in PATH" -ForegroundColor Red
    Write-Host "  Please install Docker Desktop first" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "[2/7] Cleaning up old containers..." -ForegroundColor Yellow
docker compose down 2>&1 | Out-Null
Write-Host "  ✓ Cleanup completed" -ForegroundColor Green

Write-Host ""
Write-Host "[3/7] Building Docker images (this may take a few minutes)..." -ForegroundColor Yellow
Write-Host "  Building backend and frontend images..." -ForegroundColor Gray
$buildOutput = docker compose build --progress=plain 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "  ✗ Build failed" -ForegroundColor Red
    Write-Host $buildOutput -ForegroundColor Red
    exit 1
}
Write-Host "  ✓ Build successful" -ForegroundColor Green

Write-Host ""
Write-Host "[4/7] Verifying image creation..." -ForegroundColor Yellow
Start-Sleep -Seconds 2

$allImageRepos = docker images --format "{{.Repository}}"

$backendExists = $allImageRepos | Where-Object { $_ -like "*backend*" }
$frontendExists = $allImageRepos | Where-Object { $_ -like "*frontend*" }

if ($backendExists -and $frontendExists) {
    Write-Host "  ✓ Backend image: $backendExists" -ForegroundColor Green
    Write-Host "  ✓ Frontend image: $frontendExists" -ForegroundColor Green
} else {
    Write-Host "  ✗ Image creation failed" -ForegroundColor Red
    
    if (-not $backendExists) {
        Write-Host "    Missing backend image" -ForegroundColor Red
    }
    if (-not $frontendExists) {
        Write-Host "    Missing frontend image" -ForegroundColor Red
    }
    
    Write-Host "    Available images:" -ForegroundColor Yellow
    $allImageRepos | ForEach-Object { Write-Host "      - $_" -ForegroundColor Gray }
    exit 1
}

Write-Host ""
Write-Host "[5/7] Starting services..." -ForegroundColor Yellow
docker compose up -d 2>&1 | Out-Null
Start-Sleep -Seconds 5
Write-Host "  ✓ Services started" -ForegroundColor Green

Write-Host ""
Write-Host "[6/7] Waiting for services to be ready (max 60 seconds)..." -ForegroundColor Yellow
$maxWait = 60
$waited = 0
$backendReady = $false

while ($waited -lt $maxWait -and -not $backendReady) {
    Start-Sleep -Seconds 2
    $waited += 2
    try {
        $response = Invoke-WebRequest -Uri http://localhost:8080/health -UseBasicParsing -TimeoutSec 2 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            $backendReady = $true
            Write-Host "  ✓ Backend ready (waited $waited seconds)" -ForegroundColor Green
        }
    } catch {
        Write-Host "  . Waiting for backend to start... ($waited/$maxWait seconds)" -ForegroundColor Gray
    }
}

if (-not $backendReady) {
    Write-Host "  ✗ Backend startup timeout" -ForegroundColor Red
    Write-Host "  View logs: docker compose logs backend" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "[7/7] Functionality verification..." -ForegroundColor Yellow

# Test backend health check
try {
    $health = Invoke-RestMethod -Uri http://localhost:8080/health -UseBasicParsing
    Write-Host "  ✓ Backend health check: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Backend health check failed" -ForegroundColor Red
    exit 1
}

# Test frontend access
try {
    $frontend = Invoke-WebRequest -Uri http://localhost:3000 -UseBasicParsing -TimeoutSec 5
    if ($frontend.StatusCode -eq 200) {
        Write-Host "  ✓ Frontend page accessible" -ForegroundColor Green
    }
} catch {
    Write-Host "  ⚠ Frontend access may have issues (check logs)" -ForegroundColor Yellow
}

# Test API proxy
try {
    $apiHealth = Invoke-RestMethod -Uri http://localhost:3000/api/health -UseBasicParsing
    Write-Host "  ✓ API proxy working: $($apiHealth.status)" -ForegroundColor Green
} catch {
    Write-Host "  ⚠ API proxy may have issues" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  ✓ Phase 2 verification completed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Useful commands:" -ForegroundColor Yellow
Write-Host "  View logs: docker compose logs -f" -ForegroundColor Gray
Write-Host "  View status: docker compose ps" -ForegroundColor Gray
Write-Host "  Stop services: docker compose down" -ForegroundColor Gray
Write-Host ""

