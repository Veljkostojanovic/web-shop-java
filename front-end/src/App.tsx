import { Search, Heart, ShoppingBag } from 'lucide-react';
import { LogOut, UserPlus, LogIn } from 'lucide-react';
import { Routes, Route, Link } from 'react-router-dom';
import { ProductList } from './features/products/components/ProductList';
import { LoginPage } from './features/auth/components/LoginPage';
import { RegisterPage } from './features/auth/components/RegisterPage';

function App() {
    const isLoggedIn = !!localStorage.getItem('token');

    const handleLogout = () => {
        localStorage.removeItem('token'); // Clear the token
        window.location.reload();
    };

    return (
        <div className="min-h-screen bg-white text-gray-900 font-sans">
            <div className="bg-[#111] text-white text-[10px] sm:text-xs py-2 px-6 flex justify-between items-center">
                <div className="hover:underline cursor-pointer tracking-wider">Kvantum Plus kartica</div>
                <div className="flex gap-4">
                    <span className="flex items-center gap-2"><img src="https://flagcdn.com/w20/rs.png" alt="SRB" className="w-4" /> SRB</span>

                    {isLoggedIn ? (
                        <button
                            onClick={handleLogout}
                            className="p-2 rounded-full bg-red-100 text-red-600 hover:bg-red-200 transition-all"
                        >
                            <LogOut size={22} />
                        </button>
                    ) : (
                        <div className="flex gap-4 text-sm font-medium">
                            <Link to="/register" className="hover:underline">
                                Registrujte se
                            </Link>
                            <Link to="/login" className="hover:underline">
                                Prijavite se
                            </Link>
                        </div>
                    )}

                </div>
            </div>

            <nav className="bg-[#1a1a1a] text-white px-6 py-4 flex justify-between items-center sticky top-0 z-50">
                {/* Make the Logo a Link back to Home */}
                <Link to="/" className="text-2xl font-extrabold tracking-tighter cursor-pointer">
                    WEBSHOP
                </Link>

            </nav>

            <div className="bg-[#2a2a2a] text-white text-xs sm:text-sm font-medium text-center py-2.5">
                Registrujte se i ostvarite dodatnih 25% popusta na prvu kupovinu
            </div>

            <main className="max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8 py-8">

                <Routes>
                    <Route path="/" element={<ProductList />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                </Routes>
            </main>
        </div>
    );
}

export default App;