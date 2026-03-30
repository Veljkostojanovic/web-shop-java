import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ShoppingCart, ArrowLeft, Loader2 } from 'lucide-react';
import { Product } from '../../../types/product';
import { getProductById } from '../api/getProducts';
import { useCart } from '../../../context/CartContext';

export const ProductDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const { addToCart } = useCart();

    const [product, setProduct] = useState<Product | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    useEffect(() => {
        if (!id) return;

        setLoading(true);
        getProductById(Number(id))
            .then((data) => {
                setProduct(data);
                setError(false);
            })
            .catch((err) => {
                console.error("Greška pri učitavanju proizvoda:", err);
                setError(true);
            })
            .finally(() => setLoading(false));
    }, [id]);

    if (loading) {
        return (
            <div className="flex flex-col items-center justify-center min-h-[400px]">
                <Loader2 className="animate-spin text-gray-400 mb-4" size={40} />
                <p className="text-gray-500 font-medium">Učitavanje proizvoda...</p>
            </div>
        );
    }

    if (error || !product) {
        return (
            <div className="text-center py-20">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">Proizvod nije pronađen</h2>
                <button onClick={() => navigate('/')} className="text-blue-600 hover:underline">
                    Nazad na prodavnicu
                </button>
            </div>
        );
    }

    return (
        <div className="max-w-6xl mx-auto animate-in fade-in duration-500">
            <button
                onClick={() => navigate(-1)}
                className="flex items-center gap-2 text-sm text-gray-500 hover:text-black mb-8 transition-colors group"
            >
                <ArrowLeft size={16} className="group-hover:-translate-x-1 transition-transform" />
                Nazad na proizvode
            </button>

            <div className="flex flex-col md:flex-row gap-12 lg:gap-20">
                <div className="w-full md:w-1/2">
                    <div className="aspect-[4/5] bg-gray-50 rounded-3xl overflow-hidden border border-gray-100 shadow-inner">
                        {/*{product.imageUrl ? (
                            <img
                                src={product.imageUrl}
                                alt={product.name}
                                className="w-full h-full object-cover"
                            />
                        ) : (
                            <div className="w-full h-full flex items-center justify-center text-gray-400 italic">
                                Slika nije dostupna
                        )}*/}
                            <div className="w-full h-full flex items-center justify-center text-gray-400 italic">
                                Slika nije dostupna
                            </div>
                    </div>
                </div>

                <div className="w-full md:w-1/2 flex flex-col justify-center">
                    <div className="mb-6">
                        <span className="text-xs font-black text-blue-600 uppercase tracking-[0.2em] mb-2 block">
                            {product.categoryName || 'Kategorija'}
                        </span>
                        <h1 className="text-4xl lg:text-5xl font-black text-gray-900 mb-4 leading-tight">
                            {product.name}
                        </h1>
                        <p className="text-3xl font-bold text-gray-900">
                            {product.price.toLocaleString()} RSD
                        </p>
                    </div>

                    <div className="prose prose-sm text-gray-600 mb-10 border-t pt-6">
                        <h3 className="text-sm font-bold text-gray-900 uppercase mb-2">Opis proizvoda</h3>
                        <p className="leading-relaxed">
                            {product.description || "Nema dostupnog opisa za ovaj proizvod."}
                        </p>
                    </div>

                    <div className="space-y-4">
                        <button
                            onClick={() => addToCart(product)}
                            className="w-full bg-black text-white py-5 rounded-2xl font-bold flex items-center justify-center gap-3 hover:bg-gray-800 transition-all shadow-lg active:scale-95"
                        >
                            <ShoppingCart size={20} /> Dodaj u korpu
                        </button>

                        <div className="flex items-center justify-center gap-8 py-4 border-t border-gray-100 text-[10px] text-gray-400 font-bold uppercase tracking-widest">
                            <span>Besplatna dostava</span>
                            <span>•</span>
                            <span>Povrat 14 dana</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};