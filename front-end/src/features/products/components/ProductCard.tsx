import { Product } from "../../../types/product";
import { ShoppingCart, Eye } from "lucide-react";
import { useCart } from "../../../context/CartContext";
import { Link } from "react-router-dom";

interface Props { product: Product; }

export const ProductCard = ({ product }: Props) => {
    const { addToCart } = useCart();

    return (
        <div className="group flex flex-col border border-gray-100 rounded-2xl overflow-hidden bg-white shadow-sm hover:shadow-xl transition-all duration-500 relative">
            <div className="aspect-[4/5] bg-gray-50 overflow-hidden relative">
                {/*{product.imageUrl ? (
                    <img
                        src={product.imageUrl}
                        alt={product.name}
                        className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
                    />
                ) : (
                    <div className="w-full h-full flex items-center justify-center text-gray-300 italic text-xs">Bez slike</div>
                )}*/}
                    <div className="w-full h-full flex items-center justify-center text-gray-300 italic text-xs">Bez slike</div>

                <div className="absolute inset-0 bg-black/5 opacity-0 group-hover:opacity-100 transition-opacity duration-300" />

                <div className="absolute bottom-4 left-4 right-4 flex gap-2 translate-y-4 opacity-0 group-hover:translate-y-0 group-hover:opacity-100 transition-all duration-300">
                    <button
                        onClick={() => addToCart(product)}
                        className="flex-1 bg-black text-white py-3 rounded-xl font-bold flex items-center justify-center gap-2 text-xs shadow-lg"
                    >
                        <ShoppingCart size={16} /> Kupi
                    </button>
                    <Link
                        to={`/product/${product.id}`}
                        className="bg-white text-black p-3 rounded-xl shadow-lg border border-gray-100 hover:bg-gray-50"
                    >
                        <Eye size={18} />
                    </Link>
                </div>
            </div>

            <div className="p-5 flex flex-col flex-grow bg-white">
                <span className="text-[10px] font-black text-blue-600 uppercase tracking-widest mb-1">{product.categoryName || 'General'}</span>
                <h3 className="font-bold text-gray-900 mb-1 leading-tight line-clamp-1">{product.name}</h3>
                <p className="text-xs text-gray-500 line-clamp-2 mb-4 leading-relaxed">{product.description}</p>
                <div className="mt-auto pt-4 border-t border-gray-50 flex justify-between items-end">
                    <span className="text-lg font-black text-gray-900">{product.price.toLocaleString()} RSD</span>
                </div>
            </div>
        </div>
    );
};