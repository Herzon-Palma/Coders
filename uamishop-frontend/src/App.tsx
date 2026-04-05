import { useState, useEffect } from 'react';
import './App.css';
import { api } from './api';

function App() {
  const [products, setProducts] = useState<any[]>([]);
  const [cart, setCart] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [notification, setNotification] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
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

  const showNotification = (msg: string) => {
    setNotification(msg);
    setTimeout(() => setNotification(''), 3000);
  };

  const initialize = async () => {
    setLoading(true);
    try {
      let prods = await api.getProducts();
      
      // If no products, seed some!
      if (prods.length === 0) {
        showNotification('Seeding initial products...');
        await api.seedProducts();
        prods = await api.getProducts();
      } else {
        // Activate any products that might be disabled
        await api.activateAllProducts(prods);
        prods = await api.getProducts();
      }
      setProducts(prods);

      // Create or get cart
      const cartData = await api.createCart(CLIENT_ID);
      setCart(cartData);
    } catch (error) {
      console.error('Error initializing:', error);
      showNotification('Error connecting to backend');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async (productId: string) => {
    if (!cart) return;
    try {
      const updatedCart = await api.addToCart(cart.id, productId, 1);
      setCart(updatedCart);
      showNotification('Product added to cart!');
    } catch (error) {
      console.error('Error adding to cart:', error);
      showNotification('Failed to add product');
    }
  };

  const handleCheckout = async () => {
    try {
      // Paso 1: mover el carrito a estado CHECKOUT
      await api.initCartCheckout(cart.id);
      // Paso 2: crear la orden desde el carrito en CHECKOUT
      await api.checkoutCart(CLIENT_ID, address);
      showNotification('¡Orden creada exitosamente!');
      setIsModalOpen(false);
      // Crear nuevo carrito vacío
      const newCart = await api.createCart(CLIENT_ID);
      setCart(newCart);
    } catch (error: any) {
      console.error('Checkout failed:', error);
      showNotification(`Error: ${error.message || 'Checkout fallido'}`);
    }
  };

  if (loading) {
    return <div className="loading">Cargando uamiShop...</div>;
  }

  const cartItemsCount = cart?.items?.reduce((acc: number, item: any) => acc + Number(item.cantidad), 0) || 0;
  const cartTotal = cart?.items?.reduce((acc: number, item: any) => acc + Number(item.subtotal), 0) || 0;

  return (
    <div className="container">
      <header>
        <div className="title">uamiShop</div>
        <div className="cart-summary">
          <button className="cart-button" onClick={() => setIsModalOpen(true)}>
            🛒 Carrito ({cartItemsCount})
          </button>
        </div>
      </header>

      <main>
        <h2>Nuestros Productos</h2>
        <div className="product-grid">
          {products.map(product => (
            <div key={product.id} className="product-card">
              <div className="product-image">
                {product.nombre}
              </div>
              <div className="product-info">
                <div className="product-name">{product.nombre}</div>
                <div className="product-price">${product.precio?.monto || product.precio} {product.precio?.moneda || 'MXN'}</div>
                <button className="add-button" onClick={() => handleAddToCart(product.id)}>
                  Añadir al Carrito
                </button>
              </div>
            </div>
          ))}
        </div>
      </main>

      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2>Tu Carrito</h2>
              <button className="close-button" onClick={() => setIsModalOpen(false)}>×</button>
            </div>
            
            {cart?.items?.length === 0 ? (
              <p>Tu carrito está vacío.</p>
            ) : (
              <div>
                {cart?.items?.map((item: any) => (
                  <div key={item.productoid} className="cart-item">
                    <div className="cart-item-info">
                      <h4>{item.nombreProducto || `Producto ${String(item.productoid).substring(0, 8)}...`}</h4>
                      <p>Cant: {item.cantidad} x ${Number(item.precioUnitario).toFixed(2)}</p>
                    </div>
                    <div>
                      <strong>${Number(item.subtotal).toFixed(2)}</strong>
                    </div>
                  </div>
                ))}
                <div className="cart-total">
                  Total: ${cartTotal.toFixed(2)} MXN
                </div>

                <h3 style={{ marginTop: '20px' }}>Dirección de Envío</h3>
                <div className="form-group">
                  <label>Nombre del destinatario</label>
                  <input value={address.nombreDestinatario} onChange={e => setAddress({...address, nombreDestinatario: e.target.value})} />
                </div>
                <div className="form-group">
                  <label>Calle</label>
                  <input value={address.calle} onChange={e => setAddress({...address, calle: e.target.value})} />
                </div>
                <div className="form-group">
                  <label>Ciudad</label>
                  <input value={address.ciudad} onChange={e => setAddress({...address, ciudad: e.target.value})} />
                </div>
                <div className="form-group">
                  <label>Estado</label>
                  <input value={address.estado} onChange={e => setAddress({...address, estado: e.target.value})} />
                </div>
                <div style={{ display: 'flex', gap: '10px' }}>
                  <div className="form-group" style={{ flex: 1 }}>
                    <label>C.P. (5 dígitos)</label>
                    <input value={address.codigoPostal} maxLength={5} onChange={e => setAddress({...address, codigoPostal: e.target.value})} />
                  </div>
                  <div className="form-group" style={{ flex: 1 }}>
                    <label>Teléfono (10 dígitos)</label>
                    <input value={address.telefono} maxLength={10} onChange={e => setAddress({...address, telefono: e.target.value})} />
                  </div>
                </div>
                <div className="form-group">
                  <label>País (debe ser México)</label>
                  <input value={address.pais} readOnly style={{ background: '#f5f5f5', cursor: 'not-allowed' }} />
                </div>
                <div className="form-group">
                  <label>Instrucciones (opcional)</label>
                  <input value={address.instrucciones} onChange={e => setAddress({...address, instrucciones: e.target.value})} />
                </div>

                <button className="checkout-button" onClick={handleCheckout}>
                  Pagar y Confirmar Orden
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      {notification && <div className="notification">{notification}</div>}
    </div>
  );
}

export default App;
