import { useEffect, useState } from 'react';
import { Product } from '../../../types/product';
import { getProducts } from '../api/getProducts';
import { ProductCard } from './ProductCard';

export const ProductList = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        getProducts()
            .then((data) => {
                setProducts(data);
                setLoading(false);
            })
            .catch((err) => {
                setError('Failed to load products. Is the backend running?');
                setLoading(false);
            });
    }, []);

    if (loading) return <div className="text-center p-10">Loading products...</div>;
    if (error) return <div className="text-center text-red-500 p-10">{error}</div>;

    return (
        <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
            <h2 className="text-3xl font-extrabold tracking-tight text-gray-900 mb-8">
                Latest Arrivals
            </h2>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-x-6 gap-y-10">
                {products.map((product) => (
                    <ProductCard key={product.id} product={product} />
                ))}
            </div>
        </div>
    );
};