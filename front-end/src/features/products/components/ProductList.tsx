import { useState, useEffect } from 'react';
import { ChevronDown, X } from 'lucide-react';
import { Product } from '../../../types/product';
import { getProducts } from '../api/getProducts';
import { ProductCard } from './ProductCard';

export const ProductList = () => {
    const [products, setProducts] = useState<Product[]>([]);

    useEffect(() => {
        getProducts().then(setProducts).catch(console.error);
    }, []);

    return (
        <div>
            <div className="text-xs text-gray-500 mb-6 flex gap-2">
                <span className="hover:underline cursor-pointer">WebShop Srbija</span> /
                <span className="font-semibold text-gray-900">Proizvodi</span>
            </div>

            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-end mb-8 border-b pb-4">
                <h1 className="text-4xl font-normal text-gray-900">Proizvodi</h1>
                <div className="flex items-center gap-4 text-sm mt-4 sm:mt-0">
                    <span className="text-gray-500 hidden sm:block">( {products.length} proizvoda )</span>
                    <div className="flex items-center gap-2">
                        <span className="text-gray-600">Sortiraj</span>
                        <select className="border border-gray-300 rounded px-3 py-1.5 focus:outline-none focus:border-black">
                            <option>Najnovije</option>
                            <option>Cena: Rastuća</option>
                            <option>Cena: Opadajuća</option>
                        </select>
                    </div>
                </div>
            </div>

            <div className="flex flex-col lg:flex-row gap-10">

                <aside className="w-full lg:w-[280px] flex-shrink-0">

                    <div className="space-y-4 mb-10 text-sm text-gray-700 font-medium">
                        <div className="flex justify-between hover:text-black cursor-pointer"><span>Obuća</span> <span className="text-gray-400">(72)</span></div>
                        <div className="flex justify-between hover:text-black cursor-pointer"><span>Odeća</span> <span className="text-gray-400">(51)</span></div>
                        <div className="flex justify-between hover:text-black cursor-pointer"><span>Oprema</span> <span className="text-gray-400">(14)</span></div>
                    </div>

                    <div className="mb-8">
                        <div className="flex justify-between items-center mb-4 text-sm">
                            <h3 className="font-semibold text-gray-900">Resetujte filtere</h3>
                            <X size={16} className="text-gray-500 cursor-pointer hover:text-black" />
                        </div>
                        <div className="flex flex-wrap gap-2">
                            {['mens', 'unisex', 'trcanje'].map(chip => (
                                <span key={chip} className="border border-gray-300 px-3 py-1 text-xs flex items-center gap-2 rounded-sm cursor-pointer hover:bg-gray-50">
                  {chip} <X size={12} className="text-gray-400"/>
                </span>
                            ))}
                        </div>
                    </div>

                    <div className="border-t border-gray-200">
                        {['Pol', 'Sport', 'Kroj', 'Vrste proizvoda'].map((filter) => (
                            <div key={filter} className="border-b border-gray-200 py-4 flex justify-between items-center cursor-pointer hover:bg-gray-50">
                                <span className="text-sm font-semibold text-gray-900">{filter}</span>
                                <ChevronDown size={18} className="text-gray-500" />
                            </div>
                        ))}
                    </div>
                </aside>

                <div className="flex-1">
                    <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-x-6 gap-y-12">
                        {products.map((product) => (
                            <ProductCard key={product.id} product={product} />
                        ))}
                    </div>
                </div>

            </div>
        </div>
    );
};