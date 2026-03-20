-- 관리자 계정 (비밀번호: 1234)
INSERT INTO member (email, password, name, phone, address, role, created_at)
VALUES ('admin@teamketo.com', '$2a$10$DZ4mVY6D4re5fNIQGwYCIOHJVEpuZDhUdXIptDSaPY.6Vv6kkMv9K', '관리자', '010-1111-1111', '서울 강서구', 'ADMIN', NOW());

-- 일반 회원 (비밀번호: 1234)
INSERT INTO member (email, password, name, phone, address, role, created_at)
VALUES ('user@teamketo.com', '$2a$10$DZ4mVY6D4re5fNIQGwYCIOHJVEpuZDhUdXIptDSaPY.6Vv6kkMv9K', '테스트회원', '010-1234-5678', '서울 강서구', 'USER', NOW());

-- 상품 데이터 (저포드맵)
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('토마토 비프 스튜 & 두부', 8900, '', 999, 'product1.gif', 1, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('간장 수비드 닭다리살 & 오믈렛', 8900, '', 999, 'product2.gif', 1, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('들깨 오리불고기 & 두부', 8900, '', 999, 'product3.gif', 1, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('토마토 치킨 스튜 & 두부', 8900, '', 999, 'product4.jpg', 1, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('간장 수비드 목살 & 오믈렛', 8900, '', 999, 'product5.jpg', 1, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('순살 갈비찜 & 두부', 8900, '', 999, 'product6.jpg', 1, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('순살 삼계 찜닭 & 두부', 8900, '', 999, 'product7.jpg', 1, NOW());

-- 상품 데이터 (슬로우에이징)
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('수비드 간장 치킨', 8900, '', 999, 'product8.gif', 2, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('미소버터 대구 조림', 8900, '', 999, 'product9.gif', 2, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('아라비아따 오리 찜', 8900, '', 999, 'product10.gif', 2, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('토마토 로제 닭갈비', 8900, '', 999, 'product11.jpg', 2, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('코코넛 크림 치킨커리', 8900, '', 999, 'product12.jpg', 2, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('시트러스 대구 찜', 8900, '', 999, 'product13.jpg', 2, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('미소버터 연어 조림', 8900, '', 999, 'product14.jpg', 2, NOW());

-- 상품 데이터 (오리지널)
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('수비드 통삼겹 된장 덮밥', 8900, '', 999, 'product15.jpg', 3, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('수비드 통삼겹 들기름 두부면 막국수', 8900, '', 999, 'product16.jpg', 3, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('훈제 오리 들깨 크림 리조또', 8900, '', 999, 'product17.jpg', 3, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('우삼겹 규동 브로콜리', 8900, '', 999, 'product18.jpg', 3, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('우삼겹 구이 두부면 오일 파스타', 8900, '', 999, 'product19.jpg', 3, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('BTS & 치킨 치즈 리조또', 8900, '', 999, 'product20.jpg', 3, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('소고기 버섯 들깨 덮밥', 8900, '', 999, 'product21.jpg', 3, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('저당 키토 라자냐', 8900, '', 999, 'product22.jpg', 3, NOW());

-- 상품 데이터 (시그니처)
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('강남역 호랑이 삼겹', 9900, '', 999, 'product23.jpg', 4, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('수원 왕갈비통 닭목살', 9900, '', 999, 'product24.jpg', 4, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('기사식당 최강 제육', 9900, '', 999, 'product25.jpg', 4, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('춘천 들깨 닭갈비', 9900, '', 999, 'product26.jpg', 4, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('수랏간 삼치 솥밥', 9900, '', 999, 'product27.jpg', 4, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('항아리 차돌 된장', 9900, '', 999, 'product28.jpg', 4, NOW());

-- 상품 데이터 (더클린커피)
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('초신선 더클린커피 콜롬비아 수프리모 방탄커피 원두', 21800, '', 999, 'product29.jpg', 5, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('초신선 스페셜티 에티오피아 아리차 G1 더클린 커피 예가체프 원두', 21800, '', 999, 'product30.jpg', 5, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('곰팡이 독소 없는 더클린 커피 케냐AA 원두 PLUS', 21800, '', 999, 'product31.jpg', 5, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('초신선 디카페인 원두 과테말라 안티구아 더클린 커피', 21800, '', 999, 'product32.jpg', 5, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('초신선 과테말라 SHB 스페셜티 더클린 커피', 21800, '', 999, 'product33.jpg', 5, NOW());

-- 상품 데이터 (헬시푸드)
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('팀키토 이베리코 저당 만두 냉동 굴림 글루텐프리 고기굴림만두 168g', 5900, '', 999, 'product34.jpg', 6, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('[팀키토 x 사노셀] 유기농 애플사이다비니거 스틱 18ml x 20포', 15900, '', 999, 'product35.jpg', 6, NOW());
INSERT INTO product (name, price, description, stock, image_url, category_id, created_at) VALUES ('[팀키토 x 사노셀] 유기농 애플사이다비니거 대용량 1,000ml', 14900, '', 999, 'product36.jpg', 6, NOW());

-- 조회
SELECT * FROM member;
SELECT id, name, price, category_id FROM product ORDER BY id;
