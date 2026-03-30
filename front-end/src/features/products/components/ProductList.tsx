import { useState, useEffect, useMemo } from 'react';
import { X, Search, SlidersHorizontal } from 'lucide-react';
import { Product } from '../../../types/product';
import { getProducts } from '../api/getProducts';
import { ProductCard } from './ProductCard';

export const ProductList = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [sortOption, setSortOption] = useState<string>('newest');
    const [searchQuery, setSearchQuery] = useState("");

    useEffect(() => {
        getProducts().then(setProducts).catch(console.error);
    }, []);

    const filteredAndSorted = useMemo(() => {
        let result = products.filter(p =>
            p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            p.description?.toLowerCase().includes(searchQuery.toLowerCase())
        );

        const sorted = [...result];
        switch (sortOption) {
            case 'price-asc': sorted.sort((a, b) => a.price - b.price); break;
            case 'price-desc': sorted.sort((a, b) => b.price - a.price); break;
            case 'name-asc': sorted.sort((a, b) => a.name.localeCompare(b.name)); break;
            case 'name-desc': sorted.sort((a, b) => b.name.localeCompare(a.name)); break;
            default: sorted.sort((a, b) => b.id - a.id);
        }
        return sorted;
    }, [products, searchQuery, sortOption]);

    return (
        <div className="animate-in fade-in duration-500">
            <div className="mb-8 border-b pb-6">
                <h1 className="text-4xl font-black text-gray-900 mb-6 tracking-tight">Proizvodi</h1>

                <div className="flex flex-col md:flex-row gap-4 items-center">
                    {/* SEARCH BOX WITH COUNT */}
                    <div className="relative flex-grow w-full">
                        <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                        <input
                            type="text"
                            placeholder="Pretraži po nazivu..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            className="w-full pl-12 pr-32 py-3.5 bg-gray-50 border border-gray-200 rounded-2xl outline-none focus:bg-white focus:ring-2 focus:ring-black transition-all shadow-sm"
                        />
                        <div className="absolute right-4 top-1/2 -translate-y-1/2 flex items-center gap-3">
                            {searchQuery && (
                                <span className="text-[10px] font-bold bg-black text-white px-2 py-1 rounded">
                                    {filteredAndSorted.length}
                                </span>
                            )}
                            {searchQuery && (
                                <button onClick={() => setSearchQuery("")} className="text-gray-400 hover:text-black">
                                    <X size={18} />
                                </button>
                            )}
                        </div>
                    </div>

                    {/* SORTING */}
                    <div className="flex items-center gap-3 w-full md:w-auto bg-gray-50 border border-gray-200 px-4 py-3 rounded-2xl">
                        <SlidersHorizontal size={18} className="text-gray-400" />
                        <select
                            value={sortOption}
                            onChange={(e) => setSortOption(e.target.value)}
                            className="bg-transparent text-sm font-bold text-gray-900 outline-none cursor-pointer"
                        >
                            <option value="newest">Najnovije</option>
                            <option value="price-asc">Cena: Rastuća</option>
                            <option value="price-desc">Cena: Opadajuća</option>
                            <option value="name-asc">A - Z</option>
                            <option value="name-desc">Z - A</option>
                        </select>
                    </div>
                </div>
            </div>

            {filteredAndSorted.length > 0 ? (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                    {filteredAndSorted.map((product) => (
                        <ProductCard key={product.id} product={product} />
                    ))}
                </div>
            ) : (
                <div className="text-center py-24 bg-gray-50 rounded-3xl border-2 border-dashed border-gray-200">
                    <Search size={48} className="mx-auto text-gray-200 mb-4" />
                    <h2 className="text-xl font-bold text-gray-900">Nema rezultata</h2>
                    <p className="text-gray-500 mt-1">Pokušajte sa drugom ključnom reči.</p>
                </div>
            )}
        </div>
    );
};