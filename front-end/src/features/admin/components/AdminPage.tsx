import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2 } from 'lucide-react';
import { Product } from '../../../types/product';
import { getProducts } from '../../products/api/getProducts';

export const AdminPage = () => {
    const [products, setProducts] = useState<Product[]>([]);

    useEffect(() => {
        getProducts().then(setProducts).catch(console.error);
    }, []);

    return (
        <div className="max-w-7xl mx-auto">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-extrabold text-gray-900">Admin Panel</h1>
                <button className="bg-black text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-gray-800 transition">
                    <Plus size={18} /> Dodaj proizvod
                </button>
            </div>

            <div className="bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead>
                        <tr className="bg-gray-50 border-b border-gray-200 text-sm text-gray-600">
                            <th className="p-4 font-semibold">ID</th>
                            <th className="p-4 font-semibold">Naziv proizvoda</th>
                            <th className="p-4 font-semibold">Kategorija</th>
                            <th className="p-4 font-semibold">Cena</th>
                            <th className="p-4 font-semibold text-right">Akcije</th>
                        </tr>
                        </thead>
                        <tbody>
                        {products.map((product) => (
                            <tr key={product.id} className="border-b border-gray-100 hover:bg-gray-50 transition-colors">
                                <td className="p-4 text-sm text-gray-500">#{product.id}</td>
                                <td className="p-4 font-medium text-gray-900">{product.name}</td>
                                <td className="p-4 text-sm text-gray-600">{product.categoryName || '-'}</td>
                                <td className="p-4 font-bold text-gray-900">{product.price} RSD</td>
                                <td className="p-4 flex justify-end gap-3">
                                    <button className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition">
                                        <Edit size={18} />
                                    </button>
                                    <button className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition">
                                        <Trash2 size={18} />
                                    </button>
                                </td>
                            </tr>
                        ))}
                        {products.length === 0 && (
                            <tr>
                                <td colSpan={5} className="p-8 text-center text-gray-500">Nema pronađenih proizvoda.</td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};