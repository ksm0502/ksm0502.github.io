# MORROW

React와 Vite를 기반으로 제작한 라이프스타일 쇼핑몰 프론트엔드 프로젝트입니다.

건강식품, 생활가전, 홈트레이닝, 디지털 카테고리의 12개 상품을 제공하며 상품 검색, 카테고리 필터, 가격 정렬, 상품 상세, 찜, 장바구니, Mock 로그인/회원가입 기능을 구현했습니다.

Zustand를 이용해 장바구니, 찜, 로그인 상태를 전역으로 관리하고 `persist` middleware를 적용해 새로고침 이후에도 주요 상태가 유지되도록 구성했습니다.

---

## 주요 기능

### 상품 탐색
- 라이프스타일 쇼핑몰 콘셉트의 Hero 배너
- 건강식품 / 생활가전 / 홈트레이닝 / 디지털 카테고리 바로가기
- 상품명 검색
- 카테고리 필터
- 가격 정렬
  - 기본순
  - 낮은 가격순
  - 높은 가격순
- 검색 및 필터 결과가 없을 경우 안내 메시지 표시
- PC / Tablet / Mobile 반응형 상품 그리드

### 상품 상세
- React Router를 이용한 동적 라우팅
- URL Parameter를 이용한 상품 조회
- 상품 이미지, 카테고리, 이름, 가격, 설명 표시
- 상세 페이지에서 장바구니 추가

### 장바구니
- 상품 추가
- 동일 상품 추가 시 수량 증가
- 상품 수량 증가 / 감소
- 수량이 1일 때 감소 버튼 비활성화
- 개별 상품 삭제
- 장바구니 전체 비우기
- 상품별 금액 및 총 결제금액 계산
- Header에서 전체 장바구니 수량 표시
- Zustand `persist`를 이용한 상태 유지

### 찜 목록
- 상품 카드에서 찜 추가 / 해제
- Header에서 찜 상품 개수 표시
- 찜 목록 페이지 제공
- 찜 목록에서 장바구니 추가
- 개별 찜 상품 삭제
- 찜 목록 전체 삭제
- Zustand `persist`를 이용한 상태 유지

### Mock 로그인 / 회원가입
- 이름, 이메일, 비밀번호를 이용한 회원가입
- 중복 이메일 가입 방지
- 저장된 회원 정보 기반 로그인
- 로그인 / 로그아웃 상태에 따른 Header UI 변경
- 로그인 상태 유지

> 이 프로젝트의 인증 기능은 프론트엔드 학습 및 포트폴리오용 Mock 기능입니다.  
> 실제 서버 인증이 아니며 비밀번호를 브라우저 `localStorage`에 저장하므로 운영 서비스에서 사용할 수 있는 보안 구조가 아닙니다.

### 사용자 경험
- `react-hot-toast`를 이용한 사용자 액션 피드백
- 존재하지 않는 URL 접근 시 404 페이지 표시
- Sticky Header
- MORROW 브랜드 Footer
- PC / Tablet / Mobile 반응형 레이아웃

---

## Tech Stack

### Frontend
- React 19
- JavaScript
- Vite 8
- CSS

### Libraries
- React Router DOM 7
- Zustand 5
- react-hot-toast

### Development
- ESLint
- npm

---

## Project Structure

```text
react-shop
├── public
│   ├── images
│   └── favicon.png
│
├── src
│   ├── components
│   │   ├── Footer.jsx
│   │   ├── Header.jsx
│   │   └── ProductCard.jsx
│   │
│   ├── data
│   │   └── products.js
│   │
│   ├── pages
│   │   ├── Cart.jsx
│   │   ├── Home.jsx
│   │   ├── Login.jsx
│   │   ├── NotFound.jsx
│   │   ├── ProductDetail.jsx
│   │   ├── Signup.jsx
│   │   └── Wishlist.jsx
│   │
│   ├── store
│   │   ├── authStore.js
│   │   ├── cartStore.js
│   │   └── wishlistStore.js
│   │
│   ├── styles
│   │   ├── auth.css
│   │   ├── cart.css
│   │   ├── footer.css
│   │   ├── global.css
│   │   ├── header.css
│   │   ├── home.css
│   │   ├── notFound.css
│   │   ├── product.css
│   │   ├── responsive.css
│   │   └── wishlist.css
│   │
│   ├── App.jsx
│   ├── index.css
│   └── main.jsx
│
├── README.md
├── TROUBLESHOOTING.md
├── eslint.config.js
├── index.html
├── package.json
├── package-lock.json
├── vercel.json
└── vite.config.js
```

---

## Routes

| 경로 | 페이지 | 설명 |
| --- | --- | --- |
| `/` | Home | Hero, 카테고리, 상품 목록, 검색, 필터, 정렬 |
| `/products/:id` | ProductDetail | 개별 상품 상세 정보 |
| `/wishlist` | Wishlist | 찜 상품 관리 |
| `/cart` | Cart | 장바구니 관리 |
| `/login` | Login | Mock 로그인 |
| `/signup` | Signup | Mock 회원가입 |
| `*` | NotFound | 존재하지 않는 경로의 404 페이지 |

---

## State Management

프로젝트의 전역 상태는 Zustand를 이용해 관리합니다.

### Cart Store

`src/store/cartStore.js`

주요 Action:
- `addToCart`
- `removeFromCart`
- `increaseQuantity`
- `decreaseQuantity`
- `clearCart`

동일한 상품을 다시 담으면 새로운 항목을 추가하는 대신 기존 상품의 `quantity`를 증가시킵니다.

장바구니 데이터는 Zustand `persist` middleware를 이용해 브라우저 `localStorage`의 `cart-storage`에 저장됩니다.

### Wishlist Store

`src/store/wishlistStore.js`

주요 Action:
- `toggleWishlist`
- `removeFromWishlist`
- `clearWishlist`
- `isWishlisted`

찜 목록은 `wishlist-storage`에 저장됩니다.

### Auth Store

`src/store/authStore.js`

주요 Action:
- `signup`
- `login`
- `logout`

로그인 상태는 `auth-storage`에 유지되며 Mock 회원 정보는 `mock-user` 키로 `localStorage`에 저장됩니다.

---

## Installation

```bash
npm install
npm run dev
```

프로덕션 빌드:

```bash
npm run build
```

빌드 결과 미리보기:

```bash
npm run preview
```

코드 검사:

```bash
npm run lint
```

---

## Troubleshooting

프로젝트 진행 중 실제로 겪었던 문제와 해결 과정은 별도 문서에 정리했습니다.

- [TROUBLESHOOTING.md](./TROUBLESHOOTING.md)

---

## 구현 과정에서 학습한 내용

- 컴포넌트와 페이지 단위의 구조 분리
- Props를 이용한 상품 데이터 전달
- `useState`를 이용한 검색 / 필터 / 정렬 상태 관리
- `filter`, `sort`, `map`, `reduce`를 활용한 데이터 처리
- React Router를 이용한 SPA 라우팅
- URL Parameter를 이용한 동적 상품 상세 페이지
- Zustand를 이용한 전역 상태 관리
- `persist` middleware를 이용한 브라우저 상태 저장
- 장바구니와 찜 상태를 독립적인 Store로 분리
- Mock 인증 상태 관리
- 조건부 렌더링을 이용한 빈 상태 처리
- Toast를 이용한 사용자 액션 피드백
- PC / Tablet / Mobile 반응형 레이아웃 구현
- 대형 단일 CSS 파일을 역할별 스타일 파일로 분리
- Vercel SPA 배포를 위한 rewrite 설정

초기에는 기능 구현 중심의 쇼핑몰 형태로 시작했지만, 이후 MORROW라는 라이프스타일 쇼핑몰 콘셉트로 브랜딩을 정리하고 Hero, 카테고리 탐색, 찜, Mock 인증, Footer, 반응형 디자인 등을 추가하며 프로젝트의 완성도를 높였습니다.

---

## 향후 개선 사항

- Spring 기반 Backend API 연동
- 서버 기반 회원가입 / 로그인 및 인증 처리
- 서버 기반 상품 데이터 관리
- 실제 주문 / 결제 기능
- 상품 리뷰 및 평점
- 페이지네이션 또는 무한 스크롤
- 관리자 상품 관리
- TypeScript 적용
- 테스트 코드 추가

---

## Demo

배포 후 실제 서비스 URL을 추가할 예정입니다.
