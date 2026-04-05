const BASE_URL = 'http://localhost:8080';

export const api = {
  getProducts: async () => {
    const res = await fetch(`${BASE_URL}/catalogo/api/v1/productos`);
    if (!res.ok) throw new Error('Failed to fetch products');
    return res.json();
  },

  activateAllProducts: async (products: any[]) => {
    for (const p of products) {
      if (!p.disponible) {
        await fetch(`${BASE_URL}/catalogo/api/v1/productos/${p.id}/activar`, {
          method: 'POST'
        });
      }
    }
  },

  createCart: async (clienteId: string) => {
    const res = await fetch(`${BASE_URL}/ventas/api/v1/carritos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id: clienteId })
    });
    if (!res.ok) throw new Error('Failed to create cart');
    return res.json();
  },

  addToCart: async (cartId: string, productId: string, cantidad: number) => {
    const res = await fetch(`${BASE_URL}/ventas/api/v1/carritos/${cartId}/items`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ productoId: productId, cantidad })
    });
    if (!res.ok) throw new Error('Failed to add to cart');
    return res.json();
  },

  initCartCheckout: async (cartId: string) => {
    const res = await fetch(`${BASE_URL}/ventas/api/v1/carritos/${cartId}/checkout`, {
      method: 'POST'
    });
    if (!res.ok) {
      const errText = await res.text();
      console.error('Init checkout error:', errText);
      throw new Error(`Failed to initiate cart checkout: ${errText}`);
    }
    return res.json();
  },

  checkoutCart: async (clienteId: string, direccion: any) => {
    const res = await fetch(`${BASE_URL}/ordenes/api/v1/ordenes/desde-carrito`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        clienteId,
        direccion
      })
    });
    if (!res.ok) {
      const errText = await res.text();
      console.error('Checkout error:', errText);
      throw new Error(`Failed to checkout: ${errText}`);
    }
    return res.json();
  },

  seedProducts: async () => {
    // Primero, creamos una categoria dummy
    let catId = "123e4567-e89b-12d3-a456-426614174000";
    try {
      const catRes = await fetch(`${BASE_URL}/catalogo/api/v1/categorias`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombre: 'Electrónica', descripcion: 'Artículos electrónicos' })
      });
      if (catRes.ok) {
        const cat = await catRes.json();
        catId = cat.id;
      }
    } catch(e) { console.log('Categoria might already exist or error', e); }

    // Ahora creamos productos
    const products = [
      {
        nombre: 'Laptop Pro',
        descripcion: 'Una excelente laptop',
        sku: 'LAP-001',
        precio: 15000,
        moneda: 'MXN',
        categoriaid: catId
      },
      {
        nombre: 'Smartphone X',
        descripcion: 'Último modelo',
        sku: 'CEL-002',
        precio: 12000,
        moneda: 'MXN',
        categoriaid: catId
      },
      {
        nombre: 'Audífonos Bluetooth',
        descripcion: 'Cancelación de ruido',
        sku: 'AUD-003',
        precio: 2500,
        moneda: 'MXN',
        categoriaid: catId
      }
    ];

    for (let p of products) {
      const res = await fetch(`${BASE_URL}/catalogo/api/v1/productos`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(p)
      });
      if (res.ok) {
        const created = await res.json();
        // Activate product so it's available (disponible=true)
        await fetch(`${BASE_URL}/catalogo/api/v1/productos/${created.id}/activar`, {
          method: 'POST'
        });
      }
    }
  }
};
