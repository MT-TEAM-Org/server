#!/bin/bash

BLUE_PORT=8081
GREEN_PORT=8082
NGINX_CONF="/etc/nginx/conf.d/playhive.conf"

echo "🚀 무중단 배포 시작"

### ✅ Step 0: blue 컨테이너가 떠 있는지 확인
if ! docker ps --format '{{.Names}}' | grep -q 'app-blue'; then
  echo "❌ app-blue 컨테이너가 실행 중이어야 무중단 배포가 가능합니다."
  echo "💡 'docker-compose -f docker-compose.blue.yml up -d' 명령으로 먼저 blue를 올려주세요."
  exit 1
fi

### ✅ Step 1: green 컨테이너 실행 (이미지 pull)
echo "🟢 Step 1: green 컨테이너 실행"
docker-compose -f docker-compose.green.yml pull
docker-compose -f docker-compose.green.yml up -d

### ✅ Step 2: green 컨테이너 헬스체크
echo "🔍 Step 2: green 헬스체크 시작"
for i in {1..10}; do
  STATUS=$(curl -s http://localhost:${GREEN_PORT}/actuator/health | grep '"status":"UP"')
  if [ -n "$STATUS" ]; then
    echo "✅ green 서비스 기동 성공"
    break
  fi
  echo "⏳ 대기 중... ($i/10)"
  sleep 5
done

if [ -z "$STATUS" ]; then
  echo "❌ green 헬스체크 실패. blue 유지, green 종료"
  docker-compose -f docker-compose.green.yml down
  exit 1
fi

### ✅ Step 3: nginx 포트 전환 (8081 → 8082)
echo "🔄 Step 3: nginx 라우팅 전환 (blue → green)"
if grep -q "${BLUE_PORT}" "${NGINX_CONF}"; then
  sudo sed -i "s/${BLUE_PORT}/${GREEN_PORT}/" "${NGINX_CONF}"
  sudo nginx -s reload
  echo "✅ nginx 라우팅 전환 완료"
else
  echo "⚠️ nginx 설정 파일에 기존 blue 포트가 없습니다. 수동 확인 필요"
  docker-compose -f docker-compose.green.yml down
  exit 1
fi

### ✅ Step 4: 기존 blue 중단
echo "🧹 Step 4: 기존 blue 중지"
docker-compose -f docker-compose.blue.yml down

echo "🎉 무중단 배포 완료: green 컨테이너가 현재 서비스 중"
