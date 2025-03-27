a
# Java 동시성 제어 방식 정리

## 동시성 제어 방식 비교

| 방식 | 설명 | 장점 | 단점 |
|------|------|------|------|
| `synchronized` | 블록 또는 메서드 단위로 객체 락을 걸어 순차 실행 | 간단하고 직관적<br> JVM에서 지원 |  락 범위 조절 어려움<br> 성능 저하 가능성 |
| `synchronized (String.intern())` | 특정 키(String)를 이용해 동기화 | 키 기반 잠금 가능<br> 사용자 단위 분리 가능 |  String pool 오염 위험<br> GC 누수 가능 |
| `ReentrantLock` | 명시적으로 락을 걸고 해제하는 방식 |  락 세밀 제어 가능 (타임아웃, 인터럽트 등)<br> 공정성 옵션 가능 |  락 해제를 개발자가 반드시 직접 처리해야 함 |
| `ConcurrentHashMap<K, Lock>` | 키 기반으로 락을 분리하여 동시 접근 제어 |  사용자별 락 분리 가능<br> 확장성 우수 |  메모리 관리 필요 (락 캐시 많아질 수 있음) |
| DB 비관적 락 (`FOR UPDATE`) | DB에서 row-level 락을 통해 정합성 보장 |  데이터 정확성 보장<br> 분산 시스템에서도 안전 |  성능 저하<br> 트랜잭션 관리 복잡 |
| DB 낙관적 락 (version 필드) | 업데이트 시점에 version을 비교하여 충돌 방지 |  락 없이도 정합성 확보<br> 성능 우수 |  충돌 시 재시도 로직 필요<br> 동시 충돌 가능성 있음 |
| Redis 분산 락 (Redisson 등) | 분산 환경에서 key 기반 락을 제공 |  분산 시스템에서도 동시성 제어 가능<br> TTL, 공정성 등 설정 다양 |  Redis 장애 시 위험<br> 외부 시스템 의존 필요 |

---

## 정리

- 단일 인스턴스 환경에서는 `synchronized`, `ReentrantLock`, `ConcurrentHashMap + Lock` 등 JVM 기반 락으로도 충분
- 사용자 단위 동시성 제어에는 **사용자 ID 기반 LockMap** 방식이 유용
- **분산 시스템이나 서버가 여러 대인 경우**, Redis 분산 락 또는 DB 락 사용 고려
- 동시성 제어는 **성능 ↔ 안정성** 간의 trade-off가 존재하므로 상황에 맞는 전략 선택이 중요

---

## 상황별 선택

| 상황                | 추천 방식 |
|-------------------|-----------|
| 간단한 단일 서버 환경      | `synchronized`, `ReentrantLock` |
| 사용자 단위로 락 분리      | `ConcurrentHashMap<Long, Lock>` |
| 고성능 읽기 많은 구조      | `ReadWriteLock`, `StampedLock` |
| DB 기반 트랜잭션 정합성 보장 | 비관적 락 (`FOR UPDATE`) 또는 낙관적 락 (version) |
| 멀티 인스턴스 환경        | Redis 분산 락 (`Redisson`) |

---


