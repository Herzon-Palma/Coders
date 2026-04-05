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
    calle: 'Av. Siempre Viva 123',
    ciudad: 'Springfield',
    estado: 'Estado',
    codigoPostal: '12345',
    pais: 'País'
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
      await api.checkoutCart(CLIENT_ID, address);
      showNotification('Order placed successfully!');
      setIsModalOpen(false);
      // Refresh cart
      const newCart = await api.createCart(CLIENT_ID);
      setCart(newCart);
    } catch (error) {
      console.error('Checkout failed:', error);
      showNotification('Checkout failed');
    }
  };

  if (loading) {
    return <div className="loading">Cargando uamiShop...</div>;
  }

  const cartItemsCount = cart?.items?.reduce((acc: number, item: any) => acc + item.cantidad, 0) || 0;
  const cartTotal = cart?.items?.reduce((acc: number, item: any) => acc + (item.precio * item.cantidad), 0) || 0;

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
                  <div key={item.productoId} className="cart-item">
                    <div className="cart-item-info">
                      <h4>Producto {item.productoId.substring(0, 8)}...</h4>
                      <p>Cant: {item.cantidad} x ${item.precio}</p>
                    </div>
                    <div>
                      <strong>${item.precio * item.cantidad}</strong>
                    </div>
                  </div>
                ))}
                <div className="cart-total">
                  Total: ${cartTotal}
                </div>

                <h3 style={{ marginTop: '20px' }}>Dirección de Envío</h3>
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
                    <label>C.P.</label>
                    <input value={address.codigoPostal} onChange={e => setAddress({...address, codigoPostal: e.target.value})} />
                  </div>
                  <div className="form-group" style={{ flex: 1 }}>
                    <label>País</label>
                    <input value={address.pais} onChange={e => setAddress({...address, pais: e.target.value})} />
                  </div>
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
