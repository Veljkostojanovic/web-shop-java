import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CreditCard, CheckCircle } from 'lucide-react';
import { useCart } from '../../../context/CartContext';

export const CheckoutPage = () => {
    const { totalAmount, clearCart } = useCart();
    const navigate = useNavigate();
    const [isProcessing, setIsProcessing] = useState(false);
    const [isSuccess, setIsSuccess] = useState(false);

    const handlePayment = (e: React.FormEvent) => {
        e.preventDefault();
        setIsProcessing(true);

        // to do
        setTimeout(() => {
            setIsProcessing(false);
            setIsSuccess(true);
            clearCart();
        }, 2000);
    };

    if (isSuccess) {
        return (
            <div className="max-w-md mx-auto mt-20 text-center">
                <CheckCircle size={64} className="text-green-500 mx-auto mb-6" />
                <h1 className="text-3xl font-bold mb-4">Uspešna Kupovina!</h1>
                <p className="text-gray-600 mb-8">Hvala vam na poverenju. Vaša porudžbina je primljena.</p>
                <button onClick={() => navigate('/')} className="bg-black text-white px-8 py-3 rounded-lg font-medium hover:bg-gray-800">
                    Nazad na početnu
                </button>
            </div>
        );
    }

    return (
        <div className="max-w-xl mx-auto">
            <h1 className="text-3xl font-extrabold text-gray-900 mb-8">Plaćanje</h1>

            <div className="bg-gray-50 p-6 rounded-xl border border-gray-200 mb-8">
                <div className="flex justify-between text-lg font-bold text-gray-900">
                    <span>Za naplatu:</span>
                    <span>{totalAmount.toFixed(2)} RSD</span>
                </div>
            </div>

            <form onSubmit={handlePayment} className="space-y-6 bg-white p-8 border rounded-xl shadow-sm">
                <h2 className="text-xl font-semibold flex items-center gap-2 mb-4">
                    <CreditCard size={24} className="text-gray-400" /> Podaci o kartici
                </h2>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Broj kartice</label>
                    <input type="text" placeholder="0000 0000 0000 0000" required className="w-full border-gray-300 rounded-lg p-3 border outline-none focus:border-black" />
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Ističe (MM/GG)</label>
                        <input type="text" placeholder="12/25" required className="w-full border-gray-300 rounded-lg p-3 border outline-none focus:border-black" />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">CVC</label>
                        <input type="text" placeholder="123" required className="w-full border-gray-300 rounded-lg p-3 border outline-none focus:border-black" />
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Ime na kartici</label>
                    <input type="text" placeholder="Petar Petrovic" required className="w-full border-gray-300 rounded-lg p-3 border outline-none focus:border-black" />
                </div>

                <button
                    disabled={isProcessing}
                    className="w-full bg-black text-white py-4 rounded-xl font-bold hover:bg-gray-800 transition-all disabled:opacity-50 mt-4"
                >
                    {isProcessing ? 'Procesiranje...' : `Plati ${totalAmount.toFixed(2)} RSD`}
                </button>
            </form>
        </div>
    );
};