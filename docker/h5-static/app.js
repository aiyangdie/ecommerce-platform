const API = location.origin + '/api/v1';
let token = localStorage.getItem('mall_token') || '';
let user = JSON.parse(localStorage.getItem('mall_user') || 'null');
let allProducts = [];
let currentProduct = null;
let selectedSkuId = null;

function toast(msg) {
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.style.display = 'block';
  setTimeout(() => (el.style.display = 'none'), 2000);
}

function headers() {
  const h = { 'Content-Type': 'application/json' };
  if (token) h['Authorization'] = 'Bearer ' + token;
  return h;
}

async function api(path, opts = {}) {
  const res = await fetch(API + path, { ...opts, headers: { ...headers(), ...(opts.headers || {}) } });
  const body = await res.json();
  if (body.code !== 0) throw new Error(body.message || '请求失败');
  return body.data;
}

function updateUserUI() {
  document.getElementById('user-info').textContent = user
    ? user.nickname + ' (' + user.mobile + ')'
    : '未登录 · 点击「我的」登录';
}

async function doLogin() {
  const mobile = document.getElementById('mobile').value;
  const code = document.getElementById('code').value;
  try {
    const data = await api('/auth/login', { method: 'POST', body: JSON.stringify({ mobile, code }) });
    token = data.token;
    user = data.user;
    localStorage.setItem('mall_token', token);
    localStorage.setItem('mall_user', JSON.stringify(user));
    updateUserUI();
    toast('登录成功');
    showTab('shop');
  } catch (e) {
    toast(e.message);
  }
}

function renderProductList(list) {
  const el = document.getElementById('products');
  if (!list.length) {
    el.innerHTML = '<div class="empty">暂无商品</div>';
    return;
  }
  el.innerHTML = list
    .map(
      (p) => `
      <div class="card" onclick="openDetail(${p.id})">
        <img src="${p.coverUrl || 'https://picsum.photos/seed/p' + p.id + '/80/80'}" alt="" />
        <div style="flex:1">
          <h3>${p.title}</h3>
          <div class="sub">${p.subTitle || ''}</div>
          <div class="price">¥${(p.minPrice || 0).toFixed(2)} 起</div>
        </div>
      </div>`
    )
    .join('');
}

async function loadProducts() {
  try {
    const kw = document.getElementById('keyword')?.value?.trim() || '';
    const q = kw ? '?keyword=' + encodeURIComponent(kw) : '';
    allProducts = await api('/products' + q);
    renderProductList(allProducts);
  } catch (e) {
    document.getElementById('products').innerHTML = '<div class="empty">' + e.message + '</div>';
  }
}

function searchProducts() {
  loadProducts();
}

async function openDetail(id) {
  try {
    currentProduct = await api('/products/' + id);
    selectedSkuId = currentProduct.defaultSkuId || currentProduct.skus?.[0]?.id;
    const skus = (currentProduct.skus || [])
      .map(
        (s) =>
          `<span class="sku-tag ${s.id === selectedSkuId ? 'on' : ''}" onclick="event.stopPropagation();selectSku(${s.id})">${s.skuName} ¥${s.price}</span>`
      )
      .join('');
    document.getElementById('detail-panel').innerHTML = `
      <img src="${currentProduct.coverUrl || ''}" alt="" />
      <h2>${currentProduct.title}</h2>
      <p class="sub">${currentProduct.subTitle || ''}</p>
      <div class="sku-row">${skus}</div>
      <div style="display:flex;gap:8px;margin-top:16px">
        <button class="btn" onclick="addCart(selectedSkuId)">加入购物车</button>
        <button class="btn btn-outline" onclick="buyNow(selectedSkuId)">立即购买</button>
      </div>`;
    document.getElementById('detail-panel').style.display = 'block';
    document.getElementById('shop-panel').style.display = 'none';
    document.getElementById('cart-panel').style.display = 'none';
    document.getElementById('order-panel').style.display = 'none';
    document.getElementById('login-panel').style.display = 'none';
    document.getElementById('btn-back').style.display = 'block';
    document.getElementById('search-bar').style.display = 'none';
    document.getElementById('page-title').textContent = '商品详情';
  } catch (e) {
    toast(e.message);
  }
}

function selectSku(id) {
  selectedSkuId = id;
  openDetail(currentProduct.id);
}

function closeDetail() {
  document.getElementById('detail-panel').style.display = 'none';
  document.getElementById('shop-panel').style.display = 'block';
  document.getElementById('btn-back').style.display = 'none';
  document.getElementById('search-bar').style.display = 'block';
  document.getElementById('page-title').textContent = '商城首页';
}

function needLogin() {
  if (!token) {
    toast('请先登录');
    showTab('login');
    return true;
  }
  return false;
}

async function addCart(skuId) {
  if (needLogin()) return;
  try {
    await api('/cart/items', { method: 'POST', body: JSON.stringify({ skuId, quantity: 1 }) });
    toast('已加入购物车');
  } catch (e) {
    toast(e.message);
  }
}

async function buyNow(skuId) {
  if (needLogin()) return;
  try {
    const order = await api('/orders', {
      method: 'POST',
      body: JSON.stringify({
        skuId,
        quantity: 1,
        receiverName: '演示用户',
        receiverPhone: user.mobile,
        receiverAddr: '演示地址',
      }),
    });
    await api('/orders/' + order.orderNo + '/pay', { method: 'POST' });
    toast('下单并支付成功');
    closeDetail();
    showTab('order');
  } catch (e) {
    toast(e.message);
  }
}

async function loadCart() {
  if (!token) return (document.getElementById('cart-list').innerHTML = '<div class="empty">请先登录</div>');
  try {
    const list = await api('/cart/items');
    if (!list.length) return (document.getElementById('cart-list').innerHTML = '<div class="empty">购物车为空</div>');
    document.getElementById('cart-list').innerHTML =
      list
        .map(
          (i) => `
      <div class="card">
        <div style="flex:1"><h3>${i.skuName}</h3><div class="price">¥${i.price} x ${i.quantity}</div></div>
        <button class="btn btn-sm btn-outline" onclick="removeCart(${i.id})">删除</button>
      </div>`
        )
        .join('') + `<button class="btn" style="width:100%;margin-top:8px" onclick="checkoutCart()">结算购物车</button>`;
  } catch (e) {
    document.getElementById('cart-list').innerHTML = '<div class="empty">' + e.message + '</div>';
  }
}

async function removeCart(id) {
  await api('/cart/items/' + id, { method: 'DELETE' });
  loadCart();
}

async function checkoutCart() {
  try {
    const order = await api('/orders/from-cart', {
      method: 'POST',
      body: JSON.stringify({ receiverName: '演示用户', receiverPhone: user.mobile, receiverAddr: '演示地址' }),
    });
    await api('/orders/' + order.orderNo + '/pay', { method: 'POST' });
    toast('结算成功');
    loadCart();
    showTab('order');
  } catch (e) {
    toast(e.message);
  }
}

async function loadOrders() {
  if (!token) return (document.getElementById('order-list').innerHTML = '<div class="empty">请先登录</div>');
  try {
    const list = await api('/orders');
    if (!list.length) return (document.getElementById('order-list').innerHTML = '<div class="empty">暂无订单</div>');
    const statusMap = { 10: '待支付', 20: '待发货', 30: '待收货', 40: '已完成', 50: '已取消' };
    document.getElementById('order-list').innerHTML = list
      .map(
        (o) => `
      <div class="card" style="flex-direction:column">
        <div><b>${o.orderNo}</b> <span style="float:right;color:#ff6b35">${statusMap[o.status]}</span></div>
        <div class="sub">${(o.createdAt || '').slice(0, 16)}</div>
        <div class="sub">${(o.items || []).map((x) => x.skuName + 'x' + x.quantity).join(', ')}</div>
        <div class="price">¥${o.payAmount}</div>
        <div style="margin-top:8px;display:flex;gap:8px;flex-wrap:wrap">
          ${o.status === 10 ? `<button class="btn btn-sm" onclick="payOrder('${o.orderNo}')">去支付</button><button class="btn btn-sm btn-outline" onclick="cancelOrder('${o.orderNo}')">取消</button>` : ''}
          ${o.status === 30 ? `<button class="btn btn-sm btn-outline" onclick="confirmOrder('${o.orderNo}')">确认收货</button>` : ''}
        </div>
      </div>`
      )
      .join('');
  } catch (e) {
    document.getElementById('order-list').innerHTML = '<div class="empty">' + e.message + '</div>';
  }
}

async function payOrder(no) {
  await api('/orders/' + no + '/pay', { method: 'POST' });
  toast('支付成功');
  loadOrders();
}

async function cancelOrder(no) {
  await api('/orders/' + no + '/cancel', { method: 'POST' });
  toast('已取消');
  loadOrders();
}

async function confirmOrder(no) {
  await api('/orders/' + no + '/confirm', { method: 'POST' });
  toast('已确认收货');
  loadOrders();
}

function showTab(tab) {
  closeDetail();
  document.getElementById('shop-panel').style.display = tab === 'shop' ? 'block' : 'none';
  document.getElementById('cart-panel').style.display = tab === 'cart' ? 'block' : 'none';
  document.getElementById('order-panel').style.display = tab === 'order' ? 'block' : 'none';
  document.getElementById('login-panel').style.display = tab === 'login' ? 'block' : 'none';
  const titles = { shop: '商城首页', cart: '购物车', order: '我的订单', login: '个人中心' };
  document.getElementById('page-title').textContent = titles[tab] || '商城';
  if (tab === 'shop') loadProducts();
  if (tab === 'cart') loadCart();
  if (tab === 'order') loadOrders();
}

updateUserUI();
loadProducts();
