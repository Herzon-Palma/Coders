const BASE_URL = 'http://localhost:8080';
// URL base del propio servidor de frontend (funciona en dev y en Docker).
const FRONTEND_ORIGIN = window.location.origin;

type ImagenRequest = {
  url: string;
  altText: string;
  orden: number;
};

type ProductoSeed = {
  nombre: string;
  descripcion: string;
  sku: string;
  precio: number;
  moneda: string;
  categoriaid: string;
  imagenes: ImagenRequest[];
};

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

  getOrders: async (clienteId: string) => {
    const res = await fetch(`${BASE_URL}/ordenes/api/v1/ordenes/cliente/${clienteId}`, {
      method: 'GET'
    });
    if (!res.ok) {
      console.error('Failed to fetch orders');
      return [];
    }
    return res.json();
  },

  seedProducts: async () => {
    const normalize = (s: string) => String(s || '').trim().toLowerCase();

    const listCategories = async () => {
      const res = await fetch(`${BASE_URL}/catalogo/api/v1/categorias`);
      if (!res.ok) return [];
      const cats = await res.json();
      return Array.isArray(cats) ? cats : [];
    };

    const ensureCategory = async (nombre: string, descripcion: string, cache: any[]) => {
      const existing = cache.find((c: any) => normalize(c?.nombre) === normalize(nombre));
      if (existing?.id) return existing.id;

      const createRes = await fetch(`${BASE_URL}/catalogo/api/v1/categorias`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombre, descripcion }),
      });
      if (!createRes.ok) {
        const txt = await createRes.text();
        throw new Error(`Failed to create category ${nombre}: ${txt}`);
      }
      const created = await createRes.json();
      cache.push(created);
      return created.id;
    };

    const catsCache = await listCategories();

    const categorias = {
      computacion: await ensureCategory('Computación', 'Equipo y accesorios para estudiar y programar', catsCache),
      electronica: await ensureCategory('Electrónica', 'Gadgets y periféricos', catsCache),
      ropa: await ensureCategory('Ropa', 'Ropa cómoda para la uni', catsCache),
      utiles: await ensureCategory('Útiles', 'Papelería y herramientas de estudio', catsCache),
    };

    // Helper: construye un ImagenRequest apuntando a la imagen local servida por el frontend.
    // Las imágenes viven en public/imagenes/ y Vite/Nginx las sirve en <origin>/imagenes/<file>.
    const localImg = (file: string, alt: string): ImagenRequest => ({
      url: `${FRONTEND_ORIGIN}/imagenes/${file}`,
      altText: alt,
      orden: 1,
    });

    const desired: ProductoSeed[] = [
      {
        nombre: 'Laptop para Programación 14"',
        descripcion: 'Ligera, rápida y lista para compilar proyectos y correr Docker.',
        sku: 'LAP-001',
        precio: 18999,
        moneda: 'MXN',
        categoriaid: categorias.computacion,
        imagenes: [localImg('laptop-para-programacion-14_0.png', 'Laptop para programación')],
      },
      {
        nombre: 'Teclado Mecánico Compacto',
        descripcion: 'Switches táctiles y diseño 75% para escritorios pequeños.',
        sku: 'TEC-002',
        precio: 1399,
        moneda: 'MXN',
        categoriaid: categorias.computacion,
        imagenes: [localImg('teclado-mecanico-compacto_0.png', 'Teclado mecánico')],
      },
      {
        nombre: 'Mouse Ergonómico',
        descripcion: 'Ideal para largas sesiones de código y diseño de interfaces.',
        sku: 'MOU-003',
        precio: 499,
        moneda: 'MXN',
        categoriaid: categorias.computacion,
        imagenes: [localImg('mouse-ergonomico_0.png', 'Mouse ergonómico')],
      },
      {
        nombre: 'Monitor 27" IPS',
        descripcion: 'Más espacio para editor, terminal y documentación.',
        sku: 'MON-004',
        precio: 3499,
        moneda: 'MXN',
        categoriaid: categorias.computacion,
        imagenes: [localImg('monitor-27-ips_0.png', 'Monitor 27 pulgadas')],
      },
      {
        nombre: 'SSD NVMe 1TB',
        descripcion: 'Arranques rápidos y builds más cortos.',
        sku: 'SSD-005',
        precio: 1599,
        moneda: 'MXN',
        categoriaid: categorias.computacion,
        imagenes: [localImg('ssd-nvme-1tb_0.png', 'SSD NVMe 1TB')],
      },
      {
        nombre: 'Hub USB-C 6-en-1',
        descripcion: 'Conecta HDMI, USB y carga desde un solo puerto.',
        sku: 'HUB-006',
        precio: 799,
        moneda: 'MXN',
        categoriaid: categorias.electronica,
        imagenes: [localImg('hub-usb-c-6-en-1_0.png', 'Hub USB-C')],
      },
      {
        nombre: 'Audífonos Over-Ear',
        descripcion: 'Para concentrarte en clase o en la biblioteca.',
        sku: 'AUD-007',
        precio: 999,
        moneda: 'MXN',
        categoriaid: categorias.electronica,
        imagenes: [localImg('audifonos-over-ear_0.png', 'Audífonos over-ear')],
      },
      {
        nombre: 'Mochila para Laptop',
        descripcion: 'Compartimento acolchado y espacio para cuadernos.',
        sku: 'MCH-008',
        precio: 899,
        moneda: 'MXN',
        categoriaid: categorias.ropa,
        imagenes: [localImg('mochila-laptop_0.png', 'Mochila para laptop')],
      },
      {
        nombre: 'Hoodie Universitario',
        descripcion: 'Cómodo para laboratorios fríos y largas noches de entrega.',
        sku: 'SUD-009',
        precio: 699,
        moneda: 'MXN',
        categoriaid: categorias.ropa,
        imagenes: [localImg('hoodie-universitario_0.png', 'Hoodie universitario')],
      },
      {
        nombre: 'Playera Minimalista',
        descripcion: 'Tela suave, ideal para el día a día en la uni.',
        sku: 'PLY-010',
        precio: 249,
        moneda: 'MXN',
        categoriaid: categorias.ropa,
        imagenes: [localImg('playera-minimalista_0.png', 'Playera minimalista')],
      },
      {
        nombre: 'Libreta de Cuadricula',
        descripcion: 'Perfecta para algoritmos, diagramas y apuntes.',
        sku: 'LIB-011',
        precio: 79,
        moneda: 'MXN',
        categoriaid: categorias.utiles,
        imagenes: [localImg('libreta-cuadricula_0.png', 'Libreta de cuadrícula')],
      },
      {
        nombre: 'Plumas Gel (Pack)',
        descripcion: 'Tinta suave para escribir rápido en clase.',
        sku: 'PLU-012',
        precio: 99,
        moneda: 'MXN',
        categoriaid: categorias.utiles,
        imagenes: [localImg('plumas-gel_0.png', 'Plumas gel')],
      },
      {
        nombre: 'Calculadora Científica',
        descripcion: 'Para matemáticas discretas, álgebra y física.',
        sku: 'CAL-013',
        precio: 349,
        moneda: 'MXN',
        categoriaid: categorias.utiles,
        imagenes: [localImg('calculadora-cientifica_0.png', 'Calculadora científica')],
      },
      {
        nombre: 'USB 64GB',
        descripcion: 'Respalda prácticas, presentaciones y entregas.',
        sku: 'USB-014',
        precio: 159,
        moneda: 'MXN',
        categoriaid: categorias.utiles,
        imagenes: [localImg('usb-64gb_0.png', 'Memoria USB 64GB')],
      },
      {
        nombre: 'Raspberry Pi (Kit)',
        descripcion: 'Proyectos de sistemas, redes y electrónica.',
        sku: 'RAS-015',
        precio: 1799,
        moneda: 'MXN',
        categoriaid: categorias.electronica,
        imagenes: [localImg('raspberry-pi-kit_0.png', 'Raspberry Pi Kit')],
      },
    ];

    const existing = await api.getProducts();

    const bySku = new Map<string, any>();
    for (const p of Array.isArray(existing) ? existing : []) {
      bySku.set(normalize(p?.sku), p);
    }

    for (const p of desired) {
      const current = bySku.get(normalize(p.sku));
      if (!current?.id) {
        const res = await fetch(`${BASE_URL}/catalogo/api/v1/productos`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(p),
        });
        if (res.ok) {
          const created = await res.json();
          await fetch(`${BASE_URL}/catalogo/api/v1/productos/${created.id}/activar`, { method: 'POST' });
        } else {
          const txt = await res.text();
          console.error('Seed product failed:', p.sku, txt);
        }
        continue;
      }

      const hasImages = Array.isArray(current?.imagenes) && current.imagenes.length > 0 && Boolean(current.imagenes[0]?.url);
      const needsUpdate =
        normalize(current?.nombre) !== normalize(p.nombre) ||
        normalize(current?.descripcion) !== normalize(p.descripcion) ||
        normalize(current?.moneda) !== normalize(p.moneda) ||
        String(current?.categoriaid || '') !== String(p.categoriaid || '') ||
        Number(current?.precio?.monto ?? current?.precio) !== Number(p.precio) ||
        !hasImages;

      if (needsUpdate) {
        const res = await fetch(`${BASE_URL}/catalogo/api/v1/productos/${current.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(p),
        });
        if (!res.ok) {
          const txt = await res.text();
          console.error('Update seeded product failed:', p.sku, txt);
        }
      }

      if (!current?.disponible) {
        await fetch(`${BASE_URL}/catalogo/api/v1/productos/${current.id}/activar`, { method: 'POST' });
      }
    }
  }
};
