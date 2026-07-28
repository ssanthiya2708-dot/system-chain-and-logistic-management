const express = require('express');
const cors = require('cors');
const path = require('path');
const mysql = require('mysql2/promise');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(express.static(__dirname));

// MySQL Database Connection Pool
const dbConfig = {
    host: process.env.DB_HOST || 'localhost',
    port: process.env.DB_PORT || 3306,
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || 'root',
    database: process.env.DB_NAME || 'supplychain'
};

let pool = null;
let isMySqlConnected = false;

// Memory Fallback Data
let inMemoryData = {
    users: [
        { id: 1, username: 'admin', password: 'admin123', full_name: 'System Administrator', email: 'admin@supplychain.com', role: 'admin' },
        { id: 2, username: 'manager', password: 'manager123', full_name: 'Logistics Manager', email: 'manager@supplychain.com', role: 'manager' }
    ],
    suppliers: [
        { id: 1, supplier_name: 'Global Freight & Logistics', contact_person: 'Robert Vance', email: 'robert@globalfreight.com', phone: '+1 555-0192', address: '100 Shipping Way, Chicago IL', rating: 4.8 },
        { id: 2, supplier_name: 'Apex Industrial Components', contact_person: 'Elena Rostova', email: 'elena@apexcomponents.com', phone: '+1 555-0482', address: '45 Industrial Pkwy, Houston TX', rating: 4.6 },
        { id: 3, supplier_name: 'PacNet Electronic Supplies', contact_person: 'Kenji Sato', email: 'sato@pacnet.com', phone: '+1 555-0923', address: '88 Tech Blvd, San Jose CA', rating: 4.9 }
    ],
    products: [
        { id: 1, product_name: 'Hydraulic Freight Lift Engine', category: 'Machinery', quantity: 15, price: 1250.00, min_stock_level: 5, supplier_id: 2, supplier_name: 'Apex Industrial Components' },
        { id: 2, product_name: 'Thermal Insulated Shipping Container', category: 'Packaging', quantity: 120, price: 85.50, min_stock_level: 20, supplier_id: 1, supplier_name: 'Global Freight & Logistics' },
        { id: 3, product_name: 'GPS Cargo Tracker Unit', category: 'Electronics', quantity: 80, price: 149.99, min_stock_level: 15, supplier_id: 3, supplier_name: 'PacNet Electronic Supplies' },
        { id: 4, product_name: 'Heavy-Duty Cargo Straps (Pack of 10)', category: 'Packaging', quantity: 300, price: 29.95, min_stock_level: 50, supplier_id: 1, supplier_name: 'Global Freight & Logistics' },
        { id: 5, product_name: 'Barcode Handheld Scanner Terminal', category: 'Electronics', quantity: 25, price: 399.00, min_stock_level: 8, supplier_id: 3, supplier_name: 'PacNet Electronic Supplies' }
    ],
    orders: [
        { id: 1, order_number: 'ORD-2026-1001', customer_name: 'Metro Retail Logistics', customer_email: 'orders@metroretail.com', shipping_address: '500 Commerce St, Dallas TX', order_date: '2026-07-28', status: 'Shipped', total_amount: 1635.45 },
        { id: 2, order_number: 'ORD-2026-1002', customer_name: 'Summit Distribution Inc', customer_email: 'procurement@summitdist.com', shipping_address: '12 Regional Hub Way, Atlanta GA', order_date: '2026-07-28', status: 'Processing', total_amount: 2500.00 },
        { id: 3, order_number: 'ORD-2026-1003', customer_name: 'Coastal Express Line', customer_email: 'shipping@coastalexp.com', shipping_address: '77 Seaport Blvd, Seattle WA', order_date: '2026-07-27', status: 'Delivered', total_amount: 749.95 }
    ],
    shipments: [
        { id: 1, tracking_number: 'TRK-98214-US', order_id: 1, order_number: 'ORD-2026-1001', carrier: 'FedEx Freight', origin: 'Chicago IL', destination: 'Dallas TX', current_location: 'Memphis TN Hub', status: 'In Transit', estimated_delivery: '2026-07-30' },
        { id: 2, tracking_number: 'TRK-98215-US', order_id: 2, order_number: 'ORD-2026-1002', carrier: 'DHL Cargo', origin: 'Houston TX', destination: 'Atlanta GA', current_location: 'Houston TX Terminal', status: 'Preparing', estimated_delivery: '2026-08-02' },
        { id: 3, tracking_number: 'TRK-98216-US', order_id: 3, order_number: 'ORD-2026-1003', carrier: 'UPS Freight', origin: 'San Jose CA', destination: 'Seattle WA', current_location: 'Seattle WA Facility', status: 'Delivered', estimated_delivery: '2026-07-27' }
    ]
};

async function initDatabase() {
    try {
        pool = mysql.createPool(dbConfig);
        const connection = await pool.getConnection();
        console.log('✅ Connected to MySQL Database successfully!');
        isMySqlConnected = true;
        connection.release();
    } catch (err) {
        console.warn('⚠️ MySQL Connection Warning:', err.message);
        console.warn('👉 Operating in High-Performance Local Data Mode. Ensure MySQL is running on localhost:3306 for full DB persistence.');
        isMySqlConnected = false;
    }
}

initDatabase();

// ------------------------------------------------------------
// API Endpoints
// ------------------------------------------------------------

// Auth API
app.post('/api/login', async (req, res) => {
    const { username, password } = req.body;
    if (isMySqlConnected) {
        try {
            const [rows] = await pool.query('SELECT * FROM users WHERE username = ? AND password = ?', [username, password]);
            if (rows.length > 0) {
                const user = rows[0];
                return res.json({ success: true, message: 'Login successful', role: user.role, fullName: user.full_name });
            }
        } catch (e) { console.error(e); }
    }

    const user = inMemoryData.users.find(u => u.username === username && u.password === password);
    if (user) {
        return res.json({ success: true, message: 'Login successful', role: user.role, fullName: user.full_name });
    }
    return res.status(401).json({ success: false, message: 'Invalid Username or Password' });
});

// Dashboard Stats API
app.get('/api/dashboard/stats', async (req, res) => {
    if (isMySqlConnected) {
        try {
            const [[{ totalProducts }]] = await pool.query('SELECT COUNT(*) as totalProducts FROM products');
            const [[{ totalSuppliers }]] = await pool.query('SELECT COUNT(*) as totalSuppliers FROM suppliers');
            const [[{ totalOrders }]] = await pool.query('SELECT COUNT(*) as totalOrders FROM orders');
            const [[{ activeShipments }]] = await pool.query('SELECT COUNT(*) as activeShipments FROM shipments WHERE status != "Delivered"');
            const [[{ totalValue }]] = await pool.query('SELECT SUM(price * quantity) as totalValue FROM products');
            
            return res.json({
                totalProducts: totalProducts || 0,
                totalSuppliers: totalSuppliers || 0,
                totalOrders: totalOrders || 0,
                activeShipments: activeShipments || 0,
                totalInventoryValue: totalValue || 0.00
            });
        } catch (e) { console.error(e); }
    }

    const totalValue = inMemoryData.products.reduce((acc, p) => acc + (p.price * p.quantity), 0);
    res.json({
        totalProducts: inMemoryData.products.length,
        totalSuppliers: inMemoryData.suppliers.length,
        totalOrders: inMemoryData.orders.length,
        activeShipments: inMemoryData.shipments.filter(s => s.status !== 'Delivered').length,
        totalInventoryValue: totalValue
    });
});

// Products API
app.get('/api/products', async (req, res) => {
    if (isMySqlConnected) {
        try {
            const [rows] = await pool.query('SELECT p.*, s.supplier_name FROM products p LEFT JOIN suppliers s ON p.supplier_id = s.id ORDER BY p.id DESC');
            return res.json(rows);
        } catch (e) { console.error(e); }
    }
    res.json(inMemoryData.products);
});

app.post('/api/products', async (req, res) => {
    const { productName, category, quantity, price, minStockLevel, supplierId } = req.body;
    if (isMySqlConnected) {
        try {
            const [result] = await pool.query(
                'INSERT INTO products (product_name, category, quantity, price, min_stock_level, supplier_id) VALUES (?, ?, ?, ?, ?, ?)',
                [productName, category || 'General', quantity || 0, price || 0, minStockLevel || 10, supplierId || null]
            );
            return res.json({ success: true, id: result.insertId, message: 'Product added successfully to MySQL' });
        } catch (e) { console.error(e); }
    }

    const supplier = inMemoryData.suppliers.find(s => s.id == supplierId);
    const newProduct = {
        id: inMemoryData.products.length + 1,
        product_name: productName,
        category: category || 'General',
        quantity: parseInt(quantity) || 0,
        price: parseFloat(price) || 0.0,
        min_stock_level: parseInt(minStockLevel) || 10,
        supplier_id: supplierId ? parseInt(supplierId) : null,
        supplier_name: supplier ? supplier.supplier_name : 'N/A'
    };
    inMemoryData.products.unshift(newProduct);
    res.json({ success: true, id: newProduct.id, message: 'Product added successfully' });
});

app.delete('/api/products/:id', async (req, res) => {
    const id = parseInt(req.params.id);
    if (isMySqlConnected) {
        try {
            await pool.query('DELETE FROM products WHERE id = ?', [id]);
            return res.json({ success: true, message: 'Product deleted from MySQL' });
        } catch (e) { console.error(e); }
    }

    inMemoryData.products = inMemoryData.products.filter(p => p.id !== id);
    res.json({ success: true, message: 'Product deleted' });
});

// Suppliers API
app.get('/api/suppliers', async (req, res) => {
    if (isMySqlConnected) {
        try {
            const [rows] = await pool.query('SELECT * FROM suppliers ORDER BY id DESC');
            return res.json(rows);
        } catch (e) { console.error(e); }
    }
    res.json(inMemoryData.suppliers);
});

app.post('/api/suppliers', async (req, res) => {
    const { supplier_name, contact_person, email, phone, address, rating } = req.body;
    if (isMySqlConnected) {
        try {
            const [result] = await pool.query(
                'INSERT INTO suppliers (supplier_name, contact_person, email, phone, address, rating) VALUES (?, ?, ?, ?, ?, ?)',
                [supplier_name, contact_person, email, phone, address, rating || 5.0]
            );
            return res.json({ success: true, id: result.insertId, message: 'Supplier added to MySQL' });
        } catch (e) { console.error(e); }
    }

    const newSupplier = {
        id: inMemoryData.suppliers.length + 1,
        supplier_name, contact_person, email, phone, address, rating: parseFloat(rating) || 5.0
    };
    inMemoryData.suppliers.unshift(newSupplier);
    res.json({ success: true, id: newSupplier.id, message: 'Supplier added successfully' });
});

// Orders API
app.get('/api/orders', async (req, res) => {
    if (isMySqlConnected) {
        try {
            const [rows] = await pool.query('SELECT * FROM orders ORDER BY id DESC');
            return res.json(rows);
        } catch (e) { console.error(e); }
    }
    res.json(inMemoryData.orders);
});

app.post('/api/orders', async (req, res) => {
    const { customer_name, customer_email, shipping_address, total_amount, status } = req.body;
    const order_number = 'ORD-2026-' + Math.floor(1000 + Math.random() * 9000);
    
    if (isMySqlConnected) {
        try {
            const [result] = await pool.query(
                'INSERT INTO orders (order_number, customer_name, customer_email, shipping_address, status, total_amount) VALUES (?, ?, ?, ?, ?, ?)',
                [order_number, customer_name, customer_email, shipping_address, status || 'Pending', total_amount || 0.0]
            );
            return res.json({ success: true, id: result.insertId, order_number, message: 'Order created in MySQL' });
        } catch (e) { console.error(e); }
    }

    const newOrder = {
        id: inMemoryData.orders.length + 1,
        order_number, customer_name, customer_email, shipping_address,
        order_date: new Date().toISOString().split('T')[0],
        status: status || 'Pending',
        total_amount: parseFloat(total_amount) || 0.0
    };
    inMemoryData.orders.unshift(newOrder);
    res.json({ success: true, id: newOrder.id, order_number, message: 'Order created successfully' });
});

// Shipments API
app.get('/api/shipments', async (req, res) => {
    if (isMySqlConnected) {
        try {
            const [rows] = await pool.query('SELECT s.*, o.order_number FROM shipments s LEFT JOIN orders o ON s.order_id = o.id ORDER BY s.id DESC');
            return res.json(rows);
        } catch (e) { console.error(e); }
    }
    res.json(inMemoryData.shipments);
});

app.post('/api/shipments', async (req, res) => {
    const { order_id, carrier, origin, destination, current_location, estimated_delivery } = req.body;
    const tracking_number = 'TRK-' + Math.floor(10000 + Math.random() * 90000) + '-US';
    
    if (isMySqlConnected) {
        try {
            const [result] = await pool.query(
                'INSERT INTO shipments (tracking_number, order_id, carrier, origin, destination, current_location, status, estimated_delivery) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
                [tracking_number, order_id, carrier, origin, destination, current_location || origin, 'In Transit', estimated_delivery]
            );
            return res.json({ success: true, id: result.insertId, tracking_number, message: 'Shipment created in MySQL' });
        } catch (e) { console.error(e); }
    }

    const order = inMemoryData.orders.find(o => o.id == order_id);
    const newShipment = {
        id: inMemoryData.shipments.length + 1,
        tracking_number,
        order_id: parseInt(order_id),
        order_number: order ? order.order_number : 'ORD-2026-9999',
        carrier, origin, destination,
        current_location: current_location || origin,
        status: 'In Transit',
        estimated_delivery: estimated_delivery || '2026-08-01'
    };
    inMemoryData.shipments.unshift(newShipment);
    res.json({ success: true, id: newShipment.id, tracking_number, message: 'Shipment dispatched' });
});

// Start Server
app.listen(PORT, () => {
    console.log(`🚀 Supply Chain Backend Server running on http://localhost:${PORT}`);
});
