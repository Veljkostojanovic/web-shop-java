import { LogOut, ShoppingCart, User, Search } from 'lucide-react';
import { Routes, Route, Link } from 'react-router-dom';
import { ProductList } from './features/products/components/ProductList';
import { LoginPage } from './features/auth/components/LoginPage';
import { RegisterPage } from './features/auth/components/RegisterPage';
import { ProductDetailPage } from './features/products/components/ProductDetailPage';
import { CartPage } from './features/cart/components/CartPage';
import { CheckoutPage } from './features/checkout/components/CheckoutPage';
import { AdminPage } from './features/admin/components/AdminPage';
import { useCart } from './context/CartContext';

function App() {
    const { totalItems } = useCart();
    const isLoggedIn = !!localStorage.getItem('token');

    const handleLogout = () => {
        localStorage.removeItem('token');
        window.location.reload();
    };

    return (
        <div className="min-h-screen bg-white text-gray-900 font-sans flex flex-col">
            {/* Top Utility Bar */}
            <div className="bg-[#111] text-white text-[10px] sm:text-xs py-2 px-6 flex justify-between items-center">
                <div className="hover:underline cursor-pointer tracking-wider">Kvantum Plus kartica</div>
                <div className="flex gap-4 items-center">
                    <span className="flex items-center gap-2"><img src="https://flagcdn.com/w20/rs.png" alt="SRB" className="w-4" /> SRB</span>
                    {isLoggedIn ? (
                        <div className="flex items-center gap-3">
                            <Link to="/admin" className="hover:text-gray-300 transition-colors">Admin</Link>
                            <button onClick={handleLogout} className="p-1 bg-red-500/20 text-red-400 rounded-full hover:bg-red-500/40 transition-all">
                                <LogOut size={16} />
                            </button>
                        </div>
                    ) : (
                        <div className="flex gap-4 font-medium">
                            <Link to="/register" className="hover:underline">Registrujte se</Link>
                            <Link to="/login" className="hover:underline">Prijavite se</Link>
                        </div>
                    )}
                </div>
            </div>

            {/* Sticky Navigation */}
            <nav className="bg-[#1a1a1a] text-white px-6 py-4 flex justify-between items-center sticky top-0 z-50 shadow-md">
                <Link to="/" className="text-2xl font-black tracking-tighter">WEBSHOP</Link>

                <div className="flex items-center gap-4 sm:gap-6">
                    <Link to="/cart" className="relative p-2 hover:bg-gray-800 rounded-full transition-all group">
                        <ShoppingCart size={24} />
                        {totalItems > 0 && (
                            <span className="absolute -top-1 -right-1 bg-red-600 text-white text-[10px] font-bold w-5 h-5 flex items-center justify-center rounded-full border-2 border-[#1a1a1a] animate-in zoom-in duration-300">
                                {totalItems}
                            </span>
                        )}
                    </Link>
                </div>
            </nav>

            <div className="bg-[#2a2a2a] text-white text-xs sm:text-sm font-medium text-center py-2.5">
                Registrujte se i ostvarite dodatnih 25% popusta na prvu kupovinu
            </div>

            <main className="max-w-[1600px] w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 flex-grow">
                <Routes>
                    <Route path="/" element={<ProductList />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/product/:id" element={<ProductDetailPage />} />
                    <Route path="/cart" element={<CartPage />} />
                    <Route path="/checkout" element={<CheckoutPage />} />
                    <Route path="/admin" element={<AdminPage />} />
                </Routes>
            </main>
        </div>
    );
}

export default App;