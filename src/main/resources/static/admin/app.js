const state = {
    token: localStorage.getItem("adminToken"),
    role: localStorage.getItem("adminRole"),
    expiresAt: localStorage.getItem("adminExpiresAt"),
    menuList: JSON.parse(localStorage.getItem("adminMenuList") || "[]"),
    players: [],
    items: [],
    selectedPlayer: null,
    editingPlayer: null,
    editingItem: null,
    pages: {
        players: { page: 0, size: 20, totalPages: 1 },
        inventory: { page: 0, size: 20, totalPages: 1 },
        items: { page: 0, size: 20, totalPages: 1 },
        logs: { page: 0, size: 20, totalPages: 1 },
        accounts: { page: 0, size: 20, totalPages: 1 }
    }
};

const $ = (selector) => document.querySelector(selector);

const viewMeta = {
    player: { view: "players", label: "玩家管理", icon: "P" },
    item: { view: "items", label: "道具管理", icon: "I" },
    inventory: { view: "inventory", label: "背包管理", icon: "B" },
    operationLog: { view: "logs", label: "操作紀錄", icon: "L" },
    accountManagement: { view: "accounts", label: "帳號管理", icon: "A" }
};

const elements = {
    mainNav: $("#mainNav"),
    loginPanel: $("#loginPanel"),
    loginForm: $("#loginForm"),
    accountInput: $("#accountInput"),
    passwordInput: $("#passwordInput"),
    loginMessage: $("#loginMessage"),
    workspace: $("#workspace"),
    currentRole: $("#currentRole"),
    tokenExpiry: $("#tokenExpiry"),
    logoutButton: $("#logoutButton"),
    viewTitle: $("#viewTitle"),
    playersView: $("#playersView"),
    inventoryView: $("#inventoryView"),
    itemsView: $("#itemsView"),
    logsView: $("#logsView"),
    accountsView: $("#accountsView"),
    playerTableBody: $("#playerTableBody"),
    playerSearchForm: $("#playerSearchForm"),
    playerKeyword: $("#playerKeyword"),
    prevPage: $("#prevPage"),
    nextPage: $("#nextPage"),
    pageInfo: $("#pageInfo"),
    backToPlayers: $("#backToPlayers"),
    inventoryTitle: $("#inventoryTitle"),
    inventorySubtitle: $("#inventorySubtitle"),
    inventoryLookupForm: $("#inventoryLookupForm"),
    inventoryAccountInput: $("#inventoryAccountInput"),
    inventoryTableBody: $("#inventoryTableBody"),
    prevInventoryPage: $("#prevInventoryPage"),
    nextInventoryPage: $("#nextInventoryPage"),
    inventoryPageInfo: $("#inventoryPageInfo"),
    inventoryActionForm: $("#inventoryActionForm"),
    inventoryAction: $("#inventoryAction"),
    itemIdInput: $("#itemIdInput"),
    quantityInput: $("#quantityInput"),
    reasonInput: $("#reasonInput"),
    inventoryMessage: $("#inventoryMessage"),
    playerDialog: $("#playerDialog"),
    playerEditForm: $("#playerEditForm"),
    dialogTitle: $("#dialogTitle"),
    editNickname: $("#editNickname"),
    editLevel: $("#editLevel"),
    editMessage: $("#editMessage"),
    itemTableBody: $("#itemTableBody"),
    itemSearchForm: $("#itemSearchForm"),
    itemKeyword: $("#itemKeyword"),
    newItemButton: $("#newItemButton"),
    prevItemPage: $("#prevItemPage"),
    nextItemPage: $("#nextItemPage"),
    itemPageInfo: $("#itemPageInfo"),
    itemDialog: $("#itemDialog"),
    itemForm: $("#itemForm"),
    itemDialogTitle: $("#itemDialogTitle"),
    itemNameInput: $("#itemNameInput"),
    itemTypeInput: $("#itemTypeInput"),
    itemRareInput: $("#itemRareInput"),
    itemMaxAmountInput: $("#itemMaxAmountInput"),
    itemHpInput: $("#itemHpInput"),
    itemAtkInput: $("#itemAtkInput"),
    itemDefInput: $("#itemDefInput"),
    itemDurationInput: $("#itemDurationInput"),
    itemEffectInput: $("#itemEffectInput"),
    itemDescriptionInput: $("#itemDescriptionInput"),
    itemMessage: $("#itemMessage"),
    logTableBody: $("#logTableBody"),
    includeAllLogs: $("#includeAllLogs"),
    prevLogPage: $("#prevLogPage"),
    nextLogPage: $("#nextLogPage"),
    logPageInfo: $("#logPageInfo"),
    accountTableBody: $("#accountTableBody"),
    accountSearchForm: $("#accountSearchForm"),
    accountKeyword: $("#accountKeyword"),
    newAccountButton: $("#newAccountButton"),
    prevAccountPage: $("#prevAccountPage"),
    nextAccountPage: $("#nextAccountPage"),
    accountPageInfo: $("#accountPageInfo"),
    accountDialog: $("#accountDialog"),
    accountForm: $("#accountForm"),
    newAdminAccountInput: $("#newAdminAccountInput"),
    newAdminPasswordInput: $("#newAdminPasswordInput"),
    newAdminRoleInput: $("#newAdminRoleInput"),
    accountMessage: $("#accountMessage")
};

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

function setMessage(element, text, isSuccess = false) {
    element.textContent = text || "";
    element.classList.toggle("success", Boolean(isSuccess));
}

function formatDate(value) {
    if (!value) return "-";
    return new Intl.DateTimeFormat("zh-TW", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
    }).format(new Date(value));
}

async function request(path, options = {}) {
    const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
    if (state.token) headers.Authorization = `Bearer ${state.token}`;

    const response = await fetch(path, { ...options, headers });
    const payload = await response.json().catch(() => null);

    if (!response.ok || (payload && payload.code >= 400)) {
        if (response.status === 401 || response.status === 403) {
            if (response.status === 401) {
                clearSession();
                renderSession();
            }
        }
        throw new Error(payload?.message || "操作失敗，請稍後再試");
    }

    return payload?.data;
}

function getMenu(menuKey) {
    return state.menuList.find((menu) => menu.menuKey === menuKey);
}

function can(menuKey, operation) {
    const menu = getMenu(menuKey);
    return Boolean(menu?.enabled && menu.operations?.[operation]);
}

function saveSession(loginData) {
    state.token = loginData.token;
    state.role = loginData.role;
    state.expiresAt = loginData.expiresAt;
    state.menuList = loginData.menuList || [];
    localStorage.setItem("adminToken", state.token);
    localStorage.setItem("adminRole", state.role);
    localStorage.setItem("adminExpiresAt", state.expiresAt);
    localStorage.setItem("adminMenuList", JSON.stringify(state.menuList));
}

function clearSession() {
    state.token = null;
    state.role = null;
    state.expiresAt = null;
    state.menuList = [];
    state.selectedPlayer = null;
    localStorage.removeItem("adminToken");
    localStorage.removeItem("adminRole");
    localStorage.removeItem("adminExpiresAt");
    localStorage.removeItem("adminMenuList");
}

function renderNav() {
    const enabledMenus = state.menuList.filter((menu) => menu.enabled && viewMeta[menu.menuKey]);
    const menus = enabledMenus.length ? enabledMenus : [{ menuKey: "player", menuName: "玩家管理", enabled: true }];

    elements.mainNav.innerHTML = menus.map((menu, index) => {
        const meta = viewMeta[menu.menuKey];
        return `
            <button class="nav-item ${index === 0 ? "is-active" : ""}" type="button" data-view="${meta.view}">
                <span class="nav-icon">${meta.icon}</span>
                <span>${escapeHtml(menu.menuName || meta.label)}</span>
            </button>
        `;
    }).join("");
}

function renderSession() {
    const isLoggedIn = Boolean(state.token);
    elements.loginPanel.classList.toggle("is-hidden", isLoggedIn);
    elements.workspace.classList.toggle("is-hidden", !isLoggedIn);
    elements.currentRole.textContent = state.role || "未登入";
    elements.tokenExpiry.textContent = state.expiresAt ? `有效至 ${formatDate(state.expiresAt)}` : "請先登入後台";
    renderNav();
}

function setView(viewName) {
    ["players", "inventory", "items", "logs", "accounts"].forEach((name) => {
        elements[`${name}View`].classList.toggle("is-hidden", name !== viewName);
    });

    const labelMap = {
        players: "玩家管理",
        inventory: "背包管理",
        items: "道具管理",
        logs: "操作紀錄",
        accounts: "帳號管理"
    };
    elements.viewTitle.textContent = labelMap[viewName] || "後台";

    document.querySelectorAll(".nav-item").forEach((button) => {
        button.classList.toggle("is-active", button.dataset.view === viewName);
    });
}

function renderEmptyRow(tbody, colSpan) {
    tbody.innerHTML = `<tr><td colspan="${colSpan}" class="empty-state">目前沒有資料</td></tr>`;
}

function renderPager(kind, infoElement, prevButton, nextButton) {
    const page = state.pages[kind];
    infoElement.textContent = `第 ${page.page + 1} / ${Math.max(page.totalPages, 1)} 頁`;
    prevButton.disabled = page.page <= 0;
    nextButton.disabled = page.page + 1 >= page.totalPages;
}

async function loadPage(kind, loader, page) {
    state.pages[kind].page = Math.max(page, 0);
    await loader(state.pages[kind].page);
}

function renderPlayers() {
    if (!state.players.length) {
        renderEmptyRow(elements.playerTableBody, 6);
    } else {
        elements.playerTableBody.innerHTML = state.players.map((player) => `
            <tr>
                <td>${escapeHtml(player.accountId)}</td>
                <td>${escapeHtml(player.nickname)}</td>
                <td>${escapeHtml(player.level)}</td>
                <td><span class="status ${player.status === "ACTIVE" ? "active" : "banned"}">${escapeHtml(player.status)}</span></td>
                <td>${formatDate(player.lastLoginAt)}</td>
                <td>
                    <div class="row-actions">
                        <button class="ghost-button" type="button" data-action="edit" data-id="${player.id}" ${can("player", "edit") ? "" : "disabled"}>編輯</button>
                        <button class="ghost-button" type="button" data-action="status" data-id="${player.id}" ${can("player", player.status === "ACTIVE" ? "ban" : "unban") ? "" : "disabled"}>${player.status === "ACTIVE" ? "封鎖" : "啟用"}</button>
                        <button class="secondary-button" type="button" data-action="inventory" data-id="${player.id}">背包</button>
                    </div>
                </td>
            </tr>
        `).join("");
    }
    renderPager("players", elements.pageInfo, elements.prevPage, elements.nextPage);
}

async function loadPlayers(page = 0) {
    const params = new URLSearchParams({ page, size: state.pages.players.size });
    const keyword = elements.playerKeyword.value.trim();
    if (keyword) params.set("keyword", keyword);
    const data = await request(`/api/players?${params}`);
    state.players = data.content || [];
    state.pages.players.page = data.number || 0;
    state.pages.players.totalPages = data.totalPages || 1;
    renderPlayers();
}

function findPlayer(id) {
    return state.players.find((player) => String(player.id) === String(id));
}

function openPlayerDialog(player) {
    state.editingPlayer = player;
    elements.dialogTitle.textContent = `${player.accountId} / ${player.nickname}`;
    elements.editNickname.value = player.nickname;
    elements.editLevel.value = player.level;
    setMessage(elements.editMessage, "");
    elements.playerDialog.showModal();
}

async function savePlayer(event) {
    event.preventDefault();
    if (!state.editingPlayer) return;

    try {
        await request(`/api/players/${state.editingPlayer.id}`, {
            method: "PUT",
            body: JSON.stringify({
                nickname: elements.editNickname.value.trim(),
                level: Number(elements.editLevel.value)
            })
        });
        elements.playerDialog.close();
        await loadPlayers(state.pages.players.page);
    } catch (error) {
        setMessage(elements.editMessage, error.message);
    }
}

async function togglePlayerStatus(player) {
    const nextStatus = player.status === "ACTIVE" ? "BANNED" : "ACTIVE";
    await request(`/api/players/${player.accountId}/status`, {
        method: "PATCH",
        body: JSON.stringify({ status: nextStatus })
    });
    await loadPlayers(state.pages.players.page);
}

function renderInventory(items) {
    if (!items.length) {
        renderEmptyRow(elements.inventoryTableBody, 4);
    } else {
        elements.inventoryTableBody.innerHTML = items.map((item) => `
            <tr>
                <td>${escapeHtml(item.itemId)}</td>
                <td>${escapeHtml(item.itemName || "-")}</td>
                <td>${escapeHtml(item.quantity)}</td>
                <td>${formatDate(item.updatedAt)}</td>
            </tr>
        `).join("");
    }
    renderPager("inventory", elements.inventoryPageInfo, elements.prevInventoryPage, elements.nextInventoryPage);
}

async function openInventory(player, page = 0) {
    state.selectedPlayer = player;
    setView("inventory");
    elements.inventoryTitle.textContent = `${player.nickname} 的背包`;
    elements.inventorySubtitle.textContent = `帳號 ${player.accountId}，等級 ${player.level}`;
    setMessage(elements.inventoryMessage, "");
    await loadInventory(page);
}

async function loadInventory(page = 0) {
    if (!state.selectedPlayer) return;
    const params = new URLSearchParams({ page, size: state.pages.inventory.size });
    const data = await request(`/api/players/${state.selectedPlayer.accountId}/inventory?${params}`);
    state.pages.inventory.page = data.number || 0;
    state.pages.inventory.totalPages = data.totalPages || 1;
    renderInventory(data.content || []);
}

async function lookupInventory(event) {
    event.preventDefault();
    const accountId = elements.inventoryAccountInput.value.trim();
    if (!accountId) return;

    try {
        const player = await request(`/api/players/${encodeURIComponent(accountId)}`);
        await openInventory(player, 0);
    } catch (error) {
        state.selectedPlayer = null;
        elements.inventoryTitle.textContent = "玩家背包";
        elements.inventorySubtitle.textContent = error.message;
        renderEmptyRow(elements.inventoryTableBody, 4);
    }
}

async function submitInventoryAction(event) {
    event.preventDefault();
    if (!state.selectedPlayer) return;

    try {
        await request(`/api/players/${state.selectedPlayer.accountId}/inventory/${elements.inventoryAction.value}`, {
            method: "POST",
            body: JSON.stringify({
                itemId: Number(elements.itemIdInput.value),
                quantity: Number(elements.quantityInput.value),
                reason: elements.reasonInput.value.trim()
            })
        });
        setMessage(elements.inventoryMessage, "背包已更新", true);
        elements.inventoryActionForm.reset();
        elements.quantityInput.value = 1;
        await loadInventory(state.pages.inventory.page);
    } catch (error) {
        setMessage(elements.inventoryMessage, error.message);
    }
}

function renderItems() {
    elements.newItemButton.disabled = !can("item", "create");
    if (!state.items.length) {
        renderEmptyRow(elements.itemTableBody, 7);
    } else {
        elements.itemTableBody.innerHTML = state.items.map((item) => `
            <tr>
                <td>${item.id}</td>
                <td>${escapeHtml(item.name)}</td>
                <td>${escapeHtml(item.type)}</td>
                <td>${escapeHtml(item.rare || "-")}</td>
                <td>${escapeHtml(item.maxAmount)}</td>
                <td>${escapeHtml(item.effect || "-")}</td>
                <td>
                    <div class="row-actions">
                        <button class="ghost-button" type="button" data-action="edit-item" data-id="${item.id}" ${can("item", "edit") ? "" : "disabled"}>編輯</button>
                        <button class="ghost-button" type="button" data-action="delete-item" data-id="${item.id}" ${can("item", "delete") ? "" : "disabled"}>刪除</button>
                    </div>
                </td>
            </tr>
        `).join("");
    }
    renderPager("items", elements.itemPageInfo, elements.prevItemPage, elements.nextItemPage);
}

async function loadItems(page = 0) {
    const params = new URLSearchParams({ page, size: state.pages.items.size });
    const keyword = elements.itemKeyword.value.trim();
    if (keyword) params.set("keyword", keyword);
    const data = await request(`/api/admin/items?${params}`);
    state.items = data.content || [];
    state.pages.items.page = data.number || 0;
    state.pages.items.totalPages = data.totalPages || 1;
    renderItems();
}

function openItemDialog(item = null) {
    state.editingItem = item;
    elements.itemDialogTitle.textContent = item ? `編輯 ${item.name}` : "新增道具";
    elements.itemNameInput.value = item?.name || "";
    elements.itemTypeInput.value = item?.type || 1;
    elements.itemRareInput.value = item?.rare || "COMMON";
    elements.itemMaxAmountInput.value = item?.maxAmount || 1;
    elements.itemHpInput.value = item?.hp ?? "";
    elements.itemAtkInput.value = item?.atk ?? "";
    elements.itemDefInput.value = item?.def ?? "";
    elements.itemDurationInput.value = item?.duration ?? "";
    elements.itemEffectInput.value = item?.effect || "";
    elements.itemDescriptionInput.value = item?.description || "";
    setMessage(elements.itemMessage, "");
    elements.itemDialog.showModal();
}

function itemPayload() {
    const nullableNumber = (value) => value === "" ? null : Number(value);
    return {
        name: elements.itemNameInput.value.trim(),
        type: Number(elements.itemTypeInput.value),
        rare: elements.itemRareInput.value.trim(),
        maxAmount: Number(elements.itemMaxAmountInput.value),
        hp: nullableNumber(elements.itemHpInput.value),
        atk: nullableNumber(elements.itemAtkInput.value),
        def: nullableNumber(elements.itemDefInput.value),
        duration: nullableNumber(elements.itemDurationInput.value),
        effect: elements.itemEffectInput.value.trim(),
        description: elements.itemDescriptionInput.value.trim()
    };
}

async function saveItem(event) {
    event.preventDefault();
    const isEdit = Boolean(state.editingItem);
    try {
        await request(isEdit ? `/api/admin/items/${state.editingItem.id}` : "/api/admin/items", {
            method: isEdit ? "PUT" : "POST",
            body: JSON.stringify(itemPayload())
        });
        elements.itemDialog.close();
        await loadItems(state.pages.items.page);
    } catch (error) {
        setMessage(elements.itemMessage, error.message);
    }
}

async function deleteItem(itemId) {
    await request(`/api/admin/items/${itemId}`, { method: "DELETE" });
    await loadItems(state.pages.items.page);
}

async function loadLogs(page = 0) {
    const params = new URLSearchParams({
        page,
        size: state.pages.logs.size,
        includeAll: elements.includeAllLogs.checked
    });
    const data = await request(`/api/admin/logs?${params}`);
    const logs = data.content || [];
    if (!logs.length) {
        renderEmptyRow(elements.logTableBody, 5);
    } else {
        elements.logTableBody.innerHTML = logs.map((log) => `
            <tr>
                <td>${formatDate(log.createdAt)}</td>
                <td>${escapeHtml(log.adminAccount)}</td>
                <td>${escapeHtml(log.action)}</td>
                <td>${escapeHtml(log.targetType)} / ${escapeHtml(log.targetId)}</td>
                <td>${escapeHtml(log.detail || "-")}</td>
            </tr>
        `).join("");
    }
    state.pages.logs.page = data.number || 0;
    state.pages.logs.totalPages = data.totalPages || 1;
    renderPager("logs", elements.logPageInfo, elements.prevLogPage, elements.nextLogPage);
}

async function loadAccounts(page = 0) {
    const params = new URLSearchParams({ page, size: state.pages.accounts.size });
    const keyword = elements.accountKeyword.value.trim();
    if (keyword) params.set("keyword", keyword);
    const data = await request(`/api/admin/accounts?${params}`);
    const accounts = data.content || [];
    if (!accounts.length) {
        renderEmptyRow(elements.accountTableBody, 4);
    } else {
        elements.accountTableBody.innerHTML = accounts.map((account) => `
            <tr>
                <td>${account.id}</td>
                <td>${escapeHtml(account.account)}</td>
                <td>${escapeHtml(account.role)}</td>
                <td>${formatDate(account.createdAt)}</td>
            </tr>
        `).join("");
    }
    state.pages.accounts.page = data.number || 0;
    state.pages.accounts.totalPages = data.totalPages || 1;
    renderPager("accounts", elements.accountPageInfo, elements.prevAccountPage, elements.nextAccountPage);
}

async function createAccount(event) {
    event.preventDefault();
    try {
        await request("/api/admin/accounts", {
            method: "POST",
            body: JSON.stringify({
                account: elements.newAdminAccountInput.value.trim(),
                password: elements.newAdminPasswordInput.value,
                role: elements.newAdminRoleInput.value
            })
        });
        elements.accountDialog.close();
        elements.accountForm.reset();
        await loadAccounts(state.pages.accounts.page);
    } catch (error) {
        setMessage(elements.accountMessage, error.message);
    }
}

async function loadCurrentView(viewName) {
    if (viewName === "players") await loadPlayers(state.pages.players.page);
    if (viewName === "items") await loadItems(state.pages.items.page);
    if (viewName === "logs") await loadLogs(state.pages.logs.page);
    if (viewName === "accounts") await loadAccounts(state.pages.accounts.page);
}

elements.loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    setMessage(elements.loginMessage, "");
    try {
        const data = await request("/api/admin/auth/login", {
            method: "POST",
            body: JSON.stringify({
                account: elements.accountInput.value.trim(),
                password: elements.passwordInput.value
            })
        });
        saveSession(data);
        renderSession();
        setView("players");
        await loadPlayers(0);
    } catch (error) {
        setMessage(elements.loginMessage, error.message);
    }
});

elements.logoutButton.addEventListener("click", () => {
    clearSession();
    renderSession();
    setView("players");
});

elements.mainNav.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-view]");
    if (!button) return;
    const viewName = button.dataset.view;
    setView(viewName);
    await loadCurrentView(viewName);
});

elements.playerSearchForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    await loadPlayers(0);
});
elements.prevPage.addEventListener("click", () => loadPage("players", loadPlayers, state.pages.players.page - 1));
elements.nextPage.addEventListener("click", () => loadPage("players", loadPlayers, state.pages.players.page + 1));
elements.prevInventoryPage.addEventListener("click", () => loadPage("inventory", loadInventory, state.pages.inventory.page - 1));
elements.nextInventoryPage.addEventListener("click", () => loadPage("inventory", loadInventory, state.pages.inventory.page + 1));
elements.backToPlayers.addEventListener("click", () => setView("players"));
elements.inventoryLookupForm.addEventListener("submit", lookupInventory);
elements.inventoryActionForm.addEventListener("submit", submitInventoryAction);
elements.playerEditForm.addEventListener("submit", savePlayer);

elements.playerTableBody.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button || button.disabled) return;
    const player = findPlayer(button.dataset.id);
    if (!player) return;

    try {
        if (button.dataset.action === "edit") openPlayerDialog(player);
        if (button.dataset.action === "status") await togglePlayerStatus(player);
        if (button.dataset.action === "inventory") await openInventory(player, 0);
    } catch (error) {
        alert(error.message);
    }
});

elements.itemSearchForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    await loadItems(0);
});
elements.newItemButton.addEventListener("click", () => openItemDialog());
elements.itemForm.addEventListener("submit", saveItem);
elements.prevItemPage.addEventListener("click", () => loadPage("items", loadItems, state.pages.items.page - 1));
elements.nextItemPage.addEventListener("click", () => loadPage("items", loadItems, state.pages.items.page + 1));
elements.itemTableBody.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button || button.disabled) return;
    const item = state.items.find((record) => String(record.id) === String(button.dataset.id));
    if (!item) return;

    try {
        if (button.dataset.action === "edit-item") openItemDialog(item);
        if (button.dataset.action === "delete-item" && confirm(`刪除道具 ${item.name}？`)) {
            await deleteItem(item.id);
        }
    } catch (error) {
        alert(error.message);
    }
});

elements.includeAllLogs.addEventListener("change", () => loadLogs(0));
elements.prevLogPage.addEventListener("click", () => loadPage("logs", loadLogs, state.pages.logs.page - 1));
elements.nextLogPage.addEventListener("click", () => loadPage("logs", loadLogs, state.pages.logs.page + 1));

elements.accountSearchForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    await loadAccounts(0);
});
elements.newAccountButton.addEventListener("click", () => {
    setMessage(elements.accountMessage, "");
    elements.accountDialog.showModal();
});
elements.accountForm.addEventListener("submit", createAccount);
elements.prevAccountPage.addEventListener("click", () => loadPage("accounts", loadAccounts, state.pages.accounts.page - 1));
elements.nextAccountPage.addEventListener("click", () => loadPage("accounts", loadAccounts, state.pages.accounts.page + 1));

renderSession();
setView("players");
if (state.token) {
    loadPlayers(0).catch((error) => setMessage(elements.loginMessage, error.message));
}
