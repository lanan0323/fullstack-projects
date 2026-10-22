# Banner and Buy-order Fulfillment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add admin-configurable homepage banners and let sellers fulfill an active buy order with a selected in-stock inventory item.

**Architecture:** A new `debuff_banner` resource owns carousel CRUD and a public, enabled-only query. The seller flow extends the existing buy-order transaction with an explicit `inventoryId`, preserving its wallet freeze, inventory freeze, order creation and buy-order completion transaction.

**Tech Stack:** Spring Boot 2.5, MyBatis XML, MySQL, Vue 3, Element Plus, Vite.

**Spec:** User-confirmed design in this task: configurable homepage carousel; seller selects an active buy order from a specific inventory item and sells at the bid price.

## Global Constraints

- Keep `/dev-api` to `localhost:8080` proxy and existing login/routing unchanged.
- Do not change administrator layout styles; banner administration uses the existing RuoYi admin conventions.
- A seller can fulfill only another user's active bid using their own `status=0` inventory whose `skinId` matches the bid.
- Database changes ship as an additive SQL migration; do not mutate a running database automatically.
- New back-end behavior must be compiled from the Maven reactor and the front end must complete `npm run build:prod`.

---

### Task 1: Banner data resource and migration

**Files:**
- Create: `sql/business/20260831_banner_and_buy_order.sql`
- Create: `debuff-trade/src/main/java/com/debuff/trade/domain/Banner.java`
- Create: `debuff-trade/src/main/java/com/debuff/trade/mapper/BannerMapper.java`
- Create: `debuff-trade/src/main/resources/mapper/trade/BannerMapper.xml`
- Create: `debuff-trade/src/main/java/com/debuff/trade/service/IBannerService.java`
- Create: `debuff-trade/src/main/java/com/debuff/trade/service/impl/BannerServiceImpl.java`
- Create: `debuff-trade/src/main/java/com/debuff/trade/controller/BannerController.java`

**Interfaces:**
- Produces: `GET /trade/banner/publicList` returning enabled banners ordered by `sortOrder ASC, id DESC`.
- Produces: admin CRUD at `/trade/banner` with `trade:banner:*` permissions.

- [ ] Add the migration with `debuff_banner` columns `title`, `subtitle`, `image_url`, `link_url`, `sort_order`, `status`, audit fields and banner permissions/menu.
- [ ] Add the Java domain, mapper, service and controller layers using the existing announcement resource patterns.
- [ ] Map public read to `status='0'`, never expose disabled banners.

### Task 2: Explicit-inventory buy-order fulfillment

**Files:**
- Modify: `debuff-trade/src/main/java/com/debuff/trade/controller/BuyOrderController.java`
- Modify: `debuff-trade/src/main/java/com/debuff/trade/service/IBuyOrderService.java`
- Modify: `debuff-trade/src/main/java/com/debuff/trade/service/impl/BuyOrderServiceImpl.java`
- Modify: `debuff-trade/src/main/java/com/debuff/trade/mapper/InventoryMapper.java`

**Interfaces:**
- Produces: `POST /api/buyOrder/{id}/accept` body `{ "inventoryId": 123 }`.
- Consumes: `InventoryMapper.selectInventoryEntityById(Long)` and conditional `IInventoryService.updateStatus`.

- [ ] Update the controller request body and service signature to accept an inventory ID.
- [ ] Verify inventory exists, belongs to the seller, is in stock and has the bid skin before freezing wallet or inventory.
- [ ] Preserve the existing transaction ordering: freeze buyer wallet → conditionally freeze selected inventory → create order → conditionally close buy order.

### Task 3: Admin banner editor and market carousel

**Files:**
- Create: `Debuff-ui/src/api/trade/banner.js`
- Create: `Debuff-ui/src/views/trade/banner/index.vue`
- Modify: `Debuff-ui/src/views/market/index.vue`
- Modify: `Debuff-ui/src/router/index.js` only if the static admin page needs a route (otherwise dynamic menu route resolves it).

**Interfaces:**
- Consumes: `GET /trade/banner/publicList` and `/trade/banner` CRUD.
- Produces: autoplaying market carousel with accessible indicators, manual navigation, empty-state fallback and optional link navigation.

- [ ] Add an admin editor using the existing `ImageUpload` component and status/sort controls.
- [ ] Replace the static market hero with a banner carousel; render title/subtitle as HTML and load the image through `resolveImgUrl`.
- [ ] Retain the existing branded hero as the fallback if no active banner is configured.

### Task 4: Seller UI for fulfillment

**Files:**
- Modify: `Debuff-ui/src/api/buyOrder.js`
- Modify: `Debuff-ui/src/views/inventory/index.vue`

**Interfaces:**
- Consumes: `GET /api/buyOrder/list?skinId=<id>` and `POST /api/buyOrder/<id>/accept` with selected `inventoryId`.
- Produces: “匹配求购” action for `status === 0` inventory and a confirmation dialog for a selected bid.

- [ ] Add `acceptBuyOrder(id, inventoryId)` API client.
- [ ] Fetch matching active bids for an in-stock inventory item and show price, buyer and created time.
- [ ] Confirm the selected bid, submit the inventory ID, refresh inventory statistics/list and show the resulting order path.

### Task 5: Verification

**Files:**
- Verify: `Debuff/pom.xml` reactor build
- Verify: `Debuff-ui/package.json` production build

- [ ] Run `mvn -pl debuff-admin -am -DskipTests package` and resolve compilation failures.
- [ ] Run `npm run build:prod` from `Debuff-ui`.
- [ ] Exercise local browser flow for login page, market carousel fallback/loaded state and an inventory matching-bid dialog when API data is available.
