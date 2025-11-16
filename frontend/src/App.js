import React, { useState, useEffect } from 'react';
import { ShoppingCart, Search, User, Package, TrendingUp, LogOut, Home } from 'lucide-react';

const API_BASE_URL = 'http://localhost:8080/api';

// Main App Component
export default function RetailApp() {
  const [currentView, setCurrentView] = useState('home');
  const [currentCustomer, setCurrentCustomer] = useState(null);
  const [cart, setCart] = useState([]);
  const [products, setProducts] = useState(mockProducts);
  const [searchQuery, setSearchQuery] = useState('');

  // Fetch products on mount
  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/products`);
      const data = await response.json();
      setProducts(data);
    } catch (error) {
      console.error('Error fetching products:', error);
      // Use mock data if API fails
      setProducts(mockProducts);
    }
  };

  const searchProducts = async (query) => {
    if (!query.trim()) {
      fetchProducts();
      return;
    }
    
    try {
      const response = await fetch(`${API_BASE_URL}/products/search?query=${encodeURIComponent(query)}`);
      const data = await response.json();
      setProducts(data);
      
      // Log search event
      if (currentCustomer) {
        logEvent({
          customerId: currentCustomer.customerId,
          eventData: JSON.stringify({
            event_type: 'search',
            query: query,
            results_count: data.length
          })
        });
      }
    } catch (error) {
      console.error('Error searching products:', error);
    }
  };

  const logEvent = async (eventData) => {
    try {
      await fetch(`${API_BASE_URL}/events`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(eventData)
      });
    } catch (error) {
      console.error('Error logging event:', error);
    }
  };

  const addToCart = (product) => {
    const existingItem = cart.find(item => item.productId === product.productId);
    if (existingItem) {
      setCart(cart.map(item =>
        item.productId === product.productId
          ? { ...item, quantity: item.quantity + 1 }
          : item
      ));
    } else {
      setCart([...cart, { ...product, quantity: 1 }]);
    }

    // Log add to cart event
    if (currentCustomer) {
      logEvent({
        customerId: currentCustomer.customerId,
        eventData: JSON.stringify({
          event_type: 'add_to_cart',
          product_id: product.productId,
          product_name: product.productName,
          quantity: 1,
          price: product.unitPrice
        })
      });
    }
  };

  const removeFromCart = (productId) => {
    setCart(cart.filter(item => item.productId !== productId));
  };

  const updateCartQuantity = (productId, newQuantity) => {
    if (newQuantity <= 0) {
      removeFromCart(productId);
    } else {
      setCart(cart.map(item =>
        item.productId === productId
          ? { ...item, quantity: newQuantity }
          : item
      ));
    }
  };

  const calculateTotal = () => {
    return cart.reduce((sum, item) => sum + (item.unitPrice * item.quantity), 0).toFixed(2);
  };

  const handleCheckout = async () => {
    if (!currentCustomer) {
      alert('Please sign in to checkout');
      setCurrentView('signin');
      return;
    }

    if (cart.length === 0) {
      alert('Your cart is empty');
      return;
    }

    const order = {
      customerId: currentCustomer.customerId,
      orderStatus: 'Processing',
      totalAmount: parseFloat(calculateTotal()),
      paymentMethod: 'Credit Card',
      shippingAddress: currentCustomer.address || '123 Main St',
      items: cart.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        discountPercent: 0
      }))
    };

    try {
      const response = await fetch(`${API_BASE_URL}/checkout`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(order)
      });

      if (response.ok) {
        const orderData = await response.json();
        
        // Log purchase event
        logEvent({
          customerId: currentCustomer.customerId,
          eventData: JSON.stringify({
            event_type: 'purchase',
            order_id: orderData.orderId,
            total: orderData.totalAmount,
            items: cart.length,
            payment_method: 'Credit Card'
          })
        });

        // Fetch updated customer to get new Lifetime Value
  try {
    const customerResp = await fetch(`${API_BASE_URL}/customers/${currentCustomer.customerId}`);
    if (customerResp.ok) {
      const updatedCustomer = await customerResp.json();
      setCurrentCustomer(updatedCustomer); // <-- Update parent/customer state
    }
  } catch (err) {
    console.error('Failed to refresh customer:', err);
  }
  
        alert(`Order placed successfully! Order ID: ${orderData.orderId}`);
        setCart([]);
        setCurrentView('orders');
      }
    } catch (error) {
      console.error('Checkout error:', error);
      alert('Checkout failed. Please try again.');
    }
  };

  const handleSignIn = async (customerData) => {
    try {
      const response = await fetch(`${API_BASE_URL}/customers`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(customerData)
      });

      if (response.ok) {
        const customer = await response.json();
        setCurrentCustomer(customer);
        setCurrentView('home');
        
        // Log page view event
        logEvent({
          customerId: customer.customerId,
          eventData: JSON.stringify({
            event_type: 'page_view',
            page: 'home',
            session_duration: 0
          })
        });
      }
    } catch (error) {
      console.error('Sign in error:', error);
      alert('Sign in failed. Please try again.');
    }
  };

  const handleSignOut = () => {
    setCurrentCustomer(null);
    setCart([]);
    setCurrentView('home');
  };

  // Render different views
  const renderView = () => {
    switch (currentView) {
      case 'home':
        return <HomeView products={products} addToCart={addToCart} currentCustomer={currentCustomer} logEvent={logEvent} />;
      case 'cart':
        return <CartView cart={cart} updateQuantity={updateCartQuantity} removeFromCart={removeFromCart} calculateTotal={calculateTotal} handleCheckout={handleCheckout} />;
      case 'signin':
        return <SignInView onSignIn={handleSignIn} />;
      case 'orders':
        return <OrdersView currentCustomer={currentCustomer} />;
      default:
        return <HomeView products={products} addToCart={addToCart} currentCustomer={currentCustomer} logEvent={logEvent} />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-8">
              <h1 className="text-2xl font-bold text-blue-600 cursor-pointer" onClick={() => setCurrentView('home')}>
                RetailCo
              </h1>
              <nav className="hidden md:flex space-x-6">
                <button onClick={() => setCurrentView('home')} className="flex items-center text-gray-700 hover:text-blue-600">
                  <Home className="w-4 h-4 mr-1" />
                  Home
                </button>
                <button onClick={() => setCurrentView('orders')} className="flex items-center text-gray-700 hover:text-blue-600">
                  <Package className="w-4 h-4 mr-1" />
                  Orders
                </button>
              </nav>
            </div>

            {/* Search Bar */}
            <div className="flex-1 max-w-md mx-4">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                <input
                  type="text"
                  placeholder="Search products..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && searchProducts(searchQuery)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>

            <div className="flex items-center space-x-4">
              {/* Cart */}
              <button
                onClick={() => setCurrentView('cart')}
                className="relative p-2 text-gray-700 hover:text-blue-600"
              >
                <ShoppingCart className="w-6 h-6" />
                {cart.length > 0 && (
                  <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                    {cart.length}
                  </span>
                )}
              </button>

              {/* User Account */}
              {currentCustomer ? (
                <div className="flex items-center space-x-2">
                  <span className="text-sm text-gray-700">
                    Hi, {currentCustomer.firstName}
                  </span>
                  <button
                    onClick={handleSignOut}
                    className="p-2 text-gray-700 hover:text-red-600"
                  >
                    <LogOut className="w-5 h-5" />
                  </button>
                </div>
              ) : (
                <button
                  onClick={() => setCurrentView('signin')}
                  className="flex items-center space-x-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  <User className="w-5 h-5" />
                  <span>Sign In</span>
                </button>
              )}
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 py-8">
        {renderView()}
      </main>
    </div>
  );
}

// Home View - Product Listing
function HomeView({ products, addToCart, currentCustomer, logEvent }) {
  const handleProductClick = (product) => {
    if (currentCustomer) {
      logEvent({
        customerId: currentCustomer.customerId,
        eventData: JSON.stringify({
          event_type: 'page_view',
          page: 'product_details',
          product_id: product.productId,
          product_name: product.productName
        })
      });
    }
  };

  return (
    <div>
      <div className="mb-8">
        <h2 className="text-3xl font-bold text-gray-900 mb-2">Featured Products</h2>
        <p className="text-gray-600">Discover our latest collection</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {products.map((product) => (
          <div
            key={product.productId}
            className="bg-white rounded-lg shadow-md hover:shadow-xl transition-shadow duration-300 overflow-hidden cursor-pointer"
            onClick={() => handleProductClick(product)}
          >
            <div className="h-48 bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center">
              <Package className="w-20 h-20 text-white opacity-50" />
            </div>
            <div className="p-4">
              <div className="mb-2">
                <span className="text-xs text-blue-600 font-semibold">{product.category}</span>
              </div>
              <h3 className="text-lg font-semibold text-gray-900 mb-2">
                {product.productName}
              </h3>
              <p className="text-sm text-gray-600 mb-3">{product.brand}</p>
              <div className="flex items-center justify-between">
                <div>
                  <span className="text-2xl font-bold text-gray-900">
                    ${product.unitPrice?.toFixed(2)}
                  </span>
                  {product.stockQuantity > 0 ? (
                    <p className="text-xs text-green-600 mt-1">In Stock ({product.stockQuantity})</p>
                  ) : (
                    <p className="text-xs text-red-600 mt-1">Out of Stock</p>
                  )}
                </div>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    addToCart(product);
                  }}
                  disabled={product.stockQuantity === 0}
                  className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
                >
                  Add
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// Cart View
function CartView({ cart, updateQuantity, removeFromCart, calculateTotal, handleCheckout }) {
  if (cart.length === 0) {
    return (
      <div className="text-center py-16">
        <ShoppingCart className="w-24 h-24 text-gray-300 mx-auto mb-4" />
        <h2 className="text-2xl font-semibold text-gray-900 mb-2">Your cart is empty</h2>
        <p className="text-gray-600">Add some products to get started!</p>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto">
      <h2 className="text-3xl font-bold text-gray-900 mb-8">Shopping Cart</h2>

      <div className="bg-white rounded-lg shadow-md p-6 mb-6">
        {cart.map((item) => (
          <div key={item.productId} className="flex items-center justify-between py-4 border-b last:border-b-0">
            <div className="flex items-center space-x-4 flex-1">
              <div className="w-20 h-20 bg-gradient-to-br from-blue-400 to-purple-500 rounded-lg flex items-center justify-center">
                <Package className="w-10 h-10 text-white opacity-50" />
              </div>
              <div className="flex-1">
                <h3 className="font-semibold text-gray-900">{item.productName}</h3>
                <p className="text-sm text-gray-600">{item.category}</p>
                <p className="text-lg font-semibold text-blue-600 mt-1">
                  ${item.unitPrice?.toFixed(2)}
                </p>
              </div>
            </div>

            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <button
                  onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                  className="w-8 h-8 bg-gray-200 rounded-lg hover:bg-gray-300 flex items-center justify-center text-lg font-bold"
                >
                  -
                </button>
                <span className="w-12 text-center font-semibold">{item.quantity}</span>
                <button
                  onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                  className="w-8 h-8 bg-gray-200 rounded-lg hover:bg-gray-300 flex items-center justify-center text-lg font-bold"
                >
                  +
                </button>
              </div>

              <div className="w-24 text-right">
                <p className="font-semibold text-gray-900">
                  ${(item.unitPrice * item.quantity).toFixed(2)}
                </p>
              </div>

              <button
                onClick={() => removeFromCart(item.productId)}
                className="text-red-600 hover:text-red-700 ml-4 px-3 py-1 rounded hover:bg-red-50"
              >
                Remove
              </button>
            </div>
          </div>
        ))}
      </div>

      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex justify-between items-center mb-4">
          <span className="text-lg text-gray-600">Subtotal:</span>
          <span className="text-2xl font-bold text-gray-900">${calculateTotal()}</span>
        </div>
        <button
          onClick={handleCheckout}
          className="w-full py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-semibold text-lg transition-colors"
        >
          Proceed to Checkout
        </button>
      </div>
    </div>
  );
}

// Sign In View
function SignInView({ onSignIn }) {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    address: '',
    city: '',
    state: '',
    zipCode: '',
    country: 'USA',
    registrationDate: new Date().toISOString().split('T')[0],
    customerSegment: 'Standard',
    lifetimeValue: 0
  });

  const handleSubmit = () => {
    if (!formData.firstName || !formData.lastName || !formData.email) {
      alert('Please fill in required fields');
      return;
    }
    onSignIn(formData);
  };

  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-white rounded-lg shadow-md p-8">
        <h2 className="text-3xl font-bold text-gray-900 mb-6">Create Account</h2>
        <div className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">First Name *</label>
              <input
                type="text"
                value={formData.firstName}
                onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Last Name *</label>
              <input
                type="text"
                value={formData.lastName}
                onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email *</label>
            <input
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Phone</label>
            <input
              type="tel"
              value={formData.phone}
              onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
            <input
              type="text"
              value={formData.address}
              onChange={(e) => setFormData({ ...formData, address: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">City</label>
              <input
                type="text"
                value={formData.city}
                onChange={(e) => setFormData({ ...formData, city: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">State</label>
              <input
                type="text"
                value={formData.state}
                onChange={(e) => setFormData({ ...formData, state: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Zip Code</label>
              <input
                type="text"
                value={formData.zipCode}
                onChange={(e) => setFormData({ ...formData, zipCode: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <button
            onClick={handleSubmit}
            className="w-full py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-semibold text-lg transition-colors mt-6"
          >
            Create Account & Continue
          </button>
        </div>
      </div>
    </div>
  );
}

// Orders View
function OrdersView({ currentCustomer }) {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (currentCustomer) {
      fetchCustomerOrders();
    }
  }, [currentCustomer]);

  const fetchCustomerOrders = async () => {
    setLoading(true);
  try {
    const response = await fetch(`http://localhost:8080/api/orders/${currentCustomer.customerId}`);
    if (!response.ok) throw new Error('Failed to fetch orders');
    const data = await response.json();
    setOrders(data);
    } catch (error) {
      console.error('Error fetching orders:', error);
    } finally {
      setLoading(false);
    }
  };

  if (!currentCustomer) {
    return (
      <div className="text-center py-16">
        <User className="w-24 h-24 text-gray-300 mx-auto mb-4" />
        <h2 className="text-2xl font-semibold text-gray-900 mb-2">Sign in to view orders</h2>
        <p className="text-gray-600">Please sign in to see your order history</p>
      </div>
    );
  }

  return (
    <div>
      <h2 className="text-3xl font-bold text-gray-900 mb-8">My Account</h2>
      
      {/* Customer Profile Card */}
      <div className="bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg shadow-lg p-6 mb-6 text-white">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-2xl font-bold mb-2">
              {currentCustomer.firstName} {currentCustomer.lastName}
            </h3>
            <p className="text-blue-100 mb-1">{currentCustomer.email}</p>
            {currentCustomer.phone && (
              <p className="text-blue-100">{currentCustomer.phone}</p>
            )}
          </div>
          <div className="text-right">
            <div className="bg-white bg-opacity-20 rounded-lg px-4 py-2 mb-2">
              <p className="text-xs uppercase tracking-wide">Segment</p>
              <p className="text-xl font-bold">{currentCustomer.customerSegment || 'Standard'}</p>
            </div>
            <div className="bg-white bg-opacity-20 rounded-lg px-4 py-2">
              <p className="text-xs uppercase tracking-wide">Lifetime Value</p>
              <p className="text-xl font-bold">${(currentCustomer.lifetimeValue || 0).toFixed(2)}</p>
            </div>
          </div>
        </div>
        
        {currentCustomer.address && (
          <div className="mt-4 pt-4 border-t border-white border-opacity-30">
            <p className="text-sm text-blue-100">
              {currentCustomer.address}
              {currentCustomer.city && `, ${currentCustomer.city}`}
              {currentCustomer.state && `, ${currentCustomer.state}`}
              {currentCustomer.zipCode && ` ${currentCustomer.zipCode}`}
            </p>
          </div>
        )}
      </div>

      {/* Order History */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
          <Package className="w-5 h-5 mr-2 text-blue-600" />
          Order History
        </h3>
        
        {loading ? (
          <p className="text-gray-600">Loading orders...</p>
        ) : orders.length > 0 ? (
          <div className="space-y-4">
            {orders.map((order) => (
              <div key={order.orderId} className="border border-gray-200 rounded-lg p-4">
                <div className="flex justify-between items-center">
                  <div>
                    <p className="font-semibold">Order #{order.orderId}</p>
                    <p className="text-sm text-gray-600">{order.orderDate}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold text-lg">${order.totalAmount.toFixed(2)}</p>
                    <p className="text-sm text-gray-600">{order.orderStatus}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-8">
            <Package className="w-16 h-16 text-gray-300 mx-auto mb-3" />
            <p className="text-gray-600 mb-2">No orders yet</p>
            <p className="text-sm text-gray-500">Your orders will appear here after checkout</p>
          </div>
        )}
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">
        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600 mb-1">Total Orders</p>
              <p className="text-3xl font-bold text-gray-900">{orders.length}</p>
            </div>
            <TrendingUp className="w-12 h-12 text-blue-600 opacity-20" />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600 mb-1">Member Since</p>
              <p className="text-lg font-semibold text-gray-900">
                {currentCustomer.registrationDate ? 
                  new Date(currentCustomer.registrationDate).toLocaleDateString('en-US', { 
                    month: 'short', 
                    year: 'numeric' 
                  }) : 'Recently'}
              </p>
            </div>
            <User className="w-12 h-12 text-purple-600 opacity-20" />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600 mb-1">Total Spent</p>
              <p className="text-3xl font-bold text-green-600">
                ${(currentCustomer.lifetimeValue || 0).toFixed(2)}
              </p>
            </div>
            <ShoppingCart className="w-12 h-12 text-green-600 opacity-20" />
          </div>
        </div>
      </div>
    </div>
  );
}

// Mock data in case API is not available
const mockProducts = [
  { productId: 1, productName: 'Laptop Pro 15', category: 'Electronics', brand: 'TechBrand', unitPrice: 1299.99, stockQuantity: 50 },
  { productId: 2, productName: 'Wireless Mouse', category: 'Electronics', brand: 'TechBrand', unitPrice: 29.99, stockQuantity: 200 },
  { productId: 3, productName: 'USB-C Cable', category: 'Electronics', brand: 'TechBrand', unitPrice: 19.99, stockQuantity: 500 },
  { productId: 4, productName: 'Office Chair', category: 'Furniture', brand: 'ComfortCo', unitPrice: 299.99, stockQuantity: 30 },
  { productId: 5, productName: 'Standing Desk', category: 'Furniture', brand: 'ComfortCo', unitPrice: 599.99, stockQuantity: 15 },
  { productId: 6, productName: 'Notebook Set', category: 'Stationery', brand: 'WriteCo', unitPrice: 12.99, stockQuantity: 1000 },
  { productId: 7, productName: 'Pen Pack (10)', category: 'Stationery', brand: 'WriteCo', unitPrice: 8.99, stockQuantity: 800 },
  { productId: 8, productName: 'Monitor 27"', category: 'Electronics', brand: 'TechBrand', unitPrice: 349.99, stockQuantity: 40 }
];