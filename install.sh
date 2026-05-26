#!/usr/bin/env bash
# 一键安装脚本 (Linux / macOS)
# 用法: chmod +x install.sh && ./install.sh

set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

echo "========================================"
echo "  开源电商系统 - 一键安装"
echo "========================================"

if ! command -v docker >/dev/null 2>&1; then
  echo "[错误] 请先安装 Docker: https://docs.docker.com/get-docker/"
  exit 1
fi

if ! docker info >/dev/null 2>&1; then
  echo "[错误] Docker 未运行"
  exit 1
fi

if [ ! -f .env ]; then
  cp .env.example .env
  echo "[OK] 已创建 .env"
fi

echo "[..] 构建并启动 MySQL + Redis + API + 管理端 + H5 ..."
docker compose -f docker/docker-compose.yml up -d --build

echo ""
echo "等待 API 就绪..."
for i in $(seq 1 30); do
  if curl -sf http://127.0.0.1:8080/api/health >/dev/null 2>&1; then
    break
  fi
  sleep 3
done

echo ""
echo "========================================"
echo "  安装完成！"
echo "========================================"
echo "  H5 商城:     http://127.0.0.1:8082"
echo "  管理后台:    http://127.0.0.1:8081"
echo "  后端 API:    http://127.0.0.1:8080"
echo "  API 文档:    http://127.0.0.1:8080/doc.html"
echo ""
echo "  管理员: admin / admin123"
echo "  用户: 任意手机号 + 验证码 123456"
echo ""
echo "  停止: docker compose -f docker/docker-compose.yml down"
