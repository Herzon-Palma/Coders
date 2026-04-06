import { useState, useEffect, Component, useMemo } from 'react';
import './index.css';
import { api } from './api';

class ErrorBoundary extends Component<any, any> {
  constructor(props: any) { super(props); this.state = { hasError: false, errorStr: '' }; }
  static getDerivedStateFromError(error: any) { window.alert("CRASH: " + error.toString() + " \n " + error.stack); return { hasError: true, errorStr: error.toString() + " \n " + error.stack }; }
  render() { if (this.state.hasError) return <div style={{color:'red', padding:'50px'}}><pre>{this.state.errorStr}</pre></div>; return this.props.children; }
}

// ─── Helpers de categoría ────────────────────────────────────────────────────
async function fetchCategoryMap(baseUrl: string): Promise<Map<string, string>> {
  try {
    const res = await fetch(`${baseUrl}/catalogo/api/v1/categorias`);
    if (!res.ok) return new Map();
    const cats: { id: string; nombre: string }[] = await res.json();
    return new Map(cats.map(c => [c.id, c.nombre]));
  } catch {
    return new Map();
  }
}

// Emojis por nombre de categoría (fallback genérico)
const CATEGORY_ICONS: Record<string, string> = {
  'Computación': '💻',
  'Electrónica': '⚡',
  'Ropa': '👕',
  'Útiles': '📐',
};

function getCategoryIcon(nombre: string): string {
  return CATEGORY_ICONS[nombre] ?? '🛍️';
}

function stringToColor(str: string): string {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  const h = Math.abs(hash) % 360;
  return `hsl(${h}, 70%, 40%)`;
}

function MainApp() {
  const [products, setProducts] = useState<any[]>([]);
  const [cart, setCart] = useState<any>(null);
  const [addedProducts, setAddedProducts] = useState<Record<string, boolean>>({});
  const [myOrders, setMyOrders] = useState<any[]>([]);
  const [currentView, setCurrentView] = useState<'CATALOG' | 'ORDERS' | 'CHECKOUT'>('CATALOG');
  const [activeCategory, setActiveCategory] = useState<string>('Todos');

  const [loading, setLoading] = useState(true);
  const [notification, setNotification] = useState('');
  const [address, setAddress] = useState({
    nombreDestinatario: 'Juan Pérez',
    calle: 'Av. Siempre Viva 123',
    ciudad: 'Ciudad de México',
    estado: 'CDMX',
    codigoPostal: '06600',
    pais: 'México',
    telefono: '5512345678',
    instrucciones: ''
  });

  const CLIENT_ID = '123e4567-e89b-12d3-a456-426614174000';

  useEffect(() => {
    initialize();
  }, []);

  useEffect(() => {
    if (currentView === 'ORDERS') {
      fetchOrders();
    }
  }, [currentView]);

  const showNotification = (msg: string) => {
    setNotification(msg);
    setTimeout(() => setNotification(''), 3000);
  };

  const fetchOrders = async () => {
    try {
      const orders = await api.getOrders(CLIENT_ID);
      setMyOrders(Array.isArray(orders) ? orders : []);
    } catch (e: any) {
      console.error(e);
      showNotification('Error al cargar órdenes');
      setMyOrders([]);
    }
  }

  const initialize = async () => {
    setLoading(true);
    try {
      let prods = await api.getProducts();
      
      if (prods.length < 15) {
        showNotification('Inicializando catálogo...');
        await api.seedProducts();
        prods = await api.getProducts();
      }

      try {
        await api.activateAllProducts(prods);
        prods = await api.getProducts();
      } catch (e) {
        console.warn('Activate all products failed (some may lack images):', e);
      }

      // Enriquecer productos con el nombre de su categoría
      const catMap = await fetchCategoryMap('http://localhost:8080');
      const enriched = prods.map((p: any) => ({
        ...p,
        categoriaNombre: catMap.get(p.categoriaid) ?? 'Sin categoría',
      }));
      setProducts(enriched);

      const cartData = await api.createCart(CLIENT_ID);
      setCart(cartData);
    } catch (error) {
      console.error('Error initializing:', error);
      showNotification('Error de conexión. Intenta recargar.');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async (productId: string) => {
    if (!cart) return;
    try {
      const updatedCart = await api.addToCart(cart.id, productId, 1);
      setCart(updatedCart);
      
      setAddedProducts(prev => ({ ...prev, [productId]: true }));
      setTimeout(() => setAddedProducts(prev => ({ ...prev, [productId]: false })), 1500);

      showNotification('¡Producto agregado al carrito!');
    } catch (error) {
      console.error('Error adding to cart:', error);
      showNotification('Hubo un error al agregar el producto');
    }
  };

  const handleCheckout = async () => {
    try {
      await api.initCartCheckout(cart.id);
      await api.checkoutCart(CLIENT_ID, address);
      
      showNotification('✅ ¡Orden creada exitosamente!');
      setCurrentView('ORDERS');
      
      const newCart = await api.createCart(CLIENT_ID);
      setCart(newCart);
    } catch (error: any) {
      console.error('Checkout failed:', error);
      showNotification(`Error: ${error.message || 'Checkout fallido'}`);
    }
  };

  // ─── Agrupación y filtrado por categoría ───────────────────────────────────
  const categories = useMemo(() => {
    const names = new Set<string>();
    for (const p of products) {
      const cat = p?.categoriaNombre || p?.categoria?.nombre || 'Sin categoría';
      names.add(cat);
    }
    return ['Todos', ...Array.from(names)];
  }, [products]);

  const groupedProducts = useMemo(() => {
    const byCategory: Record<string, any[]> = {};
    for (const p of products) {
      const cat = p?.categoriaNombre || p?.categoria?.nombre || 'Sin categoría';
      if (!byCategory[cat]) byCategory[cat] = [];
      byCategory[cat].push(p);
    }
    return byCategory;
  }, [products]);

  const filteredGroups = useMemo(() => {
    if (activeCategory === 'Todos') return groupedProducts;
    return { [activeCategory]: groupedProducts[activeCategory] ?? [] };
  }, [activeCategory, groupedProducts]);

  const isAddressValid = useMemo(() => {
    return address.nombreDestinatario.trim() !== '' &&
           address.calle.trim() !== '' &&
           address.ciudad.trim() !== '' &&
           address.estado.trim() !== '' &&
           address.codigoPostal.trim().length === 5 &&
           address.telefono.trim().length === 10;
  }, [address]);
  // ──────────────────────────────────────────────────────────────────────────

  if (loading) {
    return (
      <div className="loading">
        <div className="loading-content">
          <div className="loading-spinner" />
          <span>Sincronizando con UamiShop...</span>
        </div>
      </div>
    );
  }

  const cartItemsCount = cart?.items?.reduce((acc: number, item: any) => acc + Number(item.cantidad), 0) || 0;
  const cartTotal = cart?.items?.reduce((acc: number, item: any) => acc + Number(item.subtotal), 0) || 0;

  return (
    <div className="container">
      <header className="glass-panel">
        <div className="brand-title">🛒 UamiShop</div>
        <div className="nav-controls">
          <button 
            className={`tab-button ${currentView === 'CATALOG' ? 'active' : ''}`}
            onClick={() => setCurrentView('CATALOG')}
          >
            <span className="nav-icon">📦</span>
            <span className="nav-label">Catálogo</span>
          </button>
          <button 
            className={`tab-button ${currentView === 'ORDERS' ? 'active' : ''}`}
            onClick={() => setCurrentView('ORDERS')}
          >
            <span className="nav-icon">📜</span>
            <span className="nav-label">Mis Órdenes</span>
          </button>
          <button 
            className={`action-button cart-btn ${currentView === 'CHECKOUT' ? 'active' : ''}`}
            onClick={() => setCurrentView('CHECKOUT')}
          >
            <span className="cart-icon">🛒</span>
            <span className="nav-label">Carrito</span>
            {cartItemsCount > 0 && <span key={cartItemsCount} className="cart-badge">{cartItemsCount}</span>}
          </button>
        </div>
      </header>

      <main>
        {currentView === 'CATALOG' && (
          <>
            {/* Barra de filtro por categoría */}
            <div className="category-filter-bar">
              {categories.map(cat => (
                <button
                  key={cat}
                  className={`category-pill ${activeCategory === cat ? 'active' : ''}`}
                  onClick={() => setActiveCategory(cat)}
                >
                  {cat !== 'Todos' && <span>{getCategoryIcon(cat)}</span>}
                  {cat}
                </button>
              ))}
            </div>

            {/* Secciones por categoría */}
            {Object.entries(filteredGroups).map(([catName, prods], catIndex) => (
              <section key={catName} className="category-section animate-slide-up" style={{ animationDelay: `${catIndex * 0.1}s` }}>
                <div className="category-section-header">
                  <span className="category-section-icon">{getCategoryIcon(catName)}</span>
                  <h2 className="category-section-title">{catName}</h2>
                  <span className="category-section-count">{prods.length} producto{prods.length !== 1 ? 's' : ''}</span>
                </div>
                <div className="product-grid">
                  {prods.map((product, prodIndex) => (
                    <div key={product.id} className="glass-panel product-card animate-pop-in" style={{ animationDelay: `${0.1 + (prodIndex * 0.05)}s` }}>
                      <div className="product-image" aria-label={product?.nombre || 'Producto'}>
                        {product?.imagenes?.[0]?.url ? (
                          <img
                            src={product.imagenes[0].url}
                            alt={product.imagenes?.[0]?.altText || product.nombre || 'Producto'}
                            loading="lazy"
                            onError={(e) => {
                              const el = e.currentTarget;
                              el.style.display = 'none';
                              const parent = el.parentElement;
                              if (parent) parent.classList.add('image-fallback');
                            }}
                          />
                        ) : (
                          <span className="product-image-fallback" style={{ background: stringToColor(product.id) }}>
                            {product?.nombre ? String(product.nombre).substring(0, 3).toUpperCase() : 'PRO'}
                          </span>
                        )}
                        <div className="product-category-badge">
                          {getCategoryIcon(catName)} {catName}
                        </div>
                      </div>
                      <div className="product-info">
                        <div className="product-name">{product.nombre}</div>
                        <div className="product-desc">{product.descripcion}</div>
                        <div className="product-price">${Number(product.precio?.monto || product.precio).toLocaleString()} <span className="product-currency">{product.precio?.moneda || 'MXN'}</span></div>
                        <button 
                          className={`action-button add-to-cart-btn ${addedProducts[product.id] ? 'added' : ''}`} 
                          onClick={() => handleAddToCart(product.id)}
                        >
                          {addedProducts[product.id] ? '✔ Añadido' : '+ Añadir al Carrito'}
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </section>
            ))}
          </>
        )}

        {currentView === 'ORDERS' && (
          <>
            <h2 style={{ marginBottom: '24px', fontWeight: 300, color: 'var(--text-secondary)' }}>Historial de Órdenes</h2>
            <div className="orders-list">
              {!Array.isArray(myOrders) || myOrders.length === 0 ? (
                <div className="glass-panel empty-state">
                  <div className="empty-state-icon">📦</div>
                  <div className="empty-state-text">Aún no tienes órdenes registradas.</div>
                  <button className="action-button" onClick={() => setCurrentView('CATALOG')}>
                    Ir al Catálogo
                  </button>
                </div>
              ) : (
                [...myOrders].reverse().map((orden, index) => (
                  <div key={orden?.ordenId || index} className="glass-panel order-card animate-slide-up" style={{ animationDelay: `${index * 0.08}s` }}>
                    <div className="order-header">
                      <span className="order-title">Orden #{myOrders.length - index}</span>
                      <span className={`order-status status-${orden?.estadoOrden}`}>{orden?.estadoOrden ? String(orden.estadoOrden).replace('_', ' ') : 'N/A'}</span>
                    </div>
                    <div className="order-items">
                      {Array.isArray(orden?.items) && orden.items.map((item: any, idx: number) => (
                        <div key={idx} className="order-item">
                          <div className="order-item-thumb">
                            {item?.imagenUrl ? (
                              <img
                                src={item.imagenUrl}
                                alt={item?.nombreProducto || 'Producto'}
                                loading="lazy"
                                onError={(e) => {
                                  const el = e.currentTarget;
                                  el.style.display = 'none';
                                  const parent = el.parentElement;
                                  if (parent) parent.classList.add('image-fallback');
                                }}
                              />
                            ) : (
                              <span className="order-item-fallback" style={{ background: stringToColor(item.productoid || item.nombreProducto || 'unknown') }}>
                                {item?.nombreProducto ? String(item.nombreProducto).substring(0, 2).toUpperCase() : 'PR'}
                              </span>
                            )}
                          </div>
                          <div className="order-item-meta">
                            <div className="order-item-name">{item?.nombreProducto || 'Producto'}</div>
                            <div className="order-item-sub">x{item?.cantidad || 0}{item?.sku ? ` · ${item.sku}` : ''}</div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                ))
              )}
            </div>
          </>
        )}

        {currentView === 'CHECKOUT' && (
          <div className="checkout-page glass-panel">
            <h2 style={{ marginBottom: '24px', fontWeight: 300, color: 'var(--text-secondary)' }}>Resumen de Compra</h2>
            
            {cart?.items?.length === 0 ? (
              <div className="empty-cart">
                <div className="empty-state-icon">🛒</div>
                <p style={{ color: 'var(--text-secondary)' }}>Tu carrito está vacío. Agrega productos desde el catálogo.</p>
                <button className="action-button" style={{ marginTop: '16px' }} onClick={() => setCurrentView('CATALOG')}>
                  Ir al Catálogo
                </button>
              </div>
            ) : (
              <div className="checkout-grid">
                <div className="checkout-items-section glass-panel-inner animate-slide-right">
                  <h3 style={{ marginTop: '0', marginBottom: '16px', color: 'var(--text-primary)' }}>Productos</h3>
                  <div style={{ maxHeight: '400px', overflowY: 'auto', paddingRight: '12px' }} className="custom-scroll">
                    {cart?.items?.map((item: any) => (
                      <div key={item.productoid} className="cart-item">
                        <div className="cart-item-info">
                          <h4 style={{ margin: '0 0 4px 0', fontWeight: 600 }}>{item.nombreProducto || `Producto ${String(item.productoid).substring(0, 8)}`}</h4>
                          <p style={{ margin: 0, fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Cant: {item.cantidad} × ${Number(item.precioUnitario).toLocaleString()}</p>
                        </div>
                        <div style={{ fontWeight: 'bold' }}>
                          ${Number(item.subtotal).toLocaleString()}
                        </div>
                      </div>
                    ))}
                  </div>
                  <div className="cart-total" style={{ borderTop: '1px solid var(--border-color)', paddingTop: '16px', marginTop: '16px' }}>
                    Total Final: ${cartTotal.toLocaleString()} MXN
                  </div>
                </div>

                <div className="checkout-form-section glass-panel-inner animate-slide-left">
                  <h3 style={{ marginTop: '0', marginBottom: '20px', color: 'var(--primary-color)' }}>Datos de Envío</h3>
                  <div className="form-group">
                    <label>Nombre del destinatario</label>
                    <input value={address.nombreDestinatario} onChange={e => setAddress({...address, nombreDestinatario: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>Calle y Número</label>
                    <input value={address.calle} onChange={e => setAddress({...address, calle: e.target.value})} />
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label>Ciudad</label>
                      <input value={address.ciudad} onChange={e => setAddress({...address, ciudad: e.target.value})} />
                    </div>
                    <div className="form-group">
                      <label>Estado</label>
                      <input value={address.estado} onChange={e => setAddress({...address, estado: e.target.value})} />
                    </div>
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label>C.P. (5 dígitos)</label>
                      <input value={address.codigoPostal} maxLength={5} onChange={e => setAddress({...address, codigoPostal: e.target.value})} />
                    </div>
                    <div className="form-group">
                      <label>Teléfono (10 dígitos)</label>
                      <input value={address.telefono} maxLength={10} onChange={e => setAddress({...address, telefono: e.target.value})} />
                    </div>
                  </div>
                  <div className="form-group">
                    <label>País (Solo México)</label>
                    <input value={address.pais} readOnly style={{ opacity: 0.5 }} />
                  </div>

                  <button 
                    className="checkout-button" 
                    onClick={handleCheckout}
                    disabled={!isAddressValid}
                  >
                    Pagar y Procesar Orden
                  </button>
                </div>
              </div>
            )}
          </div>
        )}
      </main>

      {notification && <div className="notification">{notification}</div>}
    </div>
  );
}

export default function App() {
  return <ErrorBoundary><MainApp /></ErrorBoundary>;
}
