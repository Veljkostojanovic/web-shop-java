import { Trash2, Plus, Minus } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../../../context/CartContext';

export const CartPage = () => {
    const { cartItems, updateQuantity, removeFromCart, totalAmount } = useCart();
    const navigate = useNavigate();

    if (cartItems.length === 0) {
        return (
            <div className="text-center py-20">
                <h2 className="text-2xl font-bold mb-4">Vaša korpa je prazna</h2>
                <Link to="/" className="text-blue-600 font-medium hover:underline">Nastavite sa kupovinom</Link>
            </div>
        );
    }

    return (
        <div className="max-w-5xl mx-auto">
            <h1 className="text-3xl font-extrabold text-gray-900 mb-10">Vaša Korpa</h1>
            <div className="flex flex-col lg:flex-row gap-12">
                <div className="flex-1">
                    <div className="space-y-6">
                        {cartItems.map((item) => (
                            <div key={item.id} className="grid grid-cols-1 sm:grid-cols-12 items-center gap-4 py-4 border-b border-gray-100">
                                <div className="col-span-1 sm:col-span-6 flex items-center gap-4">
                                    <div className="w-20 h-20 bg-gray-100 rounded-lg flex-shrink-0 border"></div>
                                    <div>
                                        <h3 className="font-semibold text-gray-900">{item.name}</h3>
                                        <p className="text-sm text-gray-500">{item.price} RSD</p>
                                    </div>
                                </div>

                                <div className="col-span-1 sm:col-span-3 flex justify-center items-center gap-3">
                                    <button onClick={() => updateQuantity(item.id, -1)} className="p-1 border rounded hover:bg-gray-50"><Minus size={16}/></button>
                                    <span className="w-8 text-center font-medium">{item.quantity}</span>
                                    <button onClick={() => updateQuantity(item.id, 1)} className="p-1 border rounded hover:bg-gray-50"><Plus size={16}/></button>
                                </div>

                                <div className="col-span-1 sm:col-span-3 flex justify-between sm:justify-end items-center gap-4">
                                    <span className="font-bold text-gray-900">{(item.price * item.quantity).toFixed(2)} RSD</span>
                                    <button onClick={() => removeFromCart(item.id)} className="text-red-400 hover:text-red-600 transition-colors">
                                        <Trash2 size={20} />
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="w-full lg:w-80 bg-gray-50 p-6 rounded-2xl border border-gray-100 h-fit">
                    <h2 className="text-lg font-bold mb-6">Pregled porudžbine</h2>
                    <div className="flex justify-between mb-8 text-xl font-extrabold text-gray-900">
                        <span>Ukupno</span>
                        <span>{totalAmount.toFixed(2)} RSD</span>
                    </div>
                    <button
                        onClick={() => navigate('/checkout')}
                        className="w-full bg-black text-white py-4 rounded-xl font-bold hover:bg-gray-800 transition-all"
                    >
                        Nastavi na plaćanje
                    </button>
                </div>
            </div>
        </div>
    );
};